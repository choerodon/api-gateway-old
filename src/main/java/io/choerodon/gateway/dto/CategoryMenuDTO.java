package io.choerodon.gateway.dto;

import io.choerodon.mybatis.entity.BaseDTO;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author jiameng.cao
 * @date 2019/6/4
 */
@Table(name = "fd_category_menu")
public class CategoryMenuDTO extends BaseDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String categoryCode;
    private String menuCode;
    private String ResourceLevel;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCategoryCode() {
        return categoryCode;
    }

    public void setCategoryCode(String categoryCode) {
        this.categoryCode = categoryCode;
    }

    public String getMenuCode() {
        return menuCode;
    }

    public void setMenuCode(String menuCode) {
        this.menuCode = menuCode;
    }

    public String getResourceLevel() {
        return ResourceLevel;
    }

    public void setResourceLevel(String resourceLevel) {
        ResourceLevel = resourceLevel;
    }
}
