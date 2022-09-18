# RecipesApp
 
- Spring boot application to manage Recipes and its CRUD operations
- A web application to demonstrate the functionalities of spring boot coupled with various technolgoies like JPA, MongoDB, Lombok, Junit, Docker etc.
- This app is developed using test driven development approach.
- The code coverage is achieved using Junit for unit testing and springBootTest for integration testing
- Mockitoes were used to test controller to make unit testing light weight
- Springdoc open api is used to generate the api documentation

# Installation steps
- Clone project using https://github.com/dilnawazbs/RecipesApp.git
- Navigate to springBoot class and run the application using IDE -or
- Navigate to the root of the project and run 'mvn spring-boot:run' after cloning
- Or create and run image in docker container by running ```docker compose up``` 
- http://localhost:8080/v3/api-docs/ to get the api specification in JSON format
- http://localhost:8080/swagger-ui/index.html to get the api specification in html format

# Features
- User can perform CRUD operations for recipies.
- User can perform filter to get desired recipies.

# REST API
The REST API to the example app is described below.

<img width="1106" alt="Screenshot 2022-09-18 at 15 08 00" src="https://user-images.githubusercontent.com/12380793/190904478-741030d7-fe4f-4e3d-bbd3-3cbc55f8c4f8.png">

## Create new recipe
### Request
```POST /recipes```

```Content-Type: application/json```

Request body
```
{
    "title": "Fried egg with tomato",
    "servings": 2,
    "ingredients": [
        "egg",
        "tomato" ],
    "instructions": "Crack the egg on the pan with little oil. And bake in oven for 2 minutes.",
    "category": "NON_VEGETARIAN"
}
```

eg: http://localhost:8080/recipes/

### Response
```
HTTP/1.1 201 Created
Date: Sun, 18 Sep 2022 12:53:32 GMT
Status: 201 Created
Content-Type: application/json

{
    "id": "62f151e322515f7318da0990",
    "title": "Fried egg with tomato",
    "servings": 2,
    "ingredients": [
        "egg",
        "tomato"
    ],
    "instructions": "Crack the egg on the pan with little oil. And bake in oven for 2 minutes.",
    "category": "NON_VEGETARIAN"
}
```

## Get list of recipes
### Request
```GET /recipes```

```Content-Type: application/json```

eg: http://localhost:8080/recipes/

### Response
```
HTTP/1.1 200 OK
Date: Sun, 18 Sep 2022 12:54:53 GMT
Status: 200 OK
Content-Type: application/json

[
    {
        "id": "62f151e322515f7318da0990",
        "title": "Fried egg with tomato",
        "servings": 2,
        "ingredients": [
            "egg",
            "tomato"
        ],
        "instructions": "Crack the egg on the pan with little oil. And bake in oven for 2 minutes.",
        "category": "NON_VEGETARIAN"
    }
]
```
## Get specific recipe
### Request
```GET /recipes/id```

```Content-Type: application/json```

eg: http://localhost:8080/recipes/62f151e322515f7318da0990

### Response
```
HTTP/1.1 200 OK
Date: Sun, 18 Sep 2022 12:55:35 GMT
Status: 200 OK
Content-Type: application/json

{
    "id": "62f151e322515f7318da0990",
    "title": "Fried egg with tomato",
    "servings": 2,
    "ingredients": [
        "egg",
        "tomato"
    ],
    "instructions": "Crack the egg on the pan with little oil. And bake in oven for 2 minutes.",
    "category": "NON_VEGETARIAN"
}
```

## Get a non-existent recipe
### Request
```GET /recipes/id```

```Content-Type: application/json```

eg: http://localhost:8080/recipes/2efe31a4bf0d67460064e9b

