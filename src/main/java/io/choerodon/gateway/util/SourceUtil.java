package io.choerodon.gateway.util;


import org.springframework.util.AntPathMatcher;

import java.util.Map;

/**
 * url中获取sourceId工具类
 *
 * @author superlee
 * @since 2019-07-04
 */
public class SourceUtil {

    private SourceUtil() {
    }


    /**
     * @param uri            trueUri
     * @param matchPath      需要匹配的uri，一般为permission的path字段
     * @param matchFieldName 要匹配的字段名
     * @param matcher        匹配器
     * @return id
     */
    public static Long getSourceId(final String uri,
                                   final String matchPath,
                                   final String matchFieldName,
                                   final AntPathMatcher matcher) {
        Map<String, String> map = matcher.extractUriTemplateVariables(matchPath, uri);
        if (map.size() < 1) {
            return null;
        }
        String value = map.get(matchFieldName);
        if (value != null) {
            return Long.parseLong(value);
        }
        return null;
    }

}
