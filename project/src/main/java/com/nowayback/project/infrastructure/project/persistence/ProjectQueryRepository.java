package com.nowayback.project.infrastructure.project.persistence;

import static org.springframework.util.StringUtils.hasText;

import com.nowayback.project.application.project.dto.Cursor;
import com.nowayback.project.application.project.dto.ProjectCard;
import com.nowayback.project.domain.project.entity.Category;
import com.nowayback.project.domain.project.entity.QProject;
import com.nowayback.project.domain.project.entity.QProjectMetrics;
import com.nowayback.project.domain.project.entity.QProjectRankSnapshot;
import com.nowayback.project.domain.project.repository.CategoryRepository;
import com.nowayback.project.domain.project.vo.ProjectSortType;
import com.nowayback.project.domain.project.vo.ProjectStatus;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectQueryRepository {

    private static final String ALL_CATEGORY = "ALL";
    private final JPAQueryFactory queryFactory;
    private final CategoryRepository categoryRepository;

    public List<ProjectCard> searchProjects(
        String rootCategoryCode,
        String categoryCode,
        Set<ProjectStatus> statuses,
        Cursor cursor
    ) {
        UUID rootId = resolveCategoryId(rootCategoryCode);
        UUID categoryId = resolveCategoryId(categoryCode);

        List<UUID> ids = buildProjectIds(rootId, categoryId, statuses, cursor.sortType())
            .offset(cursor.getOffset())
            .limit(cursor.size())
            .fetch();

        return buildProjectCardQuery(ids, cursor.sortType()).fetch();
    }

    public Long count(
        String rootCategoryCode,
        String categoryCode,
        Set<ProjectStatus> statuses,
        Long limit
    ) {
        UUID rootId = resolveCategoryId(rootCategoryCode);
        UUID categoryId = resolveCategoryId(categoryCode);

        return countProjects(rootId, categoryId, statuses, limit);
    }

    private JPAQuery<UUID> buildProjectIds(
        UUID rootId,
        UUID categoryId,
        Set<ProjectStatus> statuses,
        ProjectSortType sortType
    ) {
        QProject p = QProject.project;
        QProjectMetrics pm = QProjectMetrics.projectMetrics;
        QProjectRankSnapshot rs = QProjectRankSnapshot.projectRankSnapshot;

        ProjectSortType sort = (sortType != null) ? sortType : ProjectSortType.RECOMMENDED;

        JPAQuery<UUID> q = queryFactory
            .select(p.id)
            .from(p)
            .where(
                buildStatusFilter(p, statuses),
                buildCategoryFilter(p.rootCategoryId, rootId),
                buildCategoryFilter(p.categoryId, categoryId)
            );

        if (sort == ProjectSortType.RECOMMENDED || sort == ProjectSortType.POPULAR) {
            q.leftJoin(rs).on(rs.projectId.eq(p.id));
        } else if (sort == ProjectSortType.AMOUNT_DESC || sort == ProjectSortType.BACKERS_DESC) {
            q.leftJoin(pm).on(pm.projectId.eq(p.id));
        }

        return q.orderBy(resolveOrderBy(sort, p, pm, rs), p.id.desc());
    }

    private JPAQuery<ProjectCard> buildProjectCardQuery(
        List<UUID> ids,
        ProjectSortType sortType
    ) {
        QProject p = QProject.project;
        QProjectMetrics pm = QProjectMetrics.projectMetrics;
        QProjectRankSnapshot rs = QProjectRankSnapshot.projectRankSnapshot;

        return queryFactory
            .select(Projections.constructor(
                ProjectCard.class,
                p.id,
                p.title,
                p.thumbnailUrl,
                p.goalAmount,
                pm.pledgedAmount.coalesce(0L),
                pm.backers.coalesce(0),
                p.period.endDate,
                rs.popularityScore
            ))
            .from(p)
            .leftJoin(pm).on(pm.projectId.eq(p.id))
            .leftJoin(rs).on(rs.projectId.eq(p.id))
            .where(
                p.id.in(ids)
            )
            .orderBy(
                resolveOrderBy(sortType, p, pm, rs),
                p.id.desc()
            );
    }

    private Long countProjects(
        UUID rootId,
        UUID categoryId,
        Set<ProjectStatus> statuses,
        Long limit
    ) {
        QProject p = QProject.project;

        List<UUID> ids = queryFactory
            .select(p.id)
            .from(p)
            .where(
                buildStatusFilter(p, statuses),
                buildCategoryFilter(p.rootCategoryId, rootId),
                buildCategoryFilter(p.categoryId, categoryId)
            )
            .limit(limit)
            .fetch();

        return (long) ids.size();
    }

    private UUID resolveCategoryId(String categoryCode) {
        if (!hasText(categoryCode) || ALL_CATEGORY.equalsIgnoreCase(categoryCode)) {
            return null;
        }

        return categoryRepository.findByCode(categoryCode)
            .map(Category::getId)
            .orElseThrow(() -> new IllegalArgumentException(
                "Category not found: " + categoryCode
            ));
    }

    private OrderSpecifier<?> resolveOrderBy(
        ProjectSortType sortType,
        QProject p,
        QProjectMetrics pm,
        QProjectRankSnapshot rs
    ) {
        ProjectSortType sort = (sortType != null) ? sortType : ProjectSortType.RECOMMENDED;

        return switch (sort) {
            case RECOMMENDED, POPULAR -> rs.popularityScore.desc().nullsLast();
            case AMOUNT_DESC -> pm.pledgedAmount.desc().nullsLast();
            case BACKERS_DESC -> pm.backers.desc().nullsLast();
            case DEADLINE -> p.period.endDate.asc();
            case LATEST -> p.createdAt.desc();
        };
    }

    private BooleanExpression buildStatusFilter(QProject p, Set<ProjectStatus> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return p.status.in(
                ProjectStatus.LIVE,
                ProjectStatus.SUCCESS,
                ProjectStatus.FAIL,
                ProjectStatus.ENDED
            );
        }
        return p.status.in(statuses);
    }

    private BooleanExpression buildCategoryFilter(
        SimpleExpression<UUID> expression,
        UUID categoryId
    ) {
        return categoryId != null ? expression.eq(categoryId) : null;
    }
}
