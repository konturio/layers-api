package io.kontur.layers.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.dto.CollectionCreateDto;
import io.kontur.layers.dto.CollectionUpdateDto;
import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.test.AbstractIntegrationTest;
import io.kontur.layers.util.JsonUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static io.kontur.layers.test.CustomMatchers.url;
import static io.kontur.layers.test.TestDataHelper.buildCollectionCreateDtoN;
import static io.kontur.layers.test.TestDataHelper.buildLayerN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("PUT /collections/{id}")
public class CollectionsPutIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

    @Test
    @DisplayName("should update a layer")
    @WithMockUser("pigeon")
    public void testPutCollection() throws Exception {
        //GIVEN
        CollectionCreateDto collection = buildCollectionCreateDtoN(0);
        String id = collection.getId();
        mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //WHEN
        CollectionCreateDto updatedCollection = buildCollectionCreateDtoN(1);

        String response = mockMvc.perform(put("/collections/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(updatedCollection)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.id", is("pubId_0")));
        assertThat(json, hasJsonPath("$.title", is("name_1")));
        assertThat(json, hasJsonPath("$.description", is("description_1")));
        assertThat(json, hasJsonPath("$.copyrights", is("copyrights_1")));
        assertThat(json, hasJsonPath("$.properties.prop1", is("propValue1_1")));
        assertThat(json, hasJsonPath("$.properties.prop2", is("propValue2_1")));
        assertThat(json, hasJsonPath("$.featureProperties.featureProp1", is("featureProperty_1")));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='tiles')].href", contains(url("https://www.example.com"))));
        assertThat(json, hasNoJsonPath("$.extent"));//absent because no features
    }

    @Test
    @DisplayName("404 when layer does not exist")
    @WithMockUser("pigeon")
    public void collectionIsNotFound() throws Exception {
        //GIVEN
        CollectionCreateDto collection = buildCollectionCreateDtoN(0);
        mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //WHEN
        collection = buildCollectionCreateDtoN(1);
        mockMvc.perform(put("/collections/" + collection.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("should update collection by public id")
    @WithMockUser("owner_2")
    public void updateWhatNeedsToBeUpdated() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        Layer layer = buildLayerN(2);
        String id = layer.getPublicId();
        testDataMapper.insertLayer(layer);
        testDataMapper.insertLayer(buildLayerN(3));

        CollectionCreateDto updatedCollection = buildCollectionCreateDtoN(100);

        mockMvc.perform(put("/collections/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(updatedCollection)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //WHEN
        String response = mockMvc.perform(get("/collections"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(3));
        assertThat(json.read("$.collections[?(@.id=='pubId_1')].title"), contains("name_1"));
        assertThat(json.read("$.collections[?(@.id=='pubId_1')].title"), hasSize(1));
        assertThat(json.read("$.collections[?(@.id=='pubId_2')].title"), contains("name_100"));
        assertThat(json.read("$.collections[?(@.id=='pubId_2')].title"), hasSize(1));
        assertThat(json.read("$.collections[?(@.id=='pubId_3')].title"), contains("name_3"));
        assertThat(json.read("$.collections[?(@.id=='pubId_3')].title"), hasSize(1));
    }

    @Test
    @DisplayName("restrict unauthorized access")
    public void testAuthorization() throws Exception {
        //GIVEN
        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        //WHEN
        mockMvc.perform(put("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().is4xxClientError())
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @WithMockUser("pigeon")
    public void collectionWithInvalidGeometryIsNotSaved_8985() throws Exception {
        //GIVEN
        CollectionCreateDto collection = buildCollectionCreateDtoN(0);
        String id = collection.getId();
        mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //WHEN
        String collectionString = "{\"description\":\"\",\"title\":\"test title\",\"itemType\":\"tiles\",\"copyrights\":\"\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[98.3111572265625,68.32423359706064],[98.887939453125,68.32423359706064],[98.887939453125,68.52421309659984],[98.3111572265625,68.52421309659984]]]}}";

        String json = mockMvc.perform(put("/collections/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(collectionString))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.geometry.msg", not(emptyOrNullString())));
    }

}