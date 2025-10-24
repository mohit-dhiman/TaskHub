package md.java.taskhub.auth.controller;

import md.java.taskhub.auth.dto.AuthResponse;
import md.java.taskhub.auth.dto.LoginRequest;
import md.java.taskhub.auth.dto.RegisterRequest;
import md.java.taskhub.auth.dto.UserProfileDto;
import md.java.taskhub.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Validated @RequestBody RegisterRequest request) {
        AuthResponse authResponse = authService.register(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Validated @RequestBody LoginRequest request) {
        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getProfile(@RequestParam String username) {
        UserProfileDto userProfileDto = authService.getProfile(username);
        return ResponseEntity.ok(userProfileDto);
    }
}
