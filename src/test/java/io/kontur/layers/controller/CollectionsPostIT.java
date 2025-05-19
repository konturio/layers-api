package io.kontur.layers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.dto.CollectionCreateDto;
import io.kontur.layers.dto.CollectionUpdateDto;
import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Application;
import io.kontur.layers.test.AbstractIntegrationTest;
import io.kontur.layers.util.JsonUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.UUID;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasNoJsonPath;
import static io.kontur.layers.ApiConstants.APPLICATION_GEO_JSON;
import static io.kontur.layers.test.CustomMatchers.url;
import static io.kontur.layers.test.TestDataHelper.buildApplication;
import static io.kontur.layers.test.TestDataHelper.buildCollectionCreateDtoN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("POST /collections")
public class CollectionsPostIT extends AbstractIntegrationTest {

    @Autowired
    private TestDataMapper testDataMapper;

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
        assertThat(json, hasJsonPath("$.copyrights[0]", is("copyrights_1")));
        assertThat(json, hasJsonPath("$.properties.prop1", is("propValue1_1")));
        assertThat(json, hasJsonPath("$.properties.prop2", is("propValue2_1")));
        assertThat(json, hasJsonPath("$.featureProperties.featureProp1", is("featureProperty_1")));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='tiles')].href", contains(url("https://www.example.com"))));
        assertThat(json, hasJsonPath("$.tileSize", is(1)));
        assertThat(json, hasJsonPath("$.minZoom", is(1)));
        assertThat(json, hasJsonPath("$.maxZoom", is(1)));
        assertThat(json, hasNoJsonPath("$.extent"));//absent because no features
    }

    @Test
    @WithMockUser("pigeon")
    public void testPostRasterCollection() throws Exception {
        //GIVEN
        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        collection.setItemType(CollectionUpdateDto.Type.raster);
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
        assertThat(json, hasJsonPath("$.itemType", is("raster")));
        assertThat(json, hasJsonPath("$.description", is("description_1")));
        assertThat(json, hasJsonPath("$.copyrights[0]", is("copyrights_1")));
        assertThat(json, hasJsonPath("$.properties.prop1", is("propValue1_1")));
        assertThat(json, hasJsonPath("$.properties.prop2", is("propValue2_1")));
        assertThat(json, hasJsonPath("$.featureProperties.featureProp1", is("featureProperty_1")));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='tiles')].href", contains(url("https://www.example.com"))));
        assertThat(json, hasNoJsonPath("$.extent"));//absent because no features
    }

    @Test
    @WithMockUser("pigeon")
    public void testPostAndGetCollection() throws Exception {
        //GIVEN
        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        //WHEN
        mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        String json = mockMvc.perform(get("/collections/pubId_1"))
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
        assertThat(json, hasJsonPath("$.featureProperties.featureProp1", is("featureProperty_1")));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='tiles')].href", contains(url("https://www.example.com"))));
        assertThat(json, hasNoJsonPath("$.extent"));//absent because no features
    }

    @Test
    @WithMockUser("pigeon")
    public void collectionIdCanBeNull_9028() throws Exception {
        //GIVEN
        CollectionCreateDto collection = buildCollectionCreateDtoN(1);
        collection.setId(null);
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
        assertThat(json, hasJsonPath("$.id", not(emptyOrNullString())));
    }

    @Test
    @WithMockUser("pigeon")
    public void collectionIdCanBeEmpty_9028() throws Exception {
        //GIVEN
        CollectionCreateDto collection = buildCollectionCreateDtoN(1);
        collection.setId("");
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
        assertThat(json, hasJsonPath("$.id", not(emptyOrNullString())));
    }

    @Test
    @WithMockUser("pigeon")
    public void collectionIdCantContainSpecialSymbols_8700() throws Exception {
        //GIVEN
        CollectionCreateDto collection = buildCollectionCreateDtoN(1);
        collection.setId("not_these_{}[]");
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
    @WithMockUser("pigeon")
    public void itemTypeShouldNotBeNull_8701() throws Exception {
        //GIVEN
        String collection = "{\"name\":\"Name1\",\"itemType\":null,\"properties\":{},\"id\":\"myId_1\"}";
        //WHEN
        String response = mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(collection))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.fieldErrors.itemType.msg", not(emptyOrNullString())));
    }

    @Test
    @WithMockUser("pigeon")
    public void validationMessageShouldContainFieldName_8702() throws Exception {
        //GIVEN
        String collection = "{\"name\":\"Name1\",\"itemType\":\"\",\"properties\":{},\"id\":\"myId_1\"}";

        //WHEN
        String response = mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(collection))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.fieldErrors.itemType.msg", not(emptyOrNullString())));
    }

    @Test
    @WithMockUser("pigeon")
    public void validationMessageShouldContainFieldName_8702_2() throws Exception {
        //GIVEN
        String collection = "{\"title\":[],\"properties\":{},\"id\":\"myId_1\"}";

        //WHEN
        String response = mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(collection))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasJsonPath("$.fieldErrors.title.msg", not(emptyOrNullString())));
    }

    @Test
    @WithMockUser("pigeon")
    public void validationMessageForInvalidJsonShouldNotHaveFieldStructure_8702_3() throws Exception {
        //GIVEN
        String collection = "{\"title\":\"fooBar\"\"description\":\"string\"}";

        //WHEN
        String response = mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(collection))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json, hasNoJsonPath("$.fieldErrors.title.msg"));
        assertThat(json, hasJsonPath("$.msg", not(emptyOrNullString())));
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
                .andExpect(status().isForbidden())
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

    @Test
    @WithMockUser("pigeon")
    public void featuresForLayersWithoutTitleCanBeFound_8986() throws Exception {
        //GIVEN
        String collectionWithoutTitle = "{\"description\":\"\",\"link\":{\"href\":\"http://data.example.com/buildings/123\",\"rel\":\"alternate\",\"type\":\"application/geo+json\",\"hreflang\":\"en\",\"title\":\"My home\",\"length\":0},\"itemType\":\"vector\",\"copyrights\":[],\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[98.3111572265625,68.32423359706064],[98.887939453125,68.32423359706064],[98.887939453125,68.52421309659984],[98.3111572265625,68.52421309659984],[98.3111572265625,68.32423359706064]]]},\"properties\":{},\"legend\":{},\"id\":\"test_layer15\"}";

        mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(collectionWithoutTitle))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //WHEN
        //THEN
        mockMvc.perform(get("/collections/test_layer15/items"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(APPLICATION_GEO_JSON))
                .andReturn().getResponse().getContentAsString();

    }

    @Test
    @WithMockUser("pigeon")
    public void collectionWithInvalidGeometryIsNotSaved_8985() throws Exception {
        //GIVEN
        String collection = "{\"description\":\"\",\"title\":\"test title\",\"itemType\":\"raster\",\"copyrights\":[],\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[[[98.3111572265625,68.32423359706064],[98.887939453125,68.32423359706064],[98.887939453125,68.52421309659984],[98.3111572265625,68.52421309659984]]]},\"id\":\"test_layer15\"}";

        //WHEN
        String json = mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(collection))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.geometry.msg", not(emptyOrNullString())));
    }

    @Test
    @WithMockUser("pigeon")
    public void addNewLayersIntoUserLayersGroup() throws Exception {
        //GIVEN
        new TransactionTemplate(transactionManager)
                .execute(status ->
                        jdbcTemplate.update(
                                "INSERT INTO layers_group_properties (name) " +
                                        "VALUES ('user_layers') " +
                                        "ON CONFLICT (name) DO NOTHING;"));

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
        assertThat(json, hasJsonPath("$.group.name", is("user_layers")));
    }

    @Test
    @WithMockUser("pigeon")
    public void saveNewCollectionStyleRule() throws Exception {
        //GIVEN
        Application app = buildApplication(1);
        testDataMapper.insertApplication(app);

        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        collection.setAppId(app.getId());
        final ObjectNode legendStyle = new ObjectMapper().createObjectNode();
        legendStyle.put("rule1", "legendValue1");
        collection.setLegendStyle(legendStyle);

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
        assertThat(json, hasJsonPath("$.legendStyle.rule1", is("legendValue1")));
    }

    @Test
    @WithMockUser("pigeon")
    public void saveNewCollectionDisplayRule() throws Exception {
        //GIVEN
        Application app = buildApplication(1);
        testDataMapper.insertApplication(app);

        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        collection.setAppId(app.getId());
        final ObjectNode rule = new ObjectMapper().createObjectNode();
        rule.put("rule1", "legendValue1");
        collection.setDisplayRule(rule);

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
        assertThat(json, hasJsonPath("$.displayRule.rule1", is("legendValue1")));
    }

    @Test
    @WithMockUser("pigeon")
    public void saveNewCollectionStyleAndDisplayRule() throws Exception {
        //GIVEN
        Application app = buildApplication(1);
        testDataMapper.insertApplication(app);

        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        collection.setAppId(app.getId());
        final ObjectNode rule = new ObjectMapper().createObjectNode();
        rule.put("rule1", "legendValue1");
        collection.setDisplayRule(rule);
        final ObjectNode legendStyle = new ObjectMapper().createObjectNode();
        legendStyle.put("styleRule1", "styleRuleValue1");
        collection.setLegendStyle(legendStyle);

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
        assertThat(json, hasJsonPath("$.displayRule.rule1", is("legendValue1")));
        assertThat(json, hasJsonPath("$.legendStyle.styleRule1", is("styleRuleValue1")));
    }

    @Test
    @WithMockUser("pigeon")
    public void skipStyleRuleSaving_UnknownAppId() throws Exception {
        //GIVEN
        CollectionUpdateDto collection = buildCollectionCreateDtoN(1);
        collection.setAppId(UUID.randomUUID());
        final ObjectNode legendStyle = new ObjectMapper().createObjectNode();
        legendStyle.put("rule1", "legendValue1");
        collection.setLegendStyle(legendStyle);

        //WHEN
        mockMvc.perform(post("/collections")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JsonUtil.writeJson(collection)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        //THEN;
    }
}