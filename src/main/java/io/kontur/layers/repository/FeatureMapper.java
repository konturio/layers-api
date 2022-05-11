package io.kontur.layers.repository;

import io.kontur.layers.dto.DateTimeRange;
import io.kontur.layers.dto.FeaturePropertiesFilter;
import io.kontur.layers.repository.model.LayerFeature;
import io.micrometer.core.annotation.Timed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface FeatureMapper {

    @Timed("db.queries.features.search")
    List<LayerFeature> getFeatures(@Param("collectionId") String collectionId,
                                   @Param("limit") Integer limit,
                                   @Param("offset") Integer offset,
                                   @Param("geometry") String geometry,
                                   @Param("bbox") List<BigDecimal> bbox,
                                   @Param("dateTime") DateTimeRange dateTime,
                                   @Param("propFilterList") List<FeaturePropertiesFilter> propFilterList);

    @Timed("db.queries.features.byId")
    Optional<LayerFeature> getFeature(@Param("collectionId") String collectionId, @Param("featureId") String featureId);

    @Timed("db.queries.features.total")
    Optional<Integer> getFeaturesTotal(@Param("collectionId") String collectionId,
                                       @Param("geometry") String geometry,
                                       @Param("bbox") List<BigDecimal> bbox,
                                       @Param("dateTime") DateTimeRange dateTime,
                                       @Param("propFilterList") List<FeaturePropertiesFilter> propFilterList);

    @Timed("db.queries.features.upsert")
    List<LayerFeature> upsertFeatures(List<LayerFeature> features);

    @Timed("db.queries.features.delete.byId")
    Optional<LayerFeature> deleteFeature(@Param("owner") String owner,
                                         @Param("collectionId") String collectionId,
                                         @Param("featureId") String featureId);

    @Timed("db.queries.features.delete")
    void deleteFeaturesNotInList(@Param("owner") String owner,
                                         @Param("collectionId") String collectionId,
                                         @Param("featureIds") List<String> featureIds);
}
