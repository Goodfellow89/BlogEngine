package main.response.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentsResponse {

    private int id;
    private long timestamp;
    private String text;
    private UserResponse user;
}