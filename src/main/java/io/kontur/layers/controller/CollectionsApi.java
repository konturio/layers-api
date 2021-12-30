package io.kontur.layers.controller;

import io.kontur.layers.controller.exceptions.Error;
import io.kontur.layers.controller.exceptions.WebApplicationException;
import io.kontur.layers.controller.validation.ValidBbox;
import io.kontur.layers.dto.*;
import io.kontur.layers.service.CollectionService;
import io.kontur.layers.service.FeatureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.lang.Exception;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static io.kontur.layers.ApiConstants.APPLICATION_GEO_JSON;
import static io.kontur.layers.controller.exceptions.Error.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@Validated
@RequestMapping("/collections")
public class CollectionsApi {

    protected static final int COLLECTION_ITEMS_LIMIT = 100000;
    protected static final int COLLECTIONS_LIMIT = 100000;
    protected static final int COLLECTION_ITEMS_DEFAULT_LIMIT = 10;
    protected static final int COLLECTIONS_DEFAULT_LIMIT = 10;
    private static final Set<String> PREDEFINED_FIELDS = Set.of("limit", "offset", "bbox", "datetime", "geom",
            "excludeGeometry");

    private final CollectionService collectionService;
    private final FeatureService featureService;
    private final HttpServletRequest servletRequest;

    public CollectionsApi(CollectionService collectionService, FeatureService featureService,
                          HttpServletRequest servletRequest) {
        this.collectionService = collectionService;
        this.featureService = featureService;
        this.servletRequest = servletRequest;
    }

    @GetMapping
    @Operation(summary = "the feature collections in the dataset", description = "", tags = {"Capabilities"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The feature collections shared by this API.  The dataset is organized as one or more feature collections. This resource provides information about and access to the collections.  The response contains the list of collections. For each collection, a link to the items in the collection (path `/collections/{collectionId}/items`, link relation `items`) as well as key information about the collection. This information includes:  * A local identifier for the collection that is unique for the dataset; * A list of coordinate reference systems (CRS) in which geometries may be returned by the server. The first CRS is the default coordinate reference system (the default is always WGS 84 with axis order longitude/latitude); * An optional title and description for the collection; * An optional extent that can be used to provide an indication of the spatial and temporal extent of the collection - typically derived from the data; * An optional indicator about the type of the items in the collection (the default value, if the indicator is not provided, is 'feature').", content = @Content(schema = @Schema(implementation = Collections.class))),
            @ApiResponse(responseCode = "500", description = "A server error occurred.", content = @Content(schema = @Schema(implementation = Exception.class)))})
    public ResponseEntity getCollections(
            @Parameter(in = ParameterIn.QUERY, description = "The optional limit parameter limits the number of collections that are presented in the response document. Minimum = 1. Maximum = 100000. Default = 10.", schema = @Schema(allowableValues = {}, minimum = "1", maximum = "100000"))
            @Min(1)
            @RequestParam(value = "limit", defaultValue = "10") Integer limit,
            @Parameter(in = ParameterIn.QUERY, description = "The optional offset parameter specifies the index within the result set from which the server begins presenting results in the response. Minimum = 0", schema = @Schema(allowableValues = {}, minimum = "0"))
            @Min(0)
            @RequestParam(value = "offset", defaultValue = "0") Integer offset) {
        int lmt = Math.min(limit == null ? COLLECTIONS_DEFAULT_LIMIT : limit, COLLECTIONS_LIMIT);
        Collections collections = collectionService.getCollections(null, lmt, offset, true);
        return ResponseEntity.ok(collections);
    }

