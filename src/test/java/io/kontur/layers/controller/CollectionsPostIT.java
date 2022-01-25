package io.kontur.layers.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.dto.CollectionCreateDto;
import io.kontur.layers.dto.CollectionUpdateDto;
import io.kontur.layers.test.AbstractIntegrationTest;
import io.kontur.layers.util.JsonUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static io.kontur.layers.test.CustomMatchers.url;
import static io.kontur.layers.test.TestDataHelper.buildCollectionCreateDtoN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST /collections")
public class CollectionsPostIT extends AbstractIntegrationTest {

    @Test
    @DisplayName("should save new layer")
    @WithMockUser("pigeon")
    public void testPostCollection() throws Exception {
        //GIVEN
        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        //WHEN
        String response = mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.id", is("pubId_1")));
        assertThat(json, hasJsonPath("$.title", is("name_1")));
        assertThat(json, hasJsonPath("$.description", is("description_1")));
        assertThat(json, hasJsonPath("$.copyrights", is("copyrights_1")));
        assertThat(json, hasJsonPath("$.properties.prop1", is("propValue1_1")));
        assertThat(json, hasJsonPath("$.properties.prop2", is("propValue2_1")));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='tiles')].href", contains(url("https://www.example.com"))));
        assertThat(json, hasNoJsonPath("$.extent"));//absent because no features
    }

    @Test
    @DisplayName("id is required")
    @WithMockUser("pigeon")
    public void collectionIdCantBeNull() throws Exception {
        //GIVEN
        CollectionCreateDto collection = buildCollectionCreateDtoN(1);
        collection.setId(null);
        //WHEN
        String response = mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.fieldErrors.id.msg", not(emptyOrNullString())));
    }


    @Test
    @DisplayName("empty string is not valid id #8697")
    @WithMockUser("pigeon")
    public void collectionIdCantBeEmpty_8697() throws Exception {
        //GIVEN
        CollectionCreateDto collection = buildCollectionCreateDtoN(1);
        collection.setId("");
        //WHEN
        String response = mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.fieldErrors.id.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("should not be able to save layers with the same public id")
    @WithMockUser("pigeon")
    public void shouldBeUnableToDuplicatePublicId() throws Exception {
        //GIVEN
        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        //WHEN
        mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        String response = mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(response, hasJsonPath("$.msg", is("Layer with such id already exists")));
    }

    @Test
    @DisplayName("restrict unauthorized access")
    public void testAuthorization() throws Exception {
        //GIVEN
        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        //WHEN
        mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    @DisplayName("save new layer without geometry")
    @WithMockUser("pigeon")
    public void testPostCollectionWithoutGeometry() throws Exception {
        //GIVEN
        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        collection.setGeometry(null);
        //WHEN
        String response = mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.id", is("pubId_1")));
        assertThat(json, hasJsonPath("$.title", is("name_1")));
    }

}