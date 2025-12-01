<<<<<<<< HEAD:funding/src/main/java/com/nowayback/funding/application/outbox/service/OutboxService.java
package com.nowayback.funding.application.outbox.service;
========
package com.nowayback.funding.domain.service;
>>>>>>>> e4871a16310cb3e5a6c4e0786d8db206ced153ba:funding/src/main/java/com/nowayback/funding/domain/service/OutboxService.java

import java.util.UUID;

public interface OutboxService {

	void markAsPublished(UUID eventId);

	void incrementRetryCount(UUID id);
}
