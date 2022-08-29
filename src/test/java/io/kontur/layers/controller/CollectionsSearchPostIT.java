package io.kontur.layers.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.dto.CollectionUpdateDto;
import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.test.AbstractIntegrationTest;
import io.kontur.layers.util.JsonUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.wololo.geojson.Geometry;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static io.kontur.layers.test.TestDataHelper.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST /collections/search")
public class CollectionsSearchPostIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @DisplayName("should return links and collections")
    public void testGetCollection() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        testDataMapper.insertLayer(buildLayerN(2));
        testDataMapper.insertLayer(buildLayerN(3));
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.links"), is(empty()));
        assertThat(json.read("$.collections"), hasSize(3));

        assertThat(json, hasJsonPath("$.collections[0].featureProperties.featureProp1", is("featureProperty_1")));

    }

    @Test
    @DisplayName("should limit collections to 1")
    public void testGetCollectionLimit1() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        testDataMapper.insertLayer(buildLayerN(2));
        testDataMapper.insertLayer(buildLayerN(3));
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"limit\":1}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json.read("$.numberMatched"), is(3));
        assertThat(json.read("$.numberReturned"), is(1));
    }

    @Test
    @DisplayName("pagination should work")
    public void testGetCollectionPagination() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        testDataMapper.insertLayer(buildLayerN(2));
        testDataMapper.insertLayer(buildLayerN(3));
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"limit\":2, \"offset\":0}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(2));
        assertThat(json.read("$.numberMatched"), is(3));
        assertThat(json.read("$.numberReturned"), is(2));

        //WHEN
        response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"limit\":2, \"offset\":2}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json.read("$.numberMatched"), is(3));
        assertThat(json.read("$.numberReturned"), is(1));
    }

    @Test
    @DisplayName("geometry intersection should work")
    public void testGetCollectionGeometryIntersection() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        testDataMapper.insertLayer(buildLayerN(2));
        testDataMapper.insertLayer(buildLayerN(3));
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"geometry\":{\"type\":\"Point\",\"coordinates\":[0,3]}}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json, hasJsonPath("$.collections[0].id", is("pubId_3")));
        assertThat(json.read("$.numberMatched"), is(1));
    }

    @Test
    @DisplayName("geometry intersection shouldn't find layers with null geometry")
    public void testGetCollectionNullGeometryIntersection() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        Layer layer = buildLayerN(2);
        ReflectionTestUtils.setField(layer, "geometry", null);
        testDataMapper.insertLayer(layer);

        testDataMapper.insertLayer(buildLayerN(3));
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"geometry\":{\"type\":\"Point\",\"coordinates\":[0,3]}}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json, hasJsonPath("$.collections[0].id", is("pubId_3")));
        assertThat(json.read("$.numberMatched"), is(1));
    }

    @Test
    public void testGetCollectionGeometryIntersectionAndGlobal() throws Exception {
        //GIVEN
        Layer layer1 = buildLayerN(1);
        ReflectionTestUtils.setField(layer1, "geometry", null);
        ReflectionTestUtils.setField(layer1, "isGlobal", false);
        testDataMapper.insertLayer(layer1);
        Layer layer2 = buildLayerN(2);
        ReflectionTestUtils.setField(layer2, "geometry", null);
        ReflectionTestUtils.setField(layer2, "isGlobal", true);
        testDataMapper.insertLayer(layer2);

        testDataMapper.insertLayer(buildLayerN(3));
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"geometry\":{\"type\":\"Point\",\"coordinates\":[0,3]}}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(2));
        assertThat(json, hasJsonPath("$.collections[0].id", is("pubId_2")));
        assertThat(json, hasJsonPath("$.collections[1].id", is("pubId_3")));
        assertThat(json.read("$.numberMatched"), is(2));
    }

    @Test
    public void testGetGlobalCollections() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        Layer layer = buildLayerN(2);
        ReflectionTestUtils.setField(layer, "geometry", null);
        ReflectionTestUtils.setField(layer, "isGlobal", true);
        testDataMapper.insertLayer(layer);

        testDataMapper.insertLayer(buildLayerN(3));
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"omitLocalCollections\": true}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json, hasJsonPath("$.collections[0].id", is("pubId_2")));
        assertThat(json.read("$.numberMatched"), is(1));
    }

    @Test
    @DisplayName("geometry intersection should find layers by feature geometry")
    public void testGetCollectionFeatureGeometryIntersection() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));

        Layer layer = buildLayerN(2);
        ReflectionTestUtils.setField(layer, "geometry", null);
        long id = testDataMapper.insertLayer(layer);
        testDataMapper.insertFeature(id, buildPolygonN(3));

        testDataMapper.insertLayer(buildLayerN(3));
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"geometry\":{\"type\":\"Point\",\"coordinates\":[0,3]}}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(2));
    }

    @Test
    @DisplayName("geometry is geojson only")
    public void testGetCollectionGeometry_Fail() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        testDataMapper.insertLayer(buildLayerN(2));
        testDataMapper.insertLayer(buildLayerN(3));
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"geometry\":\"POINT(0 3)\"}"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.fieldErrors", not(emptyOrNullString())));
    }

    @Test
    @WithMockUser("owner_3")
    public void testGetOwnedAndPublicCollections() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        Layer layer2 = buildLayerN(2);
        ReflectionTestUtils.setField(layer2, "isPublic", false);
        testDataMapper.insertLayer(layer2);
        Layer layer3 = buildLayerN(3);
        ReflectionTestUtils.setField(layer3, "isPublic", false);
        testDataMapper.insertLayer(layer3);
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(2));
        assertThat(json.read("$.numberMatched"), is(2));
        assertThat(json.read("$.numberReturned"), is(2));
        assertThat(json.read("$.collections[*].id"), containsInAnyOrder("pubId_1", "pubId_3"));
    }

    @Test
    @WithMockUser("owner_3")
    public void testGetOwnedAndPublicCollections_2() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        Layer layer2 = buildLayerN(2);
        ReflectionTestUtils.setField(layer2, "isPublic", false);
        testDataMapper.insertLayer(layer2);
        Layer layer3 = buildLayerN(3);
        ReflectionTestUtils.setField(layer3, "isPublic", false);
        testDataMapper.insertLayer(layer3);
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"collectionOwner\": \"ANY\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(2));
        assertThat(json.read("$.numberMatched"), is(2));
        assertThat(json.read("$.numberReturned"), is(2));
        assertThat(json.read("$.collections[*].id"), containsInAnyOrder("pubId_1", "pubId_3"));
    }

    @Test
    @WithMockUser("owner_3")
    public void testGetOnlyOwnedCollection() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        Layer layer2 = buildLayerN(2);
        ReflectionTestUtils.setField(layer2, "isPublic", false);
        testDataMapper.insertLayer(layer2);
        Layer layer3 = buildLayerN(3);
        ReflectionTestUtils.setField(layer3, "isPublic", false);
        testDataMapper.insertLayer(layer3);
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"collectionOwner\": \"ME\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json.read("$.numberMatched"), is(1));
        assertThat(json.read("$.numberReturned"), is(1));
        assertThat(json.read("$.collections[*].id"), containsInAnyOrder("pubId_3"));
    }

    @Test
    @WithMockUser("owner_3")
    public void testGetOthersCollections() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        Layer layer2 = buildLayerN(2);
        ReflectionTestUtils.setField(layer2, "isPublic", false);
        testDataMapper.insertLayer(layer2);
        Layer layer3 = buildLayerN(3);
        ReflectionTestUtils.setField(layer3, "isPublic", false);
        testDataMapper.insertLayer(layer3);
        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"collectionOwner\": \"NOT_ME\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json.read("$.numberMatched"), is(1));
        assertThat(json.read("$.numberReturned"), is(1));
        assertThat(json.read("$.collections[*].id"), containsInAnyOrder("pubId_1"));
    }

    @Test
    @DisplayName("should not obtain non visible layer")
    public void testSearchNonVisibleCollection() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        layer.setVisible(false);
        testDataMapper.insertLayer(layer);

        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.links"), is(empty()));
        assertThat(json.read("$.collections"), is(empty()));
    }

    @Test
    @WithMockUser("owner_1")
    public void searchForCollectionAfterCreation_8837() throws Exception {
        //GIVEN
        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        collection.setGeometry(JsonUtil.readJson(
                "{\"type\":\"Polygon\",\"coordinates\":[[[98.3111572265625,68.32423359706064],[98.887939453125,68.32423359706064],[98.887939453125,68.52421309659984],[98.3111572265625,68.52421309659984],[98.3111572265625,68.32423359706064]]]}",
                Geometry.class));

        mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content(
                                "{\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[98.3111572265625,68.32423359706064],[98.887939453125,68.32423359706064],[98.887939453125,68.52421309659984],[98.3111572265625,68.52421309659984],[98.3111572265625,68.32423359706064]]]},\"limit\":1,\"offset\":0}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json.read("$.collections[*].id"), contains("pubId_1"));
    }

    @Test
    public void InvalidGeometryInFilter_8985() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        //WHEN
        String json = mockMvc.perform(post("/collections/search")
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
    public void getApplication() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        testDataMapper.insertLayer(buildLayerN(2));
        testDataMapper.insertLayer(buildLayerN(3));

        Application app = buildApplication(1);
        testDataMapper.insertApplication(app);

        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_1", 1), app.getId());
        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_2", 2), app.getId());
        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_3", 3), app.getId());


        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"appId\": \"" + app.getId().toString() + "\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(3));
        assertThat(json.read("$.collections[*].id"), containsInAnyOrder("pubId_1", "pubId_2", "pubId_3"));
        assertThat(json.read("$.collections[*].styleRule"), not(empty()));
    }

    @Test
    public void getApplicationWithNotPublicLayers() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        layer.setPublic(false);
        testDataMapper.insertLayer(layer);
        Layer layer2 = buildLayerN(2);
        layer2.setVisible(false);
        testDataMapper.insertLayer(layer2);
        testDataMapper.insertLayer(buildLayerN(3));

        Application app = buildApplication(1);
        testDataMapper.insertApplication(app);

        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_1", 1), app.getId());
        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_2", 2), app.getId());
        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_3", 3), app.getId());


        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"appId\": \"" + app.getId().toString() + "\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json.read("$.collections[*].id"), containsInAnyOrder("pubId_3"));
        assertThat(json.read("$.collections[*].styleRule"), not(empty()));
    }

    @Test
    public void getNotPublicLayerApplicationWithNotPublicLayers() throws Exception {
        //GIVEN
        Layer layer = buildLayerN(1);
        layer.setPublic(false);
        testDataMapper.insertLayer(layer);
        Layer layer2 = buildLayerN(2);
        layer2.setVisible(false);
        testDataMapper.insertLayer(layer2);
        testDataMapper.insertLayer(buildLayerN(3));

        Application app = buildApplication(1);
        app.setShowAllPublicLayers(false);
        testDataMapper.insertApplication(app);

        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_1", 1), app.getId());
        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_2", 2), app.getId());
        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_3", 3), app.getId());


        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"appId\": \"" + app.getId().toString() + "\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(2));
        assertThat(json.read("$.collections[*].id"), containsInAnyOrder("pubId_1", "pubId_3"));
        assertThat(json.read("$.collections[*].styleRule"), not(empty()));
    }

    @Test
    public void getNotPublicApplication() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        testDataMapper.insertLayer(buildLayerN(2));
        testDataMapper.insertLayer(buildLayerN(3));

        Application app = buildApplication(1);
        app.setIsPublic(false);
        testDataMapper.insertApplication(app);

        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_1", 1), app.getId());
        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_2", 2), app.getId());
        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_3", 3), app.getId());


        //WHEN
        mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"appId\": \"" + app.getId().toString() + "\"}"))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(APPLICATION_JSON));

        //THEN
    }

    @Test
    public void filterByCollectionIds() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        testDataMapper.insertLayer(buildLayerN(2));
        testDataMapper.insertLayer(buildLayerN(3));

        Application app = buildApplication(1);
        testDataMapper.insertApplication(app);

        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_1", 1), app.getId());
        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_2", 2), app.getId());
        testDataMapper.insertApplicationLayer(buildApplicationLayerDto("pubId_3", 3), app.getId());


        //WHEN
        String response = mockMvc.perform(post("/collections/search")
                        .contentType(APPLICATION_JSON)
                        .content("{\"collectionIds\":[\"pubId_2\", \"pubId_3\", \"not_exists\"], " +
                                "\"appId\": \"" + app.getId().toString() + "\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(2));
        assertThat(json.read("$.collections[*].id"), containsInAnyOrder("pubId_2", "pubId_3"));
        assertThat(json.read("$.collections[*].styleRule"), not(empty()));
    }
}