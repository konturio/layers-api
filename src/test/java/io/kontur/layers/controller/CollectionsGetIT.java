package io.kontur.layers.controller;

import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.repository.model.LayerFeature;
import io.kontur.layers.test.AbstractIntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static io.kontur.layers.test.CustomMatchers.url;
import static io.kontur.layers.test.TestDataHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GET /collections/{collectionId}")
public class CollectionsGetIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @DisplayName("response should match spec")
    public void testGetCollection() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.id", is("pubId_1")));
        assertThat(json, hasJsonPath("$.title", is("name_1")));
        assertThat(json, hasJsonPath("$.description", is("description_1")));
        assertThat(json, hasJsonPath("$.copyrights[0]", is("copyrights_1")));
        assertThat(json, hasJsonPath("$.properties.prop1", is("propValue1_1")));
        assertThat(json, hasJsonPath("$.properties.prop2", is("propValue2_1")));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='items' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items"))));
        assertThat(json, hasNoJsonPath("$.extent"));//absent because no features
        assertThat(json, hasJsonPath("$.ownedByUser", is(false)));
    }

    @Test
    @DisplayName("missing collection should respond with 404")
    public void missingCause404() throws Exception {
        //GIVEN
        //WHEN
        String json = mockMvc.perform(get("/collections/missing"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("3d spatial extent must be correct")
    @Disabled("As extent is optional it was removed for performance reason #8857")
    public void spatialExtent3dMustBeCorrect() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildFeatureN(1, "POINT(0 1 1000)"));
        testDataMapper.insertFeature(id, buildFeatureN(2, "POINT(0 1 0)"));
        testDataMapper.insertFeature(id, buildFeatureN(3, "POINT(0 2 -2000)"));
        testDataMapper.insertFeature(id, buildFeatureN(4, "POINT(0 3)"));
        testDataMapper.insertFeature(id, buildFeatureN(5, "POINT(0 1 1001)"));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.extent.spatial.bbox", contains(contains(0, 1, -2000, 0, 3, 1001))));
        assertThat(json, hasJsonPath("$.extent.spatial.crs", url("http://www.opengis.net/def/crs/OGC/1.3/CRS84")));
    }

    @Test
    @DisplayName("2d spatial extent must be correct")
    @Disabled("As extent is optional it was removed for performance reason #8857")
    public void spatialExtent2dMustBeCorrect() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildFeatureN(1, "POINT(0 2)"));
        testDataMapper.insertFeature(id, buildFeatureN(2, "POINT(0 1)"));
        testDataMapper.insertFeature(id, buildFeatureN(3, "POINT(-1 2)"));
        testDataMapper.insertFeature(id, buildFeatureN(4, "POINT(0 3)"));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.extent.spatial.bbox", contains(contains(-1, 1, 0, 3))));
        assertThat(json, hasJsonPath("$.extent.spatial.crs", url("http://www.opengis.net/def/crs/OGC/1.3/CRS84")));
    }

    @Test
    @DisplayName("temporal extent must be correct")
    @Disabled("As extent is optional it was removed for performance reason #8857")
    public void temporalExtentMustBeCorrect() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        final LayerFeature feature1 = buildPolygonN(1);
        final LayerFeature feature2 = buildPolygonN(2);
        final LayerFeature feature3 = buildPolygonN(3);
        final LayerFeature feature4 = buildPolygonN(4);
        testDataMapper.insertFeatures(id, List.of(feature1, feature2, feature3, feature4));
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.extent.temporal.interval", contains(
                contains(feature1.getLastUpdated().toString(), feature4.getLastUpdated().toString()))));
        assertThat(json,
                hasJsonPath("$.extent.temporal.trs", url("http://www.opengis.net/def/uom/ISO-8601/0/Gregorian")));
    }

    @Test
    @DisplayName("single item temporal extent must be correct")
    @Disabled("As extent is optional it was removed for performance reason #8857")
    public void singleItemTemporalExtentMustBeCorrect() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);
        final LayerFeature feature1 = buildPolygonN(1);
        testDataMapper.insertFeature(id, feature1);
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.extent.temporal.interval", contains(
                contains(feature1.getLastUpdated().toString(), feature1.getLastUpdated().toString()))));
        assertThat(json,
                hasJsonPath("$.extent.temporal.trs", url("http://www.opengis.net/def/uom/ISO-8601/0/Gregorian")));
    }

    @Test
    @DisplayName("should not obtain non visible layer")
    public void testGetNonVisibleCollection() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        layer.setVisible(false);
        testDataMapper.insertLayer(layer);
        //WHEN
        //THEN
        mockMvc.perform(get("/collections/" + layer.getPublicId()))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));
    }

    @Test
    @DisplayName("url to tiles is present for tiles layers")
    public void returnLinkToTiles_8626() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        layer.setType("tiles");
        layer.setUrl("https://example.com");
        testDataMapper.insertLayer(layer);
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.rel=='tiles')].href", contains("https://example.com")));
    }

    @Test
    public void returnLink_RasterLayer() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        layer.setType("raster");
        layer.setUrl("https://example.com");
        testDataMapper.insertLayer(layer);
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.rel=='tiles')].href", contains("https://example.com")));
    }

    @Test
    public void returnLink_VectorLayer() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        layer.setType("vector");
        layer.setUrl("https://example.com");
        testDataMapper.insertLayer(layer);
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.rel=='tiles')].href", contains("https://example.com")));
    }
}