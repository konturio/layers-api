package io.kontur.layers.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.kontur.layers.dto.*;
import io.kontur.layers.ApiConstants;
import io.kontur.layers.repository.LayerMapper;
import io.kontur.layers.repository.model.Layer;
import org.springframework.stereotype.Service;
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
    private final ObjectMapper objectMapper;

    public CollectionService(LayerMapper layerMapper, LinkFactory linkFactory, ObjectMapper objectMapper) {
        this.layerMapper = layerMapper;
        this.linkFactory = linkFactory;
        this.objectMapper = objectMapper;
    }

    public Collections getCollections(Integer limit, Integer offset) {
        final List<Layer> layers = layerMapper.getLayers(limit, offset);
        int numberMatched = layers.isEmpty() ? 0 : layers.get(0).getNumberMatched();

        final List<Collection> collections = layers.stream().map(this::toCollection).collect(Collectors.toList());

        final List<Link> links = new ArrayList<>();

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

        return new Collections().collections(collections)
                .numberMatched(numberMatched)
                .numberReturned(collections.size())
                .links(links);
    }

    public Optional<Collection> getCollection(String collectionId) {
        return layerMapper.getLayer(collectionId).map(this::toCollection);
    }

    private Collection toCollection(Layer layer) {
        final Link items = linkFactory.createLocal(
                UriComponentsBuilder.fromPath(ApiConstants.COLLECTION_ITEMS_ENDPOINT).build(layer.getPublicId()).toString(),
                ITEMS, APPLICATION_GEO_JSON, layer.getName());

        return new Collection()
                .id(layer.getPublicId())
                .title(layer.getName())
                .description(layer.getDescription())
                .crs(List.of("http://www.opengis.net/def/crs/OGC/1.3/CRS84"))
                .links(List.of(items))
                .extent(getExtent(layer));
    }

    private Extent getExtent(Layer layer) {
        try {
            Extent extent = null;
            if (layer.getSpatialExtent() != null || layer.getTemporalExtent() != null) {
                extent = new Extent();
                if (layer.getSpatialExtent() != null) {
                    //parse as float to fix float to double conversion artifacts in postgis
                    List<Float> box = objectMapper.readValue(layer.getSpatialExtent(), new TypeReference<>() {
                    });
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
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
