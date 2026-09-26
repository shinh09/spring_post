package com.board.post.service;

import com.board.common.dto.PageResponse;
import com.board.common.error.BusinessException;
import com.board.common.error.ErrorCode;
import com.board.member.entity.Member;
import com.board.member.repository.MemberRepository;
import com.board.post.dto.request.PostCreateRequest;
import com.board.post.dto.request.PostUpdateRequest;
import com.board.post.dto.response.PostListResponse;
import com.board.post.dto.response.PostResponse;
import com.board.post.entity.Post;
import com.board.post.repository.PostRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public PostService(
            PostRepository postRepository,
            MemberRepository memberRepository
    ) {
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public PostResponse create(
            Long memberId,
            PostCreateRequest request
    ) {
        Member author = getMember(memberId);

        Post post = new Post(
                request.title().trim(),
                request.content().trim(),
                author
        );

        return PostResponse.from(
                postRepository.save(post)
        );
    }

    public PageResponse<PostListResponse> findAll(
            Pageable pageable
    ) {
        Page<PostListResponse> postPage =
                postRepository.findAllForList(pageable);

        return PageResponse.from(postPage);
    }

    public PostResponse findById(Long postId) {
        return PostResponse.from(
                getPostWithAuthor(postId)
        );
    }

    @Transactional
    public PostResponse update(
            Long memberId,
            Long postId,
            PostUpdateRequest request
    ) {
        Post post = getPostWithAuthor(postId);

        validateAuthor(post, memberId);

        post.update(
                request.title().trim(),
                request.content().trim()
        );

        return PostResponse.from(post);
    }

    @Transactional
    public void delete(
            Long memberId,
            Long postId
    ) {
        Post post = getPostWithAuthor(postId);

        validateAuthor(post, memberId);

        postRepository.delete(post);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.MEMBER_NOT_FOUND
                        )
                );
    }

    private Post getPostWithAuthor(Long postId) {
        return postRepository.findWithAuthorById(postId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.POST_NOT_FOUND
                        )
                );
    }

    private void validateAuthor(
            Post post,
            Long memberId
    ) {
        if (!post.isWrittenBy(memberId)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN
            );
        }
    }
}