package main.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagsRepository extends CrudRepository<Tag, Integer> {

    @Query(value = "select * from tags where name = :tag", nativeQuery = true)
    Tag findTagByName(@Param("tag") String tag);

    @Query(value = "select tags.name from tags, tag2post where tags.id = tag2post.tag_id and tag2post.post_id = :id", nativeQuery = true)
    List<String> findTagNamesByPostId(@Param("id") int id);
}
