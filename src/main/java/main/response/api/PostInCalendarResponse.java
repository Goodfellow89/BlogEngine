package main.response.api;

import java.time.LocalDate;

public interface PostInCalendarResponse {

    LocalDate getDate();
    Integer getPostCount();
}
