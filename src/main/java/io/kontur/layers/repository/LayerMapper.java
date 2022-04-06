package io.kontur.layers.repository;

import io.kontur.layers.dto.CollectionOwner;
import io.kontur.layers.repository.model.Layer;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface LayerMapper {

    Optional<Layer> getOwnedLayer(String publicId, String userName);

    default Optional<Layer> getLayer(String publicId, String userName) {
        return getLayer(publicId, userName, null, true);
    }

    default Optional<Layer> getLayer(String publicId, String userName, UUID appId, boolean showAllPublic) {
        return getLayers(null, userName,1, 0, CollectionOwner.ANY, appId, showAllPublic, publicId).stream().findFirst();
    }

    default int getLayersTotal(@Param("geometry") String geometry,
                           @Param("userName") String userName,
                           @Param("collectionOwner") CollectionOwner collectionOwner,
                           @Param("publicIds") String... publicIds) {
        return getLayersTotal(geometry, userName, collectionOwner, null, true, publicIds);
    }

    int getLayersTotal(@Param("geometry") String geometry,
                           @Param("userName") String userName,
                           @Param("collectionOwner") CollectionOwner collectionOwner,
                           @Param("appId") UUID appId,
                           @Param("showAllPublic") boolean showAllPublic,
                           @Param("publicIds") String... publicIds);

    default List<Layer> getLayers(@Param("geometry") String geometry,
                          @Param("userName") String userName,
                          @Param("limit") Integer limit,
                          @Param("offset") Integer offset,
                          @Param("collectionOwner") CollectionOwner collectionOwner,
                          @Param("publicIds") String... publicIds) {
        return getLayers(geometry, userName, limit, offset, collectionOwner, null, true, publicIds);
    }

    List<Layer> getLayers(@Param("geometry") String geometry,
                          @Param("userName") String userName,
                          @Param("limit") Integer limit,
                          @Param("offset") Integer offset,
                          @Param("collectionOwner") CollectionOwner collectionOwner,
                          @Param("appId") UUID appId,
                          @Param("showAllPublic") boolean showAllPublic,
                          @Param("publicIds") String... publicIds);

    Layer insertLayer(Layer layer);

    Layer updateLayer(Layer layer);

    Layer deleteLayer(@Param("owner") String owner,
                      @Param("id") String id);

    List<Layer> getApplicationLayers(@Param("appId") UUID appId,
                                     @Param("getDefaultOnly") boolean getDefaultOnly);
}
