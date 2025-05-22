package io.kontur.layers.repository;

import io.kontur.layers.dto.DateTimeRange;
import io.kontur.layers.dto.FeaturePropertiesFilter;
import io.kontur.layers.dto.SortOrder;
import io.kontur.layers.repository.model.LayerFeature;
import io.micrometer.core.annotation.Timed;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
@Timed(value = "db.query", histogram = true)
public interface FeatureMapper {

    @Timed(value = "db.query", histogram = true)
    List<LayerFeature> getFeatures(@Param("collectionId") String collectionId,
                                   @Param("limit") Integer limit,
                                   @Param("offset") Integer offset,
                                   @Param("geometry") String geometry,
                                   @Param("bbox") List<BigDecimal> bbox,
                                   @Param("dateTime") DateTimeRange dateTime,
                                   @Param("propFilterList") List<FeaturePropertiesFilter> propFilterList,
                                   @Param("order") SortOrder order);

    @Timed(value = "db.query", histogram = true)
    Optional<LayerFeature> getFeature(@Param("collectionId") String collectionId, @Param("featureId") String featureId);

    @Timed(value = "db.query", histogram = true)
    Optional<Integer> getFeaturesTotal(@Param("collectionId") String collectionId,
                                       @Param("geometry") String geometry,
                                       @Param("bbox") List<BigDecimal> bbox,
                                       @Param("dateTime") DateTimeRange dateTime,
                                       @Param("propFilterList") List<FeaturePropertiesFilter> propFilterList);

    @Timed(value = "db.query", histogram = true)
    List<LayerFeature> addFeatures(List<LayerFeature> features);

    @Timed(value = "db.query", histogram = true)
    List<LayerFeature> upsertFeatures(List<LayerFeature> features);

    @Timed(value = "db.query", histogram = true)
    Optional<LayerFeature> deleteFeature(@Param("owner") String owner,
                                         @Param("collectionId") String collectionId,
                                         @Param("featureId") String featureId);

    @Timed(value = "db.query", histogram = true)
    void deleteFeaturesNotInList(@Param("owner") String owner,
                                 @Param("collectionId") String collectionId,
                                 @Param("featureIds") List<String> featureIds);

    @Timed(value = "db.query", histogram = true)
    List<String> checkIfFeatureIdExists(@Param("layerId") Long layerId,
                                        @Param("featureIds") List<String> featureIds);
}
