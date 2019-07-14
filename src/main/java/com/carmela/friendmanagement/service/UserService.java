package com.carmela.friendmanagement.service;

import com.carmela.friendmanagement.model.User;
import com.carmela.friendmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public User createUser(User user) {
        return userRepository.save(user);
    }

    @Transactional
    public void addFriend(String email1, String email2) throws Exception{

        if(email1 != null && email1.equals(email2)) {
           throw new Exception("Cannot connect same users");
        }

        User person = userRepository.findByEmail(email1);
        User friend = userRepository.findByEmail(email2);

        if(person == null || friend == null) {
            throw new Exception("User(s) not found");
        } else if(person.hasFriend(friend)) {
            throw new Exception("Already friends");
        } else if(person.hasBlocked(friend) || friend.hasBlocked(person)) {
            throw new Exception("User is blocked");
        }

        person.addFriend(friend);
        userRepository.save(friend);
    }

    @Transactional(readOnly=true)
    public Set<User> getFriends(String email) throws Exception{
        User user = userRepository.findByEmail(email);

        if(user == null) {
            throw new Exception("User not found");
        }

        return user.getFriends();
    }

    @Transactional(readOnly=true)
    public Set<User> getMutualFriends(String email1, String email2) throws Exception{

        if(email1 != null && email1.equals(email2)) {
            throw new Exception("Invalid request");
        }

        User user1 = userRepository.findByEmail(email1);
        User user2 = userRepository.findByEmail(email2);

        if(user1 == null || user2 == null) {
            throw new Exception("User(s) not found");
        }

        Set<User> user2Friends = user2.getFriends();

        return user1.getFriends().stream()
                .filter(user2Friends::contains)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void addFollower(String requestor, String target) throws Exception {

        if(requestor != null && requestor.equals(target)) {
            throw new Exception("Cannot follow self");
        }

        User user1 = userRepository.findByEmail(requestor);
        User user2 = userRepository.findByEmail(target);

        if(user1 == null || user2 == null) {
            throw new Exception("User(s) not found");
        } else if(user2.hasFollower(user1)) {
            throw new Exception("Already followed");
        }

        user2.addFollower(user1);
        userRepository.save(user2);
    }

    @Transactional
    public void addBlockList(String requestor, String target) throws Exception{

        if(requestor != null && requestor.equals(target)) {
            throw new Exception("Cannot block self");
        }

        User user1 = userRepository.findByEmail(requestor);
        User user2 = userRepository.findByEmail(target);

        if(user1 == null || user2 == null) {
            throw new Exception("User(s) not found");
        } else if(user1.hasBlocked(user2)){
            throw new Exception("Already blocked");
        }

        user1.blockUser(user2);
        userRepository.save(user1);
    }

    @Transactional(readOnly = true)
    public Set<User> getUsersToBeNotified(String sender, String text) throws Exception{

        User user = userRepository.findByEmail(sender);

        if(user == null) {
            throw new Exception("User not found");
        }

        // friends to be notified
        Set<User> recipients = userRepository.findFriendsNotBlocked(sender);

        // get followers
        recipients.addAll(userRepository.findFollowersNotBlocked(sender));

        // mentioned user
        Pattern pattern = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        String[] mentioned = Arrays.stream(text.split(" "))
                .filter(t ->
                        pattern.matcher(t).matches()
               )
               .toArray(String[]::new);
        if(mentioned != null && mentioned.length != 0){
            recipients.addAll(userRepository.findUsersMentionedNotBlocked(sender, mentioned));
        }

        return recipients;
    }

}
