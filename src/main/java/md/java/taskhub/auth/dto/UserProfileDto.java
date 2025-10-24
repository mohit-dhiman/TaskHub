package md.java.taskhub.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class UserProfileDto {
    private UUID id;
    private String username;
    private String email;
}
