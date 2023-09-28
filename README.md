# Users API

## About project
Test assignment. RESTful API based on the web Spring Boot application.\
API for manipulating with users information.

It has the following functionality:
 - Create user. Allowed to register users who are more than 18 years old. The value [18] is taken from properties file.
 - update one/some user fields 
 - Update all user fields 
 - Delete user 
 - Search for users by birth date range.

## Checkstyle
This project is fully compliant with Google Checks specifications.
Read more [here](https://google.github.io/styleguide/javaguide.html).

To configure this code convention in your IDE,
use the Checkstyle plugin and the [config file](config/checkstyle.xml).

To run checkstyle, execute `mvn clean verify -P checkstyle`

## Swagger(OpenAPI 3)

After running application, the Swagger UI page is available at http://localhost:8080/swagger-ui.html