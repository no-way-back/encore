package com.nowayback.user.fixture;

import com.nowayback.user.application.dto.command.LoginUserCommand;
import com.nowayback.user.application.dto.command.SignupUserCommand;
import com.nowayback.user.application.dto.command.UpdateUserInfoCommand;
import com.nowayback.user.application.dto.query.SearchUserQuery;
import com.nowayback.user.application.dto.result.LoginResult;
import com.nowayback.user.application.dto.result.UserResult;
import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;
import com.nowayback.user.presentation.dto.request.LoginUserRequest;
import com.nowayback.user.presentation.dto.request.SignupUserRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;

public class UserFixture {

    public static final UUID USER_UUID = UUID.randomUUID();

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password123!";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String EMAIL = "email@example.com";
    public static final String MODIFIED_EMAIL = "modified@example.com";
    public static final String NICKNAME = "nickname";
    public static final String MODIFIED_NICKNAME = "modifiedNickname";

    public static final String ACCESS_TOKEN = "access token";

    private static final Pageable PAGEABLE = PageRequest.of(0, 10);

    public static User createUser() {
        return User.create(
                USERNAME,
                ENCODED_PASSWORD,
                EMAIL,
                NICKNAME,
                UserRole.USER
        );
    }

    /* user entity */

    public static User createUser(UserRole role) {
        return User.create(
                USERNAME,
                ENCODED_PASSWORD,
                EMAIL,
                NICKNAME,
                role
        );
    }

    public static User createUserWithStatus(UserStatus initialStatus) {
        User user = createUser();
        setPrivateField(user, "status", initialStatus);
        return user;
    }

    public static User createUserWithRole(UserRole role) {
        User user = createUser();
        setPrivateField(user, "role", role);
        return user;
    }

    public static User createUserWithStatusAndRole(UserStatus status, UserRole role) {
        User user = createUser();
        setPrivateField(user, "status", status);
        setPrivateField(user, "role", role);
        return user;
    }

    public static User createUser(UUID id, UserStatus status, UserRole role) {
        User user = createUser();
        setPrivateField(user, "id", id);
        setPrivateField(user, "status", status);
        setPrivateField(user, "role", role);
        return user;
    }

    private static final List<User> USER_LIST = List.of(
            createUser(),
            createUser(),
            createUser()
    );
    public static final Page<User> USER_PAGE = new PageImpl<>(USER_LIST, PAGEABLE, USER_LIST.size());

    /* user command */

    public static final SignupUserCommand SIGNUP_USER_COMMAND = SignupUserCommand.of(
            USERNAME,
            PASSWORD,
            EMAIL,
            NICKNAME,
            UserRole.USER
    );

    public static final LoginUserCommand LOGIN_USER_COMMAND = LoginUserCommand.of(
            USERNAME,
            PASSWORD
    );

    public static final UpdateUserInfoCommand UPDATE_USER_INFO_COMMAND = UpdateUserInfoCommand.of(
            USER_UUID,
            MODIFIED_NICKNAME
    );

    /* user query */

    private static final String KEYWORD = "keyword";
    private static final List<String> SEARCH_FIELDS = List.of("username", "nickname");

    public static final SearchUserQuery SEARCH_USER_QUERY = SearchUserQuery.of(
            KEYWORD,
            SEARCH_FIELDS,
            null,
            null,
            PAGEABLE
    );

    /* user result */

    public static final UserResult USER_RESULT = UserResult.from(createUser());
    public static final LoginResult LOGIN_RESULT = LoginResult.from(
            ACCESS_TOKEN,
            createUser()
    );

   public static final Page<UserResult> USER_RESULT_PAGE = USER_PAGE.map(UserResult::from);

    /* user request */

    public static final SignupUserRequest VALID_SIGNUP_USER_REQUEST = new SignupUserRequest(
            USERNAME,
            PASSWORD,
            EMAIL,
            NICKNAME,
            UserRole.USER
    );

    public static final SignupUserRequest INVALID_SIGNUP_USER_REQUEST = new SignupUserRequest(
            "",
            "short",
            "invalid-email",
            "",
            UserRole.USER
    );

    public static final LoginUserRequest VALID_LOGIN_USER_REQUEST = new LoginUserRequest(
            USERNAME,
            PASSWORD
    );

    public static final LoginUserRequest INVALID_LOGIN_USER_REQUEST = new LoginUserRequest(
            "",
            ""
    );

    /* private methods */

    private static void setPrivateField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
