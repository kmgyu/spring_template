package example.spring_template.playground.dashboard;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final DashboardRepository dashboardRepository;

    public Post findById(Long id) {
        return dashboardRepository.findById(id).get();
    }

    public Page<Post> findAll(Pageable pageable) {
        return dashboardRepository.findAll(pageable);
    }

    public boolean createPost(Post post) {
        dashboardRepository.save(post);
        return true;
    }
}
