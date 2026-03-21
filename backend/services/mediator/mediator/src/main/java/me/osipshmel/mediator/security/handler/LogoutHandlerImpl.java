package me.osipshmel.mediator.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.osipshmel.mediator.repository.TokenRepository;
import me.osipshmel.mediator.repository.entity.Token;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class LogoutHandlerImpl implements LogoutHandler{

    private final TokenRepository tokenRepository;

    public LogoutHandlerImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @Nullable Authentication authentication) {

        String authHeader = request.getHeader("Authorization");
        // If it is right authentification request, we check the token,
        // otherwise we'll pass the work on
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }

        String token = authHeader.substring(7);
        Token tokenEntity = tokenRepository.findByAccessToken(token).orElse(null);

        if(tokenEntity!=null){
            tokenEntity.setLoggedOut(true);
            tokenRepository.save(tokenEntity);
        }
    }
}
