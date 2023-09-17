package order;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import models.Order;

import java.util.List;

import static io.restassured.RestAssured.given;

public class OrderClient {
    public static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    public static final String INGREDIENTS_URL = "/api/ingredients";
    public static final String ORDERS_URL = "/api/orders";

    private Order order;

    public OrderClient() {
        RestAssured.baseURI = BASE_URI;
    }

    @Step("Получение списка всех ингредиенов")
    public List<String> getIngredients() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .get(INGREDIENTS_URL).jsonPath().getList("data._id");
    }

    @Step("Создание заказа")
    public Response createOrder(Order order, String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .and().auth().oauth2(accessToken)
                .body(order)
                .when()
                .post(ORDERS_URL);
    }

    @Step("Создание заказа без авторизации")
    public Response createOrderNoAuthorizing(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post(ORDERS_URL);
    }

    @Step("Получение заказов без авторизации")
    public Response getOrdersNoAuthorizing() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .when()
                .get(ORDERS_URL);
    }

    @Step("Получение заказов с авторизацией")
    public Response getOrders(String accessToken) {
        return given()
                .header("Content-type", "application/json")
                .and().auth().oauth2(accessToken)
                .when()
                .get(ORDERS_URL);
    }
}
