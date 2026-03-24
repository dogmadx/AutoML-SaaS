package me.osipshmel.mediator.stubs.controller;

import me.osipshmel.mediator.repository.entity.User;
import me.osipshmel.mediator.stubs.dto.UserInfoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class DemoController {

    @GetMapping("api/v1/public/hello")
    public ResponseEntity<String> hello(){
        return ResponseEntity.ok("Hello");
    }

    @GetMapping("api/v1/user/me")
    public ResponseEntity<UserInfoResponse> getMe(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(new UserInfoResponse(user.getUsername(), user.getEmail()));

    }
}
