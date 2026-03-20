package me.osipshmel.mediator.repository;

import me.osipshmel.mediator.repository.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {

    @Query("""
            SELECT t FROM Token t inner join User u
            on t.user.id = u.id
            where t.user.id = :userId and t.loggedOut = false and t.isRevoked = false
            """)
    List<Token> findAllAccessTokenByUser(Long userId);

    Optional<Token> findByAccessToken(String accessToken);

    Optional<Token> findByRefreshToken(String refreshToken);
}
