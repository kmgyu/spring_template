package example.spring_template.playground.comment;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

public class CommentDTO {

  @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
  public static class CreateRequest {
    @NotNull private Long postId;
    @NotNull private Long userId;
    @NotBlank private String content;
  }

  @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
  public static class UpdateRequest {
    @NotBlank private String content;
  }

  @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
  public static class Response {
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
  }
}