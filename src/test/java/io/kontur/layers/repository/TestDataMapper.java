package io.kontur.layers.repository;

import io.kontur.layers.repository.model.LayerFeature;
import io.kontur.layers.repository.model.Layer;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TestDataMapper {

    @Update({"delete from layers_features;",
            "delete from layers"})
    void databaseCleanup();

    @Select({"with ins as (insert into layers (public_id, name, description, geom, last_updated, source_updated, copyrights, properties, is_public, is_visible, owner, type, url, feature_properties) values ",
            "(#{publicId},#{name},#{description},#{geometry}::geometry,#{lastUpdated},#{sourceLastUpdated},#{copyrights},#{properties}::jsonb,#{isPublic},#{isVisible},#{owner},#{type},#{url},#{featureProperties}::jsonb) returning id) ",
            "select * from ins"})
    long insertLayer(Layer layer);

    @Select({"with ins as (insert into layers_features (layer_id, feature_id, geom, properties, last_updated) values ",
            "(#{collectionId},#{feature.featureId},ST_GeomFromGeoJSON(#{feature.geometry}),#{feature.properties}::jsonb,#{feature.lastUpdated}) returning feature_id) ",
            "select * from ins"})
    String insertFeature(@Param("collectionId") long collectionId, @Param("feature") LayerFeature feature);

    @Insert({"<script>",
            "insert into layers_features (layer_id, feature_id, geom, properties, last_updated) values ",
            "<foreach item='feature' collection='features' separator=','> ",
            "(#{collectionId},#{feature.featureId},ST_GeomFromGeoJSON(#{feature.geometry}),#{feature.properties}::jsonb,#{feature.lastUpdated}) ",
            "</foreach> ",
            "</script>"})
    void insertFeatures(@Param("collectionId") long collectionId, @Param("features") List<LayerFeature> features);
}
