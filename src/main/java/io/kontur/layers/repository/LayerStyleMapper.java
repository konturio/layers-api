package io.kontur.layers.repository;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.kontur.layers.repository.model.LayerStyle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Optional;

@Mapper
public interface LayerStyleMapper {

    Optional<LayerStyle> getLayerStyle(@Param("layerId") Long layerId);

    ObjectNode insertLayerStyle(@Param("layerId") Long layerId,
                           @Param("style") ObjectNode style);

    ObjectNode updateLayerStyle(@Param("layerStyleId") Long layerStyleId,
                                @Param("style") ObjectNode style);

}
