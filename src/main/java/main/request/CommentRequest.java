package main.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class CommentRequest {

    @Nullable
    @JsonProperty("parent_id")
    private Integer parentId;

    @NotNull
    @JsonProperty("post_id")
    private Integer postId;

    private String text;
}
