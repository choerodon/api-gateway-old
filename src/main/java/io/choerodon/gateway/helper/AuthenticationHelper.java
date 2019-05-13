package io.choerodon.gateway.helper;

import io.choerodon.gateway.domain.*;
import io.choerodon.gateway.filter.authentication.HelperFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_JWT;
import static io.choerodon.core.variable.RequestVariableHolder.HEADER_TOKEN;

/**
 * 鉴权逻辑helper
 *
 * @author superlee
 * @since 2019-04-29
 */
@Component
public class AuthenticationHelper {

    private static final String ACCESS_TOKEN_PARAM = "access_token";

    private static final String ACCESS_TOKEN_PREFIX = "bearer";

    private static final String ACCESS_TOKEN_PREFIX_UPPER_STARTED = "Bearer";

    private List<HelperFilter> helperFilters;

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationHelper.class);

    public AuthenticationHelper(Optional<List<HelperFilter>> optionalHelperFilters) {
        helperFilters = optionalHelperFilters.orElseGet(Collections::emptyList)
                .stream()
                .sorted(Comparator.comparing(HelperFilter::filterOrder))
                .collect(Collectors.toList());
    }

    public ResponseContext authentication(HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(new CheckRequest(parse(request),
                request.getRequestURI(), request.getMethod().toLowerCase()), new CheckResponse());

        CheckResponse checkResponse = requestContext.response;
        ResponseContext responseContext = new ResponseContext();
        try {
            for (HelperFilter t : helperFilters) {
                if (t.shouldFilter(requestContext) && !t.run(requestContext)) {
                    break;
                }
            }
        } catch (Exception e) {
            checkResponse.setStatus(CheckState.EXCEPTION_GATEWAY_HELPER);
            checkResponse.setMessage("gateway helper error happened: " + e.toString());
            LOGGER.info("Check permission error", e);
        }
        if (checkResponse.getStatus().getValue() < 300) {
            responseContext.setHttpStatus(HttpStatus.OK);
            LOGGER.debug("Request 200, context: {}", requestContext);
        } else if (checkResponse.getStatus().getValue() < 500) {
            responseContext.setHttpStatus(HttpStatus.FORBIDDEN);
            LOGGER.info("Request 403, context: {}", requestContext);
        } else {
            responseContext.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
            LOGGER.info("Request 500, context: {}", requestContext);
        }

        if (checkResponse.getJwt() != null && responseContext.getHttpStatus().is2xxSuccessful()) {
            request.setAttribute(HEADER_JWT, checkResponse.getJwt());
        }
        if (checkResponse.getMessage() != null) {
            responseContext.setRequestMessage(checkResponse.getMessage());
        }
        responseContext.setRequestStatus(checkResponse.getStatus().name());
        responseContext.setRequestCode(checkResponse.getStatus().getCode());
        return responseContext;
    }

    private String parse(final HttpServletRequest req) {
        String token = req.getHeader(HEADER_TOKEN);
        if (token == null && req.getQueryString() != null && req.getQueryString().contains(ACCESS_TOKEN_PARAM)) {
            for (String i : req.getQueryString().split("&")) {
                if (i.startsWith(ACCESS_TOKEN_PARAM)) {
                    token = i.substring(ACCESS_TOKEN_PARAM.length() + 1);
                }
            }
        }
        if (token != null) {
            if (token.startsWith(ACCESS_TOKEN_PREFIX_UPPER_STARTED)) {
                token = token.replace(ACCESS_TOKEN_PREFIX_UPPER_STARTED, ACCESS_TOKEN_PREFIX);
            }
            if (token.startsWith(ACCESS_TOKEN_PREFIX)) {
                token = token.replaceFirst("%20", " ");
            } else {
                token = ACCESS_TOKEN_PREFIX + " " + token;
            }
        }
        return token;
    }
}