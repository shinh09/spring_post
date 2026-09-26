package com.board.comment.service;

import com.board.comment.dto.request.CommentCreateRequest;
import com.board.comment.dto.request.CommentUpdateRequest;
import com.board.comment.dto.response.CommentResponse;
import com.board.comment.entity.Comment;
import com.board.comment.repository.CommentRepository;
import com.board.common.error.BusinessException;
import com.board.common.error.ErrorCode;
import com.board.member.entity.Member;
import com.board.member.repository.MemberRepository;
import com.board.post.entity.Post;
import com.board.post.repository.PostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    public CommentService(
            CommentRepository commentRepository,
            PostRepository postRepository,
            MemberRepository memberRepository
    ) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public CommentResponse create(
            Long memberId,
            Long postId,
            CommentCreateRequest request
    ) {
        Member author = getMember(memberId);
        Post post = getPost(postId);

        Comment comment = new Comment(
                request.content().trim(),
                author,
                post
        );

        return CommentResponse.from(
                commentRepository.save(comment)
        );
    }

    public List<CommentResponse> findAllByPostId(
            Long postId
    ) {
        validatePostExists(postId);

        return commentRepository
                .findAllWithAuthorByPostId(postId)
                .stream()
                .map(CommentResponse::from)
                .toList();
    }

    @Transactional
    public CommentResponse update(
            Long memberId,
            Long commentId,
            CommentUpdateRequest request
    ) {
        Comment comment = getComment(commentId);

        validateAuthor(comment, memberId);

        comment.update(request.content().trim());

        return CommentResponse.from(comment);
    }

    @Transactional
    public void delete(
            Long memberId,
            Long commentId
    ) {
        Comment comment = getComment(commentId);

        validateAuthor(comment, memberId);

        commentRepository.delete(comment);
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.MEMBER_NOT_FOUND
                        )
                );
    }

    private Post getPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.POST_NOT_FOUND
                        )
                );
    }

    private Comment getComment(Long commentId) {
        return commentRepository
                .findWithAuthorAndPostById(commentId)
                .orElseThrow(() ->
                        new BusinessException(
                                ErrorCode.COMMENT_NOT_FOUND
                        )
                );
    }

    private void validatePostExists(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new BusinessException(
                    ErrorCode.POST_NOT_FOUND
            );
        }
    }

    private void validateAuthor(
            Comment comment,
            Long memberId
    ) {
        if (!comment.isWrittenBy(memberId)) {
            throw new BusinessException(
                    ErrorCode.FORBIDDEN
            );
        }
    }
}