package io.kontur.layers.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Feature;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.test.AbstractIntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static io.kontur.layers.ApiConstants.APPLICATION_GEO_JSON;
import static io.kontur.layers.test.CustomMatchers.matchesRfc3339DatePattern;
import static io.kontur.layers.test.CustomMatchers.url;
import static io.kontur.layers.test.TestDataHelper.buildLayerN;
import static io.kontur.layers.test.TestDataHelper.buildPointN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PUT /collections/{id}/items")
public class CollectionsItemsPutIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @DisplayName("should upsert items")
    @WithMockUser("owner_1")
    public void testPutItems() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        Feature feature1 = buildPointN(1);
        testDataMapper.insertFeature(id, feature1);

        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"featureId_1\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue1_updated\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.96484375,39.774769485295465]}},{\"id\":\"featureId_2\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue2_inserted\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[9.2724609375,39.50404070558415]}}]}";

        //WHEN
        String response = mockMvc.perform(put("/collections/" + layer.getPublicId() + "/items")
                        .contentType(APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.type", is("FeatureCollection")));
        assertThat(json, hasNoJsonPath("$.links"));
        assertThat(json, hasJsonPath("$.timeStamp", matchesRfc3339DatePattern()));
        assertThat(json, hasJsonPath("$.features", hasSize(2)));
        assertThat(json, hasNoJsonPath("$.numberMatched"));
        assertThat(json, hasJsonPath("$.numberReturned", is(2)));

        assertThat(json, hasJsonPath("$.features[0].type", is("Feature")));
        assertThat(json, hasJsonPath("$.features[0].properties.prop1", is("propValue1_updated")));
        assertThat(json, hasNoJsonPath("$.features[0].properties.prop2"));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_1")));
        assertThat(json, hasJsonPath("$.features[0].links", not(empty())));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("Point")));
        assertThat(json, hasJsonPath("$.features[0].links[?(@.rel=='self' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items/featureId_1"))));
        assertThat(json,
                hasJsonPath("$.features[0].links[?(@.rel=='collection' && @.type=='application/geo+json')].href",
                        contains(url(BASE_URL + "/collections/pubId_1"))));

        assertThat(json, hasJsonPath("$.features[1].type", is("Feature")));
        assertThat(json, hasJsonPath("$.features[1].properties.prop1", is("propValue2_inserted")));
        assertThat(json, hasNoJsonPath("$.features[1].properties.prop2"));
        assertThat(json, hasJsonPath("$.features[1].id", is("featureId_2")));
        assertThat(json, hasJsonPath("$.features[1].links", not(empty())));
        assertThat(json, hasJsonPath("$.features[1].geometry.type", is("Point")));
        assertThat(json, hasJsonPath("$.features[1].links[?(@.rel=='self' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items/featureId_2"))));
        assertThat(json,
                hasJsonPath("$.features[0].links[?(@.rel=='collection' && @.type=='application/geo+json')].href",
                        contains(url(BASE_URL + "/collections/pubId_1"))));
    }

    @Test
    @DisplayName("try upsert and get")
    @WithMockUser("owner_1")
    public void testPutGetItems() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        Feature feature1 = buildPointN(1);
        testDataMapper.insertFeature(id, feature1);

        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"featureId_1\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue1_updated\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.96484375,39.774769485295465]}},{\"id\":\"featureId_2\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue2_inserted\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[9.2724609375,39.50404070558415]}}]}";

        mockMvc.perform(put("/collections/" + layer.getPublicId() + "/items")
                        .contentType(APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON));

        //WHEN
        String response = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.features", hasSize(2)));

        assertThat(json, hasJsonPath("$.features[0].type", is("Feature")));
        assertThat(json, hasJsonPath("$.features[0].properties.prop1", is("propValue1_updated")));
        assertThat(json, hasNoJsonPath("$.features[0].properties.prop2"));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_1")));
        assertThat(json, hasJsonPath("$.features[0].links", not(empty())));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("Point")));
        assertThat(json, hasJsonPath("$.features[0].links[?(@.rel=='self' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items/featureId_1"))));
        assertThat(json,
                hasJsonPath("$.features[0].links[?(@.rel=='collection' && @.type=='application/geo+json')].href",
                        contains(url(BASE_URL + "/collections/pubId_1"))));

        assertThat(json, hasJsonPath("$.features[1].type", is("Feature")));
        assertThat(json, hasJsonPath("$.features[1].properties.prop1", is("propValue2_inserted")));
        assertThat(json, hasNoJsonPath("$.features[1].properties.prop2"));
        assertThat(json, hasJsonPath("$.features[1].id", is("featureId_2")));
        assertThat(json, hasJsonPath("$.features[1].links", not(empty())));
        assertThat(json, hasJsonPath("$.features[1].geometry.type", is("Point")));
        assertThat(json, hasJsonPath("$.features[1].links[?(@.rel=='self' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items/featureId_2"))));
        assertThat(json,
                hasJsonPath("$.features[0].links[?(@.rel=='collection' && @.type=='application/geo+json')].href",
                        contains(url(BASE_URL + "/collections/pubId_1"))));
    }

    @Test
    @DisplayName("should not be able to put items into other users collection")
    @WithMockUser("pigeon")
    public void testPutItemsIntoOtherUsersCollection() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);

        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"featureId_1\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue1_updated\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.96484375,39.774769485295465]}},{\"id\":\"featureId_2\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue2_inserted\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[9.2724609375,39.50404070558415]}}]}";

        //WHEN
        String response = mockMvc.perform(put("/collections/" + layer.getPublicId() + "/items")
                        .contentType(APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("restrict unauthorized access")
    public void testAuthorization() throws Exception {
        //GIVEN
        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"featureId_1\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue1_updated\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.96484375,39.774769485295465]}},{\"id\":\"featureId_2\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue2_inserted\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[9.2724609375,39.50404070558415]}}]}";

        //WHEN
        mockMvc.perform(put("/collections/whatever/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
    }
}