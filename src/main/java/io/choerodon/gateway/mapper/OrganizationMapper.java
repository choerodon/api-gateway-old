package io.choerodon.gateway.mapper;

import io.choerodon.gateway.dto.OrganizationDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 权限mapper
 *
 * @author Eugen
 */
public interface OrganizationMapper extends Mapper<OrganizationDTO> {
    /**
     * 根据组织ID获取组织类别
     *
     * @param orgId 组织主键
     * @return 组织类别
     */
    String getCategoryByOrgId(@Param("org_id") Long orgId);
}
