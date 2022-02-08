package main.service;

import lombok.RequiredArgsConstructor;
import main.model.User;
import main.repository.PostsRepository;
import main.repository.UsersRepository;
import main.response.api.AuthCheckResponse;
import main.response.api.UserResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthCheckService {

    private final UsersRepository usersRepository;
    private final PostsRepository postsRepository;

    public AuthCheckResponse getAuthCheckResponse() {
        AuthCheckResponse authCheckResponse = new AuthCheckResponse();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = usersRepository.findByEmail(authentication.getName());

        if (user != null) {
            authCheckResponse.setResult(true);
            UserResponse userResponse = new UserResponse();
            userResponse.setId(user.getId());
            userResponse.setName(user.getName());
            userResponse.setPhoto(user.getPhoto());
            userResponse.setEmail(user.getEmail());

            if (user.isModerator()) {
                userResponse.setModeration(true);
                userResponse.setModerationCount(postsRepository.countAllNewPosts(user.getId()));
                userResponse.setSettings(true);
            } else {
                userResponse.setModeration(false);
                userResponse.setModerationCount(0);
            }

            authCheckResponse.setUser(userResponse);
        }

        return authCheckResponse;
    }
}
