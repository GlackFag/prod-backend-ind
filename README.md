# Travelgentle
## Бот-помощник для плаирования путешествий
https://t.me/travelgentle_bot

### Установка
1) Скачайте релиз в репозиории
2) Установите его выполнив команду docker load -i your-directory/image.tar
3) Скачайте docker-compose.yml файл, он расположен в корне проекта
4) В директории, где находится docker-compose.yml, выполните команду docker-compose up

### Внешние интеграции
Проект использует PostgreSQL. В планах есть прикрутить Redis

### DB schema
![drawSQL-image-export-2024-03-22](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/624daca7-2c4e-4d73-a7ba-f6bbcf9c766c)

### Демонстрация работы
1) При первом обращении к боту через /start, он предлагает зарегистрироваться, это необходимо сделать, чтобы продолжить.

![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/3b6cf7ed-31cf-4f44-8a08-9e92cd9cadfd)

  Имя должно состоять только из латинских букв и цифр, иначе бот будет требовать ввести валидное имя

![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/60026298-6ec6-4451-ab79-8eaa94bbc60e)

  Возраст же должен быть в промежутке [1;120]
  Далее бот запросит вашу локацию, она необходима для завершения регистрации.


2) Посмотреть или редактировать введенные данные можно, введя команду /me

![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/84870674-9492-4bca-95fa-758a5f482866)

  Так же есть возможность поставить описание(bio), максимальная длина 200 символов.


3) Создание нового путешествия через команду /newtravel

![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/7954ef70-837e-4ae1-a034-ae3cf39f9f84)



  При желании можно добавить ещё какие-то города(поинты), а можно оставить только один

4) Просматривать свои путешествия можно в /mytravels

![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/6e3b5144-0489-4345-9991-bac7f3cd583c)
  
![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/bb35a642-32ad-43d2-8fcc-aebd8ff3d6a0)

Тут же её можно удалить

![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/5733f4a5-918c-4d22-a213-bc52a14ca030)

5) Чтобы пригласить друга в поездку, набираем /invite
Выбираем поездку

![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/d703e4c9-c5ec-4d0b-940a-35c7275e2cc0)

И получаем пригласительный код

![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/c0f64074-fe3d-492e-b70d-32b40ff5fd49)

6) Чтобы присоединиться набираем /jointravel и вводим код
![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/7cadd217-ae78-4060-a9ac-89bda099fac0)

7) Так же есть функция поиска достопримечательностей в текущем городе /suggestsight

![image](https://github.com/Central-University-IT-prod/backend-GlackFag/assets/99576022/29f5d945-2e2f-4600-a2cb-0ee91ad4a18e)





