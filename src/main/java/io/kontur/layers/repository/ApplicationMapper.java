package io.kontur.layers.repository;

import io.kontur.layers.repository.model.Application;
import io.micrometer.core.annotation.Timed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.UUID;

@Mapper
@Timed(value = "db.query", histogram = true)
public interface ApplicationMapper {

    @Timed(value = "db.query", histogram = true)
    Optional<Application> getApplication(@Param("appId") UUID appId);

    @Timed(value = "db.query", histogram = true)
    Optional<Application> getApplicationOwnedOrPublic(@Param("appId") UUID appId, @Param("owner") String owner);

    @Timed(value = "db.query", histogram = true)
    Application insertApplication(Application application);

    @Timed(value = "db.query", histogram = true)
    Application updateApplication(Application application);

    @Timed(value = "db.query", histogram = true)
    Application deleteApplication(@Param("appId") UUID appId, @Param("owner") String owner);
}
