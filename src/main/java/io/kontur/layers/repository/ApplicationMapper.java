package io.kontur.layers.repository;

import io.kontur.layers.repository.model.Application;
import io.micrometer.core.annotation.Timed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.UUID;

@Mapper
public interface ApplicationMapper {

    @Timed("db.queries.apps.byId")
    Optional<Application> getApplication(@Param("appId") UUID appId);

    @Timed("db.queries.apps.byId.public")
    Optional<Application> getApplicationOwnedOrPublic(@Param("appId") UUID appId, @Param("owner") String owner);

    @Timed("db.queries.apps.insert")
    Application insertApplication(Application application);

    @Timed("db.queries.apps.update")
    Application updateApplication(Application application);

    @Timed("db.queries.apps.delete")
    Application deleteApplication(@Param("appId") UUID appId, @Param("owner") String owner);
}
