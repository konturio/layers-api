package io.kontur.layers.repository;

import io.kontur.layers.dto.DateTimeRange;
import io.kontur.layers.dto.FeaturePropertiesFilter;
import io.kontur.layers.repository.model.Feature;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface FeatureMapper {

    List<Feature> getFeatures(@Param("collectionId") String collectionId,
                                     @Param("limit") Integer limit,
                                     @Param("offset") Integer offset,
                                     @Param("geometry") String geometry,
                                     @Param("bbox") List<BigDecimal> bbox,
                                     @Param("dateTime") DateTimeRange dateTime,
                                     @Param("propFilterList") List<FeaturePropertiesFilter> propFilterList);

    Optional<Feature> getFeature(@Param("collectionId") String collectionId, @Param("featureId") String featureId);

    Optional<Integer> getFeaturesTotal(@Param("collectionId") String collectionId,
                                       @Param("geometry") String geometry,
                                       @Param("bbox") List<BigDecimal> bbox,
                                       @Param("dateTime") DateTimeRange dateTime,
                                       @Param("propFilterList") List<FeaturePropertiesFilter> propFilterList);

    List<Feature> upsertFeatures(List<Feature> features);

    Optional<Feature> deleteFeature(@Param("owner") String owner,
                          @Param("collectionId") String collectionId,
                          @Param("featureId") String featureId);
}
