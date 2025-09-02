package example.spring_template.playground.user;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/playground")
public class LoginController {

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {
        // 이미 로그인 상태면 홈으로 리다이렉트
        if (authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/";
        }
        // templates/login.html 렌더
        return "auth/login";
    }
}
