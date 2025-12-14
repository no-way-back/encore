package com.nowayback.funding.infrastructure.aop;

import java.lang.reflect.Method;
import java.util.Objects;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @DistributedLock 선언 시 수행되는 Aop class
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

	private static final String REDISSON_LOCK_PREFIX = "funding:lock:";

	private final RedissonClient redissonClient;
	private final AopForTransaction aopForTransaction;

	@Around("@annotation(com.nowayback.funding.infrastructure.aop.DistributedLock)")
	public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

		Objects.requireNonNull(distributedLock, "@DistributedLock 어노테이션을 찾을 수 없습니다.");

		String lockKey = distributedLock.key();
		if (lockKey == null || lockKey.trim().isEmpty()) {
			throw new IllegalArgumentException(
				"락 키가 비어 있습니다. method=" + method.getName()
			);
		}

		Object dynamicValue = CustomSpringELParser.getDynamicValue(
			signature.getParameterNames(),
			joinPoint.getArgs(),
			lockKey
		);

		if (dynamicValue == null) {
			throw new IllegalArgumentException(
				"SpEL 파싱 실패. expression=" + lockKey
			);
		}

		String key = REDISSON_LOCK_PREFIX + dynamicValue;
		RLock rLock = redissonClient.getLock(key);

		try {
			boolean available = rLock.tryLock(distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());

			if (!available) {
				log.warn("분산 락 획득 실패 - key={}", key);
				return null;
			}

			log.debug("분산 락 획득 성공 - key={}", key);

			if (distributedLock.useTransaction() && aopForTransaction != null) {
				return aopForTransaction.proceed(joinPoint);
			}
			return joinPoint.proceed();

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			log.error("락 획득 중 인터럽트 발생 - key={}", key, e);
			throw e;

		} finally {
			try {
				if (rLock.isHeldByCurrentThread()) {
					rLock.unlock();
					log.debug("분산 락 해제 - key={}", key);
				}
			} catch (IllegalMonitorStateException e) {
				log.warn("이미 해제된 락입니다. method={}, key={}",
					method.getName(), key
				);
			}
		}
	}
}
