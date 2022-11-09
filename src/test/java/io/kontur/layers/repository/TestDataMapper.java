package io.kontur.layers.repository;

import io.kontur.layers.dto.ApplicationLayerDto;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.repository.model.LayerFeature;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.UUID;

@Mapper
public interface TestDataMapper {

    @Select({"with ins as (insert into layers (public_id, name, description, geom, last_updated, source_updated, copyrights, properties, is_public, is_visible, owner, type, url, api_key, feature_properties, is_global) values ",
            "(#{publicId},#{name},#{description},#{geometry}::geometry,#{lastUpdated},#{sourceLastUpdated},#{copyrights},#{properties}::jsonb,#{isPublic},#{isVisible},#{owner},#{type},#{url},#{apiKey},#{featureProperties}::jsonb,#{isGlobal}) returning id) ",
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

    @Select({"with ins as (insert into apps (id, show_all_public_layers, is_public, owner, name, icon_url) values ",
            "(#{id},#{showAllPublicLayers},#{isPublic},#{owner},#{name},#{iconUrl}) returning id) ",
            "select * from ins"})
    UUID insertApplication(Application app);

    @Insert({"insert into apps_layers (app_id, layer_id, is_default, display_rule, style_rule) values " +
            "(#{appId}, #{appLayer.layerId}, #{appLayer.isDefault}, #{appLayer.styleRule}::jsonb, " +
            "#{appLayer.displayRule}::jsonb)"})
    void insertApplicationLayer(@Param("appLayer") ApplicationLayerDto appLayer, @Param("appId") UUID appId);
}
