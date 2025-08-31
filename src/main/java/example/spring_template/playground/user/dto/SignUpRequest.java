package example.spring_template.playground.user.dto;

import jakarta.validation.constraints.*;

public record SignUpRequest(
        @NotBlank @Size(min = 3, max = 50) String username,
        @NotBlank @Size(min = 8, max = 100) String password,
        @Email @Size(max = 100) String email
) {}
