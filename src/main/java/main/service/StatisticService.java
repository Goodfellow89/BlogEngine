package main.service;

import lombok.RequiredArgsConstructor;
import main.model.ModerationStatus;
import main.model.User;
import main.repository.PostVotesRepository;
import main.repository.PostsRepository;
import main.repository.SettingsRepository;
import main.repository.UsersRepository;
import main.response.api.StatisticResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.ZoneId;

@Service
@RequiredArgsConstructor
public class StatisticService {

    private final PostsRepository postsRepository;
    private final UsersRepository usersRepository;
    private final PostVotesRepository votesRepository;
    private final SettingsRepository settingsRepository;

    public StatisticResponse getMy() {

        User user = usersRepository.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName());

        StatisticResponse response = new StatisticResponse();

        response.setPostsCount(postsRepository.countAllMyActivePosts(user.getId(), ModerationStatus.ACCEPTED.toString()));
        response.setLikesCount(votesRepository.countAllVotesOfMyPosts(1, user.getId()));
        response.setDislikesCount(votesRepository.countAllVotesOfMyPosts(-1, user.getId()));
        response.setViewsCount(postsRepository.countAllViewsOfMyPosts(user.getId()));

        Timestamp firstPublication = postsRepository.findTimeOfFirstMyPost(user.getId());
        if (firstPublication == null) {
            response.setFirstPublication(0);
        } else {
            response.setFirstPublication(firstPublication.toLocalDateTime().atZone(ZoneId.systemDefault()).toEpochSecond());
        }

        return response;
    }

    public StatisticResponse getAll() {
        if (settingsRepository.getSetting("STATISTICS_IS_PUBLIC").getValue().equals("YES")) {
            return getStatistics();
        }
        return getStatisticsForModerator();
    }

    @PreAuthorize("hasAuthority('moderator')")
    public StatisticResponse getStatisticsForModerator() {
        return getStatistics();
    }

    private StatisticResponse getStatistics() {
        StatisticResponse response = new StatisticResponse();

        response.setPostsCount(postsRepository.countAllAcceptedPosts());
        response.setLikesCount(votesRepository.allVotesCount(1));
        response.setDislikesCount(votesRepository.allVotesCount(-1));
        response.setViewsCount(postsRepository.countAllViews());

        Timestamp firstPublication = postsRepository.findTimeOfFirstPost();
        if (firstPublication == null) {
            response.setFirstPublication(0);
        } else {
            response.setFirstPublication(firstPublication.toLocalDateTime().atZone(ZoneId.systemDefault()).toEpochSecond());
        }

        return response;
    }
}
