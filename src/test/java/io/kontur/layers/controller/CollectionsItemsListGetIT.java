package io.kontur.layers.controller;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.AbstractIntegrationTest;
import io.kontur.layers.repository.LayerMapper;
import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Feature;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.service.FeatureService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static io.kontur.layers.ApiConstants.APPLICATION_GEO_JSON;
import static io.kontur.layers.CustomMatchers.matchesRfc3339DatePattern;
import static io.kontur.layers.CustomMatchers.url;
import static io.kontur.layers.controller.CollectionsApi.COLLECTION_ITEMS_LIMIT;
import static io.kontur.layers.test.TestDataHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GET /collections/{collectionId}/items")
public class CollectionsItemsListGetIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;
    @Autowired
    private FeatureService featureService;

    @Test
    @DisplayName("response should match spec")
    public void featuresShouldMatchSpec() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN

        assertThat(json, hasJsonPath("$.type", is("FeatureCollection")));
        assertThat(json, hasJsonPath("$.links", not(empty())));
        assertThat(json, hasJsonPath("$.timeStamp", matchesRfc3339DatePattern()));
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.numberMatched", is(1)));
        assertThat(json, hasJsonPath("$.numberReturned", is(1)));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='self' && @.type=='application/geo+json')].href",
                anyOf(contains(url(BASE_URL + "/collections/pubId_1/items?limit=10&offset=0")),
                        contains(url(BASE_URL + "/collections/pubId_1/items")))));

        assertThat(json, hasJsonPath("$.links[?(@.rel=='next' && @.type=='application/geo+json')].href", empty()));

        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[0].type", is("Feature")));
        assertThat(json, hasJsonPath("$.features[0].properties.prop1", is("propValue1_1")));
        assertThat(json, hasJsonPath("$.features[0].properties.prop2", is("propValue2_1")));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_1")));
        assertThat(json, hasJsonPath("$.features[0].links", not(empty())));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("Polygon")));
        assertThat(json, hasJsonPath("$.features[0].links[?(@.rel=='self' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items/featureId_1"))));
        assertThat(json,
                hasJsonPath("$.features[0].links[?(@.rel=='collection' && @.type=='application/geo+json')].href",
                        contains(url(BASE_URL + "/collections/pubId_1"))));
    }

    @Test
    @DisplayName("pagination should work")
    public void paginationShouldWork() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        IntStream.rangeClosed(1, 25).forEach(i -> testDataMapper.insertFeature(id, buildPolygonN(i)));
        //WHEN

        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("limit", "10")
                        .queryParam("offset", "10")
                        .queryParam("prop1", "*")
                        .queryParam("prop2", "*"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.rel=='next' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items?limit=10&offset=20&prop1=*&prop2=*"))));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='prev' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items?limit=10&offset=0&prop1=*&prop2=*"))));
        assertThat(json, hasJsonPath("$.timeStamp", matchesRfc3339DatePattern()));
        assertThat(json, hasJsonPath("$.numberMatched", is(25)));
        assertThat(json, hasJsonPath("$.numberReturned", is(10)));
    }

    @Test
    @DisplayName("should not be 'next' link if no more items")
    public void noNextLinkIfNoMoreItems() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        IntStream.rangeClosed(1, 25).forEach(i -> testDataMapper.insertFeature(id, buildPolygonN(i)));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("limit", "10")
                        .queryParam("offset", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.rel=='next' && @.type=='application/geo+json')].href", empty()));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='prev' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items?limit=10&offset=10"))));
        assertThat(json, hasJsonPath("$.timeStamp", matchesRfc3339DatePattern()));
        assertThat(json, hasJsonPath("$.numberMatched", is(25)));
        assertThat(json, hasJsonPath("$.numberReturned", is(5)));
    }

    @Test
    @DisplayName("should not be 'prev' link if at start")
    public void noPrevLinkIfAtStart() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        IntStream.rangeClosed(1, 25).forEach(i -> testDataMapper.insertFeature(id, buildPolygonN(i)));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("limit", "10")
                        .queryParam("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.rel=='prev' && @.type=='application/geo+json')].href", empty()));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='next' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items?limit=10&offset=10"))));
        assertThat(json, hasJsonPath("$.timeStamp", matchesRfc3339DatePattern()));
        assertThat(json, hasJsonPath("$.numberMatched", is(25)));
        assertThat(json, hasJsonPath("$.numberReturned", is(10)));
    }

    @Test
    @DisplayName("on missing collection should have 404 status and json body")
    public void shouldHaveJsonErrorResponse() throws Exception {
        //GIVEN
        //WHEN
        String json = mockMvc.perform(get("/collections/not_existing/items"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("on unexpected exception 500 status should be returned")
    public void error500() throws Exception {
        //GIVEN
        LayerMapper layerMapperMock = Mockito.mock(LayerMapper.class);
        when(layerMapperMock.getLayerName(any())).thenThrow(new RuntimeException("test"));

        LayerMapper originalLayerMapper = (LayerMapper) ReflectionTestUtils.getField(featureService, "layerMapper");
        ReflectionTestUtils.setField(featureService, "layerMapper", layerMapperMock);

        //WHEN
        String json = mockMvc.perform(get("/collections/some/items"))
                .andDo(print())
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.msg", is("internal server error")));

        ReflectionTestUtils.setField(featureService, "layerMapper", originalLayerMapper);
    }

    @Test
    @DisplayName("limit should be positive ")
    public void limitShouldBeGreaterThan0() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("limit", "-10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.limit.msg", not(emptyOrNullString())));
    }

    private String initForBboxTests() {
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildPolygonN(1));
        return layer.getPublicId();
    }

    @Test
    @DisplayName("bbox may be empty")
    public void bboxEmpty() throws Exception {
        //GIVEN
        String publicLayerId = initForBboxTests();
        //WHEN
        String json = mockMvc.perform(get("/collections/" + publicLayerId + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.numberMatched", is(1)));
    }

    @Test
    @DisplayName("bbox should have 4 coordinates")
    public void bboxShouldHave4Coordinates() throws Exception {
        //GIVEN
        String publicLayerId = initForBboxTests();
        //WHEN
        String json = mockMvc.perform(get("/collections/" + publicLayerId + "/items")
                        .queryParam("bbox", "0,0,30,30"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.numberMatched", is(1)));
    }

    @Test
    @DisplayName("bbox should have 4 coordinates, not 1")
    public void bboxShouldHave4Coordinates_not1() throws Exception {
        //GIVEN
        String publicLayerId = initForBboxTests();
        //WHEN
        String json = mockMvc.perform(get("/collections/" + publicLayerId + "/items")
                        .queryParam("bbox", "0"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.bbox.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("bbox should have 4 coordinates, not 3")
    public void bboxShouldHave4Coordinates_not3() throws Exception {
        //GIVEN
        String publicLayerId = initForBboxTests();
        //WHEN
        String json = mockMvc.perform(get("/collections/" + publicLayerId + "/items")
                        .queryParam("bbox", "1,2,3"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.bbox.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("bbox should have 4 coordinates, not 5")
    public void bboxShouldHave4Coordinates_not5() throws Exception {
        //GIVEN
        String publicLayerId = initForBboxTests();
        //WHEN
        String json = mockMvc.perform(get("/collections/" + publicLayerId + "/items")
                        .queryParam("bbox", "1,2,3,4,5"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.bbox.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("bbox should have valid coordinates")
    public void bboxShouldHaveValidCoordinates() throws Exception {
        //GIVEN
        String publicLayerId = initForBboxTests();
        //WHEN
        String json = mockMvc.perform(get("/collections/" + publicLayerId + "/items")
                        .queryParam("bbox", "-180,-90,180,90"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.numberMatched", is(1)));
    }

    @Test
    @DisplayName("bbox should have valid coordinates (wrong Xmin)")
    public void bboxShouldHaveValidCoordinates_wrongXmin() throws Exception {
        //GIVEN
        String publicLayerId = initForBboxTests();
        //WHEN
        String json = mockMvc.perform(get("/collections/" + publicLayerId + "/items")
                        .queryParam("bbox", "-181,-90,180,90"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.bbox.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("bbox should have valid coordinates (wrong Ymin)")
    public void bboxShouldHaveValidCoordinates_wrongYmin() throws Exception {
        //GIVEN
        String publicLayerId = initForBboxTests();
        //WHEN
        String json = mockMvc.perform(get("/collections/" + publicLayerId + "/items")
                        .queryParam("bbox", "-180,-91,180,90"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.bbox.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("bbox should have valid coordinates (wrong Xmax)")
    public void bboxShouldHaveValidCoordinates_wrongXmax() throws Exception {
        //GIVEN
        String publicLayerId = initForBboxTests();
        //WHEN
        String json = mockMvc.perform(get("/collections/" + publicLayerId + "/items")
                        .queryParam("bbox", "-180,-90,181,90"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.bbox.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("bbox should have valid coordinates (wrong Ymax)")
    public void bboxShouldHaveValidCoordinates_wrongYmax() throws Exception {
        //GIVEN
        String publicLayerId = initForBboxTests();
        //WHEN
        String json = mockMvc.perform(get("/collections/" + publicLayerId + "/items")
                        .queryParam("bbox", "-180,-90,180,91"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.bbox.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("bbox should restrict response by geometry")
    public void bboxShouldRestrictResponseByGeometry() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildPolygonN(1));
        testDataMapper.insertFeature(id, buildPolygonN(3));
        testDataMapper.insertFeature(id, buildPolygonN(4));

        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("bbox", "3,3,4,4"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.numberMatched", is(2)));
        assertThat(json, hasJsonPath("$.numberReturned", is(2)));
    }

    @Test
    @DisplayName("response for invalid query param should be correct (bug#2348)")
    public void responseForInvalidQueryParamShouldBeCorrect() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("limit", "opop"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.limit.msg", is("invalid numeric value")));
    }

    @Test
    @DisplayName("If you ask for 500000, you might get up to 100000 (server-limited) and if there are more, a next link;")
    public void serverLimit1500() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        IntStream.rangeClosed(0, 101).forEach(value -> {
            final List<Feature> features = IntStream.rangeClosed(1, 1001)
                    .mapToObj(i -> buildFeatureN(value * 10000 + i, null))
                    .collect(Collectors.toList());
            testDataMapper.insertFeatures(id, features);
        });
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("limit", String.valueOf(COLLECTION_ITEMS_LIMIT)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.numberMatched", is(102102)));
        assertThat(json, hasJsonPath("$.numberReturned", is(100000)));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='next' && @.type=='application/geo+json')].href",
                                     contains(BASE_URL + "/collections/pubId_1/items?limit=100000&offset=100000")));
    }

    @Test
    @DisplayName("offset should be positive ")
    public void offsetShouldBePositive() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("offset", "-5"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.offset.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("'Point' feature should match spec")
    public void pointFeatureShouldMatchSpec() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildPointN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("Point")));
        assertThat(json, hasJsonPath("$.features[0].geometry.coordinates", contains(0, 1)));
    }

    @Test
    @DisplayName("'GeometryCollection' feature should match spec")
    public void geometryCollectionFeatureShouldMatchSpec() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildGeometryCollectionN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("GeometryCollection")));
        assertThat(json, hasJsonPath("$.features[0].geometry.geometries[0].type", is("Point")));
        assertThat(json, hasJsonPath("$.features[0].geometry.geometries[0].coordinates", contains(0, 1)));
        assertThat(json, hasJsonPath("$.features[0].geometry.geometries[1].type", is("LineString")));
        assertThat(json, hasJsonPath("$.features[0].geometry.geometries[1].coordinates", contains(
                contains(0, 0), contains(1, 0), contains(1, 1))));
    }

    @Test
    @DisplayName("'LineString' feature should match spec")
    public void lineStringFeatureShouldMatchSpec() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildLineStringN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("LineString")));
        assertThat(json, hasJsonPath("$.features[0].geometry.coordinates", contains(
                contains(0, 0), contains(1, 0), contains(1, 1))));
    }

    @Test
    @DisplayName("'MultiLineString' feature should match spec")
    public void multiLineStringFeatureShouldMatchSpec() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildMultiLineStringN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("MultiLineString")));
    }

    @Test
    @DisplayName("'MultiPoint' feature should match spec")
    public void multiPointFeatureShouldMatchSpec() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildMultipointN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("MultiPoint")));
    }

    @Test
    @DisplayName("'MultiPolygon' feature should match spec")
    public void multiPolygonFeatureShouldMatchSpec() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        final String featureWkt = "SRID=4326;MULTIPOLYGON(((0 0,4 0,4 4,0 4,0 0),(1 1,2 1,2 2,1 2,1 1)), ((-1 -1,-1 -2,-2 -2,-2 -1,-1 -1)))";
        final OffsetDateTime lastUpdated = OffsetDateTime.of(2020, 4, 15, 15, 30, 0, 0, offset()).plusMinutes(10);
        testDataMapper.insertFeature(id, new Feature(null, "featureId_" + 10, featureWkt, null, lastUpdated));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("MultiPolygon")));
    }

    @Test
    @DisplayName("null fields must be skipped")
    public void nullFieldsMustBeSkipped() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildMultiPolygonN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.hreflang)]", empty()));
    }

    @Test
    @DisplayName("'next' link should work (bug#2353)")
    public void nextLinkShouldWork() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        IntStream.rangeClosed(1, 25).forEach(i -> testDataMapper.insertFeature(id, buildPolygonN(i)));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();

        final List<String> nextLinks = JsonPath.read(json, "$.links[?(@.rel=='next' && @.type=='application/geo+json')].href");
        String s = mockMvc.perform(get(nextLinks.get(0)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(s, hasJsonPath("$.numberMatched", is(25)));
        assertThat(s, hasJsonPath("$.numberReturned", is(10)));
    }

    @Test
    @DisplayName("property filter exact match should work")
    public void exactMatchPropertyFilterShouldWork() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildPolygonN(1));
        testDataMapper.insertFeature(id, buildPolygonN(2));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("prop1", "propValue1_2"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.numberMatched", is(1)));
        assertThat(json, hasJsonPath("$.numberReturned", is(1)));
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[?(@.properties.prop1=='propValue1_2')]", hasSize(1)));
    }

    @Test
    @DisplayName("property filter sql like pattern should be escaped")
    public void sqlLikePatternEscaped() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        final Feature feature1 = buildPolygonN(1);
        ((ObjectNode) feature1.getProperties()).put("prop1", "xx%aa");
        final Feature feature2 = buildPolygonN(2);
        ((ObjectNode) feature2.getProperties()).put("prop1", "%x%");

        testDataMapper.insertFeatures(id, List.of(feature1, feature2));
        //WHEN

        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("prop1", "%x%"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
    }

    @Test
    @DisplayName("field in property filter must appear not more then once")
    public void fieldInPropFilterMustAppearNotMoreThenOnce() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("prop1", "propValue1_1")
                        .queryParam("prop1", "propValue1_2"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.prop1.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("property filters should be combined with AND")
    public void propertyFiltersCombinedWithAnd() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        IntStream.rangeClosed(1, 21).forEach(i -> {
            testDataMapper.insertFeature(id, buildPolygonN(i));
        });
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("prop1", "*1")
                        .queryParam("prop2", "propValue2_1*"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.numberMatched", is(2)));
        assertThat(json, hasJsonPath("$.numberReturned", is(2)));
        assertThat(json, hasJsonPath("$.features", hasSize(2)));

        assertThat(json, hasJsonPath(
                "$.features[?(@.properties.prop1=='propValue1_1' && @.properties.prop2=='propValue2_1')]", hasSize(1)));
        assertThat(json, hasJsonPath(
                "$.features[?(@.properties.prop1=='propValue1_11' && @.properties.prop2=='propValue2_11')]", hasSize(1)));
    }

    @Test
    @DisplayName("property filter wildcard match should work")
    public void wildcardPropertyFilterShouldWork() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        IntStream.rangeClosed(1, 21).forEach(i -> {
            testDataMapper.insertFeature(id, buildPolygonN(i));
        });
        //WHEN
         String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("prop1", "*1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.numberMatched", is(3)));
        assertThat(json, hasJsonPath("$.numberReturned", is(3)));
        assertThat(json, hasJsonPath("$.features", hasSize(3)));

        assertThat(json, hasJsonPath("$.features[?(@.properties.prop1=='propValue1_1')]", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[?(@.properties.prop1=='propValue1_11')]", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[?(@.properties.prop1=='propValue1_21')]", hasSize(1)));
    }

    @Test
    @DisplayName("property filter for missing property should not be ignored")
    public void missingPropertyShouldNotBeIgnored() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        IntStream.rangeClosed(1, 21).forEach(i -> testDataMapper.insertFeature(id, buildPolygonN(i)));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("limit", "30")
                        .queryParam("prop3", "*"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", empty()));
    }

    @Test
    @DisplayName("'datetime' interval should work")
    public void dateTimeParameterShouldWork() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        final Feature feature1 = buildPolygonN(1);
        testDataMapper.insertFeature(id, feature1);
        final Feature feature2 = buildPolygonN(2);
        testDataMapper.insertFeature(id, feature2);
        testDataMapper.insertFeature(id, buildPolygonN(3));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                .queryParam("datetime", feature1.getLastUpdated() + "/" + feature2.getLastUpdated()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(2)));
        assertThat(json, hasJsonPath("$.features[?(@.id=='featureId_1')]", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[?(@.id=='featureId_2')]", hasSize(1)));
    }

    @Test
    @DisplayName("'datetime' interval with open end should work")
    public void dateTimeParameterOpenEndShouldWork() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        final Feature feature1 = buildPolygonN(1);
        testDataMapper.insertFeature(id, feature1);
        final Feature feature2 = buildPolygonN(2);
        testDataMapper.insertFeature(id, feature2);
        testDataMapper.insertFeature(id, buildPolygonN(3));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("datetime", feature2.getLastUpdated() + "/.."))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(2)));
        assertThat(json, hasJsonPath("$.features[?(@.id=='featureId_2')]", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[?(@.id=='featureId_3')]", hasSize(1)));
    }

    @Test
    @DisplayName("'datetime' interval with open start should work")
    public void dateTimeParameterOpenStartShouldWork() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        final Feature feature1 = buildPolygonN(1);
        testDataMapper.insertFeature(id, feature1);
        final Feature feature2 = buildPolygonN(2);
        testDataMapper.insertFeature(id, feature2);
        testDataMapper.insertFeature(id, buildPolygonN(3));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("datetime", "../" + feature2.getLastUpdated()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(2)));
        assertThat(json, hasJsonPath("$.features[?(@.id=='featureId_1')]", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[?(@.id=='featureId_2')]", hasSize(1)));
    }

    @Test
    @DisplayName("'datetime' interval with double open ended value should fail")
    public void dateTimeDoubleOpenEndedShouldFail() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("datetime", "../.."))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.datetime.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("'datetime' validation should work")
    public void dateTimeValidationShouldWork() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("datetime", "2020-04-03T12:00:00")) //invalid date - missing timezone
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.datetime.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("'datetime' exact match should work")
    public void dateTimeExactMatchShouldWork() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        final Feature feature1 = buildPolygonN(1);
        final OffsetDateTime someDate = OffsetDateTime.of(2020, 4, 15, 15, 30, 0, 0, offset());
        feature1.setLastUpdated(someDate);
        testDataMapper.insertFeature(id, feature1);
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("datetime", someDate.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
    }

    @Test
    @DisplayName("'datetime' parameter should have start before end")
    public void dateTimeStartShouldBeBeforeEnd() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final OffsetDateTime someDate = OffsetDateTime.of(2020, 4, 15, 15, 30, 0, 0, offset());
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("datetime", someDate.toString() + "/" + someDate.minusMinutes(1).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.datetime.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("empty geometry is valid case as per requirement (C. in /req/core/fc-bbox-response )")
    public void emptyGeometryIsValidCase() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildFeatureN(1, null));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("bbox", "0,0,30,30"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
    }

    @Test
    @DisplayName("z-coord for 'bbox' should be supported as meters above/below surface")
    public void zCoordForBboxParam() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildFeatureN(1, "SRID=4326;POINT(0 1 1000)"));
        testDataMapper.insertFeature(id, buildFeatureN(2, "SRID=4326;POINT(0 1 0)"));
        testDataMapper.insertFeature(id, buildFeatureN(3, "SRID=4326;POINT(0 2 -2000)"));
        testDataMapper.insertFeature(id, buildFeatureN(4, "SRID=4326;POINT(0 3)"));
        testDataMapper.insertFeature(id, buildFeatureN(5, "SRID=4326;POINT(0 1 1001)"));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items")
                        .queryParam("bbox", "0,0,0,3,3,1000"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(3)));
        assertThat(json, hasJsonPath("$.features[?(@.id=='featureId_1')]", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[?(@.id=='featureId_2')]", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[?(@.id=='featureId_4')]", hasSize(1)));
    }

    @Test
    @DisplayName("empty response should have status 200 and empty feature collection")
    public void emptyResponse() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN

        assertThat(json, hasJsonPath("$.type", is("FeatureCollection")));
        assertThat(json, hasJsonPath("$.features", empty()));
    }
}
