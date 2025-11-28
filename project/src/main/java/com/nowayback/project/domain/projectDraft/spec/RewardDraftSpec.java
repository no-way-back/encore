package com.nowayback.project.domain.projectDraft.spec;


import com.nowayback.project.domain.projectDraft.vo.RewardPrice;
import java.util.List;

public record RewardDraftSpec(
    String title,
    RewardPrice price,
    Integer limitCount,
    Integer purchaseLimitPerPerson,
    List<RewardOptionSpec> options
) {}