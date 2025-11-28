package com.nowayback.project.domain.projectDraft.vo;

import com.nowayback.project.domain.projectDraft.entity.ProjectRewardOptionDraft;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Embeddable
public class RewardOptions {

    public RewardOptions() {
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "project_reward_draft_id")
    private List<ProjectRewardOptionDraft> optionDrafts = new ArrayList<>();

    public List<ProjectRewardOptionDraft> asReadOnly() {
        return Collections.unmodifiableList(optionDrafts);
    }

    public void clear() {
        optionDrafts.clear();
    }

    public void add(ProjectRewardOptionDraft rewardOptionDraft) {
        optionDrafts.add(rewardOptionDraft);
    }
}
