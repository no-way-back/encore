package com.nowayback.project.domain.projectDraft.entity;

import static com.nowayback.project.fixture.ProjectDraftFixture.USER_UUID;
import static com.nowayback.project.fixture.ProjectDraftFixture.completedFundingDraft;
import static com.nowayback.project.fixture.ProjectDraftFixture.completedRewardDraft;
import static com.nowayback.project.fixture.ProjectDraftFixture.completedSettlementDraft;
import static com.nowayback.project.fixture.ProjectDraftFixture.completedStoryDraft;
import static com.nowayback.project.fixture.ProjectDraftFixture.createDraft;
import static com.nowayback.project.fixture.ProjectDraftFixture.createDraftAllCompleted;
import static com.nowayback.project.fixture.ProjectDraftFixture.createDraftWithStatus;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.nowayback.project.domain.exception.ProjectErrorCode;
import com.nowayback.project.domain.exception.ProjectException;
import com.nowayback.project.domain.projectDraft.vo.ProjectDraftStatus;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("프로젝트 드래프트 엔티티")
class ProjectDraftTest {

    @Nested
    @DisplayName("드래프트 생성")
    class Create {

        @Test
        @DisplayName("정상 생성 시 userId가 설정되고 상태는 DRAFT이다.")
        void create_givenValidUserId_thenSuccess() {
            /* when */
            ProjectDraft draft = createDraft();

            /* then */
            assertThat(draft.getUserId()).isEqualTo(USER_UUID);
            assertThat(draft.getStatus()).isEqualTo(ProjectDraftStatus.DRAFT);
        }
    }

    @Nested
    @DisplayName("드래프트 수정 가능 여부 검사")
    class EnsureUpdatable {

        @Test
        @DisplayName("status가 SUBMITTED이면 수정 불가 예외가 발생한다.")
        void ensureUpdatable_givenSubmittedStatus_thenThrow() {
            /* given */
            ProjectDraft draft = createDraftWithStatus(ProjectDraftStatus.SUBMITTED);

            /* then */
            assertThatThrownBy(draft::ensureUpdatable)
                .isInstanceOf(ProjectException.class)
                .hasMessage(ProjectErrorCode.INVALID_PROJECT_DRAFT_STATUS.getMessage());
        }

        @Test
        @DisplayName("status가 DRAFT이면 ensureUpdatable()는 정상적으로 통과한다.")
        void ensureUpdatable_givenDraftStatus_thenSuccess() {
            /* given */
            ProjectDraft draft = createDraft();

            /* when & then */
            assertThatNoException().isThrownBy(draft::ensureUpdatable);
        }
    }

    @Nested
    @DisplayName("드래프트 제출")
    class Submit {

