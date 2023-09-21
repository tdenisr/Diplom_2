import com.github.javafaker.Faker;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import models.User;
import order.OrderClient;
import order.OrderGenerator;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import user.UserClient;
import user.UserGenerator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;

public class GetUserOrdersTest {
    Faker faker = new Faker();
    private final OrderClient orderClient = new OrderClient();
    private final OrderGenerator orderGenerator = new OrderGenerator();
    UserClient userClient = new UserClient();
    User user;
    String accessToken;

    @Before
    public void setup() {
        //создаем пользователя с произвольным количеством заказов.
        user = UserGenerator.getRandomUser();
        userClient.create(user);
        accessToken = userClient.getUserToken(user);
        for (int i = 0; i < faker.number().numberBetween(1, 100); i++) {
            orderClient.createOrder(orderGenerator.getRandomOrder(), accessToken);
        }
    }

    @Test
    @DisplayName("Получение заказов пользователя с авторизацей")
    public void getOrdersByUser() {
        Response response = orderClient.getOrders(accessToken);
        assertEquals("Неверный статус ответа при получении списка заказов", HttpStatus.SC_OK, response.statusCode());
        response.then().assertThat().body("success", equalTo(true))
                .body("orders", notNullValue())
                .body("orders.ingredients", notNullValue())
                .body("total", notNullValue())
                .body("totalToday", notNullValue());
    }

    @Test
    @DisplayName("Получение заказов пользователя без авторизации")
    public void getOrdersByUserNoAuthorizing() {
        Response response = orderClient.getOrdersNoAuthorizing();
        assertEquals("Неверный статус ответа при получении списка заказов", HttpStatus.SC_UNAUTHORIZED, response.statusCode());
        response.then().assertThat().body("success", equalTo(false))
                .body("message", equalTo("You should be authorised"));
    }

    @After
    public void tearDown() {
        Response response = userClient.deleteUser(user);
        assertEquals("Ошибка при удалении пользователя", HttpStatus.SC_ACCEPTED, response.getStatusCode());
    }
}
