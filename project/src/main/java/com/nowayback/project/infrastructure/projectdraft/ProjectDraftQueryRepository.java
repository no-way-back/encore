package com.nowayback.project.infrastructure.projectdraft;

import static com.nowayback.project.domain.projectDraft.entity.QProjectDraft.projectDraft;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ProjectDraftQueryRepository{

    private final JPAQueryFactory queryFactory;

    public Page<ProjectDraft> searchDrafts(
        UUID userId,
        ProjectDraftStatus status,
        int page,
        int size
    ) {
        BooleanExpression condition = searchCondition(userId, status);

        List<ProjectDraft> drafts = queryFactory
            .selectFrom(projectDraft)
            .where(condition)
            .orderBy(projectDraft.createdAt.desc())
            .offset((long) page * size)
            .limit(size)
            .fetch();

        JPAQuery<Long> total = queryFactory
            .select(projectDraft.count())
            .from(projectDraft)
            .where(condition);

        return PageableExecutionUtils.getPage(
            drafts,
            PageRequest.of(page, size),
            total::fetchOne
        );
    }


    private BooleanExpression searchCondition(UUID userId, ProjectDraftStatus status) {
        return userIdEq(userId)
            .and(statusEq(status));
    }

    private BooleanExpression userIdEq(UUID userId) {
        return projectDraft.userId.eq(userId);
    }

    private BooleanExpression statusEq(ProjectDraftStatus status) {
        return valueEq(projectDraft.status, status);
    }

    private <T> BooleanExpression valueEq(SimpleExpression<T> expression, T value) {
        return value == null ? null : expression.eq(value);
    }
}
