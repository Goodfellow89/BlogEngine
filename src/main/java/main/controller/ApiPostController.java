package main.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.RequiredArgsConstructor;
import main.request.PostRequest;
import main.response.api.EditResponse;
import main.response.api.PostInPostResponse;
import main.response.api.PostResponse;
import main.service.EditPostService;
import main.service.GetPostService;
import main.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/post")
@RequiredArgsConstructor
public class ApiPostController {

    private final GetPostService getPostService;
    private final EditPostService editPostService;
    private final VoteService voteService;

    @GetMapping
    public ResponseEntity<PostResponse> post(int offset, int limit, String mode) {
        return new ResponseEntity<>(getPostService.getPostResponse(offset, limit, mode), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PostResponse> postSearch(int offset, int limit, String query) {
        return new ResponseEntity<>(getPostService.getPostSearchResponse(offset, limit, query), HttpStatus.OK);
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostResponse> postByDate(int offset, int limit, String date) {
        return new ResponseEntity<>(getPostService.getPostByDateResponse(offset, limit, date), HttpStatus.OK);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostResponse> postByTag(int offset, int limit, String tag) {
        return new ResponseEntity<>(getPostService.getPostByTagResponse(offset, limit, tag), HttpStatus.OK);
    }

    @GetMapping("/{ID}")
    public ResponseEntity<PostInPostResponse> postById(@PathVariable("ID") int id) {
        PostInPostResponse postByIdResponse = getPostService.getPostByIdResponse(id);
        if (postByIdResponse == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity<>(postByIdResponse, HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @GetMapping("/my")
    public ResponseEntity<PostResponse> postMy(int offset, int limit, String status) {
        return new ResponseEntity<>(getPostService.getMyPosts(offset, limit, status), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @PostMapping
    public ResponseEntity<EditResponse> addPost(@RequestBody PostRequest request) {
        return new ResponseEntity<>(editPostService.addPost(request), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('user')")
    @PutMapping("/{ID}")
    public ResponseEntity<EditResponse> editPost(@PathVariable("ID") int id, @RequestBody PostRequest request) {
        EditResponse response = editPostService.editPost(id, request);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PreAuthorize(("hasAuthority('user')"))
    @PostMapping("/like")
    public EditResponse like(@RequestBody Map<String, Integer> id) {
        return voteService.vote(id.get("post_id"), 1);
    }

    @PreAuthorize(("hasAuthority('user')"))
    @PostMapping("/dislike")
    public EditResponse dislike(@RequestBody Map<String, Integer> id) {
        return voteService.vote(id.get("post_id"), -1);
    }

    @PreAuthorize(("hasAuthority('moderator')"))
    @GetMapping("/moderation")
    public ResponseEntity<PostResponse> moderation(int offset, int limit, String status) {
        return new ResponseEntity<>(getPostService.getPostsToModeration(offset, limit, status), HttpStatus.OK);
    }
}