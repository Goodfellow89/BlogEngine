package main.repository;

import main.model.PostComment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostCommentsRepository extends CrudRepository<PostComment, Integer> {

    @Query(value = "select count(*) from post_comments where post_id = :post_id", nativeQuery = true)
    int commentsCount(@Param("post_id") int postId);

    @Query(value = "select * from post_comments where post_id = :post_id", nativeQuery = true)
    List<PostComment> findCommentsByPostId(@Param("post_id") int postId);

    @Query(value = "select * from post_comments where text = :text", nativeQuery = true)
    PostComment findCommentByText(@Param("text") String text);
}
