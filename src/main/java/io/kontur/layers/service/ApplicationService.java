package io.kontur.layers.service;

import io.kontur.layers.controller.exceptions.WebApplicationException;
import io.kontur.layers.dto.*;
import io.kontur.layers.repository.ApplicationLayerMapper;
import io.kontur.layers.repository.ApplicationMapper;
import io.kontur.layers.repository.LayerMapper;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.repository.model.ApplicationLayer;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.util.AuthorizationUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ApplicationService {

    private final ApplicationMapper applicationMapper;
    private final ApplicationLayerMapper applicationLayerMapper;
    private final LayerMapper layerMapper;
    private final CollectionService collectionService;

    public ApplicationService(ApplicationMapper applicationMapper,
                              ApplicationLayerMapper applicationLayerMapper, LayerMapper layerMapper,
                              CollectionService collectionService) {
        this.applicationMapper = applicationMapper;
        this.applicationLayerMapper = applicationLayerMapper;
        this.layerMapper = layerMapper;
        this.collectionService = collectionService;
    }

    @Transactional(readOnly = true)
    public ApplicationDto getApplication(UUID appId, boolean includeCollectionsInfo) {
        ApplicationDto app = applicationMapper.getApplication(appId,
                        AuthorizationUtils.getAuthenticatedUserName())
                .map(this::toDto)
                .orElseThrow(() -> new WebApplicationException(HttpStatus.NOT_FOUND,
                        "Application with such id can not be found"));
        if (includeCollectionsInfo) {
            Map<String, ApplicationLayer> layersRules = applicationLayerMapper.getApplicationLayersRules(app.getId())
                    .stream()
                    .collect(Collectors.toMap(ApplicationLayer::getLayerId, Function.identity()));
            List<Collection> collections = layerMapper.getApplicationLayers(app.getId(), true)
                    .stream()
                    .map(l -> Pair.of(l, layersRules.get(l.getPublicId())))
                    .map(pair -> {
                        Layer layer = pair.getLeft();
                        ApplicationLayer applicationLayer = pair.getRight();
                        Collection collection = collectionService.toCollection(layer);
                        collection.setDisplayRule(applicationLayer.getDisplayRule());
                        collection.setStyleRule(applicationLayer.getStyleRule());
                        collection.setLegend(applicationLayer.getStyleRule()); //TODO remove
                        return collection;
                    })
                    .toList();
            app.setDefaultCollections(collections);
        }
        return app;
    }

    @Transactional
    public ApplicationDto createApplication(ApplicationCreateDto body) {
        try {
            Application app = applicationMapper.insertApplication(toModel(body, body.getId()));
            if (!CollectionUtils.isEmpty(body.getLayers())) {
                upsertApplicationLayers(body.getLayers(), app);
            }
            return toDto(app);
        } catch (DuplicateKeyException ex) {
            throw new WebApplicationException(HttpStatus.BAD_REQUEST, "Application with such id already exists");
        }
    }

    @Transactional
    public ApplicationDto updateApplication(UUID applicationId, ApplicationUpdateDto body) {
        Application app = applicationMapper.updateApplication(toModel(body, applicationId));
        if (app == null) {
            throw new WebApplicationException(HttpStatus.NOT_FOUND, "Application with such id can not be found");
        }
        if (!CollectionUtils.isEmpty(body.getLayers())) {
            upsertApplicationLayers(body.getLayers(), app);
        }
        return toDto(app);
    }

    @Transactional
    public void deleteApplication(UUID appId) {
        Application app = applicationMapper.deleteApplication(appId,
                AuthorizationUtils.getAuthenticatedUserName());
        if (app == null) {
            throw new WebApplicationException(HttpStatus.NOT_FOUND, "Application with such id can not be found");
        }
    }

    private void upsertApplicationLayers(List<ApplicationLayerDto> appLayers, Application app) {
        List<String> layerIds = appLayers.stream()
                .map(ApplicationLayerDto::getLayerId)
                .toList();

        List<String> layersIdsToUpdate = layerMapper.getLayers(null, AuthorizationUtils.getAuthenticatedUserName(),
                        appLayers.size(), 0, CollectionOwner.ANY, layerIds.toArray(new String[0]))
                .stream()
                .map(Layer::getPublicId)
                .toList();

        if (layerIds.size() != layersIdsToUpdate.size()) {
            List<String> diff = layerIds.stream()
                    .filter(v -> !layersIdsToUpdate.contains(v))
                    .toList();

            throw new WebApplicationException(HttpStatus.BAD_REQUEST,
                    String.format("Unable to find requested layers: %s", String.join(",", diff)));
        }

        applicationLayerMapper.upsertLayers(app.getId(), appLayers);
    }

    private ApplicationDto toDto(Application app) {
        return ApplicationDto.builder()
                .id(app.getId())
                .isPublic(app.getIsPublic())
                .showAllPublicLayers(app.getShowAllPublicLayers())
                .build();
    }

    private Application toModel(ApplicationUpdateDto body, UUID id) {
        return Application.builder()
                .id(id)
                .isPublic(body.isPublic())
                .showAllPublicLayers(body.isShowAllPublicLayers())
                .owner(AuthorizationUtils.getAuthenticatedUserName())
                .build();
    }
}
