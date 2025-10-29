package md.java.taskhub.authservice.controller;

import md.java.taskhub.authservice.dto.AuthResponseDto;
import md.java.taskhub.authservice.dto.LoginRequestDto;
import md.java.taskhub.authservice.dto.RegisterRequestDto;
import md.java.taskhub.authservice.dto.UserProfileDto;
import md.java.taskhub.authservice.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@Validated @RequestBody RegisterRequestDto request) {
        AuthResponseDto authResponse = authService.register(request);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Validated @RequestBody LoginRequestDto request) {
        AuthResponseDto authResponse = authService.login(request);
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getProfile() {
        UserProfileDto userProfileDto = authService.getMyProfile();
        return ResponseEntity.ok(userProfileDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDto> getUser(@PathVariable UUID id) {
        UserProfileDto userProfileDto = authService.getUser(id);
        return ResponseEntity.ok(userProfileDto);
    }
}
