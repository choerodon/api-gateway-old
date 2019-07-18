package io.choerodon.gateway.mapper;

import io.choerodon.gateway.dto.CategoryMenuDTO;
import io.choerodon.mybatis.common.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 类别权限Mapper
 *
 * @author Eugen
 */
public interface CategoryMenuMapper extends Mapper<CategoryMenuDTO> {
    List<CategoryMenuDTO> selectByMenuCodeList(@Param("level") String level,
                                               @Param("categories") List<String> categories,
                                               @Param("menuCodeList") List<String> menuCodeList);

}
