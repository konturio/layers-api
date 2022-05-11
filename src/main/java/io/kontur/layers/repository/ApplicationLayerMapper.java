package io.kontur.layers.repository;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.dto.ApplicationLayerDto;
import io.kontur.layers.repository.model.ApplicationLayer;
import io.micrometer.core.annotation.Timed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface ApplicationLayerMapper {

    @Timed("db.queries.appLayers.byId")
    List<ApplicationLayer> getApplicationLayersRules(@Param("appId") UUID appId);

    @Timed("db.queries.appLayers.upsert")
    List<ApplicationLayer> upsertLayers(@Param("appId") UUID appId,
                                        @Param("layers") List<ApplicationLayerDto> layers);

    @Timed("db.queries.appLayers.delete")
    void deleteAppLayersNotInList(@Param("appId") UUID appId,
                                  @Param("layerIds") List<String> layerIds);

    @Timed("db.queries.appLayers.update")
    Optional<ApplicationLayer> updateStyleAndDisplayRules(@Param("appId") UUID appId,
                                                    @Param("layerId") String layerId,
                                                    @Param("styleRule") ObjectNode styleRule,
                                                    @Param("displayRule") ObjectNode displayRule);
}
