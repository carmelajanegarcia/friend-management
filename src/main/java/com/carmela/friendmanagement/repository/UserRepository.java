package com.carmela.friendmanagement.repository;

import com.carmela.friendmanagement.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    Boolean existsByEmail(String email);

    @Query("SELECT f1\n" +
            "FROM User u1, User u2\n" +
            "INNER JOIN u1.friends f1\n" +
            "INNER JOIN u2.friends f2\n" +
            "WHERE u1.email = :email1\n" +
            "AND u2.email = :email2\n" +
            "AND f1.email = f2.email")
    Set<User> findMutualFriends(@Param("email1") String email1, @Param("email2") String email2);

    @Query("SELECT f from User u "
            +"inner join u.friends f "
            +"left join f.blockedUsers b "
            +"where u.email = :email "
            +"and (b is null or u not in b)")
    Set<User> findFriendsNotBlocked(@Param("email") String email);

    @Query("SELECT f from User u "
            +"inner join u.followers f "
            +"left join f.blockedUsers b "
            +"where u.email = :email "
            +"and (b is null or u not in b)")
    Set<User> findFollowersNotBlocked(@Param("email") String email);

    @Query("SELECT u from User u "
            +"left join u.blockedUsers b "
            +"where (u.email in :mentioned) "
            +"and (b is null or :sender not in b.email)")
    Set<User> findUsersMentionedNotBlocked(@Param("sender") String sender, @Param("mentioned") String[] mentioned);

}
