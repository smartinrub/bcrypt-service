# bcrypt-service

## Getting started

Run Spring Boot application from your favourite IDE

## Usage Example

1. Create new user

```shell
curl -H "Content-Type: application/json" -X POST -d '{"email":"test@test.com","password":"test"}' http://localhost:8080
```

2. Check user creation by curl or accessing H2 console

```shell
curl -GET http://localhost:8080/2
```
*id=1 is the user created during migration*

http://localhost:8080/h2

![](https://github.com/smartinrub/bcrypt-service/blob/master/login-H2.png?s=50)

3. Login using new user and password

```shell
curl -H "Content-Type: application/json" -X POST -d '{"email":"test@test.com","password":"test"}' http://localhost:8080/login
```

output:
```
Welcome test@test.com
```
