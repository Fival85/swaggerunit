# SwaggerUnit
SwaggerUnit is a framework that can be used for the validation of service requests and responses against a swagger definition.

Currently under construction and will be changed to English as the main language.
Further information will then be provided in this Readme.

# ideas
- [REFACTORING] I wish a strict seperation of swaggerunit and spring. The spring-framework should only be used to adapt the rest call with the spring rest-template. I think the interface SwaggerUnitAdapter only needs a validate method with the reference to the SwaggerUnitCore. SwaggerUnitCore could be initialized by the SwaggerUnitRule. So the complete logic with the validation scope will be moved to the SwaggerUnitCore or the SwaggerUnitRule also. After this we have a very small link to other developers who only wants to create a adapter for they unit-test. 


