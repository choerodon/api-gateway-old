# Changelog

这个项目的所有显著变化都将被记录在这个文件中。


## [0.17.0] -2019-05-24

### 新增
- gateway-helper合并到api-gateway
- 添加filter流程图
- 添加knowledge-service和sms-service路由

### 修改
- 请求header同时加入Jwt_Token和Authorization，支持平滑升级

## [0.16.0] - 2019-04-19

### 更新

- 修改初始化配置策略为override

## [0.15.0] - 2019-03-19

### 更新

- 升级`spring boot`版本为`2.0.6`。
- 升级`spring cloud`版本为`Finchley.SR2`。

### 修改

- 修改ci文件

## [0.14.0] - 2019-02-19

### 修改

- 修改`choerodon-starter`依赖版本为`0.8.0.RELEASE`

## [0.13.0] - 2019-01-08

### 修改

- 升级`choerodon-starter`依赖版本为`0.9.0.RELEASE`

## [0.12.0] - 2018-12-12

### 新增

- 新增跨域配置

### 修改

- 升级`choerodon-starter`依赖版本为`0.8.0.RELEASE`

### 移除

- 移除zipkin依赖及相关配置
- 移除hystrix-stream依赖
- 移除kafka依赖及相关配置


## [0.11.0] - 2018-11-13

### 修复

- 修复请求为`multipart/form-data`时，转发到`gateway-helper`处理失败的异常

### 修改

- 更新了基础镜像
- 优化日志打印
- 修改转发到`gateway-helper`的connect为keep-alive

### 新增

- 添加了自定义的`etagFilter`

### 删除

- 去除了`api-gateway`的`etagFilter`

## [0.10.0] - 2018-09-27

### 修改

- 更新license 
- 修改了ci文件
- 更新了基础镜像


### 新增

- 添加了单元测试


## [0.9.0] - 2018-08-17

### 修改

- 升级`choerodon-framework-parent`依赖版本为`0.8.0.RELEASE`。
- 升级`choerodon-starter`依赖版本为`0.6.0.RELEASE`。
- 修改了请求返回的`response header`。

## [0.8.0] - 2018-07-20

### 修改

- 升级choerodon-starter依赖版本为0.5.4.RELEASE。

## [0.7.0] - 2018-06-22

### 修改

- 升级了chart中dbtool的版本为0.5.2。
- 升级choerodon-starter依赖版本为0.5.3.RELEASE。

## [0.6.0] - 2018-06-08

### 新增

- 添加关于`JWT`日志的打印配置。

### 删除
- 去除了不用的`RequestVariableHolder.LABEL`
