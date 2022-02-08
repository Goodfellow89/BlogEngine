package main.response.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class PostCommentResponse {

    private boolean result;
    private ConcurrentHashMap<String, String> errors;
    private int id;
}
