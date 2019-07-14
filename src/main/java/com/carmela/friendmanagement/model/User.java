package com.carmela.friendmanagement.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name = "email_address", nullable = false, unique = true)
    private String email;

    @JsonIgnore
    @ManyToMany
    @JoinTable(name="user_friend",
                joinColumns = {@JoinColumn(name="user_id")},
                inverseJoinColumns = {@JoinColumn(name="friend_id")})
    private Set<User> friends = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name="user_friend",
            joinColumns = {@JoinColumn(name="friend_id")},
            inverseJoinColumns = {@JoinColumn(name="user_id")})
    private Set<User> friendOf;

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="user_following",
                joinColumns = {@JoinColumn(name="user_id")},
                inverseJoinColumns = {@JoinColumn(name="following_id")})
    private Set<User> following = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "following", cascade = CascadeType.ALL)
    private Set<User> followers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name="user_blocked",
            joinColumns = {@JoinColumn(name="user_id")},
            inverseJoinColumns = {@JoinColumn(name="blocked_id")})
    private Set<User> blockedUsers = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "blockedUsers", cascade = CascadeType.ALL)
    private Set<User> blockedBy = new HashSet<>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<User> getFriends() {
        return friends;
    }

    public void setFriends(Set<User> friends) {
        this.friends = friends;
    }

    public Set<User> getFriendOf() {
        return friendOf;
    }

    public void setFriendOf(Set<User> friendOf) {
        this.friendOf = friendOf;
    }

    public Set<User> getFollowing() {
        return following;
    }

    public void setFollowing(Set<User> following) {
        this.following = following;
    }

    public Set<User> getFollowers() {
        return followers;
    }

    public void setFollowers(Set<User> followers) {
        this.followers = followers;
    }

    public Set<User> getBlockedUsers() {
        return blockedUsers;
    }

    public void setBlockedUsers(Set<User> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    public Set<User> getBlockedBy() {
        return blockedBy;
    }

    public void setBlockedBy(Set<User> blockedBy) {
        this.blockedBy = blockedBy;
    }

    public void addFriend(User friend) {
        this.friends.add(friend);
        friend.getFriends().add(this);
    }

    public void addFollower(User follower) {
        this.followers.add(follower);
        follower.following.add(this);
    }

    public void blockUser(User user) {
        this.blockedUsers.add(user);
        user.blockedBy.add(this);
    }

    public boolean hasFriend(User user) {
        return this.getFriends() != null && this.getFriends().contains(user);
    }

    public boolean hasFollower(User user) {
        return this.getFollowers() != null && this.getFollowers().contains(user);
    }

    public boolean hasBlocked(User user) {
        return this.getBlockedUsers() != null && this.getBlockedUsers().contains(user);
    }
}
