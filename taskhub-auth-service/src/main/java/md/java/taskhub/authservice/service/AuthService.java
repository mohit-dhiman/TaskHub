package md.java.taskhub.authservice.service;

import jakarta.persistence.EntityNotFoundException;
import md.java.taskhub.authservice.dto.AuthResponseDto;
import md.java.taskhub.authservice.dto.LoginRequestDto;
import md.java.taskhub.authservice.dto.RegisterRequestDto;
import md.java.taskhub.authservice.dto.UserProfileDto;
import md.java.taskhub.authservice.entity.User;
import md.java.taskhub.authservice.entity.UserRole;
import md.java.taskhub.authservice.repository.UserRepository;
import md.java.taskhub.authservice.util.JwtService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,  JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.USER);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponseDto(token, user.getUsername());
    }

    public AuthResponseDto login(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username or password"));
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        String token = jwtService.generateToken(user.getUsername());
        return new AuthResponseDto(token, user.getUsername());
    }

    public UserProfileDto getMyProfile() {
        User user = getCurrentUser();
        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail());
    }

    public UserProfileDto getUser(UUID id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("User not found")
        );
        return new UserProfileDto(user.getId(), user.getUsername(), user.getEmail());
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User principalUser = (User) authentication.getPrincipal();
        String username = principalUser.getUsername();
        User user = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException("User not found with username: " + username)
        );
        return user;
    }
}
