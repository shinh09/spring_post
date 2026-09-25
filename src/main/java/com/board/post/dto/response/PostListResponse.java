package com.board.post.dto.response;

import java.time.LocalDateTime;

public record PostListResponse(
        Long id,
        String title,
        String authorNickname,
        long commentCount,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}