package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends CrudRepository<User, Integer> {

    @Query(value = "select count(*) from users where email = :email", nativeQuery = true)
    int emailCount(@Param("email") String email);

    @Query(value = "select * from users where email = :email", nativeQuery = true)
    User findByEmail(@Param("email") String email);

    @Query(value = "select * from users where code = :code", nativeQuery = true)
    User findByCode(@Param("code") String code);
}
