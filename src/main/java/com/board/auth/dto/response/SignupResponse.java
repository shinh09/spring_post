package com.board.auth.dto.response;

import com.board.member.entity.Member;

import java.time.LocalDateTime;

public record SignupResponse(
        Long id,
        String email,
        String nickname,
        LocalDateTime createdAt
) {

    public static SignupResponse from(Member member) {
        return new SignupResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getCreatedAt()
        );
    }
}