package main.response.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_ABSENT)
@NoArgsConstructor
public class AuthCheckResponse {

    public AuthCheckResponse(boolean result) {
        this.result = result;
    }

    private boolean result;
    private UserResponse user;
}
