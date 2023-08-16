package io.kontur.layers.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.repository.TestDataMapper;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST /collections/{id}/items")
public class CollectionsItemsPostIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @WithMockUser("owner_1")
    public void testPostItems() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        testDataMapper.insertFeature(id, buildPointN(1));

        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"featureId_2\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue2_via_rest\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.96484375,39.774769485295465]}}]}";

        //WHEN
        String response = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items")
                        .contentType(APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.type", is("FeatureCollection")));
        assertThat(json, hasJsonPath("$.timeStamp", matchesRfc3339DatePattern()));
        assertThat(json, hasJsonPath("$.features", hasSize(1)));
        assertThat(json, hasNoJsonPath("$.numberMatched"));
        assertThat(json, hasJsonPath("$.numberReturned", is(1)));

        assertThat(json, hasJsonPath("$.features[0].type", is("Feature")));
        assertThat(json, hasJsonPath("$.features[0].properties.prop1", is("propValue2_via_rest")));
        assertThat(json, hasNoJsonPath("$.features[0].properties.prop2"));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_2")));
        assertThat(json, hasJsonPath("$.features[0].links", not(empty())));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("Point")));
        assertThat(json, hasJsonPath("$.features[0].links[?(@.rel=='self' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items/featureId_2"))));
        assertThat(json,
                hasJsonPath("$.features[0].links[?(@.rel=='collection' && @.type=='application/geo+json')].href",
                        contains(url(BASE_URL + "/collections/pubId_1"))));
    }

    @Test
    @WithMockUser("owner_1")
    public void testPostAndGetItems() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        testDataMapper.insertFeature(id, buildPointN(1));

        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"featureId_2\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue2_via_rest\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.96484375,39.774769485295465]}}]}";

        //WHEN
        mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items")
                        .contentType(APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();

        String response = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.type", is("FeatureCollection")));
        assertThat(json, hasJsonPath("$.timeStamp", matchesRfc3339DatePattern()));
        assertThat(json, hasJsonPath("$.features", hasSize(2)));
        assertThat(json, hasJsonPath("$.numberMatched", is(2)));
        assertThat(json, hasJsonPath("$.numberReturned", is(2)));

        assertThat(json, hasJsonPath("$.features[0].type", is("Feature")));
        assertThat(json, hasJsonPath("$.features[0].properties.prop1", is("propValue1_1")));
        assertThat(json, hasJsonPath("$.features[0].properties.prop2", is("propValue2_1")));
        assertThat(json, hasJsonPath("$.features[0].id", is("featureId_1")));
        assertThat(json, hasJsonPath("$.features[0].links", not(empty())));
        assertThat(json, hasJsonPath("$.features[0].geometry.type", is("Point")));
        assertThat(json, hasJsonPath("$.features[0].links[?(@.rel=='self' && @.type=='application/geo+json')].href",
                contains(url(BASE_URL + "/collections/pubId_1/items/featureId_1"))));
        assertThat(json,
                hasJsonPath("$.features[0].links[?(@.rel=='collection' && @.type=='application/geo+json')].href",
                        contains(url(BASE_URL + "/collections/pubId_1"))));

        assertThat(json, hasJsonPath("$.features[1].type", is("Feature")));
        assertThat(json, hasJsonPath("$.features[1].properties.prop1", is("propValue2_via_rest")));
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
    @WithMockUser("pigeon")
    public void testPostItemsIntoOtherUsersCollection() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);

        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"featureId_1\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue1_updated\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.96484375,39.774769485295465]}},{\"id\":\"featureId_2\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue2_inserted\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[9.2724609375,39.50404070558415]}}]}";

        //WHEN
        mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items")
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
        mockMvc.perform(post("/collections/whatever/items")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @WithMockUser("owner_1")
    public void featureIdValidation_8700() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);

        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"id_{}\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue1_updated\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.96484375,39.774769485295465]}}]}";

        //WHEN
        String response = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items")
                        .contentType(APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.fieldErrors.id.msg", not(emptyOrNullString())));

    }

    @Test
    @WithMockUser("owner_1")
    public void emptyIdReturnBadRequest() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        testDataMapper.insertLayer(layer);

        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue1_updated\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.96484375,39.774769485295465]}}]}";

        //WHEN
        mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items")
                        .contentType(APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().string("{\"msg\":\"Feature id is missing\"}"))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @WithMockUser("owner_1")
    public void addFeaturesWithInvalidGeometry_8985() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        String geoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\",\"id\":\"feature_id4\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[29.135742187499996,53.30462107510271],[29.794921874999996,53.30462107510271],[29.794921874999996,54.03358633521085],[29.135742187499996,54.03358633521085],[29.135742187499996,54.30462107510271]]]}}]}";

        //WHEN
        String response = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items")
                        .contentType(APPLICATION_JSON)
                        .content(geoJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.fieldErrors.body.msg", not(emptyOrNullString())));
    }

    @Test
    @WithMockUser("owner_1")
    public void testPostEmptyItems() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        testDataMapper.insertFeature(id, buildPointN(1));
        testDataMapper.insertFeature(id, buildPointN(10));
        testDataMapper.insertFeature(id, buildPointN(50));

        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[]}";

        mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items")
                        .contentType(APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();

        //WHEN
        String response2 = mockMvc.perform(get("/collections/" + layer.getPublicId() + "/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json2 = JsonPath.parse(response2);
        assertThat(json2, hasJsonPath("$.features", hasSize(3)));
    }

    @Test
    @WithMockUser("owner_1")
    public void testPostExistingItems() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        final long id = testDataMapper.insertLayer(layer);

        testDataMapper.insertFeature(id, buildPointN(1));

        String updatedGeoJson = "{\"type\":\"FeatureCollection\",\"features\":[{\"id\":\"featureId_1\",\"type\":\"Feature\",\"properties\":{\"prop1\":\"propValue2_via_rest\"},\"geometry\":{\"type\":\"Point\",\"coordinates\":[8.96484375,39.774769485295465]}}]}";

        //WHEN
        String response = mockMvc.perform(post("/collections/" + layer.getPublicId() + "/items")
                        .contentType(APPLICATION_JSON)
                        .content(updatedGeoJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.type", is("FeatureCollection")));
    }
}