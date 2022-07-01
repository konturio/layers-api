package io.kontur.layers.controller;

import io.kontur.layers.controller.exceptions.Error;
import io.kontur.layers.controller.exceptions.WebApplicationException;
import io.kontur.layers.dto.*;
import io.kontur.layers.service.CollectionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.lang.Exception;
import java.util.Optional;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@Validated
@RequestMapping("/collections")
public class CollectionsApi {

    protected static final int COLLECTIONS_LIMIT = 1000;
    protected static final int COLLECTIONS_DEFAULT_LIMIT = 10;

    private final CollectionService collectionService;

    public CollectionsApi(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/{collectionId}")
    @Operation(summary = "describe the feature collection with id `collectionId`", description = "", tags = {"Capabilities"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Information about the feature collection with id `collectionId`.  The response contains a linkto the items in the collection (path `/collections/{collectionId}/items`,link relation `items`) as well as key information about the collection. This information includes:  * A local identifier for the collection that is unique for the dataset; * A list of coordinate reference systems (CRS) in which geometries may be returned by the server. The first CRS is the default coordinate reference system (the default is always WGS 84 with axis order longitude/latitude); * An optional title and description for the collection; * An optional extent that can be used to provide an indication of the spatial and temporal extent of the collection - typically derived from the data; * An optional indicator about the type of the items in the collection (the default value, if the indicator is not provided, is 'feature').", content = @Content(schema = @Schema(implementation = Collection.class))),
            @ApiResponse(responseCode = "404", description = "The requested URI was not found."),
            @ApiResponse(responseCode = "500", description = "A server error occurred.", content = @Content(schema = @Schema(implementation = Exception.class)))})
    public ResponseEntity describeCollection(
            @Parameter(in = ParameterIn.PATH, description = "local identifier of a collection", required = true)
            @PathVariable("collectionId") String collectionId) {
        Optional<Collection> collection = collectionService.getCollection(collectionId);
        final Collection entity = collection.orElseThrow(
                () -> new WebApplicationException(NOT_FOUND,
                        Error.errorFmt("Collection '%s' not found", collectionId)));
        return ResponseEntity.ok(entity);
    }

    @GetMapping
    @Operation(summary = "the feature collections in the dataset", description = "", tags = {"Capabilities"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The feature collections shared by this API.  The dataset is organized as one or more feature collections. This resource provides information about and access to the collections.  The response contains the list of collections. For each collection, a link to the items in the collection (path `/collections/{collectionId}/items`, link relation `items`) as well as key information about the collection. This information includes:  * A local identifier for the collection that is unique for the dataset; * A list of coordinate reference systems (CRS) in which geometries may be returned by the server. The first CRS is the default coordinate reference system (the default is always WGS 84 with axis order longitude/latitude); * An optional title and description for the collection; * An optional extent that can be used to provide an indication of the spatial and temporal extent of the collection - typically derived from the data; * An optional indicator about the type of the items in the collection (the default value, if the indicator is not provided, is 'feature').", content = @Content(schema = @Schema(implementation = Collections.class))),
            @ApiResponse(responseCode = "500", description = "A server error occurred.", content = @Content(schema = @Schema(implementation = Exception.class)))})
    public ResponseEntity getCollections(
            @Parameter(in = ParameterIn.QUERY, description = "The optional limit parameter limits the number of collections that are presented in the response document. Minimum = 1. Maximum = 1000. Default = 10.", schema = @Schema(allowableValues = {}, minimum = "1", maximum = "1000"))
            @Min(1)
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @Parameter(in = ParameterIn.QUERY, description = "The optional offset parameter specifies the index within the result set from which the server begins presenting results in the response. Minimum = 0", schema = @Schema(allowableValues = {}, minimum = "0"))
            @Min(0)
            @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        int lmt = Math.min(limit == null ? COLLECTIONS_DEFAULT_LIMIT : limit, COLLECTIONS_LIMIT);
        Collections collections = collectionService.getCollections(null, lmt, offset, true,
                CollectionOwner.ANY, null, java.util.Collections.emptyList());
        return ResponseEntity.ok(collections);
    }

    @PostMapping("/search")
    @Operation(summary = "search feature collections in the dataset", description = """
            ## Body parameters
            **geometry** filters collections that intersect with provided geometry \s
            **collectionOwner** limit to collections created by authenticated user or created by others \s
            **appID** limit to collections specified for the application. Collection style and display rules are added to the response if appId is provided""", tags = {"Capabilities"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The feature collections shared by this API.  The dataset is organized as one or more feature collections. This resource provides information about and access to the collections.  The response contains the list of collections. For each collection, a link to the items in the collection (path `/collections/{collectionId}/items`, link relation `items`) as well as key information about the collection. This information includes:  * A local identifier for the collection that is unique for the dataset; * A list of coordinate reference systems (CRS) in which geometries may be returned by the server. The first CRS is the default coordinate reference system (the default is always WGS 84 with axis order longitude/latitude); * An optional title and description for the collection; * An optional extent that can be used to provide an indication of the spatial and temporal extent of the collection - typically derived from the data; * An optional indicator about the type of the items in the collection (the default value, if the indicator is not provided, is 'feature').", content = @Content(schema = @Schema(implementation = Collections.class))),
            @ApiResponse(responseCode = "500", description = "A server error occurred.", content = @Content(schema = @Schema(implementation = Exception.class)))})
    public ResponseEntity searchCollections(
            @RequestBody @Valid CollectionsSearchDto body) {
        int lmt = Math.min(body.getLimit() == null ? COLLECTIONS_DEFAULT_LIMIT : body.getLimit(), COLLECTIONS_LIMIT);
        Collections collections = collectionService.getCollections(body.getGeometry(), lmt, body.getOffset(),
                false, body.getCollectionOwner(), body.getAppId(), body.getCollectionIds());
        return ResponseEntity.ok(collections);
    }

    @PostMapping
    @Operation(summary = "Create new collection in the dataset", tags = {"Capabilities"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Newly created feature collection.", content = @Content(schema = @Schema(implementation = Collection.class)))})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity createCollection(
            @RequestBody @Valid CollectionCreateDto body) {
        Collection collection = collectionService.createCollection(body);
        return ResponseEntity.ok(collection);
    }

    @PutMapping("/{collectionId}")
    @Operation(summary = "Update collection in the dataset", tags = {"Capabilities"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Newly created feature collection.", content = @Content(schema = @Schema(implementation = Collection.class)))})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity updateCollection(
            @Parameter(in = ParameterIn.PATH, description = "local identifier of a collection", required = true)
            @PathVariable("collectionId") String collectionId,
            @RequestBody @Valid CollectionUpdateDto body) {
        Collection collection = collectionService.updateCollection(collectionId, body);
        return ResponseEntity.ok(collection);
    }

    @DeleteMapping("/{collectionId}")
    @Operation(summary = "Remove collection from the dataset", tags = {"Capabilities"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success")})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity deleteCollection(
            @Parameter(in = ParameterIn.PATH, description = "local identifier of a collection", required = true)
            @PathVariable("collectionId") String collectionId) {
        collectionService.deleteCollection(collectionId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
