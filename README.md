
In the PhotoAppApiUsers project, ensure that the 'gateway.ip' configuration is set to the current gateway IP address. Failure to do so will result in a 403 Forbidden error when attempting to access the application.


# Running Test environment with maven command
```bash 
mvn spring-boot:run -Dspring-boot.arguments=--spring.profiles.active=test
```




