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
public interface LayerMapper {

    @Timed("db.queries.layers.user.owned")
    Optional<Layer> getOwnedLayer(String publicId, String userName);

    @Timed("db.queries.layers.user.accessible")
    default Optional<Layer> getLayer(String publicId, String userName) {
        return getLayer(publicId, userName, null, true);
    }

    @Timed("db.queries.layers.user.accessible.byAppId")
    default Optional<Layer> getLayer(String publicId, String userName, UUID appId, boolean showAllPublic) {
        return getLayers(null, userName,1, 0, CollectionOwner.ANY, appId, showAllPublic, publicId).stream().findFirst();
    }

    @Timed("db.queries.layers.total.noAppId")
    default int getLayersTotal(@Param("geometry") String geometry,
                           @Param("userName") String userName,
                           @Param("collectionOwner") CollectionOwner collectionOwner,
                           @Param("publicIds") String... publicIds) {
        return getLayersTotal(geometry, userName, collectionOwner, null, true, publicIds);
    }

    @Timed("db.queries.layers.total")
    int getLayersTotal(@Param("geometry") String geometry,
                           @Param("userName") String userName,
                           @Param("collectionOwner") CollectionOwner collectionOwner,
                           @Param("appId") UUID appId,
                           @Param("showAllPublic") boolean showAllPublic,
                           @Param("publicIds") String... publicIds);

    @Timed("db.queries.layers.search.noAppId")
    default List<Layer> getLayers(@Param("geometry") String geometry,
                          @Param("userName") String userName,
                          @Param("limit") Integer limit,
                          @Param("offset") Integer offset,
                          @Param("collectionOwner") CollectionOwner collectionOwner,
                          @Param("publicIds") String... publicIds) {
        return getLayers(geometry, userName, limit, offset, collectionOwner, null, true, publicIds);
    }

    @Timed("db.queries.layers.search")
    List<Layer> getLayers(@Param("geometry") String geometry,
                          @Param("userName") String userName,
                          @Param("limit") Integer limit,
                          @Param("offset") Integer offset,
                          @Param("collectionOwner") CollectionOwner collectionOwner,
                          @Param("appId") UUID appId,
                          @Param("showAllPublic") boolean showAllPublic,
                          @Param("publicIds") String... publicIds);

    @Timed("db.queries.layers.add")
    Layer insertLayer(Layer layer);

    @Timed("db.queries.layers.update")
    Layer updateLayer(Layer layer);

    @Timed("db.queries.layers.delete")
    Layer deleteLayer(@Param("owner") String owner,
                      @Param("id") String id);

    @Timed("db.queries.layers.application.select")
    List<Layer> getApplicationLayers(@Param("appId") UUID appId,
                                     @Param("getDefaultOnly") boolean getDefaultOnly);
}