### Response
```
HTTP/1.1 404 Not Found
Date: Sun, 18 Sep 2022 12:57:58 GMT
Status: 404 Not Found
Content-Type: application/json

{
    "timestamp": "2022-09-18T12:57:58.611+00:00",
    "message": "Recipe not found for this id :: 2efe31a4bf0d67460064e9b",
    "details": "uri=/recipes/2efe31a4bf0d67460064e9b"
}
```
## Update a existing recipe
### Request
```PUT /recipes/id```

```Content-Type: application/json```

Request body:
```
{
    "title": "Fried egg with tomato and potato",
    "servings": 7,
    "ingredients": [
        "egg",
        "tomato",
        "potato" ],
    "instructions": "Crack the egg on the pan with little oil. And bake in oven for 2 minutes.",
    "category": "NON_VEGETARIAN"
}
```

eg: http://localhost:8080/recipes/62f151e322515f7318da0990

### Response
```
HTTP/1.1 200 OK
Date: Sun, 18 Sep 2022 13:01:50 GMT
Status: 200 OK
Content-Type: application/json

{
    "id": "62f151e322515f7318da0990",
    "title": "Fried egg with tomato and potato",
    "servings": 7,
    "ingredients": [
        "egg",
        "potato",
        "tomato"
    ],
    "instructions": "Crack the egg on the pan with little oil. And bake in oven for 2 minutes.",
    "category": "NON_VEGETARIAN"
}
```
## Update recipe using recipe Json Patch
### Request
```PATCH /recipes/id```

```Content-Type: application/json-patch+json```

Request body:
```
[
  { "op": "add", "path": "/servings", "value": 4 },
  { "op": "replace", "path": "/ingredients", "value": ["egg white"] }
]
```

eg: http://localhost:8080/recipe/62f151e322515f7318da0990

### Response
```
HTTP/1.1 204 OK
Date: Sun, 18 Sep 2022 13:17:28 GMT
Status: 204 OK
```

## Update recipe using recipe Json Patch
### Request
```PATCH /recipes/id```

```Content-Type: application/merge-patch+json```

Request body:
```
{
    "servings": 6,
    "category": "VEGETARIAN"
}
```

eg: http://localhost:8080/recipes/62f151e322515f7318da0990

### Response
```
HTTP/1.1 204 OK
Date: Sun, 18 Sep 2022 13:22:29 GMT
Status: 204 OK
```

## Get list of recipies using filter criteria
    The filteration works as below:
    - servings:{{total servinngs}}.  eg: ```servings=4```
    - category:{{Category Type}} eg: ```category=VEGETARIAN```  or ```category=NON_VEGETARIAN```
    - instructions:{{textToSearchInInstructions}} eg: ```instructions=oven```
    - ingredients: use excludes / includes eg: ```includes=potato```  or ```excludes:potato```

### Request
```GET /recipes?includes=potato&&servings=2&&category=VEGETARIAN```


```Content-Type: application/json```

eg: http://localhost:8080/recipes?includes=potato&&servings=2&&category=VEGETARIAN

### Response
```
HTTP/1.1 200 OK
Date: Sun, 18 Sep 2022 13:04:23 GMT
Status: 200 OK
Content-Type: application/json

[
    {
        "id": "62f1575722515f7318da0992",
        "title": "Mixed veg",
        "servings": 2,
        "ingredients": [
            "bell pepper",
            "potato",
            "mushroom"
        ],
        "instructions": "Chop all the ingredients and stir it in kadai with oil and spices",
        "category": "VEGETARIAN"
    }
]
```
## Delete recipe with id
### Request
```DELETE /recipes/id```

```Content-Type: application/json```

eg: http://localhost:8080/recipes/62f151e322515f7318da0990

### Response
```
HTTP/1.1 204 OK
Date: Sun, 18 Sep 2022 12:49:50 GMT
Status: 204 OK

```

## Delete all the recipies
### Request
```DELETE /recipes```

```Content-Type: application/json```

eg: http://localhost:8080/recipes

### Response
```
HTTP/1.1 204 OK
Date: Sun, 18 Sep 2022 12:52:24 GMT
Status: 204 OK

```
