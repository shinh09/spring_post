package com.board.comment.repository;

import com.board.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            SELECT comment
            FROM Comment comment
            JOIN FETCH comment.author
            WHERE comment.post.id = :postId
            ORDER BY comment.createdAt ASC
            """)
    List<Comment> findAllWithAuthorByPostId(
            @Param("postId") Long postId
    );

    @Query("""
            SELECT comment
            FROM Comment comment
            JOIN FETCH comment.author
            JOIN FETCH comment.post
            WHERE comment.id = :commentId
            """)
    Optional<Comment> findWithAuthorAndPostById(
            @Param("commentId") Long commentId
    );
}