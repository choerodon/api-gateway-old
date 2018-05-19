# api-gateway
> The gateway service is responsible for routing requests to real services. Use choerodon-starter-config-client to pull configuration information including routes from config-server to implement dynamic routing. After requesting to remove the request body, it is forwarded to gateway-helper for authentication, flow restriction, and the generation of JWT.

Choerodon's gateway service is responsible for routing requests to real services.

![Flow chart](screenshot/flow_chart.png)

## Feature
- In the future, spring cloud zuul may be deprecated and linkerd or other gateway services may be used.

## Requirements
- The project is a project about eureka client. The local operation needs to cooperate with register-server, and the online operation needs to cooperate with go-register-server.
- It needs to cooperate with gateway-helper to complete the gateway functions such as authentication, traffic limiting, and routing.

## To get the code

```
git clone https://github.com/choerodon/api-gateway.git

```

## Installation and Getting Started
- Start register-server
- Go to the project directory， run mvn spring-boot:run， or run GatewayApplication in idea

## Usage
- Build mirror

   Pull source code to execute mvn clean install, generate api.jar in the target directory, copy it to src/main/docker directory, there are dockerfile, perform docker build as a mirror.
- Use existing mirror


- After creating the mirror, create a new deployment on k8s, and then create a new service, ingress. You can refer to the deployment file of chart directory in the code to write.

## Dependencies
- go-register-server:   The registration of service.
- config-server：The configuration of service
- kafka

## Reporting Issues

If you find any shortcomings or bugs, please describe them in the Issue.
    
## How to Contribute
Pull requests are welcome! Follow this link for more information on how to contribute.

## Note
- It needs to cooperate with gateway-helper to complete the gateway functions such as authentication, traffic limiting and routing.
