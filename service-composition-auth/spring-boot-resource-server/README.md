# Spring Boot Security - Resource Server

## Topics

### How-To Run
- Start local Keycloak
- Create realm "training" by importing `training_realm.json`
- Adapt Keycloak URI in `src/main/resources/application.properties` if necessary
- (If you're interested, comment in the Spring Security logging config property) 
- Run Java app with `./mvnw spring-boot:run`
- Send a request to public endpoint without auth under `http://host:port/public-endpoint`
- Send a request to protected endpoint under `http://host:port/protected-endpoint`
- Get a valid token as described below for successful authentication
- Retry `/protected-endpoint`
- Send a request to `/admin-endpoint`
- Assign role `admin` to test user
- Get new token
- Retry `/admin-endpoint`
- Retry `/protected-endpoint` and see changed list of roles the user has

### How-To Authenticate
- Get token from Keycloak\
    ```sh
    curl --request POST \
    --url http://localhost:8081/realms/training/protocol/openid-connect/token \
    --header 'Content-Type: application/x-www-form-urlencoded' \
    --data grant_type=password \
    --data client_id=spring-example-app \
    --data username=test \
    --data password=test
    ```
- Set token as bearer in app request\
    ```sh
    curl --request GET \
    --url http://localhost:8080/protected-endpoint \
    --header 'Authorization: Bearer <access_token>'
    ```

## Exercises
