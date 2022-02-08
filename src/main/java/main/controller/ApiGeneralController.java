package main.controller;

import jdk.jfr.ContentType;
import lombok.RequiredArgsConstructor;
import main.request.CommentRequest;
import main.request.ModerationRequest;
import main.request.ProfileRequest;
import main.response.api.*;
import main.service.*;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartResolver;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Map;

@RestController
@RequestMapping("/api/")
@RequiredArgsConstructor
public class ApiGeneralController {

    private final InitResponse initResponse;
    private final SettingsService settingsService;
    private final TagsService tagsService;
    private final CalendarService calendarService;
    private final ImageService imageService;
    private final CommentService commentService;
    private final StatisticService statisticService;
    private final ProfileService profileService;
    private final EditPostService editPostService;

    @GetMapping("/init")
    public InitResponse init() {
        return initResponse;
    }

    @GetMapping("/settings")
    public SettingsResponse settings() {
        return settingsService.getSettingsResponse();
    }

    @GetMapping("/tag")
    public TagResponse tag(String query) {
        return tagsService.getTagResponse(query);
    }

    @GetMapping("/calendar")
    public CalendarResponse calendar(Integer year) {
        return calendarService.getCalendarResponse(year);
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/image")
    public ResponseEntity<?> addImage(@RequestBody MultipartFile image) {
        EditResponse response = imageService.addImage(image);
        if (response.isResult()) {
            return new ResponseEntity<>(response.getErrors().get("path"), HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping("/comment")
    public ResponseEntity<?> addComment(@Valid @RequestBody CommentRequest request) {
        PostCommentResponse response = commentService.addComment(request);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        if (response.getId() != 0) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/statistics/my")
    public StatisticResponse myStatistics() {
        return statisticService.getMy();
    }

    @GetMapping("/statistics/all")
    public StatisticResponse statistics() {
        return statisticService.getAll();
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping(value = "/profile/my", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EditResponse editMyProfile(ProfileRequest request, @RequestPart MultipartFile photo) {
        return profileService.editMyProfile(request, photo);
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping(value = "/profile/my", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EditResponse editMyProfileWithoutImage(@RequestBody ProfileRequest request) {
        return profileService.editMyProfile(request, null);
    }

    @PreAuthorize("hasAuthority('moderator')")
    @PutMapping("/settings")
    public void editSettings(@RequestBody Map<String, Boolean> request){
        settingsService.editSettings(request);
    }

    @PreAuthorize("hasAuthority('moderator')")
    @PostMapping("/moderation")
    public EditResponse moderate(@RequestBody ModerationRequest request) {
        return editPostService.moderatePost(request);
    }
}