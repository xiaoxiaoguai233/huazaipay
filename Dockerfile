FROM java:latest
WORKDIR /root/
COPY xxpay-agent/target/xxpay-agent.jar .
COPY xxpay-manage/target/xxpay-manage.jar .
COPY xxpay-merchant/target/xxpay-merchant.jar .
COPY xxpay-pay/target/xxpay-pay-1.0.0.jar .
COPY xxpay-service/target/xxpay-service-1.0.0.jar .
COPY xxpay-task/target/xxpay-task.jar .
COPY start.sh .
ENTRYPOINT ["bash","-x","/root/start.sh"]