package example.spring_template.auth;

import example.spring_template.auth.dto.SignUpRequestDTO;
import example.spring_template.auth.dto.SignUpResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthUserService {
    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;



  public SignUpResponseDTO signUp(SignUpRequestDTO req) {
    // 1) 정규화 및 기초 검증
    final String username = normalizeUsername(req.getUsername());
    final String email = normalizeEmail(req.getEmail());
    validateBasics(username, req.getRawPassword(), email);

    // 2) 중복 검사
    assertUniqueUsername(username);
    assertUniqueEmail(email);

    // 3) 비밀번호 인코딩
    final String encoded = passwordEncoder.encode(req.getRawPassword());

    // 4) 역할 결정 (enum 우선, null이면 level 사용, 둘 다 없으면 USER)
    final AuthRole role = resolveRole(req.getRole(), req.getRoleLevel());

    // 5) 엔티티 생성
    AuthUser user = buildAuthUser(username, encoded, email, role);

    // 6) 저장
    user = authUserRepository.save(user);

    // 7) 응답 매핑
    return new SignUpResponseDTO(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getRole(),
            user.getCreatedAt()
    );
  }

  // ========= private helpers =========

  private String normalizeUsername(String username) {
    if (username == null) throw new IllegalArgumentException("username is required");
    // 필요 시 소문자 통일/허용 문자 제한 등 정책 반영
    return username.trim();
  }

  private String normalizeEmail(String email) {
    if (email == null) throw new IllegalArgumentException("email is required");
    return email.trim();
  }

  private void validateBasics(String username, String rawPassword, String email) {
    if (rawPassword == null || rawPassword.isBlank())
      throw new IllegalArgumentException("password is required");
    if (username.isBlank())
      throw new IllegalArgumentException("username must not be blank");
    if (email.isBlank())
      throw new IllegalArgumentException("email must not be blank");

    // 선택: 비밀번호 정책 검사 (길이/조합/누설 여부 등)
    // validatePasswordStrength(rawPassword);
  }

  private void assertUniqueUsername(String username) {
    if (authUserRepository.existsByUsername(username)) {
      throw new IllegalStateException("username already exists: " + username);
    }
  }

  private void assertUniqueEmail(String email) {
    if (authUserRepository.existsByEmail(email)) {
      throw new IllegalStateException("email already exists: " + email);
    }
  }

  private AuthRole resolveRole(AuthRole role, Integer roleLevel) {
    if (role != null) return role;
    if (roleLevel != null) return AuthRole.fromLevel(roleLevel);
    return AuthRole.USER;
  }

  private AuthUser buildAuthUser(String username, String encodedPassword, String email, AuthRole role) {
    return AuthUser.builder()
            .username(username)
            .password(encodedPassword)
            .email(email)
            .role(role)
            .enabled(true)
            .accountNonExpired(true)
            .accountNonLocked(true)
            .credentialsNonExpired(true)
            .passwordMiss((short) 0)
            .build();
  }

  // 선택: 비밀번호 정책 검사 예시
  @SuppressWarnings("unused")
  private void validatePasswordStrength(String rawPassword) {
    if (rawPassword.length() < 8) {
      throw new IllegalArgumentException("password must be at least 8 characters");
    }
    // 필요 시 대문자/소문자/숫자/특수문자 규칙 추가
  }


}
