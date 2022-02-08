package main.response.api;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TagResponse {

    private List<TagInTagResponse> tags;
}
