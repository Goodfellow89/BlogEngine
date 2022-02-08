package main.service;

import lombok.RequiredArgsConstructor;
import main.model.Post;
import main.model.PostComment;
import main.repository.PostCommentsRepository;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import main.request.CommentRequest;
import main.response.api.EditResponse;
import main.response.api.PostCommentResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostsRepository postsRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final UsersRepository usersRepository;

    public PostCommentResponse addComment(CommentRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        PostCommentResponse response = new PostCommentResponse();
        ConcurrentHashMap<String, String> errors = new ConcurrentHashMap<>();

        Post post = postsRepository.findById(request.getPostId()).orElse(null);
        Integer parentId = request.getParentId();
        PostComment parentComment = parentId == null ? null : postCommentsRepository.findById(parentId).orElse(null);

        if (post == null || (parentId != null &&
                (parentComment == null || !postCommentsRepository.findCommentsByPostId(request.getPostId()).contains(parentComment)))) {
            return null;
        }

        if (request.getText().length() < 2) {
            errors.put("text", "Текст комментария не задан или слишком короткий");
        }

        if (errors.isEmpty()) {
            PostComment comment = new PostComment();
            comment.setPost(post);
            comment.setTime(ZonedDateTime.now());
            comment.setText(request.getText());
            comment.setUser(usersRepository.findByEmail(authentication.getName()));

            if (parentId != null) {
                comment.setPostComment(parentComment);
            }
            postCommentsRepository.save(comment);

            response.setId(comment.getId());
            return response;
        }

        response.setResult(false);
        response.setErrors(errors);
        return response;
    }
}
