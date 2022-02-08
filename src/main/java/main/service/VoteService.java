package main.service;

import lombok.RequiredArgsConstructor;
import main.model.Post;
import main.model.PostVote;
import main.model.User;
import main.repository.PostVotesRepository;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import main.response.api.EditResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class VoteService {

    private final PostVotesRepository postVotesRepository;
    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;

    public EditResponse vote(int postId, int value) {

        User user = usersRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());
        Post post = postsRepository.findById(postId).orElse(null);
        EditResponse response = new EditResponse();

        if (post == null) {
            return null;
        }

        PostVote vote = postVotesRepository.findCurrentVote(user.getId(), postId);

        if (vote == null && !user.equals(post.getUser())) {
            vote = new PostVote();
            vote.setValue(value);
            vote.setTime(ZonedDateTime.now());
            vote.setUser(user);
            vote.setPost(post);

            postVotesRepository.save(vote);
            response.setResult(true);
        } else if (vote != null && vote.getValue() == (-1 * value)) {
            vote.setValue(value);
            vote.setTime(ZonedDateTime.now());

            postVotesRepository.save(vote);
            response.setResult(true);
        }

        return response;
    }
}
