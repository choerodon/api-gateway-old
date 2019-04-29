
package io.choerodon.gateway.domain;

public enum CheckState {

    /**
     * 全局层接口权限校验通过
     */
    SUCCESS_PASS_SITE(201, "success.permission.sitePass"),

    /**
     * 项目层权限校验通过
     */
    SUCCESS_PASS_PROJECT(202, "success.permission.projectPass"),

    /**
     * 组织层权限校验通过
     */
    SUCCESS_PASS_ORG(203, "success.permission.organizationPass"),

    /**
     * 该接口为gateway-helper设置的跳过权限校验的接口，允许通过
     */
    SUCCESS_SKIP_PATH(204, "success.permission.skipPath"),

    /**
     * 公共接口，允许访问
     */
    SUCCESS_PUBLIC_ACCESS(205, "success.permission.publicAccess"),

    /**
     * loginAccess接口且已经登录，允许访问
     */
    SUCCESS_LOGIN_ACCESS(206, "success.permission.loginAccess"),

    /**
     * 超级管理员用户，且接口为非内部接口，允许访问
     */
    SUCCESS_ADMIN(207, "success.permission.adminUser"),

    /**
     * gateway-helper关闭权限校验，一切请求都允许访问
     */
    SUCCESS_PERMISSION_DISABLED(208, "success.permission.disabled"),

    /**
     * 未找到该请求对应的zuul路由，请在路由管理页面添加路由
     */
    PERMISSION_SERVICE_ROUTE(401, "error.permission.routeNotFound"),

    /**
     * 内部接口禁止访问，只允许服务内部调用
     */
    PERMISSION_WITH_IN(402, "error.permission.withinForbidden"),

    /**
     * 未找到与该请求相匹配的权限
     */
    PERMISSION_MISMATCH(403, "error.permission.mismatch"),

    /**
     * 该登录用户没有此接口访问权限
     */
    PERMISSION_NOT_PASS(404, "error.permission.notPass"),

    /**
     * 该登录用户没有在此项目下的此接口访问权限
     */
    PERMISSION_NOT_PASS_PROJECT(405, "error.permission.projectNotPass"),

    /**
     * 该登录用户没有在此组织下的此接口访问权限
     */
    PERMISSION_NOT_PASS_ORG(406, "error.permission.organizationNotPass"),

    /**
     * 请求头部没有access_token
     */
    PERMISSION_ACCESS_TOKEN_NULL(407, "error.permission.accessTokenNull"),

    /**
     * accessToken不合法
     */
    PERMISSION_ACCESS_TOKEN_INVALID(408, "error.permission.accessTokenInvalid"),

    /**
     * accessToken已过期
     */
    PERMISSION_ACCESS_TOKEN_EXPIRED(409, "error.permission.accessTokenExpired"),

    /**
     * 通过access_token从oauthServer获取userDetails失败
     */
    PERMISSION_GET_USE_DETAIL_FAILED(410, "error.permission.getUserDetailsFromOauthServer"),


    /**
     * 该项目已经被禁用
     */
    PERMISSION_DISABLED_PROJECT(411, "error.permission.projectDisabled"),

    /**
     * 该组织已经被禁用
     */
    PERMISSION_DISABLED_ORG(412, "error.permission.organizationDisabled"),

    /**
     * 访问过于频繁
     */
    RATE_LIMIT_NOT_PASS(301, "error.visit.frequent"),

    /**
     * gatewayHelper发生异常
     */
    EXCEPTION_GATEWAY_HELPER(501, "error.gatewayHelper.exception"),

    /**
     * 无法获取jwt
     */
    EXCEPTION_OAUTH_SERVER(502, "error.oauthServer.exception"),

    /**
     * 接口异常。项目下的接口路径必须包含project_id
     */
    API_ERROR_PROJECT_ID(503, "error.api.projectId"),

    /**
     * 接口异常。组织下的接口路径必须包含organization_id
     */
    API_ERROR_ORG_ID(504, "error.api.orgId"),

    /**
     * 接口异常。本请求可以同时匹配到多个接口
     */
    API_ERROR_MATCH_MULTIPLY(505, "error.api.matchMultiplyPermission");

    private final int value;

    private final String code;

    CheckState(int value, String code) {
        this.value = value;
        this.code = code;
    }

    public int getValue() {
        return value;
    }

    public String getCode() {
        return code;
    }
}

