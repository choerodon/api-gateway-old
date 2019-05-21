package io.choerodon.gateway.service.impl;

import io.choerodon.gateway.domain.PermissionDTO;
import io.choerodon.gateway.mapper.PermissionMapper;
import io.choerodon.gateway.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static io.choerodon.gateway.filter.authentication.GetRequestRouteFilter.REQUEST_KEY_SEPARATOR;

@Service
public class PermissionServiceImpl implements PermissionService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PermissionService.class);

    private final AntPathMatcher matcher = new AntPathMatcher();

    private PermissionMapper permissionMapper;

    public PermissionServiceImpl(PermissionMapper permissionMapper) {
        this.permissionMapper = permissionMapper;
    }

    /**
     * Cacheable 设置使用二级缓存
     * 先通过method和service从数据库中查询权限；
     * 如果匹配到多条权限，则排序计算出匹配度最高的权限
     */
    @Override
    @Cacheable(value = "permission", key = "'choerodon:permission:'+#requestKey", unless = "#result == null")
    public PermissionDTO selectPermissionByRequest(String requestKey) {
        String[] request = requestKey.split(REQUEST_KEY_SEPARATOR);
        String uri = request[0];
        String method = request[1];
        List<PermissionDTO> permissions = permissionMapper.selectPermissionByMethodAndService(method, request[2]);
        List<PermissionDTO> matchPermissions = permissions.stream().filter(t -> matcher.match(t.getPath(), uri))
                .sorted((PermissionDTO o1, PermissionDTO o2) -> {
                    Comparator<String> patternComparator = matcher.getPatternComparator(uri);
                    return patternComparator.compare(o1.getPath(), o2.getPath());
                }).collect(Collectors.toList());
        int matchSize = matchPermissions.size();
        if (matchSize < 1) {
            return null;
        } else {
            PermissionDTO bestMatchPermission = matchPermissions.get(0);
            if (matchSize > 1) {
                LOGGER.info("Request: {} match multiply permission: {}, the best match is: {}",
                        uri, matchPermissions, bestMatchPermission.getPath());
            }
            return bestMatchPermission;
        }
    }


}
