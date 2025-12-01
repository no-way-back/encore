<<<<<<<< HEAD:funding/src/main/java/com/nowayback/funding/infrastructure/outbox/OutboxRepositoryImpl.java
package com.nowayback.funding.infrastructure.outbox;
========
package com.nowayback.funding.infrastructure.funding;
>>>>>>>> e4871a16310cb3e5a6c4e0786d8db206ced153ba:funding/src/main/java/com/nowayback/funding/infrastructure/funding/OutboxRepositoryImpl.java

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.nowayback.funding.domain.outbox.entity.Outbox;
import com.nowayback.funding.domain.outbox.entity.OutboxStatus;
import com.nowayback.funding.domain.outbox.repository.OutboxRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OutboxRepositoryImpl implements OutboxRepository {

	private final OutboxJpaRepository jpaRepository;

	@Override
	public Outbox save(Outbox outbox) {
		return jpaRepository.save(outbox);
	}

	@Override
	public Optional<Outbox> findById(UUID id) {
		return jpaRepository.findById(id);
	}

	@Override
	public List<Outbox> findPendingEvents() {
		return jpaRepository.findAllByStatus(OutboxStatus.PENDING);
	}

	@Override
	public void delete(Outbox outbox) {
		jpaRepository.delete(outbox);
	}
}
