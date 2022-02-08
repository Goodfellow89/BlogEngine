package main.service;

import lombok.RequiredArgsConstructor;
import main.model.ModerationStatus;
import main.model.Post;
import main.model.Tag;
import main.model.User;
import main.repository.*;
import main.request.ModerationRequest;
import main.request.PostRequest;
import main.response.api.EditResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class EditPostService {

    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;
    private final TagsRepository tagsRepository;
    private final TagToPostRepository tagToPostRepository;
    private final PostVotesRepository votesRepository;
    private final SettingsRepository settingsRepository;

    @Value("${post.min_text_length}")
    private int minTextLength;

    @Value("${post.min_title_length}")
    private int minTitleLength;


    public EditResponse addPost(PostRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        EditResponse response = new EditResponse();
        String title = request.getTitle();
        String text = request.getText();
        ConcurrentHashMap<String, String> errors = new ConcurrentHashMap<>();

        if (title.length() <= minTitleLength) {
            errors.put("title", "Заголовок не установлен");
        }
        if (text.length() < minTextLength) {
            errors.put("title", "Текст публикации слишком короткий");
        }

        if (errors.isEmpty()) {
            Post post = new Post();

            post.setActive(request.getActive() == 1);
            if (settingsRepository.getSetting("POST_PREMODERATION").getValue().equals("NO") && post.isActive()) {
                post.setModerationStatus(ModerationStatus.ACCEPTED);
            } else {
                post.setModerationStatus(ModerationStatus.NEW);
            }
            post.setText(text);
            post.setTime((request.getTimestamp() * 1000) < System.currentTimeMillis()
                    ? ZonedDateTime.now()
                    : ZonedDateTime.ofInstant(Instant.ofEpochSecond(request.getTimestamp()), ZoneId.systemDefault()));
            post.setTitle(title);
            post.setUser(usersRepository.findByEmail(authentication.getName()));
            post.setViewCount(0);
            postsRepository.save(post);

            request.getTags().forEach(t -> {
                Tag tag = tagsRepository.findTagByName(t);
                List<Post> postList;
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(t);
                    List<Post> newPostList = new ArrayList<>();
                    newPostList.add(post);
                    tag.setPosts(newPostList);
                } else {
                    tag.getPosts().add(post);
                }

                tagsRepository.save(tag);
            });

            response.setResult(true);
            return response;
        }

        response.setResult(false);
        response.setErrors(errors);
        return response;
    }

    public EditResponse editPost(int id, PostRequest request) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        EditResponse response = new EditResponse();
        ConcurrentHashMap<String, String> errors = new ConcurrentHashMap<>();

        Post post = postsRepository.findById(id).orElse(null);
        if (post == null) {
            return null;
        }

        String title = request.getTitle();
        String text = request.getText();

        if (title.length() < minTitleLength) {
            errors.put("title", "Заголовок не установлен");
        }
        if (text.length() < minTextLength) {
            errors.put("title", "Текст публикации слишком короткий");
        }

        User user = usersRepository.findByEmail(authentication.getName());

        if (errors.isEmpty() && (post.getUser().equals(user) || user.isModerator())) {
            if (post.getUser().equals(user)) {
                if (settingsRepository.getSetting("POST_PREMODERATION").getValue().equals("NO") && post.isActive()) {
                    post.setModerationStatus(ModerationStatus.ACCEPTED);
                } else {
                    post.setModerationStatus(ModerationStatus.NEW);
                    post.setModerator(null);
                }
                post.setViewCount(0);
                votesRepository.deleteVotesOfPost(id);
            }

            post.setActive(request.getActive() == 1);
            post.setText(text);
            post.setTime((request.getTimestamp() * 1000) < System.currentTimeMillis()
                    ? ZonedDateTime.now()
                    : ZonedDateTime.ofInstant(Instant.ofEpochSecond(request.getTimestamp()), ZoneId.systemDefault()));
            post.setTitle(title);
            postsRepository.save(post);

            tagToPostRepository.deleteAllTagToPostsByPostId(post.getId());

            request.getTags().forEach(t -> {
                Tag tag = tagsRepository.findTagByName(t);
                if (tag == null) {
                    tag = new Tag();
                    tag.setName(t);
                    List<Post> newPostList = new ArrayList<>();
                    newPostList.add(post);
                    tag.setPosts(newPostList);
                } else {
                    tag.getPosts().add(post);
                }

                tagsRepository.save(tag);
            });

            response.setResult(true);
            return response;
        }

        response.setResult(false);
        response.setErrors(errors);
        return response;
    }

    public EditResponse moderatePost(ModerationRequest request) {

        EditResponse response = new EditResponse();

        User user = usersRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Post post = postsRepository.findById(request.getPostId()).orElse(null);

        if (post != null && post.isActive()) {
            post.setModerator(user);
            switch (request.getDecision()) {
                case "accept":
                    post.setModerationStatus(ModerationStatus.ACCEPTED);
                    break;
                case "decline":
                    post.setModerationStatus(ModerationStatus.DECLINED);
                    break;
            }
            postsRepository.save(post);
            response.setResult(true);
        }

        return response;
    }
}
