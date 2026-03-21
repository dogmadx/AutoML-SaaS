package me.osipshmel.mediator.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.osipshmel.mediator.security.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user_table")
public class User implements UserDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(name = "username")
    private String username;
    @Column(name ="email")
    private String email;
    @Column(name ="password")
    private String password;

    //TODO!
    @Enumerated(EnumType.STRING)
    private UserRole status;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    //TODO!
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + status.name()));
    }
    @Override
    public boolean isAccountNonLocked(){return true;}
    @Override
    public boolean isAccountNonExpired(){return true;}
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
}
