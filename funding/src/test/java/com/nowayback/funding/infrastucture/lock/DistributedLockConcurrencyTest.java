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
class DistributedLockConcurrencyTest {

	@Autowired
	private FundingProjectStatisticsScheduler scheduler;

	@Autowired
	private RedissonClient redissonClient;

	@Test
	void testDistributedLock_singleExecution() throws InterruptedException {
		int threadCount = 3;
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		CountDownLatch latch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger(0);

		for (int i = 0; i < threadCount; i++) {
			executor.submit(() -> {
				String lockKey = "funding:lock:scheduler:startScheduledProjects";
				RLock lock = redissonClient.getLock(lockKey);
				try {
					// 락 획득 시만 실행
					if (lock.tryLock(0, 30, TimeUnit.MINUTES)) {
						successCount.incrementAndGet();
						scheduler.startScheduledProjects(); // void 메서드 호출
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

		assertEquals(1, successCount.get(), "분산 락은 동시에 한 인스턴스만 실행되어야 합니다.");
	}
}