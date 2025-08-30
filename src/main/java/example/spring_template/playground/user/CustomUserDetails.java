package example.spring_template.playground.user;

import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails, Serializable {
    private static final long serialVersionUID = 174726374856727L;

    private final Long id;                 // DB PK
    private final String username;          // 로그인 ID
    private final String password;         // 인코딩된 비밀번호
    private final String email;
    private final boolean emailVerified;   // 이메일 인증 여부
    private final boolean locked;          // 잠김 여부 (true = 잠김)
    private final String nickname;
    private final Collection<? extends GrantedAuthority> authorities; // 권한, 다중 권한 확장 시 그대로 사용 가능할 것...

    @Builder
    public CustomUserDetails(
            Long id,
            String username,
            String password,
            String email,
            boolean emailVerified,
            boolean locked,
            String nickname,
            Collection<? extends GrantedAuthority> authorities
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.emailVerified = emailVerified;
        this.locked = locked;
        this.nickname = nickname;
        this.authorities = authorities == null ? Collections.emptyList() : authorities;
    }

    // 스프링 시큐리티가 사용하는 "username" = 로그인 식별자
    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 필요 시 만료 로직 반영
    }

    @Override
    public boolean isAccountNonLocked() {
        return !locked; // 잠김이 아니면 true
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 필요 시 비번 만료 로직 반영
    }

    @Override
    public boolean isEnabled() {
        // 정책에 맞게 조합 (예: 이메일 인증 필수 && 잠김 아님)
        return emailVerified && !locked;
    }
}
