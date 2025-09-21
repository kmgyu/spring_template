package example.spring_template.playground.dashboard;

import example.spring_template.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playground/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    // 단건 조회
    @GetMapping("/{id}")
    public ApiResponse<Post> getPost(@PathVariable Long id) {
        Post post = dashboardService.findById(id);
        return ApiResponse.ok(post);
    }

    // 페이징 전체 조회
    @GetMapping
    public ApiResponse<Page<Post>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = dashboardService.findAll(pageable);
        return ApiResponse.ok(posts);
    }

    // 생성
    @PostMapping
    public ApiResponse<String> createPost(@RequestBody Post post) {
        boolean created = dashboardService.createPost(post);
        if (created) {
            return ApiResponse.ok("Post created successfully");
        } else {
            return ApiResponse.<String>builder()
                    .code("ERROR_CREATE_POST")
                    .message("Failed to create post")
                    .timestamp(java.time.LocalDateTime.now())
                    .build();
        }
    }
}
