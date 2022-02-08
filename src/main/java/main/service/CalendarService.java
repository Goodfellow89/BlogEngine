package main.service;

import lombok.RequiredArgsConstructor;
import main.response.api.CalendarResponse;
import main.repository.PostsRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CalendarService {

    private final PostsRepository postsRepository;

    public CalendarResponse getCalendarResponse(Integer year) {

        CalendarResponse calendarResponse = new CalendarResponse();
        int currentYear;

        List<Integer> years = postsRepository.findAllPostYears();
        calendarResponse.setYears(years);

        ConcurrentHashMap<LocalDate, Integer> posts = new ConcurrentHashMap<>();

        if (year == null || !years.contains(year)) {
            currentYear = ZonedDateTime.now().getYear();
        } else {
            currentYear = year;
        }

        postsRepository.findAllPostsOfCurrentYearByTime(currentYear).forEach(t -> posts.put(t.getDate(), t.getPostCount()));

        calendarResponse.setPosts(posts);
        return calendarResponse;
    }
}
