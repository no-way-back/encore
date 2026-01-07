package com.nowayback.project.domain.project.repository;

import com.nowayback.project.domain.project.entity.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository {

    Optional<Category> findByCode(String code);

    List<Category> findByDepthAndActiveOrderBySortOrderAsc(int depth, boolean active);

    List<Category> findByParentIdAndActiveOrderBySortOrderAsc(UUID parentId, boolean active);
}
