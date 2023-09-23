import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import models.UserCreds;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;
import user.UserGenerator;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;

public class UpdateUserTest {
    private final UserClient userClient = new UserClient();
    User user;
    private boolean userCreationFlag = true;

    @Before
    public void setup() {
        user = UserGenerator.getRandomUser();
        userClient.create(user);
    }

    @Test
    @DisplayName("Изменение name пользователя с авторизацей")
    public void patchUserNameWithAuthorizationTest() {
        userClient.login(UserCreds.credsFrom(user));
        String newUserName = UserGenerator.getRandomName();
        Response patchResponse = userClient.changeUserName(user, newUserName);
        assertEquals("Обновление имени пользователя не прошло", HttpStatus.SC_OK, patchResponse.statusCode());
        patchResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(newUserName));
    }

    @Test
    @DisplayName("Изменение email пользователя с авторизацей")
    public void patchEmailWithAuthorizationTest() {
        userClient.login(UserCreds.credsFrom(user));
        String newEmail = UserGenerator.getRandomEmail();
        Response patchResponse = userClient.changeUserEmail(user, newEmail);
        assertEquals("Обновление email пользователя не прошло", HttpStatus.SC_OK, patchResponse.statusCode());
        patchResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("user.email", equalTo(newEmail))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Изменение email пользователя на уже существующий email")
    public void patchEmailWithDuplicateEmailTest() {
        userClient.login(UserCreds.credsFrom(user));
        User duplicateEmailUser = UserGenerator.getRandomUser();
        String rightEmail = duplicateEmailUser.getEmail();
        userClient.create(duplicateEmailUser);
        Response patchResponse = userClient.changeUserEmail(duplicateEmailUser, user.getEmail());
        assertEquals("Прошло обновление email на существующей email", HttpStatus.SC_FORBIDDEN, patchResponse.statusCode());
        patchResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("User with such email already exists"));
        duplicateEmailUser.setEmail(rightEmail); //восстанавливаем пользователя для успешного удаления
    }

    @Test
    @DisplayName("Изменение name пользователя без авторизации")
    public void patchUserNameNoAuthorizationTest() {
        userClient.login(UserCreds.credsFrom(user));
        String newUserName = UserGenerator.getRandomName();
        Response patchResponse = userClient.changeUserNameNoAuthorization(user, newUserName);
        assertEquals("Невреный код ошибки", HttpStatus.SC_UNAUTHORIZED, patchResponse.statusCode());
        patchResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @Test
    @DisplayName("Изменение email пользователя без авторизации")
    public void patchEmailNoAuthorizationTest() {
        userClient.login(UserCreds.credsFrom(user));
        String email = user.getEmail();
        String newEmail = UserGenerator.getRandomEmail();
        Response patchResponse = userClient.changeUserEmailNoAuthorization(user, newEmail);
        assertEquals("Невреный код ошибки", HttpStatus.SC_UNAUTHORIZED, patchResponse.statusCode());
        patchResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
        user.setEmail(email); //восстанавливаем пользователя для успешного удаления
    }

    @After
    public void tearDown() {
        if (userCreationFlag) {
            Response response = userClient.deleteUser(user);
            assertEquals("Ошибка при удалении пользователя", HttpStatus.SC_ACCEPTED, response.getStatusCode());
        }
    }
}
