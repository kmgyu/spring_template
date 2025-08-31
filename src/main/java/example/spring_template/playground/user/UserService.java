package example.spring_template.playground.user;

import example.spring_template.playground.user.dto.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User register(SignUpRequest req) {
        if (userRepository.existsByUsername(req.username())) {
            throw new IllegalArgumentException("이미 사용 중인 사용자명입니다.");
        }
        if (req.email() != null && !req.email().isBlank() && userRepository.existsByEmail(req.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        User user = User.builder()
                .username(req.username())
                .password(passwordEncoder.encode(req.password()))
                .email(req.email())
                .role(UserRole.USER)
                .build(); // createdAt/updatedAt 은 @Created

        // 단일 권한 모델이면 여기서 기본 USER 할당 (User.role 추가한 경우)
        // user.setRole(UserRole.USER);

        return userRepository.save(user);
    }
}
