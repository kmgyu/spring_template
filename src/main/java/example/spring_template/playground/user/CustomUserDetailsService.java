package example.spring_template.playground.user;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("not found username: " + username));

        // 다중권한일 시 - roles null 방지
//        var roles = user.getRoles() == null ? Collections.<UserRole>emptySet() : user.getRoles();

        // 권한 매핑
        var authority = new SimpleGrantedAuthority(user.getRole().getValue());
        return CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .emailVerified(true)
                .locked(false)
                .nickname(user.getUsername())
                .authorities(List.of(authority))
                .build();
    }
}
