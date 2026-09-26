package com.board.comment.controller;

import com.board.comment.dto.request.CommentCreateRequest;
import com.board.comment.dto.request.CommentUpdateRequest;
import com.board.comment.dto.response.CommentResponse;
import com.board.comment.service.CommentService;
import com.board.security.MemberPrincipal;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(
            CommentService commentService
    ) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> create(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request
    ) {
        CommentResponse response = commentService.create(
                principal.memberId(),
                postId,
                request
        );

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/api/comments/{commentId}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity
                .created(location)
                .body(response);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> findAllByPostId(
            @PathVariable Long postId
    ) {
        return ResponseEntity.ok(
                commentService.findAllByPostId(postId)
        );
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponse> update(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long commentId,
            @Valid @RequestBody CommentUpdateRequest request
    ) {
        return ResponseEntity.ok(
                commentService.update(
                        principal.memberId(),
                        commentId,
                        request
                )
        );
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> delete(
            @AuthenticationPrincipal MemberPrincipal principal,
            @PathVariable Long commentId
    ) {
        commentService.delete(
                principal.memberId(),
                commentId
        );

        return ResponseEntity.noContent().build();
    }
}