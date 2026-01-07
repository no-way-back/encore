package com.nowayback.project.infrastructure.category.persistence;

import com.nowayback.project.domain.project.entity.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByCode(String code);

    List<Category> findByDepthAndActiveOrderBySortOrderAsc(int depth, boolean active);

    List<Category> findByParentIdAndActiveOrderBySortOrderAsc(UUID parentId, boolean active);
}
