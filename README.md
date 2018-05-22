# Api-Gateway
The gateway service is responsible for routing requests to real services. Use `choerodon-starter-config-client` to pull configuration information including routes from `config-server `to implement dynamic routing. After requesting to remove the request body, it is forwarded to `gateway-helper` for authentication, rateLimit.

![Flow chart](screenshot/flow_chart.png)

## Feature
- In the future, spring cloud zuul may be deprecated and `linkerd` or other gateway services may be used.

## Requirements
- The project is a project about eureka client. The local operation needs to cooperate with `register-server`, and the online operation needs to cooperate with `go-register-server`.
- It needs to cooperate with `gateway-helper` to complete the gateway functions such as authentication, traffic limiting, and routing.å

## Installation and Getting Started
- Start `register-server`
- Go to the project directory， run `mvn spring-boot:run`

## Dependencies
- `go-register-server`:   The registration of service.
- `config-server`：The configuration of service
- `kafka`

## Reporting Issues
If you find any shortcomings or bugs, please describe them in the [issue](https://github.com/choerodon/choerodon/issues/new?template=issue_template.md).

## How to Contribute
Pull requests are welcome! [Follow](https://github.com/choerodon/choerodon/blob/master/CONTRIBUTING.md) to know for more information on how to contribute.

## Note
- It needs to cooperate with gateway-helper to complete the gateway functions such as authentication, traffic limiting and routing.
