package example.spring_template.auth;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(
    name = "auth_users",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_auth_users_username", columnNames = "username"),
        @UniqueConstraint(name = "uk_auth_users_email", columnNames = "email")
    },
    indexes = {
        @Index(name = "idx_auth_users_username", columnList = "username")
    }
)
public class AuthUser implements UserDetails {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(length = 50, nullable = false)
  private String username;

  @Column(length = 255, nullable = false)
  private String password;

  @Column(length = 100, nullable = false)
  private String email;

  @Enumerated(EnumType.STRING)
  @Column(length = 20, nullable = false)
  private AuthRole role; // 단일 역할(연관관계 없음)

  // 계정 상태 플래그
  @Builder.Default
  @Column(nullable = false)
  private boolean enabled = true;

  @Builder.Default
  @Column(nullable = false)
  private boolean accountNonExpired = true;

  @Builder.Default
  @Column(nullable = false)
  private boolean accountNonLocked = true;

  @Builder.Default
  @Column(nullable = false)
  private boolean credentialsNonExpired = true;

  // 보안 운영에 유용한 메타데이터
  private Instant lastLoginAt;
  private Instant passwordChangedAt;

  @Builder.Default
  @Column(nullable = false)
  private short passwordMiss = 0;

  private Instant lockUntil; // 일정 시간 잠금이 필요하면 사용

  // 감사 필드
  @CreatedDate
  @Column(nullable = false, updatable = false)
  private Instant createdAt;

  @LastModifiedDate
  @Column(nullable = false)
  private Instant updatedAt;

  // 의도된 변경 메서드
  public void changePassword(String encoded) {
    this.password = encoded;
    this.passwordChangedAt = Instant.now();
    this.passwordMiss = 0;
  }

  public void increasePasswordMiss(short threshold, long lockSeconds) {
    this.passwordMiss++;
    if (this.passwordMiss >= threshold) {
      this.accountNonLocked = false;
      this.lockUntil = Instant.now().plusSeconds(lockSeconds);
      this.passwordMiss = 0;
    }
  }

  public void unlockIfExpired() {
    if (!this.accountNonLocked && this.lockUntil != null && Instant.now().isAfter(this.lockUntil)) {
      this.accountNonLocked = true;
      this.lockUntil = null;
    }
  }

  public void enable()  { this.enabled = true;  }
  public void disable() { this.enabled = false; }

  public void changeRole(AuthRole role) { this.role = role; }

  // UserDetails 구현
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    // 연관관계 없이 enum → 권한 문자열로 즉시 매핑
    return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
  }
  @Override public boolean isAccountNonExpired()     { return accountNonExpired; }
  @Override public boolean isAccountNonLocked()      { return accountNonLocked; }
  @Override public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
  @Override public boolean isEnabled()               { return enabled; }
}
