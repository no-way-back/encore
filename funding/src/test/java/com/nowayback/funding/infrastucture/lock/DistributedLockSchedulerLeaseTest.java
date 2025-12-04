package com.nowayback.funding.infrastucture.lock;

import static org.junit.jupiter.api.Assertions.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.nowayback.funding.infrastructure.schedule.FundingProjectStatisticsScheduler;

@SpringBootTest
public class DistributedLockSchedulerLeaseTest {

	@Autowired
	private FundingProjectStatisticsScheduler scheduler;

	@Autowired
	private RedissonClient redissonClient;

	@Test
	void distributedLock_should_allow_only_one_execution() throws InterruptedException {
		int threadCount = 5; // 동시성 테스트
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);
		AtomicInteger successCount = new AtomicInteger(0);

		for (int i = 0; i < threadCount; i++) {
			executor.submit(() -> {
				String lockKey = "funding:lock:scheduler:startScheduledProjects";
				RLock lock = redissonClient.getLock(lockKey);
				try {
					// 락 획득 시만 실행
					if (lock.tryLock(0, 5, TimeUnit.SECONDS)) { // LeaseTime 5초로 테스트용
						successCount.incrementAndGet();

						// 실제 스케줄러 호출 (void)
						scheduler.startScheduledProjects();

						// 락 유지 시간 동안 일부러 작업 지연
						Thread.sleep(2000);
					}
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				} finally {
					if (lock.isHeldByCurrentThread()) {
						lock.unlock();
					}
					latch.countDown();
				}
			});
		}

		latch.await();
		executor.shutdown();

		// 동시에 실행된 횟수는 1이어야 함
		assertEquals(1, successCount.get(), "분산 락은 동시에 한 인스턴스만 실행되어야 합니다.");
	}

	@Test
	void lock_should_auto_release_after_leaseTime() throws InterruptedException {
		String lockKey = "funding:lock:scheduler:testAutoRelease";
		RLock lock = redissonClient.getLock(lockKey);

		try {
			// 3초 LeaseTime 설정
			boolean acquired = lock.tryLock(0, 3, TimeUnit.SECONDS);
			assertTrue(acquired, "락 획득 실패");

			// LeaseTime 초과 대기
			Thread.sleep(4000);

			// 다른 스레드에서 다시 락 획득 가능해야 함
			RLock newLock = redissonClient.getLock(lockKey);
			boolean reacquired = newLock.tryLock(0, 3, TimeUnit.SECONDS);
			assertTrue(reacquired, "LeaseTime 이후 락이 자동 해제되지 않음");
			if (reacquired) newLock.unlock();

		} finally {
			if (lock.isHeldByCurrentThread()) lock.unlock();
		}
	}
}
