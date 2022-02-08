package main.repository;

import main.model.GlobalSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SettingsRepository extends CrudRepository<GlobalSetting, Integer> {

    @Query(value = "select * from global_settings where code = :code", nativeQuery = true)
    GlobalSetting getSetting(@Param("code") String code);
}
