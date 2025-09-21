package example.spring_template.playground.comment;

import example.spring_template.playground.dashboard.Post;
import example.spring_template.playground.user.User;
import org.springframework.stereotype.Component;

@Component
public class CommentConverter {

  public Comment toEntity(CommentDTO.CreateRequest dto, Post post, User user) {
    return Comment.builder()
            .post(post)
            .author(user)
            .content(dto.getContent())
            .build();
  }

  public CommentDTO.Response toResponse(Comment entity) {
    return CommentDTO.Response.builder()
            .id(entity.getId())
            .postId(entity.getPost().getId())
            .userId(entity.getAuthor().getId())
            .content(entity.getContent())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
  }
}