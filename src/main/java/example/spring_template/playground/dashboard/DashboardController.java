package example.spring_template.playground.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/playground/dashboard")
public class DashboardController {
    private final DashboardService dashboardService;

    // 단건 조회
    @GetMapping("/{id}")
    public ResponseEntity<Post> getPost(@PathVariable Long id) {
        Post post = dashboardService.findById(id);
        return ResponseEntity.ok(post);
    }

    // 페이징 전체 조회
    @GetMapping
    public ResponseEntity<Page<Post>> getPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> posts = dashboardService.findAll(pageable);
        return ResponseEntity.ok(posts);
    }

    // 생성
    @PostMapping
    public ResponseEntity<String> createPost(@RequestBody Post post) {
        boolean created = dashboardService.createPost(post);
        if (created) {
            return ResponseEntity.ok("Post created successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to create post");
        }
    }

}
