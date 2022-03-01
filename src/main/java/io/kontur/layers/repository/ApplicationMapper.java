package io.kontur.layers.repository;

import io.kontur.layers.repository.model.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;
import java.util.UUID;

@Mapper
public interface ApplicationMapper {

    Optional<Application> getApplication(@Param("appId") UUID appId, @Param("owner") String owner);

    Application insertApplication(Application application);

    Application updateApplication(Application application);

    Application deleteApplication(@Param("appId") UUID appId, @Param("owner") String owner);
}
