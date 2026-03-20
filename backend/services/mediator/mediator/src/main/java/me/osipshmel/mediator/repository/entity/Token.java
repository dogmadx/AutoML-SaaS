package me.osipshmel.mediator.repository.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="token_table")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private long id;

    @Column(name = "access_token")
    private String accessToken;
    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "is_logged_out")
    private boolean loggedOut;

    @Column(name = "is_revoked")
    private boolean isRevoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
