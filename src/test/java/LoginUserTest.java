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

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

public class LoginUserTest {
    private final UserClient userClient = new UserClient();
    User user;
    private boolean userCreationFlag = true;

    @Before
    public void setup() {
        user = UserGenerator.getRandomUser();
        userClient.create(user);
    }

    @Test
    @DisplayName("Авторизация с правильным логином и паролем")
    public void loginUserTest() {
        Response loginResponse = userClient.login(UserCreds.credsFrom(user));
        assertEquals("Не удалось авторизоваться", HttpStatus.SC_OK, loginResponse.statusCode());
        loginResponse.then().assertThat()
                .body("success", equalTo(true))
                .body("accessToken", containsString("Bearer"))
                .body("refreshToken", notNullValue())
                .body("user.email", equalTo(user.getEmail()))
                .body("user.name", equalTo(user.getName()));
    }

    @Test
    @DisplayName("Авторизация с неверным паролем")
    public void loginUserWrongPasswordTest() {
        User wrongPasswordUser = new User()
                .withEmail(user.getEmail())
                .withName(user.getName())
                .withPassword(UserGenerator.getRandomPassword());
        Response loginResponse = userClient.login(UserCreds.credsFrom(wrongPasswordUser));
        assertEquals("Ожидалось, что авторизация не пройдет", HttpStatus.SC_UNAUTHORIZED, loginResponse.statusCode());
        loginResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Авторизация с неверным email")
    public void loginUserWrongEmailTest() {
        User wrongMailUser = new User()
                .withEmail(UserGenerator.getRandomEmail())
                .withName(user.getName())
                .withPassword(user.getPassword());
        Response loginResponse = userClient.login(UserCreds.credsFrom(wrongMailUser));
        assertEquals("Ожидалось, что авторизация не пройдет", HttpStatus.SC_UNAUTHORIZED, loginResponse.statusCode());
        loginResponse.then().assertThat()
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        if (userCreationFlag) {
            Response response = userClient.deleteUser(user);
            assertEquals("Ошибка при удалении пользователя", HttpStatus.SC_ACCEPTED, response.getStatusCode());
        }
    }
}
