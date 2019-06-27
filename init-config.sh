#!/usr/bin/env bash
MAVEN_LOCAL_REPO=$(cd / && mvn help:evaluate -Dexpression=settings.localRepository -q -DforceStdout)
TOOL_GROUP_ID=io.choerodon
TOOL_ARTIFACT_ID=choerodon-tool-config
TOOL_VERSION=${1:-0.11.0.RELEASE}
TOOL_JAR_PATH=${MAVEN_LOCAL_REPO}/${TOOL_GROUP_ID/\./\/}/${TOOL_ARTIFACT_ID}/${TOOL_VERSION}/${TOOL_ARTIFACT_ID}-${TOOL_VERSION}.jar
mvn org.apache.maven.plugins:maven-dependency-plugin:get \
  -Dartifact=${TOOL_GROUP_ID}:${TOOL_ARTIFACT_ID}:${TOOL_VERSION} \
  -Dtransitive=false

mvn clean install

java -Dspring.datasource.url="jdbc:mysql://localhost/manager_service?useUnicode=true&characterEncoding=utf-8&useSSL=false&useInformationSchema=true&remarks=true" \
 -Dspring.datasource.username=root \
 -Dspring.datasource.password=root \
 -Dconfig.type=db \
 -Dservice.name=api-gateway \
 -Dservice.version=0.17.0 \
 -Dconfig.updatePolicy=override \
 -Dconfig.jar=target/app.jar \
 -jar ${TOOL_JAR_PATH}


