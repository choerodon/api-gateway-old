package io.choerodon.gateway.filter.authentication;


import io.choerodon.gateway.domain.RequestContext;

/**
 * @author flyleft
 * @author superlee
 */
public interface HelperFilter {

    /**
     * filter顺序，越小越先执行
     *
     * @return filter顺序
     */
    int filterOrder();

    /**
     * 是否执行
     *
     * @param context 请求上下文
     * @return true则执行，false不执行
     */
    boolean shouldFilter(RequestContext context);

    /**
     * 执行方法
     *
     * @param context 请求上下文
     * @return true则继续执行后面的filter，false不再执行
     */
    boolean run(RequestContext context);

}
