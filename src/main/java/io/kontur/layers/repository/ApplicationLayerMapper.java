package io.kontur.layers.repository;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.dto.ApplicationLayerDto;
import io.kontur.layers.repository.model.ApplicationLayer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface ApplicationLayerMapper {

    List<ApplicationLayer> getApplicationLayersRules(@Param("appId") UUID appId);

    List<ApplicationLayer> upsertLayers(@Param("appId") UUID appId,
                                        @Param("layers") List<ApplicationLayerDto> layers);

    Optional<ObjectNode> updateStyleRule(@Param("appId") UUID appId,
                                         @Param("layerId") String layerId,
                                         @Param("styleRule") ObjectNode styleRule);
}
