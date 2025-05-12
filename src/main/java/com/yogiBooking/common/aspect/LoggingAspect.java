package com.yogiBooking.common.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Before("within(com.yogiBooking.common.controller..*)")
    public void printBefore(JoinPoint joinPoint){
        JSONObject logMap = new JSONObject();
        logMap.put("Class", joinPoint.getSignature().getDeclaringType());
        logMap.put("Method", joinPoint.getSignature().getName());

        JSONObject argumentLogMap =  new JSONObject();
        for(Object o:joinPoint.getArgs()){
            argumentLogMap.put("Argument", o);
            argumentLogMap.put("Data type", o.getClass());
        }
        logMap.put("Arguments", argumentLogMap);
        log.info(logMap.toString());
    }


    @AfterReturning(value = "within(com.yogiBooking.common.controller..*)", returning = "results")
    public void printAfterLog(JoinPoint joinPoint, Object results) {
        log.info("Class name: {}\n", joinPoint.getSignature().getDeclaringType());
        log.info("Method name: {}\n", joinPoint.getSignature().getName());
        log.info("Return values: {}\n", results);

    }
}
