package main.response.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PostResponse {

    private int count;
    private List<PostInPostResponse> posts;
}