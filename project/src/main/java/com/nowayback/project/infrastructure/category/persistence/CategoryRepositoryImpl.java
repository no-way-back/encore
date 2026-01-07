package com.nowayback.project.infrastructure.category.persistence;

import com.nowayback.project.domain.project.entity.Category;
import com.nowayback.project.domain.project.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    @Override
    public Optional<Category> findByCode(String code) {
        return categoryJpaRepository.findByCode(code);
    }

    @Override
    public List<Category> findByDepthAndActiveOrderBySortOrderAsc(int depth, boolean active) {
        return categoryJpaRepository.findByDepthAndActiveOrderBySortOrderAsc(depth, active);
    }

    @Override
    public List<Category> findByParentIdAndActiveOrderBySortOrderAsc(UUID parentId, boolean active) {
        return categoryJpaRepository.findByParentIdAndActiveOrderBySortOrderAsc(parentId, active);
    }
}
