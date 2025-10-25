package md.java.taskhub.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import md.java.taskhub.auth.entity.UserRole;

@Getter
@Setter
public class RegisterRequestDto {
    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is require")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @Email(message = "Invalid email format")
    private String email;

    private UserRole role;
}
