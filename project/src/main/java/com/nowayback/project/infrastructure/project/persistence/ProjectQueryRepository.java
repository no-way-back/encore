package com.nowayback.project.infrastructure.project.persistence;

import static org.springframework.util.StringUtils.hasText;

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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProjectQueryRepository {

    private static final String ALL_CATEGORY = "ALL";
    private final JPAQueryFactory queryFactory;
    private final CategoryRepository categoryRepository;

    public Page<ProjectCard> searchProjects(
        String rootCategoryCode,
        String categoryCode,
        Set<ProjectStatus> statuses,
        ProjectSortType sortType,
        Pageable pageable
    ) {
        UUID rootId = resolveCategoryId(rootCategoryCode);
        UUID categoryId = resolveCategoryId(categoryCode);

        List<UUID> ids = buildProjectIds(rootId, categoryId, statuses, sortType)
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        List<ProjectCard> content = buildProjectCardQuery(ids).fetch();

        Long total = countProjects(rootId, categoryId, statuses);

        return new PageImpl<>(content, pageable, total);
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

        return queryFactory.
            select(p.id)
            .from(p)
            .leftJoin(pm).on(p.id.eq(pm.projectId))
            .leftJoin(rs).on(p.id.eq(rs.projectId))
            .where(
                buildStatusFilter(p, statuses),
                buildCategoryFilter(p.rootCategoryId, rootId),
                buildCategoryFilter(p.categoryId, categoryId)
            )
            .orderBy(
                resolveOrderBy(sortType, p, pm, rs),
                p.id.desc()
            );
    }

    private JPAQuery<ProjectCard> buildProjectCardQuery(
        List<UUID> ids
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
            );
    }

    private Long countProjects(UUID rootId, UUID categoryId, Set<ProjectStatus> statuses) {
        QProject p = QProject.project;

        Long count = queryFactory
            .select(p.count())
            .from(p)
            .where(
                buildStatusFilter(p, statuses),
                buildCategoryFilter(p.rootCategoryId, rootId),
                buildCategoryFilter(p.categoryId, categoryId)
            )
            .fetchOne();

        return count != null ? count : 0L;
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
