package io.kontur.layers.repository;

import io.kontur.layers.repository.model.Layer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface LayerMapper {

    Optional<Layer> getOwnedLayer(String publicId, String userName);

    default Optional<Layer> getLayer(String publicId, String userName) {
        return getLayers(null, userName,1, 0, false, publicId).stream().findFirst();
    }

    Integer getLayersTotal(@Param("geometry") String geometry,
                           @Param("userName") String userName,
                           @Param("ownedByUser") Boolean ownedByUser,
                           @Param("publicIds") String... publicIds);

    List<Layer> getLayers(@Param("geometry") String geometry,
                          @Param("userName") String userName,
                          @Param("limit") Integer limit,
                          @Param("offset") Integer offset,
                          @Param("ownedByUser") Boolean ownedByUser,
                          @Param("publicIds") String... publicIds);

    Layer insertLayer(Layer layer);

    Layer updateLayer(Layer layer);

    Layer deleteLayer(@Param("owner") String owner,
                      @Param("id") String id);
}
