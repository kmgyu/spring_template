package example.spring_template.auth.dto;

import example.spring_template.auth.AuthRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpResponseDTO {
  private Long id;
  private String username;
  private String email;
  private AuthRole role;
  private Instant createdAt;
}
