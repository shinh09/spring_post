package com.board.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommentCreateRequest(

        @NotBlank(message = "댓글 본문은 필수입니다.")
        @Size(
                max = 1000,
                message = "댓글 본문은 1000자 이하여야 합니다."
        )
        String content
) {
}