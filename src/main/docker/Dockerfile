FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/javabase:0.6.0
COPY app.jar /api-gateway.jar
ENTRYPOINT exec java $JAVA_OPTS  -jar /api-gateway.jar
