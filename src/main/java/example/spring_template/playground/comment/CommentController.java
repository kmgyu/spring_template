package example.spring_template.playground.comment;

import example.spring_template.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class CommentController {

  private final CommentService commentService;

  @GetMapping("/posts/{postId}/comments")
  public ApiResponse<Page<CommentDTO.Response>> list(
          @PathVariable Long postId, Pageable pageable) {
    return ApiResponse.ok(commentService.listByPost(postId, pageable));
  }

  @PostMapping("/comments")
  public ApiResponse<CommentDTO.Response> create(@Valid @RequestBody CommentDTO.CreateRequest req) {
    return ApiResponse.ok(commentService.create(req));
  }

  @GetMapping("/comments/{id}")
  public ApiResponse<CommentDTO.Response> get(@PathVariable Long id) {
    return ApiResponse.ok(commentService.get(id));
  }

  @PatchMapping("/comments/{id}")
  public ApiResponse<CommentDTO.Response> update(@PathVariable Long id,
                                                  @Valid @RequestBody CommentDTO.UpdateRequest req,
                                                  @RequestHeader("X-USER-ID") Long currentUserId,
                                                  @RequestHeader(value = "X-IS-ADMIN", defaultValue = "false") boolean isAdmin) {
    return ApiResponse.ok(commentService.update(id, req, currentUserId, isAdmin));
  }

  @DeleteMapping("/comments/{id}")
  public ApiResponse<Void> delete(@PathVariable Long id,
                                  @RequestHeader("X-USER-ID") Long currentUserId,
                                  @RequestHeader(value = "X-IS-ADMIN", defaultValue = "false") boolean isAdmin) {
    commentService.delete(id, currentUserId, isAdmin);
    return ApiResponse.ok(null);
  }
}