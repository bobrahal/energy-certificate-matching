# Energy Certificate Matching
Simple functionality using RESTful interface for matching buyer's interest in buying energy certificates.
Implemented using Kotling, Spring-boot, Spring-JPA, H2 and Maven.

A _Seller_ may add _CertificateBundle_ if:
 - _Seller_ already registered in the database (checks are done using _Seller.code_)
 - _EnergySource_ registered in the database
 - Quantity is not zero

A _Buyer_ may add _Interest_ if:
- _Buyer_ already registered in the database (checks are done using _Buyer.code_)
- Quantity is not zero

When an _Interest_ is successfully registered in the database by the _Buyer_, matching will be executed
 - Get the list of _CertificateBundles_ matching the required _EnergySource_ from the _Buyer_
 - Order the list by descending quantity
 - Match the received _Interest_ with one or more _CertificateBundles_
 - Close the _Interest_ in the database
 - Update the quantity of the _CertificateBundle_ based on the remaining quantity and _MatchType_ (FULL, PARTIAL)
 - Log the entry in the _OrderBook_

API exposes two POST endpoints with JSON parameters:
```
http://localhost:8080/add-bundle

{
    "seller": {
        "code": "00003"
    },
    "energySource": {
        "code": "WTR"
    },
    "quantity": 0,
    "issuer": "Some issuer",
    "issueDate": "2021-05-14"
}
```

```
http://localhost:8080/add-interest

{
    "buyer": {
        "code": "00003"
    },
    "certificateCode": "WTR",
    "quantity": 500
}
```

## Database Structure

Below is a screenshot that illustrates the relationship between the different entities:
 - _buyer_ and _Seller_ were kept as separate entities to allow for independent scalability
 - _certificate_bundles_ and _seller_ are linked as _many to one_ since one _seller_ could have multiple _certificate_bundles_ and one _certificate_bundles_ could only have been created by one _seller_ 
 - _interests_ and _buyer_ are linked similarly as well
 - _certificate_bundles_ and _energy_source_ are linked as _many to one_ (similar to _certificate_bundles_ and _seller_)
 - _interests_ and _energy_source_ are not linked directly since interests can be having energy sources which are not supported and thus remain unmatched 
 - _order_book_ will contain the matching entries between _interests_ and _certificate_bundles_

![DBD_Certificate_Matching](https://user-images.githubusercontent.com/78940516/118381131-71be2f80-b5f0-11eb-8ee4-01bede842600.png)

Database configuration can be found in _application.yml_ file
```
## H2 Configuration
spring:
  h2:
    console:
      enabled: true
      path: /order-console
      settings:
        trace: false
        web-allow-others: false
  datasource:
    url: jdbc:h2:mem:order-db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
    username: order
    password: order
  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        use_sql_comments: true
        format_sql: true
```

## Dependencies
[See Here](https://github.com/bobrahal/energy-certificate-matching/blob/master/pom.xml)

## Start-up
Run the following command:<br/>
```
mvn spring-boot:run
```
For the sake of simplicity, the application runs on a in-memory H2 database which is populated on start-up with 3 _EnergySources_, 5 _Sellers_ and 3 _Buyers_

## Tests
Run Tests:
```
mvn test
```
## Output
### Postman outputs
#### Adding _CertificateBundle_ using /add-bundle url. Nominal Case (200 OK)
```
{
    "message": "Sell Order (Certificate Bundle) for 500 Water certificates successfully created for seller 00003",
    "code": "Success"
}
```
#### Adding _CertificateBundle_. Seller not found (400 BAD REQUEST)
```
{
    "message": "Unable to create sell order. Seller 00007 not found",
    "code": "ERR-1"
}
```
#### Adding _CertificateBundle_. _EnergySource_ not found (400 BAD REQUEST)
```
{
    "message": "Unable to create order. Energy source HDRGN not found",
    "code": "ERR-3"
}
```
#### Adding _CertificateBundle_ or _Interest_. Invalid quantity (400 BAD REQUEST)
```
{
    "message": "Unable to create order. Quantity should be greater than zero",
    "code": "ERR-4"
}
```
#### Adding _Interest_. Nominal Case (200 OK)
```
{
    "message": "Buy Order (Interest) for 1015 WTR certificates successfully created for buyer 00003",
    "code": "Success"
}
```
#### Adding _Interest_. Unknown _EnergySource_ (400 BAD REQUEST)
```
{
    "message": "Order not matched: certificate energy source HDRGN not found",
    "code": "ERR-4"
}
```
#### Adding _Interest_. No matching found (400 BAD REQUEST)
```
{
    "message": "Order not matched: no combination of certificate bundles found that match interest",
    "code": "ERR-4"
}
```
#### Invalid URL used (400 BAD REQUEST)
```
{
    "timestamp": "2021-05-16T00:43:24.535+00:00",
    "status": 404,
    "error": "Not Found",
    "message": "No message available",
    "path": "/add-1"
}
```

## Directory structure
```
|-src
	+---main
	|   +---kotlin
	|   |   \---com
	|   |       \---cerqlar
	|   |           \---certificate
	|   |               \---matching
	|   |                   +---domain	# contains domain classes and repositories interfaces
	|   |                   +---exceptions	# contains business exceptions
	|   |                   |
	|   |                   +---infrastructure # contains technical implementations (db repo, spring boot)
	|   |                   |   \---http
	|   |                   |     |--routing # contains HTTP endpoint mapping and error handling mechanisms
	|   |                   \---service # contains orchestration mecanisms
	\---test # contains unit and integration tests
```

## Design decisions
 - Persistence mapping is done using [boilerplate entities](https://github.com/bobrahal/energy-certificate-matching/blob/master/src/main/kotlin/com/cerqlar/certificate/matching/infrastructure/db/order/bundle/CertificateBundleEntity.kt)
   and [conversions](https://github.com/bobrahal/energy-certificate-matching/blob/master/src/main/kotlin/com/cerqlar/certificate/matching/infrastructure/db/order/bundle/CertificateBundleH2Repository.kt) in order to keep [domain](https://github.com/bobrahal/energy-certificate-matching/blob/master/src/main/kotlin/com/cerqlar/certificate/matching/infrastructure/db/order/bundle/CertificateBundleH2Repository.kt) free of external libraries/framework dependencies
 - Non-nominal execution flows are handled using `Exception` throwing and Exception handlers from [ControllerAdvice](https://github.com/bobrahal/energy-certificate-matching/blob/master/src/main/kotlin/com/cerqlar/certificate/matching/infrastructure/http/routing/ControllerAdvice.kt)
