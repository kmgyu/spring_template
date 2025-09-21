package example.spring_template.playground.comment;

import example.spring_template.auth.AuthUser;
import example.spring_template.auth.AuthUserRepository;
import example.spring_template.playground.dashboard.DashboardRepository;
import example.spring_template.playground.dashboard.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

  private final CommentRepository commentRepository;
  private final DashboardRepository postRepository;
  private final AuthUserRepository userRepository;
  private final CommentConverter commentConverter;

  @Transactional
  public CommentDTO.Response create(CommentDTO.CreateRequest dto) {
    Post post = postRepository.findById(dto.getPostId())
            .orElseThrow(() -> new IllegalArgumentException("post not found: " + dto.getPostId()));
    AuthUser user = userRepository.findById(dto.getUserId())
            .orElseThrow(() -> new IllegalArgumentException("user not found: " + dto.getUserId()));

    Comment comment = commentConverter.toEntity(dto, post, user);
    Comment saved = commentRepository.save(comment);
    return commentConverter.toResponse(saved);
  }

  @Transactional(readOnly = true)
  public Page<CommentDTO.Response> listByPost(Long postId, Pageable pageable) {
    Post post = postRepository.findById(postId)
            .orElseThrow(() -> new IllegalArgumentException("post not found: " + postId));
    return commentRepository.findByPost(post, pageable).map(commentConverter::toResponse);
  }

  @Transactional(readOnly = true)
  public CommentDTO.Response get(Long id) {
    Comment c = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("comment not found: " + id));
    return commentConverter.toResponse(c);
  }

  @Transactional
  public CommentDTO.Response update(Long id, CommentDTO.UpdateRequest dto, Long currentUserId, boolean isAdmin) {
    Comment c = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("comment not found: " + id));
    if (!isAdmin && !c.getAuthor().getId().equals(currentUserId)) {
      throw new SecurityException("no permission to edit");
    }
    c.setContent(dto.getContent());
    return commentConverter.toResponse(c);
  }

  @Transactional
  public void delete(Long id, Long currentUserId, boolean isAdmin) {
    Comment c = commentRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("comment not found: " + id));
    if (!isAdmin && !c.getAuthor().getId().equals(currentUserId)) {
      throw new SecurityException("no permission to delete");
    }
    commentRepository.delete(c);
  }
}