    @PostMapping("/search")
    @Operation(summary = "seacrh feature collections in the dataset", description = "", tags = {"Capabilities"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The feature collections shared by this API.  The dataset is organized as one or more feature collections. This resource provides information about and access to the collections.  The response contains the list of collections. For each collection, a link to the items in the collection (path `/collections/{collectionId}/items`, link relation `items`) as well as key information about the collection. This information includes:  * A local identifier for the collection that is unique for the dataset; * A list of coordinate reference systems (CRS) in which geometries may be returned by the server. The first CRS is the default coordinate reference system (the default is always WGS 84 with axis order longitude/latitude); * An optional title and description for the collection; * An optional extent that can be used to provide an indication of the spatial and temporal extent of the collection - typically derived from the data; * An optional indicator about the type of the items in the collection (the default value, if the indicator is not provided, is 'feature').", content = @Content(schema = @Schema(implementation = Collections.class))),
            @ApiResponse(responseCode = "500", description = "A server error occurred.", content = @Content(schema = @Schema(implementation = Exception.class)))})
    public ResponseEntity searchCollections(
            @RequestBody @Valid CollectionsSearchDto body) {
        int lmt = Math.min(body.getLimit() == null ? COLLECTIONS_DEFAULT_LIMIT : body.getLimit(), COLLECTIONS_LIMIT);
        Collections collections = collectionService.getCollections(body.getGeometry(), lmt, body.getOffset(), false);
        return ResponseEntity.ok(collections);
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
                () -> new WebApplicationException(NOT_FOUND, Error.errorFmt("Collection '%s' not found", collectionId)));
        return ResponseEntity.ok(entity);
    }

    @GetMapping(value = "/{collectionId}/items/{featureId}", produces = APPLICATION_GEO_JSON)
    @Operation(summary = "fetch a single feature", description = "Fetch the feature with id `featureId` in the feature collection with id `collectionId`.  Use content negotiation to request HTML or GeoJSON.", tags = {"Data"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "fetch the feature with id `featureId` in the feature collection with id `collectionId`", content = @Content(schema = @Schema(implementation = FeatureGeoJSON.class))),
            @ApiResponse(responseCode = "404", description = "The requested URI was not found."),
            @ApiResponse(responseCode = "500", description = "A server error occurred.", content = @Content(schema = @Schema(implementation = Exception.class)))})
    public ResponseEntity getFeature(
            @Parameter(in = ParameterIn.PATH, description = "local identifier of a collection", required = true)
            @PathVariable("collectionId") String collectionId,
            @Parameter(in = ParameterIn.PATH, description = "local identifier of a feature", required = true)
            @PathVariable("featureId") String featureId) {
        final FeatureGeoJSON featureGeoJSON = featureService.getFeature(collectionId, featureId).orElseThrow(
                () -> new WebApplicationException(NOT_FOUND, Error.errorFmt("Feature '%s' not found", featureId)));
        return ResponseEntity.ok(featureGeoJSON);
    }

    @GetMapping(value = "/{collectionId}/items", produces = APPLICATION_GEO_JSON)
    @Operation(summary = "fetch features", description = "Fetch features of the feature collection with id `collectionId`.  Every feature in a dataset belongs to a collection. A dataset may consist of multiple feature collections. A feature collection is often a collection of features of a similar type, based on a common schema.  Use content negotiation to request HTML or GeoJSON.", tags = {"Data"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The response is a document consisting of features in the collection. The features included in the response are determined by the server based on the query parameters of the request. To support access to larger collections without overloading the client, the API supports paged access with links to the next page, if more features are selected that the page size.  The `bbox` and `datetime` parameter can be used to select only a subset of the features in the collection (the features that are in the bounding box or time interval). The `bbox` parameter matches all features in the collection that are not associated with a location, too. The `datetime` parameter matches all features in the collection that are not associated with a time stamp or interval, too.  The `limit` parameter may be used to control the subset of the selected features that should be returned in the response, the page size. Each page may include information about the number of selected and returned features (`numberMatched` and `numberReturned`) as well as links to support paging (link relation `next`).", content = @Content(schema = @Schema(implementation = FeatureCollectionGeoJSON.class))),
            @ApiResponse(responseCode = "400", description = "A query parameter has an invalid value.", content = @Content(schema = @Schema(implementation = Exception.class))),
            @ApiResponse(responseCode = "404", description = "The requested URI was not found."),
            @ApiResponse(responseCode = "500", description = "A server error occurred.", content = @Content(schema = @Schema(implementation = Exception.class)))})
    public ResponseEntity getFeatures(
            @Parameter(in = ParameterIn.PATH, description = "local identifier of a collection", required = true)
            @PathVariable("collectionId")
                    String collectionId,
            @Parameter(in = ParameterIn.QUERY, description = "The optional limit parameter limits the number of items that are presented in the response document.  Only items are counted that are on the first level of the collection in the response document. Nested objects contained within the explicitly requested items shall not be counted.  Minimum = 1. Maximum = 100000. Default = 10.", schema = @Schema(allowableValues = {}, minimum = "1", maximum = "100000"))
            @Min(1)
            @RequestParam(value = "limit", defaultValue = "10")
                    Integer limit,
            @Parameter(in = ParameterIn.QUERY, description = "The optional offset parameter specifies the index within the result set from which the server begins presenting results in the response. Minimum = 0", schema = @Schema(allowableValues = {}, minimum = "0"))
            @Min(0)
            @RequestParam(value = "offset", defaultValue = "0")
                    Integer offset,
            @Parameter(explode = Explode.FALSE, style = ParameterStyle.FORM, in = ParameterIn.QUERY, description = "Only features that have a geometry that intersects the bounding box are selected. The bounding box is provided as four or six numbers, depending on whether the coordinate reference system includes a vertical axis (height or depth):  * Lower left corner, coordinate axis 1 * Lower left corner, coordinate axis 2 * Minimum value, coordinate axis 3 (optional) * Upper right corner, coordinate axis 1 * Upper right corner, coordinate axis 2 * Maximum value, coordinate axis 3 (optional)  The coordinate reference system of the values is WGS 84 longitude/latitude (http://www.opengis.net/def/crs/OGC/1.3/CRS84).  For WGS 84 longitude/latitude the values are in most cases the sequence of minimum longitude, minimum latitude, maximum longitude and maximum latitude. However, in cases where the box spans the antimeridian the first value (west-most box edge) is larger than the third value (east-most box edge).  If the vertical axis is included, the third and the sixth number are the bottom and the top of the 3-dimensional bounding box.  If a feature has multiple spatial geometry properties, it is the decision of the server whether only a single spatial geometry property is used to determine the extent or all relevant geometries.")
            @ValidBbox
            @RequestParam(value = "bbox", required = false)
                    List<BigDecimal> bbox,
            @Parameter(in = ParameterIn.QUERY, style = ParameterStyle.SIMPLE, description = "Either a date-time or an interval, open or closed. Date and time expressions adhere to RFC 3339. Open intervals are expressed using double-dots.  Examples:  * A date-time: \"2018-02-12T23:20:50Z\" * A closed interval: \"2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\" * Open intervals: \"2018-02-12T00:00:00Z/..\" or \"../2018-03-18T12:31:12Z\"  Only features that have a temporal property that intersects the value of `datetime` are selected.  If a feature has multiple temporal properties, it is the decision of the server whether only a single temporal property is used to determine the extent or all relevant temporal properties.")
            @RequestParam(value = "datetime", required = false)
                    DateTimeRange datetime) {
        //TODO OGC API - Features - Part 2 needs to be supported
        int lmt = Math.min(limit == null ? COLLECTION_ITEMS_DEFAULT_LIMIT : limit, COLLECTION_ITEMS_LIMIT);
        Optional<FeatureCollectionGeoJSON> fc = featureService.getFeatureCollection(collectionId, lmt, offset,
                null, bbox != null ? bbox : java.util.Collections.emptyList(),
                        datetime, getCriteriaList(), true);
        return ResponseEntity.ok(fc.orElse(new FeatureCollectionGeoJSON()));
    }

    @PostMapping(value = "/{collectionId}/items/search", produces = APPLICATION_GEO_JSON)
    @Operation(summary = "search for features", description = "Search features of the feature collection with id `collectionId`.  Every feature in a dataset belongs to a collection. A dataset may consist of multiple feature collections. A feature collection is often a collection of features of a similar type, based on a common schema.  Use content negotiation to request HTML or GeoJSON.", tags = {"Data"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The response is a document consisting of features in the collection. The features included in the response are determined by the server based on the query parameters of the request. To support access to larger collections without overloading the client, the API supports paged access with links to the next page, if more features are selected that the page size.  The `bbox` and `datetime` parameter can be used to select only a subset of the features in the collection (the features that are in the bounding box or time interval). The `bbox` parameter matches all features in the collection that are not associated with a location, too. The `datetime` parameter matches all features in the collection that are not associated with a time stamp or interval, too.  The `limit` parameter may be used to control the subset of the selected features that should be returned in the response, the page size. Each page may include information about the number of selected and returned features (`numberMatched` and `numberReturned`) as well as links to support paging (link relation `next`).", content = @Content(schema = @Schema(implementation = FeatureCollectionGeoJSON.class))),
            @ApiResponse(responseCode = "400", description = "A query parameter has an invalid value.", content = @Content(schema = @Schema(implementation = Exception.class))),
            @ApiResponse(responseCode = "404", description = "The requested URI was not found."),
            @ApiResponse(responseCode = "500", description = "A server error occurred.", content = @Content(schema = @Schema(implementation = Exception.class)))})
    public ResponseEntity searchFeatures(
            @Parameter(in = ParameterIn.PATH, description = "local identifier of a collection", required = true)
            @PathVariable("collectionId")
                    String collectionId,
            @RequestBody @Valid CollectionsItemsSearchDto itemsSearchDto
    ) {
        //TODO OGC API - Features - Part 2 needs to be supported
        int lmt = Math.min(itemsSearchDto.getLimit() == null ? COLLECTION_ITEMS_DEFAULT_LIMIT : itemsSearchDto.getLimit(), COLLECTION_ITEMS_LIMIT);
        Optional<FeatureCollectionGeoJSON> fc = featureService.getFeatureCollection(collectionId, lmt,
                itemsSearchDto.getOffset(),
                itemsSearchDto.getGeometry(),
                null,
                itemsSearchDto.getDatetime(), getCriteriaList(), false);
        return ResponseEntity.ok(fc.orElse(new FeatureCollectionGeoJSON()));
    }

    private List<FeaturePropertiesFilter> getCriteriaList() {
        final Map<String, String[]> map = Optional.ofNullable(servletRequest.getParameterMap())
                .orElse(Map.of());

        return map.entrySet().stream()
                .filter(stringEntry -> !PREDEFINED_FIELDS.contains(stringEntry.getKey()))
                .map(e -> {
                    if (e.getValue().length > 1) {
                        throw new WebApplicationException(BAD_REQUEST, objectError("incorrect query parameter", fieldError(
                                e.getKey(), error("must not appear multiple times"))));
                    } else {
                        return new FeaturePropertiesFilter(
                                e.getKey(), e.getValue().length == 0 ? null : e.getValue()[0].split(","));
                    }
                })
                .collect(Collectors.toList());
    }
}
