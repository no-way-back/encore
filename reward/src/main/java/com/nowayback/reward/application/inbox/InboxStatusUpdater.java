package com.nowayback.reward.application.inbox;

import com.nowayback.reward.application.inbox.repository.InboxRepository;
import com.nowayback.reward.domain.inbox.entity.Inbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 인박스 상태 기록을 비즈니스 트랜잭션과 분리해서 커밋한다.
 * InboxProcessor 안에서 직접 호출하면 프록시를 거치지 않아 REQUIRES_NEW가 적용되지 않으므로 별도 빈으로 둔다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InboxStatusUpdater {

    private final InboxRepository inboxRepository;

    /**
     * 수신 기록을 먼저 커밋한다.
     * 처음 받은 이벤트면 true, 이미 받은 적 있는 이벤트면 false.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean recordReceived(Inbox inbox) {
        return inboxRepository.saveIfAbsent(inbox);
    }

    /**
     * 처리 실패를 별도 트랜잭션으로 기록한다.
     * 수신 기록이 이미 커밋돼 있으므로 본 작업이 롤백돼도 실패 상태와 메시지가 남는다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void markAsFailed(UUID eventId, String errorMessage) {
        inboxRepository.findById(eventId)
                .ifPresentOrElse(
                        inbox -> inbox.markAsFailed(errorMessage),
                        () -> log.warn("실패를 기록할 인박스가 없음 - eventId: {}", eventId)
                );
    }
}
