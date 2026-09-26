package com.board.auth.service;

import com.board.auth.dto.request.LoginRequest;
import com.board.auth.dto.request.SignupRequest;
import com.board.auth.dto.response.LoginResponse;
import com.board.auth.dto.response.SignupResponse;
import com.board.common.error.BusinessException;
import com.board.common.error.ErrorCode;
import com.board.member.entity.Member;
import com.board.member.repository.MemberRepository;
import com.board.security.jwt.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@Transactional(readOnly = true)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(
            MemberRepository memberRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        String normalizedEmail = normalizeEmail(request.email());
        String normalizedNickname = request.nickname().trim();

        validateEmailDuplicate(normalizedEmail);
        validateNicknameDuplicate(normalizedNickname);

        String encodedPassword = passwordEncoder.encode(
                request.password()
        );

        Member member = new Member(
                normalizedEmail,
                encodedPassword,
                normalizedNickname
        );

        Member savedMember = memberRepository.save(member);

        return SignupResponse.from(savedMember);
    }

    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = normalizeEmail(request.email());

        Member member = memberRepository.findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.LOGIN_FAILED
                        )
                );

        if (!passwordEncoder.matches(
                request.password(),
                member.getPassword()
        )) {
            throw new BusinessException(
                    ErrorCode.LOGIN_FAILED
            );
        }

        String accessToken = jwtTokenProvider.createToken(
                member.getId()
        );

        return LoginResponse.of(
                accessToken,
                jwtTokenProvider.getExpirationSeconds()
        );
    }

    private void validateEmailDuplicate(String email) {
        if (memberRepository.existsByEmail(email)) {
            throw new BusinessException(
                    ErrorCode.EMAIL_ALREADY_EXISTS
            );
        }
    }

    private void validateNicknameDuplicate(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new BusinessException(
                    ErrorCode.NICKNAME_ALREADY_EXISTS
            );
        }
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}