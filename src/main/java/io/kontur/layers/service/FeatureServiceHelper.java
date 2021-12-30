package io.kontur.layers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kontur.layers.ApiConstants;
import io.kontur.layers.controller.exceptions.Error;
import io.kontur.layers.controller.exceptions.WebApplicationException;
import io.kontur.layers.dto.FeatureGeoJSON;
import io.kontur.layers.dto.FeaturePropertiesFilter;
import io.kontur.layers.dto.GeometryGeoJSON;
import io.kontur.layers.dto.Link;
import io.kontur.layers.repository.model.Feature;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.kontur.layers.service.LinkFactory.Relation.*;
import static io.kontur.layers.service.LinkFactory.Type.APPLICATION_GEO_JSON;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Service
public class FeatureServiceHelper {

    private final LinkFactory linkFactory;
    private final ObjectMapper objectMapper;

    public FeatureServiceHelper(LinkFactory linkFactory, ObjectMapper objectMapper) {
        this.linkFactory = linkFactory;
        this.objectMapper = objectMapper;
    }

    public List<Link> getCollectionLinks(String collectionId,
                                         String collectionTitle,
                                         Integer limit,
                                         Integer offset,
                                         Integer numberMatched,
                                         List<BigDecimal> bbox,
                                         List<FeaturePropertiesFilter> filterList) {
        List<Link> linkList = new ArrayList<>();

        linkList.add(linkFactory.linkForCollectionItems(SELF, collectionId, collectionTitle, limit, offset, bbox, filterList));

        if ((numberMatched != null) && (offset + limit < numberMatched)) {
            linkList.add(linkFactory.linkForCollectionItems(NEXT, collectionId, collectionTitle, limit, offset + limit, bbox, filterList));
        }

        if (offset > 0) {
            linkList.add(linkFactory.linkForCollectionItems(PREV, collectionId, collectionTitle, limit, Math.max(offset - limit, 0), bbox, filterList));
        }

        return linkList;
    }

    public FeatureGeoJSON toFeatureGeoJson(Feature feature, String collectionId, String title) {
        Link selfLink = linkFactory.createLocal(UriComponentsBuilder.fromPath(ApiConstants.COLLECTION_ITEM_ID_ENDPOINT)
                        .build(collectionId, feature.getFeatureId()).toString(),
                SELF, APPLICATION_GEO_JSON);

        Link collectionLink = linkFactory.createLocal(UriComponentsBuilder.fromPath(ApiConstants.COLLECTION_ID_ENDPOINT).build(collectionId).toString(),
                COLLECTION, APPLICATION_GEO_JSON, title);
        try {
            return new FeatureGeoJSON()
                    .id(feature.getFeatureId())
                    .geometry(feature.getGeometry() == null
                            ? null
                            : objectMapper.readValue(feature.getGeometry(), GeometryGeoJSON.class))
                    .links(Arrays.asList(selfLink, collectionLink))
                    .properties(feature.getProperties())
                    .type(FeatureGeoJSON.TypeEnum.FEATURE);
        } catch (JsonProcessingException e) {
            throw new WebApplicationException(INTERNAL_SERVER_ERROR, Error.error("internal server error"), e);
        }
    }
}
