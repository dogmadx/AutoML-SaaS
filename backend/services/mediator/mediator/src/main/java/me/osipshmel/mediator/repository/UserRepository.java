package me.osipshmel.mediator.repository;

import me.osipshmel.mediator.repository.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    //TODO!
    //  на аннотацию вида UniqueUsername перехуячить бы
    boolean existsByUsername(String username);
    boolean existsUserByEmail(String username);
}
