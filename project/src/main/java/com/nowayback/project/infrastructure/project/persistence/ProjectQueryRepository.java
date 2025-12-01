package com.nowayback.project.infrastructure.project.persistence;

import static com.nowayback.project.domain.project.entity.QProject.project;

import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectQueryRepository {

    private final JPAQueryFactory queryFactory;

    public Page<Project> searchDrafts(ProjectStatus status, int page, int size) {
        BooleanExpression condition = searchCondition(status);

        List<Project> drafts = queryFactory
            .selectFrom(project)
            .where(condition)
            .orderBy(project.createdAt.desc())
            .offset((long) page * size)
            .limit(size)
            .fetch();

        JPAQuery<Long> total = queryFactory
            .select(project.count())
            .from(project)
            .where(condition);

        return PageableExecutionUtils.getPage(
            drafts,
            PageRequest.of(page, size),
            total::fetchOne
        );
    }


    private BooleanExpression searchCondition(ProjectStatus status) {
        return statusEq(status);
    }


    private BooleanExpression statusEq(ProjectStatus status) {
        return valueEq(project.status, status);
    }

    private <T> BooleanExpression valueEq(SimpleExpression<T> expression, T value) {
        return value == null ? null : expression.eq(value);
    }
}
