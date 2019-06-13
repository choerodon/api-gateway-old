package io.choerodon.gateway.domain;

import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;

import javax.persistence.*;
import java.util.Date;

/**
 * @author superlee
 */
@ModifyAudit
@VersionAudit
@Table(name = "fd_organization")
public class OrganizationDTO extends AuditDomain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code;

    private Boolean isEnabled;

    private Long userId;

    private String address;

    private String imageUrl;

    private Boolean isRegister;

    private Integer scale;

    private String homePage;

    @Transient
    private String userName;

    @Transient
    private String userPhone;

    @Transient
    private String userEmail;

    @Transient
    private Integer projectCount;

    @Transient
    private Integer applicationCount;

    @Transient
    private Date lastLoginAt;

    public Long getUserId() {
        return userId;
    }

    public OrganizationDTO setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public OrganizationDTO setAddress(String address) {
        this.address = address;
        return this;
    }

    public Long getId() {
        return id;
    }

    public OrganizationDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public OrganizationDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public OrganizationDTO setCode(String code) {
        this.code = code;
        return this;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public OrganizationDTO setEnabled(Boolean enabled) {
        isEnabled = enabled;
        return this;
    }

    public Boolean getRegister() {
        return isRegister;
    }

    public OrganizationDTO setRegister(Boolean register) {
        isRegister = register;
        return this;
    }

    public Integer getScale() {
        return scale;
    }

    public OrganizationDTO setScale(Integer scale) {
        this.scale = scale;
        return this;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public Integer getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(Integer projectCount) {
        this.projectCount = projectCount;
    }

    public Integer getApplicationCount() {
        return applicationCount;
    }

    public void setApplicationCount(Integer applicationCount) {
        this.applicationCount = applicationCount;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }
}
