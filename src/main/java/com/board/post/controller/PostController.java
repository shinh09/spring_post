package com.board.post.controller;

import com.board.common.dto.PageResponse;
import com.board.post.dto.request.PostCreateRequest;
import com.board.post.dto.request.PostUpdateRequest;
import com.board.post.dto.response.PostListResponse;
import com.board.post.dto.response.PostResponse;
import com.board.post.service.PostService;
import com.board.security.MemberPrincipal;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Validated
@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<PostResponse> create(
            @AuthenticationPrincipal MemberPrincipal principal,
            @Valid @RequestBody PostCreateRequest request
    ) {
        PostResponse response = postService.create(
                principal.memberId(),
                request
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{postId}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<PostListResponse>> findAll(
            @RequestParam(defaultValue = "0")
            @Min(
                    value = 0,
                    message = "페이지 번호는 0 이상이어야 합니다."
            )
            int page,

            @RequestParam(defaultValue = "10")
            @Min(
                    value = 1,
                    message = "페이지 크기는 1 이상이어야 합니다."
            )
            @Max(
                    value = 100,
                    message = "페이지 크기는 100 이하여야 합니다."
            )
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                postService.findAll(pageable)
        );
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> findById(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
                postService.findById(postId)
        );
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponse> update(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody PostUpdateRequest request
    ) {
        return ResponseEntity.ok(
                postService.update(
                        principal.memberId(),
                        postId,
                        request
                )
        );
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long postId
    ) {
        postService.delete(
                principal.memberId(),
                postId
        );

        return ResponseEntity.noContent().build();
    }
}