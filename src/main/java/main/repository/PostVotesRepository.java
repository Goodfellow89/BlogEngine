package main.repository;

import main.model.PostVote;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVote, Integer> {

    @Query(value = "select count(*) from post_votes where value = :value and post_id = :post_id", nativeQuery = true)
    int votesCount(@Param("value") int value, @Param("post_id") int postId);

    @Query(value = "select * from post_votes where user_id = :user_id and post_id = :post_id", nativeQuery = true)
    PostVote findCurrentVote(@Param("user_id") int userId, @Param("post_id") int postId);

    @Query(value = "select count(*) from post_votes, posts where post_votes.value = :value and post_votes.post_id = posts.id and posts.user_id = :user_id", nativeQuery = true)
    int countAllVotesOfMyPosts(@Param("value") int value, @Param("user_id") int userId);

    @Query(value = "select count(*) from post_votes where value = :value", nativeQuery = true)
    int allVotesCount(@Param("value") int value);

    @Transactional
    @Modifying
    @Query(value = "delete from post_votes where post_id = :post_id", nativeQuery = true)
    void deleteVotesOfPost(@Param("post_id") int postId);
}