package main.repository;

import main.model.TagToPost;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface TagToPostRepository extends CrudRepository<TagToPost, Integer> {

    @Query(value = "select * from tag2post where tag_id = :tag_id", nativeQuery = true)
    List<TagToPost> findAllTagToPostsByTagId(@Param("tag_id") int tagId);

    @Transactional
    @Modifying
    @Query(value = "delete from tag2post where post_id = :post_id", nativeQuery = true)
    void deleteAllTagToPostsByPostId(@Param("post_id") int postId);
}
