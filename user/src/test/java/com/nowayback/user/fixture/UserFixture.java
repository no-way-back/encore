package com.nowayback.user.fixture;

import com.nowayback.user.domain.entity.User;
import com.nowayback.user.domain.vo.UserRole;
import com.nowayback.user.domain.vo.UserStatus;

import java.lang.reflect.Field;
import java.util.UUID;

public class UserFixture {

    public static final UUID USER_UUID = UUID.randomUUID();

    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ENCODED_PASSWORD = "encodedPassword";
    public static final String EMAIL = "email@example.com";
    public static final String NICKNAME = "nickname";

    public static User createUser() {
        return User.create(
                USERNAME,
                ENCODED_PASSWORD,
                EMAIL,
                NICKNAME,
                UserRole.USER
        );
    }

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
