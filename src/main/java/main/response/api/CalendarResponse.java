package main.response.api;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public class CalendarResponse {
    private List<Integer> years;
    private ConcurrentHashMap<LocalDate, Integer> posts;
}