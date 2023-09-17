package order;

import com.github.javafaker.Faker;
import models.Order;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class OrderGenerator {
    private Faker fakerEn = new Faker(new Locale("en"));
    private Faker fakerRu = new Faker(new Locale("ru"));
    OrderClient orderClient = new OrderClient();
    List<String> allIngredients = orderClient.getIngredients();
    Random random = new Random();

    private List<String> getRandomIngredients(List<String> ingredients, int size) {
        List<String> resultList = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            int randomIndex = random.nextInt(ingredients.size());
            resultList.add(ingredients.get(randomIndex));
            ingredients.remove(randomIndex);
        }
        return resultList;
    }

    public Order getRandomOrder() {
        int randomSize = random.nextInt(allIngredients.size());
        return new Order().withIngredients(getRandomIngredients(allIngredients, randomSize));
    }
}
