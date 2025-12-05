package com.nowayback.payment.infrastructure.payment.repository;

import com.nowayback.payment.domain.payment.entity.Payment;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

import static com.nowayback.payment.domain.payment.entity.QPayment.*;

@Repository
@RequiredArgsConstructor
public class PaymentCustomRepositoryImpl implements PaymentCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Payment> searchPayments(UUID userId, UUID projectId, Pageable pageable) {
        BooleanExpression expression = searchCondition(userId, projectId);

        List<Payment> payments = queryFactory
                .selectFrom(payment)
                .where(expression)
                .orderBy(payment.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = queryFactory
                .select(payment.count())
                .from(payment)
                .where(expression);

        return PageableExecutionUtils.getPage(payments, pageable, total::fetchOne);
    }

    private BooleanExpression searchCondition(UUID userId, UUID projectId) {
        return andAll(
                userIdEquals(userId),
                projectIdEquals(projectId)
        );
    }

    private BooleanExpression userIdEquals(UUID userId) {
        return userId == null ? null : payment.userId.id.eq(userId);
    }

    private BooleanExpression projectIdEquals(UUID projectId) {
        return projectId == null ? null : payment.projectId.id.eq(projectId);
    }

    private BooleanExpression andAll(BooleanExpression... expressions) {
        BooleanExpression result = null;
        for (BooleanExpression expr : expressions) {
            if (expr != null) {
                result = result == null ? expr : result.and(expr);
            }
        }
        return result;
    }
}
