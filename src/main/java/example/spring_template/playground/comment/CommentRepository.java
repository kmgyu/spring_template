package example.spring_template.playground.comment;

import example.spring_template.playground.dashboard.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
  Page<Comment> findByPost(Post post, Pageable pageable);
}
