package io.kontur.layers.repository;

import io.kontur.layers.dto.DateTimeRange;
import io.kontur.layers.repository.model.Feature;
import io.kontur.layers.repository.typehandler.FeatureCollectionResultHandler;
import io.kontur.layers.service.FeatureService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Mapper
public interface FeatureMapper {

    void getFeatures(@Param("collectionId") String collectionId,
                                     @Param("limit") Integer limit,
                                     @Param("offset") Integer offset,
                                     @Param("bbox") List<BigDecimal> bbox,
                                     @Param("dateTime") DateTimeRange dateTime,
                                     @Param("propFilterList") List<FeatureService.PropFilter> propFilterList,
                                     FeatureCollectionResultHandler featureCollectionResultHandler);

    List<Feature> getFeaturesByMultipoint(@Param("collectionId") String collectionId,
                                          @Param("limit") Integer limit,
                                          @Param("offset") Integer offset,
                                          @Param("geom") String geom,
                                          @Param("dateTime") DateTimeRange dateTime,
                                          @Param("propFilterList") List<FeatureService.PropFilter> propFilterList,
                                          @Param("excludeGeometry") boolean excludeGeometry);

    Optional<Feature> getFeature(@Param("collectionId") String collectionId, @Param("featureId") String featureId);

    Optional<Integer> getFeaturesTotal(@Param("collectionId") String collectionId,
                                       @Param("bbox") List<BigDecimal> bbox,
                                       @Param("dateTime") DateTimeRange dateTime,
                                       @Param("propFilterList") List<FeatureService.PropFilter> propFilterList);

}
