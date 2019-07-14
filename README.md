# friend-management

API server deployed in heroku - https://friend-management.herokuapp.com/  
Documentation - https://friend-management.herokuapp.com/swagger-ui.html  

Technologies used: Java, Spring Boot, Hibernate, MySql, Swagger (for documentation)

## API
### 1. Create user
Endpoint: /api/users/  
Request Method: POST  
Body: 
```js
{
  "email": "carmela@gmail.com"
}
```
### 2. Connect friends
Endpoint: /api/users/friends/add  
Request Method: POST  
Body:
```js
{
  "friends": ["carmela@gmail.com", "jane@gmail.com"]
}
```

### 3. Get friends
Endpoint: /api/users/friends  
Request Method: GET  
Params:
```js
{
  "email": "carmela@gmail.com"
}
```

### 4. Get mutual friends
Endpoint: /api/users/mutual-friends  
Request Method: GET  
Params:
```js
{
  "friends": ["carmela@gmail.com", "jane@gmail.com"]
}
```

### 5. Subscribe to target
Endpoint: /api/users/subscribe    
Request Method: POST  
Body:  
```js
{
  "requestor": "carmela@gmail.com",
  "target": "jane@gmail.com"
}
```

### 6. Block user
Endpoint: /api/users/block  
Request Method: POST  
Body:
```js
{
  "requestor": "carmela@gmail.com",
  "target": "jane@gmail.com"
}
```
### 7. Get users to be notified
Endpoint: /api/users/subscribers  
Request Method: GET  
Body:
```js
{
  "sender": "carmela@gmail.com",
  "text": "Hi jane@gmail.com"
}
```
