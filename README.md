# Diplom_2
## Используемые инструменты
* Java 11 
* maven 3.8.1
* JUnit 4.13.2 
* rest-assured 5.3.1 
* allure 2.23.0 
* maven-surefire-plugin 2.22.2 
* javafaker 1.0.2 
* gson 2.10.1
##  Запуск тестов и построение отчёта
```
mvn clean test 
mvn allure:serve
```
## Тестируемая система
- API для [Stellar Burgers](https://stellarburgers.nomoreparties.site/)
- [Документация к API](https://code.s3.yandex.net/qa-automation-engineer/java/cheatsheets/paid-track/diplom/api-documentation.pdf)
## Цели
* Создать отдельный репозиторий для тестов API.
* Протестировать ручки (проверить тело и статус код ответа) 
* Обеспечить независимость тестов
* Сформировать отчёт Allure