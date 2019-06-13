package io.choerodon.gateway.filter.authentication;

import io.choerodon.core.iam.ResourceLevel;
import io.choerodon.gateway.domain.CategoryMenuDTO;
import io.choerodon.gateway.domain.CheckState;
import io.choerodon.gateway.domain.PermissionDTO;
import io.choerodon.gateway.domain.RequestContext;
import io.choerodon.gateway.mapper.CategoryMenuMapper;
import io.choerodon.gateway.mapper.OrganizationMapper;
import io.choerodon.gateway.mapper.PermissionMapper;
import io.choerodon.gateway.mapper.ProjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 普通接口(除公共接口，loginAccess接口，内部接口以外的接口)
 * 普通用户(超级管理员之外用户)的权限校验
 */
@Component
public class SourceCategoryCheckFilter implements HelperFilter {

    @Value("${choerodon.category.enabled:false}")
    private boolean enableCategory;


    private static final String PROJECT_PATH_ID = "project_id";

    private static final String ORG_PATH_ID = "organization_id";

    private final AntPathMatcher matcher = new AntPathMatcher();

    private PermissionMapper permissionMapper;
    private ProjectMapper projectMapper;
    private OrganizationMapper organizationMapper;
    private CategoryMenuMapper categoryMenuMapper;

    public SourceCategoryCheckFilter(PermissionMapper permissionMapper, ProjectMapper projectMapper, OrganizationMapper organizationMapper, CategoryMenuMapper categoryMenuMapper) {
        this.permissionMapper = permissionMapper;
        this.projectMapper = projectMapper;
        this.organizationMapper = organizationMapper;
        this.categoryMenuMapper = categoryMenuMapper;
    }

    @Override
    public int filterOrder() {
        return 65;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        PermissionDTO permission = context.getPermission();
        if (enableCategory) {
            if (ResourceLevel.SITE.value().equalsIgnoreCase(permission.getResourceLevel()) ||
                    ResourceLevel.USER.value().equalsIgnoreCase(permission.getResourceLevel())) {
                context.response.setStatus(CheckState.SUCCESS_PASS_SITE);
                context.response.setMessage("Have access to this 'site-level' interface, permission: " + context.getPermission());
                return true;
            }
            Long sourceId = ResourceLevel.ORGANIZATION.value().equalsIgnoreCase(permission.getResourceLevel()) ?
                    parseProjectOrOrgIdFromUri(context.getTrueUri(), permission.getPath(), ORG_PATH_ID) :
                    parseProjectOrOrgIdFromUri(context.getTrueUri(), permission.getPath(), PROJECT_PATH_ID);

            if (sourceId == null && ResourceLevel.PROJECT.value().equalsIgnoreCase(permission.getResourceLevel())) {
                context.response.setStatus(CheckState.API_ERROR_PROJECT_ID);
                context.response.setMessage("Project interface must have 'project_id' in path");
                return false;
            } else if (sourceId == null && ResourceLevel.ORGANIZATION.value().equalsIgnoreCase(permission.getResourceLevel())) {
                context.response.setStatus(CheckState.API_ERROR_ORG_ID);
                context.response.setMessage("Organization interface must have 'organization_id' in path");
                return false;
            } else if (sourceId != null) {
                List<String> categories = parseCategory(sourceId, permission.getResourceLevel());
                return checkCategoryMenu(context, permission.getCode(), categories, permission.getResourceLevel());
            }
        }
        return true;
    }

    private Boolean checkCategoryMenu(final RequestContext context,
                                      final String permissionCode,
                                      final List<String> categories,
                                      final String level) {
        List<String> menuCodeList = permissionMapper.selectMenuCodeByPermissionCode(permissionCode);
        if (CollectionUtils.isEmpty(menuCodeList)) {
            return true;
        }
        List<CategoryMenuDTO> select = categoryMenuMapper.selectByMenuCodeList(level, categories, menuCodeList);
        if (CollectionUtils.isEmpty(select) && ResourceLevel.ORGANIZATION.value().equalsIgnoreCase(level)) {
            context.response.setStatus(CheckState.PERMISSION_NOT_PASS_ORG);
            context.response.setMessage("No access to this organization category,category:" + categories);
            return false;
        } else if (!CollectionUtils.isEmpty(select) && ResourceLevel.ORGANIZATION.value().equalsIgnoreCase(level)) {
            context.response.setStatus(CheckState.SUCCESS_PASS_ORG);
            context.response.setMessage("Have access to this 'organization-level' interface, permission: " + context.getPermission());
            return true;
        }

        if (CollectionUtils.isEmpty(select) && ResourceLevel.PROJECT.value().equalsIgnoreCase(level)) {
            context.response.setStatus(CheckState.PERMISSION_NOT_PASS_PROJECT);
            context.response.setMessage("No access to this project category,category:" + categories);
            return false;
        } else if (!CollectionUtils.isEmpty(select) && ResourceLevel.PROJECT.value().equalsIgnoreCase(level)) {
            context.response.setStatus(CheckState.SUCCESS_PASS_PROJECT);
            context.response.setMessage("Have access to this 'project-level' interface, permission: " + context.getPermission());
            return true;
        }
        return true;
    }

    private List<String> parseCategory(final Long sourceId, final String sourceType) {
        List<String> categories = new ArrayList<>();
        if (ResourceLevel.ORGANIZATION.value().equalsIgnoreCase(sourceType)) {
            categories.add(organizationMapper.getCategoryByOrgId(sourceId));
        } else {
            categories.addAll(projectMapper.getCategoriesByProjId(sourceId));
        }
        return categories;
    }

    private Long parseProjectOrOrgIdFromUri(final String uri, final String matchPath, String id) {
        Map<String, String> map = matcher.extractUriTemplateVariables(matchPath, uri);
        if (map.size() < 1) {
            return null;
        }
        String value = map.get(id);
        if (value != null) {
            return Long.parseLong(value);
        }
        return null;
    }

}
