package io.kontur.layers.service;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kontur.layers.ApiConstants;
import io.kontur.layers.controller.exceptions.WebApplicationException;
import io.kontur.layers.dto.Collection;
import io.kontur.layers.dto.Collections;
import io.kontur.layers.dto.*;
import io.kontur.layers.repository.ApplicationLayerMapper;
import io.kontur.layers.repository.ApplicationMapper;
import io.kontur.layers.repository.LayerMapper;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.repository.model.ApplicationLayer;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.util.AuthorizationUtils;
import io.kontur.layers.util.JsonUtil;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;
import org.wololo.geojson.Geometry;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static io.kontur.layers.service.LinkFactory.Relation.*;
import static io.kontur.layers.service.LinkFactory.Type.APPLICATION_GEO_JSON;
import static io.kontur.layers.service.LinkFactory.Type.APPLICATION_JSON;

@Service
public class CollectionService {

    private final LayerMapper layerMapper;
    private final LinkFactory linkFactory;
    private final ApplicationMapper applicationMapper;
    private final ApplicationLayerMapper applicationLayerMapper;

    public CollectionService(LayerMapper layerMapper,
                             LinkFactory linkFactory, ApplicationMapper applicationMapper,
                             ApplicationLayerMapper applicationLayerMapper) {
        this.layerMapper = layerMapper;
        this.linkFactory = linkFactory;
        this.applicationMapper = applicationMapper;
        this.applicationLayerMapper = applicationLayerMapper;
    }

    @Transactional(readOnly = true)
    public Collections getCollections(Geometry geometry, Integer limit,
                                      Integer offset, boolean includeLinks,
                                      CollectionOwner collectionOwner, UUID appId,
                                      List<String> collectionIds) {
        String geometryString = geometry != null ? JsonUtil.writeJson(geometry) : null;
        String userName = AuthorizationUtils.getAuthenticatedUserName();
        CollectionOwner ownershipFilter = StringUtils.isBlank(userName) ? CollectionOwner.ANY : collectionOwner;
        final List<Layer> layers;
        int numberMatched;
        if (appId != null) {
            Application app = applicationMapper.getApplicationOwnedOrPublic(appId, userName)
                    .orElseThrow(() -> new WebApplicationException(HttpStatus.NOT_FOUND, "Application is not found"));

            layers = layerMapper.getLayers(geometryString, userName, limit, offset, ownershipFilter,
                    app.getId(), app.getShowAllPublicLayers(), collectionIds.toArray(new String[0]));
            numberMatched = layerMapper.getLayersTotal(geometryString, userName, ownershipFilter,
                    app.getId(), app.getShowAllPublicLayers(), collectionIds.toArray(new String[0]));
        } else {
            layers = layerMapper.getLayers(geometryString, userName, limit, offset, ownershipFilter,
                    collectionIds.toArray(new String[0]));
            numberMatched = layerMapper.getLayersTotal(geometryString, userName, ownershipFilter,
                    collectionIds.toArray(new String[0]));
        }

        final List<Collection> collections = layers.stream().map(this::toCollection).collect(Collectors.toList());

        final List<Link> links = new ArrayList<>();

        if (includeLinks) {
            final String title = "Collections";
            links.add(linkFactory.createLocal(
                    UriComponentsBuilder.fromPath(ApiConstants.COLLECTIONS_ENDPOINT).build().toString(),
                    SELF, APPLICATION_JSON, title));

            if (offset + limit < numberMatched) {
                links.add(linkFactory.linkForCollections(NEXT, limit, offset + limit, title));
            }

            if (offset > 0) {
                links.add(linkFactory.linkForCollections(PREV, limit, Math.max(offset - limit, 0), title));
            }
        }
        return new Collections().collections(collections)
                .numberMatched(numberMatched)
                .numberReturned(collections.size())
                .links(links);
    }

    @Transactional(readOnly = true)
    public Optional<Collection> getCollection(String collectionId) {
        return layerMapper.getLayer(collectionId, AuthorizationUtils.getAuthenticatedUserName())
                .map(this::toCollection);
    }

    @Transactional
    public Collection createCollection(CollectionCreateDto collection) {
        Layer layer = toLayer(collection, collection.getId());
        layer.setVisible(true);
        Layer newLayer = insertLayer(layer, 1);
        updateStyleRules(collection, newLayer);
        return toCollection(newLayer);
    }

    private Layer insertLayer(Layer layer, int insertAttempt) {
        boolean autoGeneratedPublicId = false;
        try {
            if (StringUtils.isBlank(layer.getPublicId())) {
                layer.setPublicId(RandomStringUtils.randomAlphanumeric(8));
                autoGeneratedPublicId = true;
            }
            return layerMapper.insertLayer(layer);
        } catch (DuplicateKeyException e) {
            if (autoGeneratedPublicId && insertAttempt <= 3) {
                layer.setPublicId(null);
                insertLayer(layer, ++insertAttempt); //try once again with other public id
            }
            throw new WebApplicationException(HttpStatus.BAD_REQUEST, "Layer with such id already exists");
        }
    }

