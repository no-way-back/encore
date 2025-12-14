package com.nowayback.user.infrastructure.repository;

import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.repository.UserRepository;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import com.nowayback.user.infrastructure.config.QueryDslConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;

import static com.nowayback.user.fixture.UserFixture.*;
import static org.assertj.core.api.Assertions.*;

@DisplayName("유저 리포지토리")
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({
        UserRepositoryImpl.class,
        UserCustomRepositoryImpl.class,
        QueryDslConfig.class
})
@EnableJpaAuditing
@Testcontainers
class UserRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgre = new PostgreSQLContainer<>("postgres:17");

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("유저 저장")
    class Save {

        @Test
        @DisplayName("유저 저장에 성공한다.")
        void save_success() {
            /* given */
            User user = createUser();

            /* when */
            User savedUser = userRepository.save(user);

            /* then */
            assertThat(savedUser.getId()).isNotNull();
            assertThat(savedUser.getCreatedAt()).isNotNull();
        }
    }

    @Nested
    @DisplayName("유저명으로 유저 존재 여부 확인")
    class ExistsByUsername {

        @Test
        @DisplayName("유저명이 존재하면 true를 반환한다.")
        void existsByUsername_whenExists_returnTrue() {
            /* given */
            User user = createUser();

            entityManager.persist(user);
            entityManager.flush();

            /* when */
            boolean exists = userRepository.existsByUsername(user.getUsername());

            /* then */
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("유저명이 존재하지 않으면 false를 반환한다.")
        void existsByUsername_whenNotExists_returnFalse() {
            /* given */
            /* when */
            boolean exists = userRepository.existsByUsername("nonexistentuser");

            /* then */
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 유저명을 조회하면 false를 반환한다.")
        void existsByUsername_whenDeleted_returnFalse() {
            /* given */
            User user = createUser();

            entityManager.persist(user);
            entityManager.flush();

            user.deactivate(USER_UUID);
            entityManager.flush();

            /* when */
            boolean exists = userRepository.existsByUsername(user.getUsername());

            /* then */
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("이메일로 유저 존재 여부 확인")
    class ExistsByEmail {

        @Test
        @DisplayName("이메일이 존재하면 true를 반환한다.")
        void existsByEmail_whenExists_returnTrue() {
            /* given */
            User user = createUser();

            entityManager.persist(user);
            entityManager.flush();

            /* when */
            boolean exists = userRepository.existsByEmail(user.getEmail());

            /* then */
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("이메일이 존재하지 않으면 false를 반환한다.")
        void existsByEmail_whenNotExists_returnFalse() {
            /* given */
            /* when */
            boolean exists = userRepository.existsByEmail("noone@example.com");

            /* then */
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 이메일을 조회하면 false를 반환한다.")
        void existsByEmail_whenDeleted_returnFalse() {
            /* given */
            User user = createUser();

            entityManager.persist(user);
            entityManager.flush();

            user.deactivate(USER_UUID);
            entityManager.flush();

            /* when */
            boolean exists = userRepository.existsByEmail(user.getEmail());

            /* then */
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("닉네임으로 유저 존재 여부 확인")
    class ExistsByNickname {

        @Test
        @DisplayName("닉네임이 존재하면 true를 반환한다.")
        void existsByNickname_whenExists_returnTrue() {
            /* given */
            User user = createUser();

            entityManager.persist(user);
            entityManager.flush();

            /* when */
            boolean exists = userRepository.existsByNickname(user.getNickname());

            /* then */
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("닉네임이 존재하지 않으면 false를 반환한다.")
        void existsByNickname_whenNotExists_returnFalse() {
            /* given */
            /* when */
            boolean exists = userRepository.existsByNickname("no_nickname");

            /* then */
            assertThat(exists).isFalse();
        }

        @Test
        @DisplayName("삭제된 닉네임을 조회하면 false를 반환한다.")
        void existsByNickname_whenDeleted_returnFalse() {
            /* given */
            User user = createUser();

            entityManager.persist(user);
            entityManager.flush();

            user.deactivate(USER_UUID);
            entityManager.flush();

            /* when */
            boolean exists = userRepository.existsByNickname(user.getNickname());

            /* then */
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("유저명으로 유저 조회")
    class FindByUsername {

        @Test
        @DisplayName("유저가 존재하면 Optional<User>을 반환한다.")
        void findByUsername_whenExists_returnUser() {
            /* given */
            User user = createUser();

            entityManager.persist(user);
            entityManager.flush();

            /* when */
            Optional<User> found = userRepository.findByUsername(user.getUsername());

            /* then */
            assertThat(found).isPresent();
            assertThat(found.get().getUsername()).isEqualTo(user.getUsername());
        }

        @Test
        @DisplayName("유저가 존재하지 않으면 empty를 반환한다.")
        void findByUsername_whenNotExists_returnEmpty() {
            /* when */
            Optional<User> found = userRepository.findByUsername("unknown_user");

            /* then */
            assertThat(found).isEmpty();
        }

        @Test
        @DisplayName("삭제된 유저를 조회하면 empty를 반환한다.")
        void findByUsername_whenDeleted_returnEmpty() {
            /* given */
            User user = createUser();

            entityManager.persist(user);
            entityManager.flush();

            user.deactivate(USER_UUID);
            entityManager.flush();

            /* when */
            Optional<User> found = userRepository.findByUsername(user.getUsername());

            /* then */
            assertThat(found).isEmpty();
        }
    }


    @Nested
    @DisplayName("아이디로 유저 조회")
    class FindById {

        @Test
        @DisplayName("아이디가 존재하면 Optional<User>을 반환한다.")
        void findById_whenExists_returnUser() {
            /* given */
            User user = createUser();
            entityManager.persist(user);
            entityManager.flush();

            /* when */
            Optional<User> found = userRepository.findById(user.getId());

            /* then */
            assertThat(found).isPresent();
            assertThat(found.get().getId()).isEqualTo(user.getId());
        }

        @Test
        @DisplayName("아이디가 존재하지 않으면 empty를 반환한다.")
        void findById_whenNotExists_returnEmpty() {
            /* when */
            Optional<User> found = userRepository.findById(USER_UUID);

            /* then */
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("활성 상태인 아이디로 유저 조회")
    class FindByIdAndStatusActive {

        @Test
        @DisplayName("활성 상태이면 User를 반환한다.")
        void findByIdAndStatusActive_whenActive_returnUser() {
            /* given */
            User user = createUserWithStatus(UserStatus.ACTIVE);
            entityManager.persist(user);
            entityManager.flush();

            /* when */
            Optional<User> found = userRepository.findByIdAndStatusActive(user.getId());

            /* then */
            assertThat(found).isPresent();
            assertThat(found.get().getStatus()).isEqualTo(UserStatus.ACTIVE);
        }

        @Test
        @DisplayName("활성 상태가 아니면 empty를 반환한다.")
        void findByIdAndStatusActive_whenNotActive_returnEmpty() {
            /* given */
            User user = createUserWithStatus(UserStatus.SUSPENDED);
            entityManager.persist(user);
            entityManager.flush();

            /* when */
            Optional<User> found = userRepository.findByIdAndStatusActive(user.getId());

            /* then */
            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("유저 검색")
    class SearchUsers {

        @Test
        @DisplayName("조건에 맞는 유저들을 페이징하여 반환한다.")
        void searchUsers_withConditions_returnPagedUsers() {
            /* given */
            String keyword = "123";
            List<String> searchFields = List.of("username", "email");
            UserStatus status = UserStatus.ACTIVE;

            User user1 = User.create("user123", ENCODED_PASSWORD, EMAIL, NICKNAME + 1, UserRole.USER);
            User user2 = User.create("user", ENCODED_PASSWORD, "123@test.com", NICKNAME + 2, UserRole.USER);
            User user3 = User.create("nonmatch", ENCODED_PASSWORD, "nonmatch@test.com", NICKNAME + 3, UserRole.USER);
            User deletedUser = User.create("deleted123", ENCODED_PASSWORD, "deleted@test.com", NICKNAME + 4, UserRole.USER);

            entityManager.persist(user1);
            entityManager.persist(user2);
            entityManager.persist(user3);
            entityManager.persist(deletedUser);
            entityManager.flush();

            deletedUser.deactivate(USER_UUID);
            entityManager.flush();

            /* when */
            Page<User> result = userRepository.searchUser(
                    keyword,
                    searchFields,
                    null,
                    status,
                    PageRequest.of(0, 10)
            );

            /* then */
            assertThat(result.getTotalElements()).isEqualTo(2);
        }

        @Test
        @DisplayName("조건에 맞는 유저가 없으면 빈 페이지를 반환한다.")
        void searchUsers_noMatchingUsers_returnEmptyPage() {
            /* given */
            /* when */
            Page<User> result = userRepository.searchUser(null, null, null, null, PageRequest.of(0, 10));

            /* then */
            assertThat(result.getTotalElements()).isEqualTo(0);
        }
    }
}