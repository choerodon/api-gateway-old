package io.choerodon.gateway.domain;

import java.time.LocalDate;

/**
 * @author superlee
 */
public class TranceSpan {

    private String url;

    private String service;

    private String method;

    private LocalDate today;

    public TranceSpan() {}

    public TranceSpan(String url, String service, String method, LocalDate today) {
        this.url = url;
        this.service = service;
        this.method = method;
        this.today = today;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public LocalDate getToday() {
        return today;
    }

    public void setToday(LocalDate today) {
        this.today = today;
    }

    @Override
    public String toString() {
        return "TranceSpan{" +
                "url='" + url + '\'' +
                ", service='" + service + '\'' +
                ", method='" + method + '\'' +
                ", today=" + today +
                '}';
    }
}
