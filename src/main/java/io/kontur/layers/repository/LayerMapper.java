package io.kontur.layers.repository;

import io.kontur.layers.repository.model.Layer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface LayerMapper {
    Optional<String> getLayerName(@Param("publicId") String publicId);

    default Optional<Layer> getLayer(String publicId) {
        return getLayers(1, 0, publicId).stream().findFirst();
    }

    List<Layer> getLayers(@Param("limit") Integer limit,
                          @Param("offset") Integer offset,
                          @Param("publicIds") String... publicIds);
}
