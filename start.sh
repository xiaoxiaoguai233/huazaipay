nohup java -jar -Dspring.profiles.active=test xxpay-service-1.0.0.jar     &>servicelog.log&
sleep 20;
nohup java -jar -Dspring.profiles.active=test xxpay-pay-1.0.0.jar         &>paylog.log&
sleep 20;
nohup java -jar -Dspring.profiles.active=test xxpay-manage.jar            &>managelog.log&
nohup java -jar -Dspring.profiles.active=test xxpay-merchant.jar          &>merchantlog.log&
nohup java -jar -Dspring.profiles.active=test xxpay-agent.jar             &>agentlog.log&
nohup java -jar -Dspring.profiles.active=test xxpay-task.jar              &>tasklog.log