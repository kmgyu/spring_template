package example.spring_template.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
  private LocalDateTime timestamp; // 응답 시각
  private String code;             // "SUCCESS", "ERROR_VALIDATION" 등 상태 코드
  private String message;          // 응답 메시지
  private T data;                  // 실제 페이로드 (nullable)

  public static <T> ApiResponse<T> ok(T data) {
    return ApiResponse.<T>builder()
            .timestamp(LocalDateTime.now())
            .code("SUCCESS")
            .message("ok")
            .data(data)
            .build();
  }
}