    @Transactional
    public Collection updateCollection(String id, CollectionUpdateDto collection) {
        Layer layer = layerMapper.updateLayer(toLayer(collection, id));
        if (layer == null) {
            throw new WebApplicationException(HttpStatus.NOT_FOUND, "Layer with such id can not be found");
        }
        updateStyleRules(collection, layer);

        return toCollection(layer);
    }

    @Transactional
    public void deleteCollection(String id) {
        Layer layer = layerMapper.deleteLayer(AuthorizationUtils.getAuthenticatedUserName(), id);
        if (layer == null) {
            throw new WebApplicationException(HttpStatus.NOT_FOUND, "Layer with such id can not be found");
        }
    }

    public Layer toLayer(CollectionUpdateDto c, String id) {
        String url = null;
        if (c.getLink() != null) {
            url = c.getLink().getHref();
        }

        return Layer.builder()
                .publicId(id)
                .name(c.getTitle())
                .description(c.getDescription())
                .url(url)
                .type(c.getItemType().name())
                .geometry(c.getGeometry() != null ? JsonUtil.writeJson(c.getGeometry()) : null)
                .copyrights(c.getCopyrights())
                .properties(c.getProperties())
                .featureProperties(c.getFeatureProperties())
                .lastUpdated(OffsetDateTime.now())
                .sourceLastUpdated(OffsetDateTime.now())
                .isPublic(false)
                .owner(AuthorizationUtils.getAuthenticatedUserName())
                .build();
    }

    public Collection toCollection(Layer layer) {
        Link link;
        if ("feature".equals(layer.getType())) {
            link = linkFactory.createLocal(
                    UriComponentsBuilder.fromPath(ApiConstants.COLLECTION_ITEMS_ENDPOINT)
                            .build(layer.getPublicId()).toString(),
                    ITEMS, APPLICATION_GEO_JSON, layer.getName());
        } else {
            link = new Link()
                    .rel("tiles")
                    .href(layer.getUrl());
        }
        return Collection.builder()
                .id(layer.getPublicId())
                .title(layer.getName())
                .description(layer.getDescription())
                .copyrights(layer.getCopyrights())
                .properties(layer.getProperties())
                .featureProperties(layer.getFeatureProperties())
                .styleRule(layer.getStyleRule())
                .displayRule(layer.getDisplayRule())
                .group(layer.getGroup())
                .category(layer.getCategory())
                .itemType(layer.getType())
                .crs(List.of("http://www.opengis.net/def/crs/OGC/1.3/CRS84"))
                .links(List.of(link))
                .extent(getExtent(layer))
                .ownedByUser(isUserOwnsLayer(layer))
                .build();
    }

    private void updateStyleRules(CollectionUpdateDto collection, Layer layer) {
        if (collection.getAppId() != null &&
                (collection.getStyleRule() != null || collection.getDisplayRule() != null)) {
            applicationMapper.getApplicationOwnedOrPublic(collection.getAppId(),
                            AuthorizationUtils.getAuthenticatedUserName())
                    .orElseThrow(() -> new WebApplicationException(HttpStatus.BAD_REQUEST,
                            "Application with such id can not be found"));

            ApplicationLayer appLayer = applicationLayerMapper.updateStyleAndDisplayRules(
                            collection.getAppId(), layer.getPublicId(), collection.getStyleRule(),
                            collection.getDisplayRule())
                    .orElseThrow(() -> new WebApplicationException(HttpStatus.BAD_REQUEST,
                            "Wasn't able to update style or display rules"));
            layer.setStyleRule(appLayer.getStyleRule());
            layer.setDisplayRule(appLayer.getDisplayRule());
        }
    }

    private Extent getExtent(Layer layer) {
        Extent extent = null;
        if (layer.getSpatialExtent() != null || layer.getTemporalExtent() != null) {
            extent = new Extent();
            if (layer.getSpatialExtent() != null) {
                //parse as float to fix float to double conversion artifacts in postgis
                List<Float> box = JsonUtil.readJson(layer.getSpatialExtent(), new TypeReference<>() {
                });
                if (box.get(2) == 0 && box.get(5) == 0) {
                    box = List.of(box.get(0), box.get(1), box.get(3), box.get(4));
                }
                final List<BigDecimal> decimals = box.stream().map(BigDecimal::new).toList();
                final ExtentSpatial spatial = new ExtentSpatial();
                spatial.setBbox(List.of(decimals));
                extent.setSpatial(spatial);
            }
            if (layer.getTemporalExtent() != null) {
                final ExtentTemporal temporal = new ExtentTemporal();
                final OffsetDateTime from = layer.getTemporalExtent().getFrom();
                final OffsetDateTime to = layer.getTemporalExtent().getTo();
                temporal.setInterval(List.of(Arrays.asList(from == null ? null : from.toString(),
                        to == null ? null : to.toString())
                ));
                extent.setTemporal(temporal);
            }

        }
        return extent;
    }

    private boolean isUserOwnsLayer(Layer layer) {
        return StringUtils.isNotEmpty(layer.getOwner()) &&
                layer.getOwner().equals(AuthorizationUtils.getAuthenticatedUserName());
    }
}
