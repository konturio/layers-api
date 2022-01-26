package io.kontur.layers.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.dto.CollectionUpdateDto;
import io.kontur.layers.test.AbstractIntegrationTest;
import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Layer;
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
    @DisplayName("should return collections owned by a user")
    @WithMockUser("owner_3")
    public void testGetOwnedCollection() throws Exception {
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
        assertThat(json.read("$.collections[*].id"), containsInAnyOrder("pubId_1", "pubId_3"));
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
        collection.setGeometry(JsonUtil.readJson("{\"type\":\"Polygon\",\"coordinates\":[[[98.3111572265625,68.32423359706064],[98.887939453125,68.32423359706064],[98.887939453125,68.52421309659984],[98.3111572265625,68.52421309659984],[98.3111572265625,68.32423359706064]]]}",
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
                        .content("{\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[98.3111572265625,68.32423359706064],[98.887939453125,68.32423359706064],[98.887939453125,68.52421309659984],[98.3111572265625,68.52421309659984],[98.3111572265625,68.32423359706064]]]},\"limit\":1,\"offset\":0}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json.read("$.collections[*].id"), contains("pubId_1"));
    }

}