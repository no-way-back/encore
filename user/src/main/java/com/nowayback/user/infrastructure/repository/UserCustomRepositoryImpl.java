package com.nowayback.user.infrastructure.repository;

import com.nowayback.user.domain.entity.QUser;
import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

import static com.nowayback.user.domain.entity.QUser.user;

@Repository
@RequiredArgsConstructor
public class UserCustomRepositoryImpl implements UserCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<User> searchUser(String keyword, List<String> searchFields, UserRole role, UserStatus status, Pageable pageable) {
        BooleanExpression expression = searchCondition(keyword, searchFields, role, status);

        List<User> users = queryFactory
                .selectFrom(user)
                .where(expression)
                .orderBy(getOrderSpecifiers(pageable.getSort()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> total = queryFactory
                .select(user.count())
                .from(user)
                .where(expression);

        return PageableExecutionUtils.getPage(users, pageable, total::fetchOne);
    }

    private BooleanExpression searchCondition(String keyword, List<String> searchFields, UserRole role, UserStatus status) {
        return andAll(
                keywordInFields(keyword, searchFields),
                roleEquals(role),
                statusEquals(status)
        );
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

    private BooleanExpression keywordInFields(String keyword, List<String> searchFields) {
        if (keyword == null || keyword.isBlank() || searchFields == null || searchFields.isEmpty()) {
            return null;
        }

        QUser user = QUser.user;
        BooleanExpression expression = null;

        for (String field : searchFields) {
            BooleanExpression fieldExpression = switch (field) {
                case "username" -> user.username.containsIgnoreCase(keyword);
                case "email" -> user.email.containsIgnoreCase(keyword);
                case "nickname" -> user.nickname.containsIgnoreCase(keyword);
                default -> null;
            };

            if (fieldExpression != null) {
                expression = expression == null ? fieldExpression : expression.or(fieldExpression);
            }
        }

        return expression;
    }

    private BooleanExpression roleEquals(UserRole role) {
        return valueEquals(user.role, role);
    }

    private BooleanExpression statusEquals(UserStatus status) {
        return valueEquals(user.status, status);
    }

    private <T> BooleanExpression valueEquals(SimpleExpression<T> expression, T value) {
        return value == null ? null : expression.eq(value);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Sort sort) {
        if (sort.isEmpty()) return new OrderSpecifier[]{user.createdAt.desc()};

        return sort.stream()
                .map(this::toOrderSpecifier)
                .filter(Objects::nonNull)
                .toArray(OrderSpecifier[]::new);
    }

    private OrderSpecifier<?> toOrderSpecifier(Sort.Order order) {
        Order direction = order.isAscending() ? Order.ASC : Order.DESC;

        return switch (order.getProperty()) {
            case "username" -> new OrderSpecifier<>(direction, user.username);
            case "email" -> new OrderSpecifier<>(direction, user.email);
            case "nickname" -> new OrderSpecifier<>(direction, user.nickname);
            case "role" -> new OrderSpecifier<>(direction, user.role);
            case "status" -> new OrderSpecifier<>(direction, user.status);
            case "createdAt" -> new OrderSpecifier<>(direction, user.createdAt);
            case "updatedAt" -> new OrderSpecifier<>(direction, user.updatedAt);
            default -> null;
        };
    }
}
