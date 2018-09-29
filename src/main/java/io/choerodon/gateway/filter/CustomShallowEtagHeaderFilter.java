package io.choerodon.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.util.DigestUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

public class CustomShallowEtagHeaderFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CustomShallowEtagHeaderFilter.class);

    private static final String HEADER_ETAG = "ETag";

    private static final String HEADER_IF_NONE_MATCH = "If-None-Match";

    private static final String HEADER_CACHE_CONTROL = "Cache-Control";

    private static final String DIRECTIVE_NO_STORE = "no-store";

    private static final String STREAMING_ATTRIBUTE = ShallowEtagHeaderFilter.class.getName() + ".STREAMING";


    private boolean writeWeakETag = false;


    public void setWriteWeakETag(boolean writeWeakETag) {
        this.writeWeakETag = writeWeakETag;
    }

    public boolean isWriteWeakETag() {
        return this.writeWeakETag;
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        HttpServletResponse responseToUse = response;
        if (!isAsyncDispatch(request) && !(response instanceof ContentCachingResponseWrapper)) {
            responseToUse = new HttpStreamingAwareContentCachingResponseWrapper(response, request);
        }

        filterChain.doFilter(request, responseToUse);

        if (!isAsyncStarted(request) && !isContentCachingDisabled(request)) {
            updateResponse(request, responseToUse);
        }
    }

    private void updateResponse(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ContentCachingResponseWrapper responseWrapper =
                WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
        Assert.notNull(responseWrapper, "ContentCachingResponseWrapper not found");
        HttpServletResponse rawResponse = (HttpServletResponse) responseWrapper.getResponse();
        int statusCode = responseWrapper.getStatusCode();

        if (rawResponse.isCommitted()) {
            responseWrapper.copyBodyToResponse();
        } else if (isEligibleForEtag(request, responseWrapper, statusCode)) {
            String responseETag = generateETagHeaderValue(responseWrapper.getContentInputStream(), this.writeWeakETag);
            rawResponse.setHeader(HEADER_ETAG, responseETag);
            String requestETag = request.getHeader(HEADER_IF_NONE_MATCH);
            if (requestETag != null && ("*".equals(requestETag) || compareETagHeaderValue(requestETag, responseETag))) {
                LOGGER.debug("request 304, uri: {} If-None-Match: {}, responseETag: {}", request.getRequestURI(), requestETag, responseETag);
                rawResponse.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            } else {
                responseWrapper.copyBodyToResponse();
            }
        } else {
            responseWrapper.copyBodyToResponse();
        }
    }


    protected boolean isEligibleForEtag(HttpServletRequest request, HttpServletResponse response,
                                        int responseStatusCode) {

        String method = request.getMethod();
        if (responseStatusCode >= 200 && responseStatusCode < 300 && HttpMethod.GET.matches(method)) {
            String cacheControl = response.getHeader(HEADER_CACHE_CONTROL);
            return (cacheControl == null || !cacheControl.contains(DIRECTIVE_NO_STORE));
        }
        return false;
    }

    protected String generateETagHeaderValue(InputStream inputStream, boolean isWeak) throws IOException {
        // length of W/ + " + 0 + 32bits md5 hash + "
        StringBuilder builder = new StringBuilder(37);
        if (isWeak) {
            builder.append("W/");
        }
        builder.append("\"0");
        DigestUtils.appendMd5DigestAsHex(inputStream, builder);
        builder.append('"');
        return builder.toString();
    }

    private boolean compareETagHeaderValue(String requestETag, String responseETag) {
        if (requestETag.startsWith("W/")) {
            requestETag = requestETag.substring(2);
        }
        if (responseETag.startsWith("W/")) {
            responseETag = responseETag.substring(2);
        }
        return requestETag.equals(responseETag);
    }


    public static void disableContentCaching(ServletRequest request) {
        Assert.notNull(request, "ServletRequest must not be null");
        request.setAttribute(STREAMING_ATTRIBUTE, true);
    }

    private static boolean isContentCachingDisabled(HttpServletRequest request) {
        return (request.getAttribute(STREAMING_ATTRIBUTE) != null);
    }


    private static class HttpStreamingAwareContentCachingResponseWrapper extends ContentCachingResponseWrapper {

        private final HttpServletRequest request;

        public HttpStreamingAwareContentCachingResponseWrapper(HttpServletResponse response, HttpServletRequest request) {
            super(response);
            this.request = request;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return (useRawResponse() ? getResponse().getOutputStream() : super.getOutputStream());
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            return (useRawResponse() ? getResponse().getWriter() : super.getWriter());
        }

        private boolean useRawResponse() {
            return isContentCachingDisabled(this.request);
        }
    }

}