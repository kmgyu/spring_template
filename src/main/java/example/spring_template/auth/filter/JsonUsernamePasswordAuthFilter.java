package example.spring_template.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import example.spring_template.auth.dto.LoginRequestDTO;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

public class JsonUsernamePasswordAuthFilter extends UsernamePasswordAuthenticationFilter {
  private final ObjectMapper om = new ObjectMapper();

  public JsonUsernamePasswordAuthFilter() {
    setFilterProcessesUrl("/api/login"); // 엔드포인트 지정
  }

  @Override
  public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
          throws AuthenticationException {

    try {
      LoginRequestDTO body = om.readValue(request.getInputStream(), LoginRequestDTO.class);
      UsernamePasswordAuthenticationToken authRequest =
              new UsernamePasswordAuthenticationToken(body.getUsername(), body.getPassword());
      setDetails(request, authRequest);
      return this.getAuthenticationManager().authenticate(authRequest);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                          FilterChain chain, Authentication authResult) throws IOException, ServletException {
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write("""
      {"success":true,"message":"login ok"}
      """);
  }

  @Override
  protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException failed) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write("""
      {"success":false,"message":"invalid credentials"}
      """);
  }
}