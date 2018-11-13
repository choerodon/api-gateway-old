FROM registry.cn-hangzhou.aliyuncs.com/choerodon-tools/javabase:0.6.0
COPY app.jar /api-gateway.jar
COPY agent /agent
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]
