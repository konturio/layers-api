package io.kontur.layers.controller;

import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.service.FeatureService;
import io.kontur.layers.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static io.kontur.layers.ApiConstants.APPLICATION_GEO_JSON;
import static io.kontur.layers.test.CustomMatchers.matchesRfc3339DatePattern;
import static io.kontur.layers.test.CustomMatchers.url;
import static io.kontur.layers.test.TestDataHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST /collections/{collectionId}/items/search")
public class CollectionsItemsSearchPostIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;
    @Autowired
    private FeatureService featureService;

    @Test
    @DisplayName("response should match spec")
    public void featuresShouldMatchSpec() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();
        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        String json = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.type", is("FeatureCollection")));
        assertThat(json, hasJsonPath("$.timeStamp", matchesRfc3339DatePattern()));
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.numberMatched", is(1)));
        assertThat(json, hasJsonPath("$.numberReturned", is(1)));

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
    @DisplayName("limit should work")
    public void limitIsWorking() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();
        testDataMapper.insertFeature(id, buildPolygonN(1));
        testDataMapper.insertFeature(id, buildPolygonN(2));
        testDataMapper.insertFeature(id, buildPolygonN(3));
        //WHEN
        String json = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"limit\":2}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(2)));
        assertThat(json, hasJsonPath("$.numberMatched", is(3)));
        assertThat(json, hasJsonPath("$.numberReturned", is(2)));
    }

    @Test
    @DisplayName("offset should work")
    public void offsetIsWorking() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();
        testDataMapper.insertFeature(id, buildPolygonN(1));
        testDataMapper.insertFeature(id, buildPolygonN(2));
        testDataMapper.insertFeature(id, buildPolygonN(3));
        //WHEN
        String json = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"limit\":2, \"offset\": 0}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(2)));
        assertThat(json, hasJsonPath("$.numberMatched", is(3)));
        assertThat(json, hasJsonPath("$.numberReturned", is(2)));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_1")));
        assertThat(json, hasJsonPath("$.features[1].id", is("featureId_2")));

        //WHEN
        json = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"limit\":2, \"offset\": 2}"))

                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.numberMatched", is(3)));
        assertThat(json, hasJsonPath("$.numberReturned", is(1)));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_3")));
    }

    @Test
    @DisplayName("geometry filter should work")
    public void geometryFilterIsWorking() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();
        testDataMapper.insertFeature(id, buildPolygonN(1));
        testDataMapper.insertFeature(id, buildPolygonN(2));
        testDataMapper.insertFeature(id, buildPolygonN(3));
        //WHEN
        String json = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"geometry\":{\"type\":\"Point\",\"coordinates\":[0,3]}}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.numberMatched", is(1)));
        assertThat(json, hasJsonPath("$.numberReturned", is(1)));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_3")));
    }

    @Test
    @DisplayName("should return items for user's private collection")
    @WithMockUser("owner_1")
    public void featuresFromPrivateCollections() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        ReflectionTestUtils.setField(layer, "isPublic", false);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();
        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        String json = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN

        assertThat(json, hasJsonPath("$.type", is("FeatureCollection")));
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_1")));
    }

    @Test
    @DisplayName("shouldn't return items for other user's private collection")
    @WithMockUser("some_user")
    public void featuresFromPrivateCollections_OtherUser() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        ReflectionTestUtils.setField(layer, "isPublic", false);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();
        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
    }

    @Test
    @DisplayName("features from private collections should not be public")
    public void featuresFromPrivateCollections_AnonymousUser() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        ReflectionTestUtils.setField(layer, "isPublic", false);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();
        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
    }

    @Test
    @DisplayName("should not obtain features for non visible layer")
    public void getFeatureForNonVisibleLayer() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        layer.setVisible(false);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();
        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        //THEN
        mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void notValidGeometryInFilter_8985() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        //WHEN
        String json = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[24.609375,12.897489183755892],[75.234375,12.897489183755892],[75.234375,45.336701909968134],[24.609375,45.336701909968134],[24.609375,12.897489183755892],[24.60222475,12.897412383755892]]]}}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.geometry.msg", not(emptyOrNullString())));
    }

    @Test
    public void featuresFromApplication() throws Exception {
        //GIVEN
        Application app = buildApplication(1);
        app.setShowAllPublicLayers(false);
        testDataMapper.insertApplication(app);

        final Layer layer = buildLayerN(1);
        ReflectionTestUtils.setField(layer, "isPublic", false);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();

        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_1", 1), app.getId());

        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        String json = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content(String.format("{\"appId\":\"%s\"}", app.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN

        assertThat(json, hasJsonPath("$.type", is("FeatureCollection")));
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_1")));
    }

    @Test
    public void featuresFromNonPublicApplicationAreNotVisible() throws Exception {
        //GIVEN
        Application app = buildApplication(1);
        app.setShowAllPublicLayers(false);
        app.setIsPublic(false);
        testDataMapper.insertApplication(app);

        final Layer layer = buildLayerN(1);
        ReflectionTestUtils.setField(layer, "isPublic", false);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();

        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_1", 1), app.getId());

        testDataMapper.insertFeature(id, buildPolygonN(1));
        //WHEN
        String json = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content(String.format("{\"appId\":\"%s\"}", app.getId())))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
    }

    @Test
    public void featuresFromDN2ApplicationWithGeometryFilter() throws Exception {
        //GIVEN
        Application app = buildApplication(1);
        app.setShowAllPublicLayers(true);
        testDataMapper.insertApplication(app);

        final Layer layer = buildLayerN(1);
        ReflectionTestUtils.setField(layer, "isPublic", true);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();

        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_1", 1), app.getId());

        testDataMapper.insertFeature(id, buildPolygonN(1));
        testDataMapper.insertFeature(id, buildPolygonN(2));
        //WHEN
        String json = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items/search")
                        .contentType(APPLICATION_JSON)
                        .content(String.format("{\"geometry\":{\"type\":\"Point\",\"coordinates\":[0,2]}, " +
                                "\"appId\":\"%s\"}", app.getId())))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN

        assertThat(json, hasJsonPath("$.type", is("FeatureCollection")));
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_2")));
    }
}
