package io.kontur.layers.repository;

import io.kontur.layers.dto.ApplicationLayerDto;
import io.kontur.layers.repository.model.ApplicationLayer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ApplicationLayerMapper {

    List<ApplicationLayer> getApplicationLayersRules(@Param("appId") UUID appId);

    List<ApplicationLayer> upsertLayers(@Param("appId") UUID appId,
                                        @Param("layers") List<ApplicationLayerDto> layers);
}
