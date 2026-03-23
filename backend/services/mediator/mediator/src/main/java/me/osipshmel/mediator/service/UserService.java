package me.osipshmel.mediator.service;

import me.osipshmel.mediator.repository.UserRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("user " + username + " not found"));
    }

    //TODO!
    //  на аннотацию вида UniqueUsername перехуячить бы
    private boolean existsByUsername(String username){
        return userRepository.existsByUsername(username);
    }
    private boolean existsByEmail(String email){
        return userRepository.existsByEmail(email);
    }
}
