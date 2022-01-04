package io.kontur.layers.service;

import com.fasterxml.jackson.core.type.TypeReference;
import io.kontur.layers.ApiConstants;
import io.kontur.layers.controller.exceptions.WebApplicationException;
import io.kontur.layers.dto.*;
import io.kontur.layers.repository.LayerMapper;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.util.AuthorizationUtils;
import io.kontur.layers.util.JsonUtil;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static io.kontur.layers.service.LinkFactory.Relation.*;
import static io.kontur.layers.service.LinkFactory.Type.APPLICATION_GEO_JSON;
import static io.kontur.layers.service.LinkFactory.Type.APPLICATION_JSON;

@Service
public class CollectionService {

    private final LayerMapper layerMapper;
    private final LinkFactory linkFactory;

    public CollectionService(LayerMapper layerMapper, LinkFactory linkFactory) {
        this.layerMapper = layerMapper;
        this.linkFactory = linkFactory;
    }

    @Transactional(readOnly = true)
    public Collections getCollections(GeometryGeoJSON geometry, Integer limit,
                                      Integer offset, boolean includeLinks) {
        String geometryString = geometry != null ? JsonUtil.writeJson(geometry) : null;
        final List<Layer> layers = layerMapper.getLayers(geometryString, AuthorizationUtils.getAuthenticatedUserName(), limit, offset);
        int numberMatched = layers.isEmpty() ? 0 : layers.get(0).getNumberMatched();

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
        return layerMapper.getLayer(collectionId, AuthorizationUtils.getAuthenticatedUserName()).map(this::toCollection);
    }

    @Transactional
    public Collection createCollection(CollectionCreateDto collection) {
        try {
            return toCollection(layerMapper.insertLayer(toLayer(collection, collection.getId())));
        } catch (DuplicateKeyException e) {
            throw new WebApplicationException(HttpStatus.BAD_REQUEST, "Layer with such id already exists");
        }
    }

    @Transactional
    public Collection updateCollection(String id, CollectionUpdateDto collection) {
        Layer layer = layerMapper.updateLayer(toLayer(collection, id));
        if (layer == null) {
            throw new WebApplicationException(HttpStatus.NOT_FOUND, "Layer with such id can not be found");
        }
        return toCollection(layer);
    }

    @Transactional
    public void deleteCollection(String id) {
        Layer layer = layerMapper.deleteLayer(AuthorizationUtils.getAuthenticatedUserName(), id);
        if (layer == null) {
            throw new WebApplicationException(HttpStatus.NOT_FOUND, "Layer with such id can not be found");
        }
    }

    private Layer toLayer(CollectionUpdateDto c, String id) {
        String url = null;
        if (c.getLink() != null && "tiles".equals(c.getLink().getRel())) {
            url = c.getLink().getHref();
        }

        return new Layer.Builder()
                .publicId(id)
                .name(c.getTitle())
                .description(c.getDescription())
                .url(url)
                .type(c.getItemType())
                .geometry(c.getGeometry() != null ? JsonUtil.writeJson(c.getGeometry()) : null)
                .copyrights(c.getCopyrights())
                .properties(c.getProperties())
                .legend(c.getLegend())
                .lastUpdated(OffsetDateTime.now())
                .sourceLastUpdated(OffsetDateTime.now())
                .isPublic(false) //TODO how do we create public kontur layers?
                .owner(AuthorizationUtils.getAuthenticatedUserName())
                .createLayer();
    }

    private Collection toCollection(Layer layer) {
        Link link;
        if (Layer.Type.tiles.equals(layer.getType())) {
            link = new Link()
                    .rel(String.valueOf(Layer.Type.tiles))
                    .href(layer.getUrl());
        } else {
            link = linkFactory.createLocal(
                    UriComponentsBuilder.fromPath(ApiConstants.COLLECTION_ITEMS_ENDPOINT)
                            .build(layer.getPublicId()).toString(),
                    ITEMS, APPLICATION_GEO_JSON, layer.getName());
        }
        return new Collection()
                .id(layer.getPublicId())
                .title(layer.getName())
                .description(layer.getDescription())
                .copyrights(layer.getCopyrights())
                .properties(layer.getProperties())
                .legend(layer.getLegend())
                .group(layer.getGroup())
                .category(layer.getCategory())
                .crs(List.of("http://www.opengis.net/def/crs/OGC/1.3/CRS84"))
                .links(List.of(link))
                .extent(getExtent(layer));
    }

    private Extent getExtent(Layer layer) {
        Extent extent = null;
        if (layer.getSpatialExtent() != null || layer.getTemporalExtent() != null) {
            extent = new Extent();
            if (layer.getSpatialExtent() != null) {
                //parse as float to fix float to double conversion artifacts in postgis
                List<Float> box = JsonUtil.readJson(layer.getSpatialExtent(), new TypeReference<>() {});
                if (box.get(2) == 0 && box.get(5) == 0) {
                    box = List.of(box.get(0), box.get(1), box.get(3), box.get(4));
                }
                final List<BigDecimal> decimals = box.stream().map(BigDecimal::new).collect(Collectors.toList());
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
}
