<<<<<<<< HEAD:funding/src/main/java/com/nowayback/funding/domain/event/OutboxEventCreated.java
package com.nowayback.funding.domain.event;
========
package com.nowayback.funding.domain.funding.event;
>>>>>>>> e4871a16310cb3e5a6c4e0786d8db206ced153ba:funding/src/main/java/com/nowayback/funding/domain/funding/event/OutboxEventCreated.java

import java.util.UUID;

public record OutboxEventCreated(UUID eventId) {
}
