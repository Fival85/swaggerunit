# SwaggerUnit
SwaggerUnit is a framework that can be used for the validation of service requests and responses against a swagger definition.

![Java CI with Maven](https://github.com/Fival85/swaggerunit/workflows/Java%20CI%20with%20Maven/badge.svg?branch=master&event=public)

Currently under construction and will be changed to English as the main language.
Further information will then be provided in this Readme.

# ideas
- [FEATURE] A simple Integration-Suite (adapter.classic) for plain java applications unit-tests (after refactoring above!) 
- [FEATURE] More configuration of the validation level. So i hope a not existing parameter could be an error
- [FEATURE] Support for junit-jupiter
- [FEATURE] At the moment swagger could only handle one swagger, but many request. It should be able to validate to different swaggers
- [REFACTOR] Splitting swaggerunit into seperate projects, so the core is one project and each adapter for itself  too. The core should have any spring dependency. I would have the ability to port it to any technology without having the spring dependency (which is outdated in future) 

## history ideas
- [DONE] instead of ignoring all not existing urls, there should be an blacklist of urls to ignore, so the developer must be decide to ignore an url.