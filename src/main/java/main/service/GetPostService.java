package main.service;

import lombok.RequiredArgsConstructor;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.User;
import main.repository.*;
import main.response.api.CommentsResponse;
import main.response.api.PostInPostResponse;
import main.response.api.PostResponse;
import main.response.api.UserResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GetPostService {

    private final PostsRepository postsRepository;
    private final PostVotesRepository postVotesRepository;
    private final PostCommentsRepository postCommentsRepository;
    private final TagsRepository tagsRepository;
    private final UsersRepository usersRepository;

    @Value("${post.min_text_length}")
    private int minTextLength;

    public PostResponse getPostResponse(int offset, int limit, String mode) {

        PostResponse postResponse = new PostResponse();
        List<Post> filteredPosts;

        switch (mode) {
            case "recent":
                filteredPosts = postsRepository.findAllAcceptedPostsOrderedByDateDesc(limit, offset);
                break;
            case "popular":
                filteredPosts = postsRepository.findAllAcceptedPostsOrderedByCommentsCount(limit, offset);
                break;
            case "best":
                filteredPosts = postsRepository.findAllAcceptedPostsOrderedByLikes(limit, offset);
                break;
            case "early":
                filteredPosts = postsRepository.findAllAcceptedPostsOrderedByDate(limit, offset);
                break;
            default:
                postResponse.setPosts(new ArrayList<>());
                return postResponse;
        }

        postResponse.setCount(postsRepository.countAllAcceptedPosts());
        postResponse.setPosts(fillPostResponses(filteredPosts));

        return postResponse;
    }

    public PostResponse getPostSearchResponse(int offset, int limit, String query) {

        if (!query.trim().isEmpty()) {
            PostResponse postResponse = new PostResponse();
            postResponse.setCount(postsRepository.countAllAcceptedPostsBySearch(query));
            postResponse.setPosts(fillPostResponses(postsRepository.findAllAcceptedPostBySearch(limit, offset, query)));
            return postResponse;
        }

        return getPostResponse(offset, limit, "recent");
    }

    public PostResponse getPostByDateResponse(int offset, int limit, String date) {

        PostResponse postResponse = new PostResponse();

        postResponse.setCount(postsRepository.countAllAcceptedPostsByDate(LocalDate.parse(date)));
        postResponse.setPosts(fillPostResponses(postsRepository.findAllAcceptedPostsByDate(limit, offset, LocalDate.parse(date))));

        return postResponse;
    }

    public PostResponse getPostByTagResponse(int offset, int limit, String tag) {

        PostResponse postResponse = new PostResponse();

        postResponse.setCount(postsRepository.countAllAcceptedPostsByTag(tag));
        postResponse.setPosts(fillPostResponses(postsRepository.findAllAcceptedPostsByTag(limit, offset, tag)));

        return postResponse;
    }

    public PostInPostResponse getPostByIdResponse(int id) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Post currentPost = postsRepository.findById(id).orElse(null);
        if (currentPost == null) {
            return null;
        }

        main.model.User authUser = usersRepository.findByEmail(authentication.getName());
        if (authUser == null || (!authUser.isModerator() && !authUser.getId().equals(currentPost.getUser().getId()))) {
            currentPost = postsRepository.findAcceptedPostById(id);
            if (currentPost == null) {
                return null;
            }
            postsRepository.updateViewCountInPost(id);
        }

        return fillPostResponse(currentPost, id);
    }

    public PostResponse getMyPosts(int offset, int limit, String status) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = usersRepository.findByEmail(authentication.getName());
        int userId = user.getId();

        PostResponse posts = new PostResponse();
        List<Post> filteredPosts;
        int countFilteredPosts;

        switch (status) {
            case "inactive":
                filteredPosts = postsRepository.findMyInactivePosts(limit, offset, userId);
                countFilteredPosts = postsRepository.countAllMyInactivePosts(userId);
                break;
            case "pending":
                filteredPosts = postsRepository.findMyActivePosts(limit, offset, userId, ModerationStatus.NEW.toString());
                countFilteredPosts = postsRepository.countAllMyActivePosts(userId, ModerationStatus.NEW.toString());
                break;
            case "declined":
                filteredPosts = postsRepository.findMyActivePosts(limit, offset, userId, ModerationStatus.DECLINED.toString());
                countFilteredPosts = postsRepository.countAllMyActivePosts(userId, ModerationStatus.DECLINED.toString());
                break;
            case "published":
                filteredPosts = postsRepository.findMyActivePosts(limit, offset, userId, ModerationStatus.ACCEPTED.toString());
                countFilteredPosts = postsRepository.countAllMyActivePosts(userId, ModerationStatus.ACCEPTED.toString());
                break;
            default:
                posts.setPosts(new ArrayList<>());
                return posts;
        }

        posts.setCount(countFilteredPosts);
        posts.setPosts(fillPostResponses(filteredPosts));

        return posts;
    }

    public PostResponse getPostsToModeration(int offset, int limit, String status) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = usersRepository.findByEmail(authentication.getName());
        int userId = user.getId();

        PostResponse posts = new PostResponse();
        List<Post> filteredPosts;
        int countFilteredPosts;

        switch (status) {
            case "new":
                filteredPosts = postsRepository.findAllNewPosts(limit, offset, userId);
                countFilteredPosts = postsRepository.countAllNewPosts(userId);
                break;
            case "declined":
                filteredPosts = postsRepository.findActivePostsWithModeration(limit, offset, userId, ModerationStatus.DECLINED.toString());
                countFilteredPosts = postsRepository.countActivePostsWithModeration(userId, ModerationStatus.DECLINED.toString());
                break;
            case "accepted":
                filteredPosts = postsRepository.findActivePostsWithModeration(limit, offset, userId, ModerationStatus.ACCEPTED.toString());
                countFilteredPosts = postsRepository.countActivePostsWithModeration(userId, ModerationStatus.ACCEPTED.toString());
                break;
            default:
                posts.setPosts(new ArrayList<>());
                return posts;
        }

        posts.setCount(countFilteredPosts);
        posts.setPosts(fillPostResponses(filteredPosts));

        return posts;
    }

    private List<PostInPostResponse> fillPostResponses(List<Post> posts) {

        List<PostInPostResponse> postInPostResponses = new ArrayList<>();

        posts.forEach(p -> {
            PostInPostResponse postInPostResponse = new PostInPostResponse();
            postInPostResponse.setId(p.getId());
            postInPostResponse.setTimestamp(p.getTime().toEpochSecond());

            UserResponse userResponse = new UserResponse();
            userResponse.setId(p.getUser().getId());
            userResponse.setName(p.getUser().getName());

            postInPostResponse.setUser(userResponse);
            postInPostResponse.setTitle(p.getTitle());
            String announce = p.getText().replaceAll("<.*?>", "");
            if (announce.length() > minTextLength) {
                postInPostResponse.setAnnounce(announce.substring(0, minTextLength).concat("..."));
            } else {
                postInPostResponse.setAnnounce(announce);
            }
            postInPostResponse.setLikeCount(postVotesRepository.votesCount(1, p.getId()));
            postInPostResponse.setDislikeCount(postVotesRepository.votesCount(-1, p.getId()));
            postInPostResponse.setCommentCount(postCommentsRepository.commentsCount(p.getId()));
            postInPostResponse.setViewCount(p.getViewCount());

            postInPostResponses.add(postInPostResponse);
        });
        return postInPostResponses;
    }

    private PostInPostResponse fillPostResponse(Post currentPost, int id) {

        PostInPostResponse post = new PostInPostResponse();

        post.setId(currentPost.getId());
        post.setTimestamp(currentPost.getTime().toEpochSecond());
        post.setActive(currentPost.isActive());

        UserResponse user = new UserResponse();
        user.setId(currentPost.getUser().getId());
        user.setName(currentPost.getUser().getName());

        post.setUser(user);
        post.setTitle(currentPost.getTitle());
        post.setText(currentPost.getText());

        post.setLikeCount(postVotesRepository.votesCount(1, currentPost.getId()));
        post.setDislikeCount(postVotesRepository.votesCount(-1, currentPost.getId()));
        post.setViewCount(currentPost.getViewCount());

        List<CommentsResponse> comments = new ArrayList<>();
        postCommentsRepository.findCommentsByPostId(id).forEach(c -> {

            CommentsResponse comment = new CommentsResponse();

            comment.setId(c.getId());
            comment.setTimestamp(c.getTime().toEpochSecond());
            comment.setText(c.getText());

            UserResponse userLeftComment = new UserResponse();
            userLeftComment.setId(c.getUser().getId());
            userLeftComment.setName(c.getUser().getName());
            userLeftComment.setPhoto(c.getUser().getPhoto());

            comment.setUser(userLeftComment);
            comments.add(comment);
        });

        post.setComments(comments);
        post.setTags(tagsRepository.findTagNamesByPostId(id));

        return post;
    }
}