        @Test
        @DisplayName("storyDraft가 null이면 제출 시 예외가 발생한다.")
        void submit_givenNullStoryDraft_thenThrow() {
            /* given */
            ProjectDraft draft = createDraft();
            draft.assignFundingDraft(completedFundingDraft());
            draft.assignSettlementDraft(completedSettlementDraft());
            draft.replaceRewardDrafts(List.of(completedRewardDraft()));

            /* then */
            assertThatThrownBy(draft::submit)
                .isInstanceOf(ProjectException.class)
                .hasMessage(ProjectErrorCode.INVALID_DRAFT_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("storyDraft가 미완료이면 제출 시 예외가 발생한다.")
        void submit_givenIncompleteStoryDraft_thenThrow() {
            /* given */
            ProjectDraft draft = createDraft();
            ProjectStoryDraft story = ProjectStoryDraft.create();
            draft.assignStoryDraft(story);

            draft.assignFundingDraft(completedFundingDraft());
            draft.assignSettlementDraft(completedSettlementDraft());
            draft.replaceRewardDrafts(List.of(completedRewardDraft()));

            /* then */
            assertThatThrownBy(draft::submit)
                .isInstanceOf(ProjectException.class)
                .hasMessage(ProjectErrorCode.INVALID_DRAFT_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("fundingDraft가 null이면 제출 시 예외가 발생한다.")
        void submit_givenNullFundingDraft_thenThrow() {
            /* given */
            ProjectDraft draft = createDraft();
            draft.assignStoryDraft(completedStoryDraft());
            draft.assignSettlementDraft(completedSettlementDraft());
            draft.replaceRewardDrafts(List.of(completedRewardDraft()));

            /* then */
            assertThatThrownBy(draft::submit)
                .isInstanceOf(ProjectException.class)
                .hasMessage(ProjectErrorCode.INVALID_DRAFT_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("fundingDraft가 미완료이면 제출 시 예외가 발생한다.")
        void submit_givenIncompleteFundingDraft_thenThrow() {
            /* given */
            ProjectDraft draft = createDraft();
            draft.assignStoryDraft(completedStoryDraft());
            draft.assignSettlementDraft(completedSettlementDraft());
            draft.replaceRewardDrafts(List.of(completedRewardDraft()));

            ProjectFundingDraft funding = ProjectFundingDraft.create();
            draft.assignFundingDraft(funding);

            /* then */
            assertThatThrownBy(draft::submit)
                .isInstanceOf(ProjectException.class)
                .hasMessage(ProjectErrorCode.INVALID_DRAFT_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("settlementDraft가 null이면 제출 시 예외가 발생한다.")
        void submit_givenNullSettlementDraft_thenThrow() {
            /* given */
            ProjectDraft draft = createDraft();
            draft.assignStoryDraft(completedStoryDraft());
            draft.assignFundingDraft(completedFundingDraft());
            draft.replaceRewardDrafts(List.of(completedRewardDraft()));

            /* then */
            assertThatThrownBy(draft::submit)
                .isInstanceOf(ProjectException.class)
                .hasMessage(ProjectErrorCode.INVALID_DRAFT_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("settlementDraft가 미완료이면 제출 시 예외가 발생한다.")
        void submit_givenIncompleteSettlementDraft_thenThrow() {
            /* given */
            ProjectDraft draft = createDraft();
            draft.assignStoryDraft(completedStoryDraft());
            draft.assignFundingDraft(completedFundingDraft());
            draft.replaceRewardDrafts(List.of(completedRewardDraft()));

            ProjectSettlementDraft settlement = ProjectSettlementDraft.create();
            draft.assignSettlementDraft(settlement);

            /* then */
            assertThatThrownBy(draft::submit)
                .isInstanceOf(ProjectException.class)
                .hasMessage(ProjectErrorCode.INVALID_DRAFT_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("rewardDrafts가 비어있으면 제출 시 예외가 발생한다.")
        void submit_givenEmptyRewardDrafts_thenThrow() {
            /* given */
            ProjectDraft draft = createDraft();
            draft.assignStoryDraft(completedStoryDraft());
            draft.assignFundingDraft(completedFundingDraft());
            draft.assignSettlementDraft(completedSettlementDraft());
            draft.replaceRewardDrafts(List.of());

            /* then */
            assertThatThrownBy(draft::submit)
                .isInstanceOf(ProjectException.class)
                .hasMessage(ProjectErrorCode.INVALID_DRAFT_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("rewardDrafts 중 하나라도 미완료이면 제출 시 예외가 발생한다.")
        void submit_givenOneIncompleteRewardDraft_thenThrow() {
            /* given */
            ProjectDraft draft = createDraft();
            draft.assignStoryDraft(completedStoryDraft());
            draft.assignFundingDraft(completedFundingDraft());
            draft.assignSettlementDraft(completedSettlementDraft());

            ProjectRewardDraft incompleteReward = ProjectRewardDraft.create();
            ProjectRewardDraft completeReward = completedRewardDraft();

            draft.replaceRewardDrafts(List.of(completeReward, incompleteReward));

            /* then */
            assertThatThrownBy(draft::submit)
                .isInstanceOf(ProjectException.class)
                .hasMessage(ProjectErrorCode.INVALID_DRAFT_SUBMISSION.getMessage());
        }

        @Test
        @DisplayName("모든 드래프트가 완료되면 제출에 성공하고 상태가 SUBMITTED로 변경된다.")
        void submit_givenAllCompleted_thenSuccess() {
            /* given */
            ProjectDraft draft = createDraftAllCompleted();

            /* when */
            draft.submit();

            /* then */
            assertThat(draft.getStatus()).isEqualTo(ProjectDraftStatus.SUBMITTED);
        }
    }

    @Nested
    @DisplayName("리워드 드래프트 교체")
    class ReplaceRewardDrafts {
        @Test
        @DisplayName("교체 시 rewardDraft.projectDraft가 설정된다.")
        void replaceRewardDrafts_givenValidList_thenAssignProjectDraft() {
            /* given */
            ProjectDraft draft = createDraft();
            ProjectRewardDraft reward = completedRewardDraft();

            /* when */
            draft.replaceRewardDrafts(List.of(reward));

            /* then */
            assertThat(reward.getProjectDraft()).isEqualTo(draft);
        }
    }
}
