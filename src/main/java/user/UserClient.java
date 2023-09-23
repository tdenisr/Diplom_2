package user;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.User;
import models.UserCreds;

import static io.restassured.RestAssured.given;

public class UserClient {
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static String CREATE_URL = "api/auth/register";
    private static String LOGIN_URL = "api/auth/login";
    private static String USER_URL = "api/auth/user";

    public UserClient() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Создание нового пользователя")
    public Response create(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(CREATE_URL);
    }

    @Step("Вход под пользователем")
    public Response login(UserCreds creds) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(creds)
                .when()
                .post(LOGIN_URL);

    }

    @Step("Удаление пользователя")
    public Response deleteUser(User user) {
        String accessToken = getUserToken(user);
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .delete(USER_URL);
    }

    @Step("Изменение name пользователя")
    public Response changeUserName(User user, String newName) {
        String accessToken = getUserToken(user);
        user.setName(newName);
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .body(user)
                .when()
                .patch(USER_URL);
    }

    @Step("Изменение name пользователя без авторизации")
    public Response changeUserNameNoAuthorization(User user, String newName) {
        user.setName(newName);
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(USER_URL);
    }

    @Step("Изменение email пользователя")
    public Response changeUserEmail(User user, String newEmail) {
        String accessToken = getUserToken(user);
        user.setEmail(newEmail);
        return given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken)
                .body(user)
                .when()
                .patch(USER_URL);
    }

    @Step("Изменение email пользователя без авторизации")
    public Response changeUserEmailNoAuthorization(User user, String newEmail) {
        user.setEmail(newEmail);
        return given()
                .header("Content-type", "application/json")
                .body(user)
                .when()
                .patch(USER_URL);
    }

    @Step("Получение токена пользователя")
    public String getUserToken(User user) {
        Response response = login(UserCreds.credsFrom(user));
        return response.jsonPath().getString("accessToken").split(" ")[1];
    }

}
