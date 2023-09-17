import io.qameta.allure.Description;
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

public class CreateUserTest {
    private final UserClient userClient = new UserClient();
    User user;
    private boolean userCreationFlag = true;

    @Before
    public void setup() {
        user = UserGenerator.getRandomUser();
    }

    @Test
    @DisplayName("Создание нового пользователя и вход под ним")
    public void createUserTest() {
        Response response = userClient.create(user);
        assertEquals("Неверный статус код при создании пользователя", HttpStatus.SC_OK, response.statusCode());
        response.then().assertThat().body("success", equalTo(true));
        Response loginResponse = userClient.login(UserCreds.credsFrom(user));
        assertEquals("Не удалось авторизоваться", HttpStatus.SC_OK, loginResponse.statusCode());
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Отправка двух одинаковых запросов на создание пользователя")
    public void createDuplicateUserTest() {
        userClient.create(user);
        Response duplicateResponse = userClient.create(user);
        assertEquals("Неверный код ошибки создании дубликата пользователя", HttpStatus.SC_FORBIDDEN, duplicateResponse.statusCode());
        duplicateResponse.then().assertThat().body("success", equalTo(false)).body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Попытка создать пользователя без email")
    public void createUserWithoutEmail() {
        user.setEmail("");
        Response response = userClient.create(user);
        assertEquals("Неверный код ошибки создании пользователя без email", HttpStatus.SC_FORBIDDEN, response.statusCode());
        response.then().assertThat().body("success", equalTo(false)).body("message", equalTo("Email, password and name are required fields"));
        userCreationFlag = false;
    }

    @Test
    @DisplayName("Попытка создать пользователя без логина")
    public void createUserWithoutName() {
        user.setName("");
        Response response = userClient.create(user);
        assertEquals("Неверный код ошибки создании пользователя без логина", HttpStatus.SC_FORBIDDEN, response.statusCode());
        response.then().assertThat().body("success", equalTo(false)).body("message", equalTo("Email, password and name are required fields"));
        userCreationFlag = false;
    }

    @Test
    @DisplayName("Попытка создать пользователя без пароля")
    public void createUserWithoutPassword() {
        user.setPassword("");
        Response response = userClient.create(user);
        assertEquals("Неверный код ошибки создании пользователя без пароля", HttpStatus.SC_FORBIDDEN, response.statusCode());
        response.then().assertThat().body("success", equalTo(false)).body("message", equalTo("Email, password and name are required fields"));
        userCreationFlag = false;
    }

    @After
    public void tearDown() {
        if (userCreationFlag) {
            Response response = userClient.deleteUser(user);
            assertEquals("Ошибка при удалении пользователя", HttpStatus.SC_ACCEPTED, response.getStatusCode());
        }
    }


}
