package main.repository;

import main.model.Post;
import main.response.api.PostInCalendarResponse;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface PostsRepository extends CrudRepository<Post, Integer> {

    String selectAccepted = "select * from posts where is_active = 1 and moderation_status = 'ACCEPTED' and time <= now()";
    String offsetAndLimit = "limit :limit offset :offset";
    String countAccepted = "select count(*) from posts where is_active = 1 and moderation_status = 'ACCEPTED' and time <= now()";

    @Query(value = "select * from posts where moderation_status = 'NEW' and is_active = 1 and user_id != :user_id " + offsetAndLimit, nativeQuery = true)
    List<Post> findAllNewPosts(@Param("limit") int limit, @Param("offset") int offset, @Param("user_id") int userId);

    @Query(value = "select count(*) from posts where moderation_status = 'NEW' and is_active = 1 and user_id != :user_id", nativeQuery = true)
    int countAllNewPosts(@Param("user_id") int userId);

    @Query(value = countAccepted, nativeQuery = true)
    int countAllAcceptedPosts();

    @Query(value = selectAccepted + " order by time desc " + offsetAndLimit, nativeQuery = true)
    List<Post> findAllAcceptedPostsOrderedByDateDesc(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "select posts.* from posts, post_comments where posts.is_active = 1 and posts.moderation_status = 'ACCEPTED' and posts.time <= now() group by posts.id order by (select count(*) from post_comments where post_comments.post_id = posts.id group by posts.id) desc, posts.title " + offsetAndLimit, nativeQuery = true)
    List<Post> findAllAcceptedPostsOrderedByCommentsCount(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = "select posts.* from posts, post_votes where posts.is_active = 1 and posts.moderation_status = 'ACCEPTED' and posts.time <= now() group by posts.id order by (select sum(value) from post_votes where post_votes.post_id = posts.id group by posts.id) desc, (select sum(value) from post_votes where post_votes.post_id = posts.id and value = 1 group by posts.id) desc, posts.title " + offsetAndLimit, nativeQuery = true)
    List<Post> findAllAcceptedPostsOrderedByLikes(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = selectAccepted + " order by time " + offsetAndLimit, nativeQuery = true)
    List<Post> findAllAcceptedPostsOrderedByDate(@Param("limit") int limit, @Param("offset") int offset);

    @Query(value = selectAccepted + " and (text like %:word% or title like %:word%) " + offsetAndLimit, nativeQuery = true)
    List<Post> findAllAcceptedPostBySearch(@Param("limit") int limit, @Param("offset") int offset, @Param("word") String word);

    @Query(value = countAccepted + " and (text like %:word% or title like %:word%)", nativeQuery = true)
    int countAllAcceptedPostsBySearch(@Param("word") String word);

    @Query(value = "select distinct year(time) from posts where is_active = 1 and moderation_status = 'ACCEPTED' and time <= now() order by time", nativeQuery = true)
    List<Integer> findAllPostYears();

    @Query(value = "select date(time) as date, count(*) as postCount from posts where is_active = 1 and moderation_status = 'ACCEPTED' and time <= now() and year(time) = :year group by date", nativeQuery = true)
    List<PostInCalendarResponse> findAllPostsOfCurrentYearByTime(@Param("year") int year);

    @Query(value = selectAccepted + " and date(time) = :date " + offsetAndLimit, nativeQuery = true)
    List<Post> findAllAcceptedPostsByDate(@Param("limit") int limit, @Param("offset") int offset, @Param("date") LocalDate date);

    @Query(value = countAccepted + " and date(time) = :date", nativeQuery = true)
    int countAllAcceptedPostsByDate(@Param("date") LocalDate date);

    @Query(value = "select posts.* from posts,tag2post, tags where posts.is_active = 1 and posts.moderation_status = 'ACCEPTED' and posts.time <= now() and tags.name = :tag and tags.id = tag2post.tag_id and tag2post.post_id = posts.id " + offsetAndLimit, nativeQuery = true)
    List<Post> findAllAcceptedPostsByTag(@Param("limit") int limit, @Param("offset") int offset, @Param("tag") String tag);

    @Query(value = "select count(*) from posts,tag2post, tags where posts.is_active = 1 and posts.moderation_status = 'ACCEPTED' and posts.time <= now() and tags.name = :tag and tags.id = tag2post.tag_id and tag2post.post_id = posts.id", nativeQuery = true)
    int countAllAcceptedPostsByTag(@Param("tag") String tag);

    @Query(value = selectAccepted + " and id = :id", nativeQuery = true)
    Post findAcceptedPostById(@Param("id") int id);

    @Transactional
    @Modifying
    @Query(value = "update posts set view_count = view_count + 1 where id = :id", nativeQuery = true)
    void updateViewCountInPost(@Param("id") int id);

    @Query(value = "select count(*) from posts where user_id = :user_id and is_active = 0", nativeQuery = true)
    int countAllMyInactivePosts(@Param("user_id") int userId);

    @Query(value = "select count(*) from posts where user_id = :user_id and is_active = 1 and moderation_status = :status", nativeQuery = true)
    int countAllMyActivePosts(@Param("user_id") int userId, @Param("status") String modStatus);

    @Query(value = "select * from posts where user_id = :user_id and is_active = 0 " + offsetAndLimit, nativeQuery = true)
    List<Post> findMyInactivePosts(@Param("limit") int limit, @Param("offset") int offset, @Param("user_id") int userId);

    @Query(value = "select * from posts where user_id = :user_id and is_active = 1 and moderation_status = :status " + offsetAndLimit, nativeQuery = true)
    List<Post> findMyActivePosts(@Param("limit") int limit, @Param("offset") int offset, @Param("user_id") int userId, @Param("status") String modStatus);

    @Query(value = "select sum(view_count) from posts where user_id = :user_id", nativeQuery = true)
    int countAllViewsOfMyPosts(@Param("user_id") int userId);

    @Query(value = "select min(time) from posts where user_id = :user_id and is_active = 1 and moderation_status = 'ACCEPTED'", nativeQuery = true)
    Timestamp findTimeOfFirstMyPost(@Param("user_id") int userId);

    @Query(value = "select sum(view_count) from posts", nativeQuery = true)
    int countAllViews();

    @Query(value = "select min(time) from posts where is_active = 1 and moderation_status = 'ACCEPTED'", nativeQuery = true)
    Timestamp findTimeOfFirstPost();

    @Query(value = "select * from posts where moderator_id = :user_id and is_active = 1 and moderation_status = :status " + offsetAndLimit, nativeQuery = true)
    List<Post> findActivePostsWithModeration(@Param("limit") int limit, @Param("offset") int offset, @Param("user_id") int userId, @Param("status") String modStatus);

    @Query(value = "select count(*) from posts where moderator_id = :user_id and is_active = 1 and moderation_status = :status", nativeQuery = true)
    int countActivePostsWithModeration(@Param("user_id") int userId, @Param("status") String modStatus);
}