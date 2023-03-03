**Prerequisites**
- Java 11 (If building project locally or else use the docker image, see details below) 

**To build project:**
````
mvn clean install # compile and run tests
mvn package # produces jar with dependencies
````

Build docker image from project root directory
````
docker build -t verifit-app .
````
Spin up the resulting container
````
docker run -p 8000:8000 -itd <IMAGE_ID>
````

Bootstrap:
Initial Hibernate loading with sample data set. Refer to resources/data.sql

**Extras:** Postman collection for available APIs and usage can be found 
in the root project director - verifit.postman_collection.json

Examples:
````
POST http://localhost:8000/attendance
{
    "name": "Lionel Pinto",
    "date": "2023-02-16"
}

GET http://localhost:8000/discountEligibility?username=lionel pinto
GET http://localhost:8000/currentStreak?username=lionel pinto
GET http://localhost:8000/attendance
````
