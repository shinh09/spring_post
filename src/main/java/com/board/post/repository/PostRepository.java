package com.board.post.repository;

import com.board.post.dto.response.PostListResponse;
import com.board.post.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query(
            value = """
                    SELECT new com.board.post.dto.response.PostListResponse(
                        p.id,
                        p.title,
                        author.nickname,
                        COUNT(comment.id),
                        p.createdAt,
                        p.updatedAt
                    )
                    FROM Post p
                    JOIN p.author author
                    LEFT JOIN p.comments comment
                    GROUP BY
                        p.id,
                        p.title,
                        author.nickname,
                        p.createdAt,
                        p.updatedAt
                    ORDER BY p.createdAt DESC
                    """,
            countQuery = """
                    SELECT COUNT(p)
                    FROM Post p
                    """
    )
    Page<PostListResponse> findAllForList(Pageable pageable);

    @EntityGraph(attributePaths = "author")
    @Query("""
            SELECT p
            FROM Post p
            WHERE p.id = :postId
            """)
    Optional<Post> findWithAuthorById(
            @Param("postId") Long postId
    );
}