import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.Order;
import models.User;
import order.OrderClient;
import order.OrderGenerator;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;
import user.UserGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class OrderCreateTest {
    Faker faker = new Faker();
    private final OrderClient orderClient = new OrderClient();
    private final OrderGenerator orderGenerator = new OrderGenerator();
    Order order;
    UserClient userClient = new UserClient();
    User user;
    String accessToken;

    @Before
    public void setup() {
        user = UserGenerator.getRandomUser();
        userClient.create(user);
        accessToken = userClient.getUserToken(user);
        order = orderGenerator.getRandomOrder();
    }

    @Test
    @DisplayName("Создание заказа c авторизацией и с ингредиентами")
    public void createOrder() {
        Response response = orderClient.createOrder(order, accessToken);
        assertEquals("Неверный статус ответа при создании заказа", HttpStatus.SC_OK, response.statusCode());
        response.then().assertThat().body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации с ингредиентами")
    public void createOrderNoAuthorizing() {
        Response response = orderClient.createOrderNoAuthorizing(order);
        assertEquals("Неверный статус ответа при создании заказа", HttpStatus.SC_OK, response.statusCode());
        response.then().assertThat().body("success", equalTo(true))
                .body("order.number", notNullValue())
                .body("name", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа c авторизацией без ингредиентов")
    public void createOrderWithOutIngredients() {
        Order orderWithoutIngredients = orderGenerator.getRandomOrder();
        orderWithoutIngredients.setIngredients(new ArrayList<>());
        Response response = orderClient.createOrder(orderWithoutIngredients, accessToken);
        assertEquals("Неверный статус ответа при создании заказа", HttpStatus.SC_BAD_REQUEST, response.statusCode());
    }

    @Test
    @DisplayName("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithOutIngredientsNoAuthorizing() {
        Order orderWithoutIngredients = orderGenerator.getRandomOrder();
        orderWithoutIngredients.setIngredients(new ArrayList<>());
        Response response = orderClient.createOrderNoAuthorizing(orderWithoutIngredients);
        assertEquals("Неверный статус ответа при создании заказа", HttpStatus.SC_BAD_REQUEST, response.statusCode());
    }

    @Test
    @DisplayName("Создание заказа c неверным хешем ингредиентов.")
    public void createOrderWrongHash() {
        Order orderWithWrongHash = orderGenerator.getRandomOrder();
        List<String> wrongIngredientsList = new ArrayList<>();
        for (int i = 0; i < faker.number().numberBetween(1, 20); i++) {
            wrongIngredientsList.add(faker.numerify("##########################"));
        }
        orderWithWrongHash.setIngredients(wrongIngredientsList);
        Response responseAuthorizing = orderClient.createOrder(orderWithWrongHash, accessToken);
        assertEquals("Неверный статус ответа при создании заказа", HttpStatus.SC_INTERNAL_SERVER_ERROR, responseAuthorizing.statusCode());
        Response responseNoAuthorizing = orderClient.createOrderNoAuthorizing(orderWithWrongHash);
        assertEquals("Неверный статус ответа при создании заказа", HttpStatus.SC_INTERNAL_SERVER_ERROR, responseNoAuthorizing.statusCode());
    }

    @After
    public void tearDown() {
        Response response = userClient.deleteUser(user);
        assertEquals("Ошибка при удалении пользователя", HttpStatus.SC_ACCEPTED, response.getStatusCode());
    }

}
