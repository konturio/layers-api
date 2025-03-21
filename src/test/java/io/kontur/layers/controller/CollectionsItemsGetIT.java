package io.kontur.layers.controller;

import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.repository.model.LayerFeature;
import io.kontur.layers.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static io.kontur.layers.ApiConstants.APPLICATION_GEO_JSON;
import static io.kontur.layers.test.CustomMatchers.url;
import static io.kontur.layers.test.TestDataHelper.buildLayerN;
import static io.kontur.layers.test.TestDataHelper.buildPolygonN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GET /collections/{collectionId}/items/{itemId}")
public class CollectionsItemsGetIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @DisplayName("response should match spec")
    public void featuresShouldMatchSpec() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();
        final LayerFeature feature = buildPolygonN(1);
        testDataMapper.insertFeature(id, feature);
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items/" + feature.getFeatureId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.type", is("Feature")));
        assertThat(json, hasJsonPath("$.properties.prop1", is("propValue1_1")));
        assertThat(json, hasJsonPath("$.properties.prop2", is("propValue2_1")));
        assertThat(json, hasJsonPath("$.id", is("featureId_1")));
        assertThat(json, hasJsonPath("$.geometry.type", is("Polygon")));
        assertThat(json, hasJsonPath("$.geometry.coordinates", contains(contains(
                contains(0.0, 0.0), contains(1.0, 0.0), contains(1.0, 1.0), contains(0.0, 1.0), contains(0.0, 0.0)))));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='self' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items/featureId_1"))));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='collection' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1"))));
    }

    @Test
    @DisplayName("on missing collection should respond with 404 status and json body")
    public void shouldHaveJsonErrorResponse() throws Exception {
        //GIVEN
        //WHEN
        String json = mockMvc.perform(get("/collections/missingCollectionId/items/any"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN

        assertThat(json, hasJsonPath("$.msg", containsString("missingCollectionId")));
    }

    @Test
    @DisplayName("on missing feature should respond with 404 status and json body")
    public void shouldBe404OnMissingFeature() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);
        //WHEN
        String json = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items/missingFeatureId"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.msg", containsString("missingFeatureId")));
    }

    @Test
    @DisplayName("should not obtain features for non visible layer")
    public void getFeatureForNonVisibleLayer() throws Exception {
        //GIVEN
        final Layer layer = buildLayerN(1);
        layer.setVisible(false);
        final testDataMapper.insertLayer(layer);
long id = layer.getId();
        final LayerFeature feature = buildPolygonN(1);
        testDataMapper.insertFeature(id, feature);
        //WHEN
        //THEN
        mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items/" + feature.getFeatureId()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}