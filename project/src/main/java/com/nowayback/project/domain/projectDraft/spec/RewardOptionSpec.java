package com.nowayback.project.domain.projectDraft.spec;

public record RewardOptionSpec(
    String name,
    Boolean isRequired,
    Integer additionalPrice,
    Integer stockQuantity,
    Integer displayOrder
) {}
