package example.spring_template.auth.dto;

import example.spring_template.auth.AuthRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequestDTO {
  private String username;
  private String rawPassword;
  private String email;
  private AuthRole role;         // null이면 기본 USER
  private Integer roleLevel;     // 선택: level로도 받을 수 있게(둘 중 하나만 사용)
}
