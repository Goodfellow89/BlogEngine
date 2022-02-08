package main.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CaptchaRepository extends CrudRepository<CaptchaCode, Integer> {

    @Query(value = "select * from captcha_codes where secret_code = :secret_code", nativeQuery = true)
    CaptchaCode findCaptchaBySecretCode(@Param("secret_code") String secretCaptcha);

    @Transactional
    @Modifying
    @Query(value = "delete from captcha_codes where time <= adddate(now(), interval - :minutes minute)", nativeQuery = true)
    void deleteOldCaptcha(@Param("minutes") int minutes);
}
