package org.tursunkulov.authorization.aspect;

import java.time.Duration;
import java.time.Instant;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect {
  @Before("execution( * org.tursunkulov.authorization.controller.*.*( .. ))")
  public void logBefore(JoinPoint joinPoint) {
    System.out.println("Вызвано перед методом: " + joinPoint.getSignature().getName());
  }

  @Around("execution( * org.tursunkulov.authorization.controller.*.*( .. ))")
  public Object measureExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
    Instant startTime = Instant.now();
    Object result = joinPoint.proceed();
    Instant endTime = Instant.now();
    System.out.println(
        "Время исполнения метода "
            + joinPoint.getSignature().getName()
            + " равно "
            + Duration.between(startTime, endTime).toMillis()
            + " ms");
    return result;
  }
}
