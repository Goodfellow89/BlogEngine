package main.response.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostInPostResponse {

    private int id;
    private long timestamp;
    private Boolean active;
    private UserResponse user;
    private String title;
    private String announce;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private List<CommentsResponse> comments;
    private List<String> tags;
}