package io.choerodon.gateway.my

import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.client.ClientHttpResponse
import static io.choerodon.core.variable.RequestVariableHolder.HEADER_TOKEN

/**
 * Created by superlee on 2018/9/18.
 */
class MyClientHttpResponse implements ClientHttpResponse {

    private HttpStatus httpStatus

    private HttpHeaders headers = new HttpHeaders()

    MyClientHttpResponse() {
        headers.add(HEADER_TOKEN, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwYXNzd29yZCI6InVua25vd24gcGFzc3dvcmQiLCJ1c2VybmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOltdLCJhY2NvdW50Tm9uRXhwaXJlZCI6dHJ1ZSwiYWNjb3VudE5vbkxvY2tlZCI6dHJ1ZSwiY3JlZGVudGlhbHNOb25FeHBpcmVkIjp0cnVlLCJlbmFibGVkIjp0cnVlLCJ1c2VySWQiOjEsImVtYWlsIjpudWxsLCJ0aW1lWm9uZSI6IkNUVCIsImxhbmd1YWdlIjoiemhfQ04iLCJvcmdhbml6YXRpb25JZCI6MSwiYWRkaXRpb25JbmZvIjpudWxsLCJhZG1pbiI6dHJ1ZX0.uPTErjLVVlAIiMC7fa-pSQNcK2o6ioAVgBLgb-Gt_yE")
        httpStatus = HttpStatus.valueOf(200)
    }

    MyClientHttpResponse(int code) {
        headers.add(HEADER_TOKEN, "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJwYXNzd29yZCI6InVua25vd24gcGFzc3dvcmQiLCJ1c2VybmFtZSI6ImFkbWluIiwiYXV0aG9yaXRpZXMiOltdLCJhY2NvdW50Tm9uRXhwaXJlZCI6dHJ1ZSwiYWNjb3VudE5vbkxvY2tlZCI6dHJ1ZSwiY3JlZGVudGlhbHNOb25FeHBpcmVkIjp0cnVlLCJlbmFibGVkIjp0cnVlLCJ1c2VySWQiOjEsImVtYWlsIjpudWxsLCJ0aW1lWm9uZSI6IkNUVCIsImxhbmd1YWdlIjoiemhfQ04iLCJvcmdhbml6YXRpb25JZCI6MSwiYWRkaXRpb25JbmZvIjpudWxsLCJhZG1pbiI6dHJ1ZX0.uPTErjLVVlAIiMC7fa-pSQNcK2o6ioAVgBLgb-Gt_yE")
        httpStatus = HttpStatus.valueOf(code)
    }

    void setStatusCode(int code) {
        httpStatus = HttpStatus.valueOf(code)
    }

    @Override
    HttpStatus getStatusCode() throws IOException {
        return httpStatus
    }

    @Override
    int getRawStatusCode() throws IOException {
        return 0
    }

    @Override
    String getStatusText() throws IOException {
        return null
    }

    @Override
    void close() {

    }

    @Override
    InputStream getBody() throws IOException {
        return null
    }

    @Override
    HttpHeaders getHeaders() {
        return headers
    }

    void addHeaders(String headerName, String headerValue) {
        headers.add(headerName, headerValue)
    }

}
