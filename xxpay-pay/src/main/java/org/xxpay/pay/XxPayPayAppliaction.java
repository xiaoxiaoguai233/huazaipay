package org.xxpay.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.util.TimeZone;

/**
 *
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages={"org.xxpay"})
public class XxPayPayAppliaction {

    public static void main(String[] args) {
        SpringApplication.run(XxPayPayAppliaction.class, args);
    }
}
