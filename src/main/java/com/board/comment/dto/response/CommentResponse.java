package com.board.comment.dto.response;

import com.board.comment.entity.Comment;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        Long authorId,
        String authorNickname,
        Long postId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getAuthor().getId(),
                comment.getAuthor().getNickname(),
                comment.getPost().getId(),
                comment.getCreatedAt(),
                comment.getUpdatedAt()
        );
    }
}