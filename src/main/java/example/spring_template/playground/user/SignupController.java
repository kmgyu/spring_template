package example.spring_template.playground.user;

import example.spring_template.playground.user.dto.SignupRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class SignupController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("form", new SignupRequest("", "", ""));
        return "signup"; // templates/signup.html
    }

    @PostMapping("/signup")
    public String signup(@Valid @ModelAttribute("form") SignupRequest form,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()) return "signup";

        User user;
        try {
            user = userService.register(form);
        } catch (IllegalArgumentException e) {
            bindingResult.reject("signup.error", e.getMessage());
            return "signup";
        }

        // 선택: 자동 로그인
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_USER")); // 단일권한이면 고정
        var principal = CustomUserDetails.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .emailVerified(true)
                .locked(false)
                .nickname(user.getUsername())
                .authorities(authorities)
                .build();

        var auth = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(auth);

        return "redirect:/";
    }
}

//for Rest controller
//@RestController
//@RequiredArgsConstructor
//@RequestMapping("/api/auth")
//public class AuthApiController {
//    private final UserService userService;
//
//    @PostMapping("/signup")
//    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
//        userService.register(req);
//        return ResponseEntity.status(HttpStatus.CREATED).build();
//    }
//}