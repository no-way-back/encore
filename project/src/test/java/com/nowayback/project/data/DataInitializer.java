package com.nowayback.project.data;

import com.nowayback.project.domain.project.entity.Category;
import com.nowayback.project.domain.project.entity.Project;
import com.nowayback.project.domain.project.repository.CategoryRepository;
import com.nowayback.project.domain.project.vo.Period;
import com.nowayback.project.domain.project.vo.ProjectDraftId;
import com.nowayback.project.domain.project.vo.UserId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
public class DataInitializer {

    @PersistenceContext
    EntityManager entityManager;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Autowired
    CategoryRepository categoryRepository;

    static final int BULK_INSERT_SIZE = 2000;
    static final int EXECUTE_SIZE = 6000;
    static final int THREADS = 10;

    @Test
    void initializer() throws InterruptedException {
        // 1) 하위 카테고리(depth=1) 전부 가져오기
        List<Category> subCategories =
            categoryRepository.findByDepthAndActiveOrderBySortOrderAsc(1, true);

        if (subCategories.isEmpty()) {
            throw new IllegalStateException("활성 하위 카테고리(depth=1)가 없습니다.");
        }

        CountDownLatch latch = new CountDownLatch(EXECUTE_SIZE);
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);

        for (int t = 0; t < EXECUTE_SIZE; t++) {
            final int taskIndex = t;

            executorService.submit(() -> {
                try {
                    // 2) taskIndex 기반으로 균등 분배
                    Category c = subCategories.get(taskIndex % subCategories.size());
                    UUID categoryId = c.getId();
                    UUID rootCategoryId = c.getParentId(); // depth=1이면 parent가 루트라고 가정

                    insert(categoryId, rootCategoryId);
                } finally {
                    latch.countDown();
                    System.out.println("latch countDown= " + latch.getCount());
                }
            });
        }

        latch.await();
        executorService.shutdown();
        executorService.awaitTermination(10, TimeUnit.MINUTES);
    }

    void insert(UUID categoryId, UUID rootCategoryId) {
        transactionTemplate.executeWithoutResult(status -> {
            for (int i = 0; i < BULK_INSERT_SIZE; i++) {
                Project project = createProject(categoryId, rootCategoryId);
                entityManager.persist(project);

                if ((i + 1) % 500 == 0) {
                    entityManager.flush();
                    entityManager.clear();
                }
            }
        });
    }

    private Project createProject(UUID categoryId, UUID rootCategoryId) {
        // ✅ 여기만 너희 VO/팩토리/생성자에 맞춰서 수정하면 끝
        return Project.create(
            UserId.create(UUID.randomUUID()),
            ProjectDraftId.create(UUID.randomUUID()),
            "title-" + UUID.randomUUID(),
            "summary",
            categoryId,
            rootCategoryId,
            null,
            "<p>content</p>",
            100_000L,
            Period.create(LocalDate.now(), LocalDate.now().plusDays(30)),
            null
        );
    }
}
