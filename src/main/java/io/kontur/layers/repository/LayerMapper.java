package io.kontur.layers.repository;

import io.kontur.layers.dto.CollectionOwner;
import io.kontur.layers.repository.model.Layer;
import io.micrometer.core.annotation.Timed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
@Timed(value = "db.query", histogram = true)
public interface LayerMapper {

    @Timed(value = "db.query", histogram = true)
    Optional<Layer> getOwnedLayer(String publicId, String userName);

    @Timed(value = "db.query", histogram = true)
    default Optional<Layer> getLayer(String publicId, String userName) {
        return getLayer(publicId, userName, null, true);
    }

    @Timed(value = "db.query", histogram = true)
    default Optional<Layer> getLayer(String publicId, String userName, UUID appId, boolean showAllPublic) {
        return getLayers(null, false, userName, 1, 0, CollectionOwner.ANY, appId, showAllPublic, publicId).stream()
                .findFirst();
    }

    @Timed(value = "db.query", histogram = true)
    default int getLayersTotal(@Param("geometry") String geometry,
                               @Param("omitLocalCollections") boolean omitLocalCollections,
                               @Param("userName") String userName,
                               @Param("collectionOwner") CollectionOwner collectionOwner,
                               @Param("publicIds") String... publicIds) {
        return getLayersTotal(geometry, omitLocalCollections, userName, collectionOwner, null, true, publicIds);
    }

    @Timed(value = "db.query", histogram = true)
    int getLayersTotal(@Param("geometry") String geometry,
                       @Param("omitLocalCollections") boolean omitLocalCollections,
                       @Param("userName") String userName,
                       @Param("collectionOwner") CollectionOwner collectionOwner,
                       @Param("appId") UUID appId,
                       @Param("showAllPublic") boolean showAllPublic,
                       @Param("publicIds") String... publicIds);

    @Timed(value = "db.query", histogram = true)
    default List<Layer> getLayers(@Param("geometry") String geometry,
                                  @Param("omitLocalCollections") boolean omitLocalCollections,
                                  @Param("userName") String userName,
                                  @Param("limit") Integer limit,
                                  @Param("offset") Integer offset,
                                  @Param("collectionOwner") CollectionOwner collectionOwner,
                                  @Param("publicIds") String... publicIds) {
        return getLayers(geometry, omitLocalCollections, userName, limit, offset, collectionOwner, null, true, publicIds);
    }

    @Timed(value = "db.query", histogram = true)
    List<Layer> getLayers(@Param("geometry") String geometry,
                          @Param("omitLocalCollections") boolean omitLocalCollections,
                          @Param("userName") String userName,
                          @Param("limit") Integer limit,
                          @Param("offset") Integer offset,
                          @Param("collectionOwner") CollectionOwner collectionOwner,
                          @Param("appId") UUID appId,
                          @Param("showAllPublic") boolean showAllPublic,
                          @Param("publicIds") String... publicIds);

    @Timed(value = "db.query", histogram = true)
    Layer insertLayer(Layer layer);

    @Timed(value = "db.query", histogram = true)
    Layer updateLayer(Layer layer);

    @Timed(value = "db.query", histogram = true)
    Layer deleteLayer(@Param("owner") String owner,
                      @Param("id") String id);

    @Timed(value = "db.query", histogram = true)
    List<Layer> getApplicationLayers(@Param("appId") UUID appId,
                                     @Param("getDefaultOnly") boolean getDefaultOnly);
}
