package com.carmela.friendmanagement.controller;


import com.carmela.friendmanagement.model.User;
import com.carmela.friendmanagement.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Api(value="Friend Management System", description="User operations in the system")
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @ApiOperation(value = "Add a user", response = User.class)
    @PostMapping
    public User createUser(
            @ApiParam(value = "User object to store in database table. Example: {email:'carmela@gmail.com'}", required = true)
            @RequestBody User user) {
        return userService.createUser(user);
    }

    @ApiOperation(value = "Create a connection (friendship) between 2 users", response = ResponseEntity.class)
    @PostMapping("/friends/add")
    public ResponseEntity addFriend(
            @ApiParam(value = "Json object that contains the array of email addresses of 2 users to be connected. Example: {\"friends\": [\"carmela@gmail.com\",\"jane@gmail.com\"]}", required = true)
            @RequestBody Map<String, List<String>> request) {

        Map<String, Object> responseData = new HashMap<>();

        if(request == null || request.get("friends") == null || request.get("friends").size() != 2) {
            responseData.put("success", false);
            responseData.put("message", "Invalid request");
            return ResponseEntity.badRequest().body(responseData);
        }

        try {
            List<String> friends = request.get("friends");
            userService.addFriend(friends.get(0), friends.get(1));
            responseData.put("success", true);
            return ResponseEntity.ok(responseData);
        }
        catch(Exception e) {
            responseData.put("success", false);
            responseData.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(responseData);
        }

    }

    @ApiOperation(value = "Get the friend list of given email", response = ResponseEntity.class)
    @GetMapping("/friends")
    public ResponseEntity getFriends(
            @ApiParam(value = "Email of user to get friend list", required = true)
            @RequestParam("email") String email) {

        Map<String, Object> responseData = new HashMap<>();

        try {
            Set<User> friends = userService.getFriends(email);
            responseData.put("success", true);
            responseData.put("friends", friends.stream().map(User::getEmail).toArray(String[]::new));
            responseData.put("count", friends.size());
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            responseData.put("success", false);
            responseData.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(responseData);
        }

    }

    @ApiOperation(value = "Get the mutual friend list of given 2 emails", response = ResponseEntity.class)
    @GetMapping("/mutual-friends")
    public ResponseEntity getMutualFriends(
            @ApiParam(value = "Email of users to get mutual friend list. Must have 2 entries", required = true)
            @RequestParam("friends") List<String> emails) {
        Map<String, Object> responseData = new HashMap<>();
        if(emails == null || emails.size() != 2) {
            responseData.put("success", false);
            responseData.put("message", "Invalid request");
            return ResponseEntity.badRequest().body(responseData);
        }

        try {
            Set<User> friends = userService.getMutualFriends(emails.get(0), emails.get(1));
            responseData.put("success", true);
            responseData.put("friends", friends.stream().map(User::getEmail).toArray(String[]::new));
            responseData.put("count", friends.size());
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            responseData.put("success", false);
            responseData.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(responseData);
        }

    }

    @ApiOperation(value = "Subscribe to updates from the target by the requestor", response = ResponseEntity.class)
    @PostMapping("/subscribe")
    public ResponseEntity addFollower(
            @ApiParam(value = "Json object that contains the requestor email and the target email to be subscribed. Example: {\"requestor\": \"carmela@gmail.com\", \"target\":\"jane@gmail.com\"}", required = true)
            @RequestBody Map<String, String> request) {
        Map<String, Object> responseData = new HashMap<>();
        if(request == null || request.get("requestor") == null || request.get("target") == null){
            responseData.put("success", false);
            responseData.put("message", "Invalid request");
            return ResponseEntity.badRequest().body(responseData);
        }

        try {
            userService.addFollower(request.get("requestor"), request.get("target"));
            responseData.put("success", true);
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            responseData.put("success", false);
            responseData.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(responseData);
        }
    }

    @ApiOperation(value = "Block updates from the target by the requestor", response = ResponseEntity.class)
    @PostMapping("/block")
    public ResponseEntity addBlockList(
            @ApiParam(value = "Json object that contains the requestor email and the target email to be blocked. Example: {\"requestor\": \"carmela@gmail.com\", \"target\":\"jane@gmail.com\"}", required = true)
            @RequestBody Map<String, String> request) {
        Map<String, Object> responseData = new HashMap<>();
        if(request == null || request.get("requestor") == null || request.get("target") == null){
            responseData.put("success", false);
            responseData.put("message", "Invalid request");
            return ResponseEntity.badRequest().body(responseData);
        }

        try {
            userService.addBlockList(request.get("requestor"), request.get("target"));
            responseData.put("success", true);
            return ResponseEntity.ok(responseData);
        } catch(Exception e) {
            responseData.put("success", false);
            responseData.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(responseData);
        }
    }

    @ApiOperation(value = "Get the list of users that will be notified by the given sender", response = ResponseEntity.class)
    @GetMapping("/subscribers")
    public ResponseEntity getUsersToBeNotified(
            @ApiParam(value = "Sender that will send notification to subscribed users and text that contains the message", required = true)
            @RequestParam("sender") String sender, @RequestParam("text") String text) {
        Map<String, Object> responseData = new HashMap<>();
        try {
            Set<User> recipients = userService.getUsersToBeNotified(sender, text);
            responseData.put("success", true);
            responseData.put("recipients", recipients.stream().map(User::getEmail).toArray(String[]::new));
            return ResponseEntity.ok(responseData);
        } catch(Exception e) {
            responseData.put("success", false);
            responseData.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(responseData);
        }
    }

}
