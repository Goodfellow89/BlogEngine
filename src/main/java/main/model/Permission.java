package main.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Permission {
    USER("user"),
    MODERATOR("moderator");

    private final String permission;
}
