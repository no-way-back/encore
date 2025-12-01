<<<<<<<< HEAD:funding/src/main/java/com/nowayback/funding/infrastructure/outbox/OutboxJpaRepository.java
package com.nowayback.funding.infrastructure.outbox;
========
package com.nowayback.funding.infrastructure.funding;
>>>>>>>> e4871a16310cb3e5a6c4e0786d8db206ced153ba:funding/src/main/java/com/nowayback/funding/infrastructure/funding/OutboxJpaRepository.java

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nowayback.funding.domain.outbox.entity.Outbox;
import com.nowayback.funding.domain.outbox.entity.OutboxStatus;

public interface OutboxJpaRepository extends JpaRepository<Outbox, UUID> {

	List<Outbox> findAllByStatus(OutboxStatus status);
}