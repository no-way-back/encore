package com.nowayback.project.fixture;

import com.nowayback.project.domain.projectDraft.entity.ProjectDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectFundingDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectRewardDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectSettlementDraft;
import com.nowayback.project.domain.projectDraft.entity.ProjectStoryDraft;
import com.nowayback.project.domain.projectDraft.spec.RewardOptionSpec;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import com.nowayback.project.domain.projectDraft.vo.RewardPrice;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class ProjectDraftFixture {

    public static final UUID USER_UUID = UUID.randomUUID();

    public static ProjectDraft createDraft() {
        return ProjectDraft.create(USER_UUID);
    }

    public static ProjectDraft createDraftWithStatus(ProjectDraftStatus status) {
        ProjectDraft draft = createDraft();
        setStatus(draft, status);
        return draft;
    }

    public static ProjectDraft createDraftAllCompleted() {
        ProjectDraft draft = createDraft();
        draft.assignStoryDraft(completedStoryDraft());
        draft.assignFundingDraft(completedFundingDraft());
        draft.assignSettlementDraft(completedSettlementDraft());
        draft.replaceRewardDrafts(List.of(completedRewardDraft()));
        return draft;
    }

    public static ProjectStoryDraft completedStoryDraft() {
        ProjectStoryDraft story = ProjectStoryDraft.create();
        story.update(
            "title",
            "summary",
            "category",
            "https://thumbnail",
            "{\"content\": \"json\"}"
        );
        return story;
    }

    public static ProjectFundingDraft completedFundingDraft() {
        ProjectFundingDraft funding = ProjectFundingDraft.create();
        funding.update(
            10000L,
            LocalDate.now().plusDays(1),
            LocalDate.now().plusDays(10)
        );
        return funding;
    }

    public static ProjectSettlementDraft completedSettlementDraft() {
        ProjectSettlementDraft settlement = ProjectSettlementDraft.create();
        settlement.update(
            "123-45-67890",   // businessNumber optional
            "신한은행",
            "110-222-333333",
            "홍길동"
        );
        return settlement;
    }

    public static ProjectRewardDraft completedRewardDraft() {
        ProjectRewardDraft reward = ProjectRewardDraft.create();
        reward.update(
            "리워드 제목",
            new RewardPrice(10000L, 3000, 50000),
            10,
            1,
            List.of()
        );
        return reward;
    }

    public static RewardOptionSpec validRewardOptionSpec() {
        return new RewardOptionSpec(
            1000,
            10,
            1
        );
    }

    private static void setStatus(ProjectDraft draft, ProjectDraftStatus status) {
        try {
            var field = ProjectDraft.class.getDeclaredField("status");
            field.setAccessible(true);
            field.set(draft, status);
        } catch (NoSuchFieldException e) {
            throw new IllegalStateException("status 필드를 찾을 수 없습니다", e);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("status 필드에 접근할 수 없습니다", e);
        }
    }
}
