package io.kontur.layers.controller;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import io.kontur.layers.repository.TestDataMapper;
import io.kontur.layers.repository.model.Layer;
import io.kontur.layers.repository.model.LayerFeature;
import io.kontur.layers.test.AbstractIntegrationTest;
import net.minidev.json.JSONArray;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.stream.IntStream;

import static com.jayway.jsonpath.matchers.JsonPathMatchers.hasJsonPath;
import static io.kontur.layers.test.CustomMatchers.url;
import static io.kontur.layers.test.TestDataHelper.buildLayerN;
import static io.kontur.layers.test.TestDataHelper.buildPolygonN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("GET /collections")
public class CollectionsListGetIT extends AbstractIntegrationTest {

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
        String response = mockMvc.perform(get("/collections"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.links"), not(empty()));

        final JSONArray selfs = json.read("$.links[?(@.rel=='self' && @.type=='application/json')].href");
        assertThat(json.read("$.links[?(@.rel=='self' && @.type=='application/json')]"), hasSize(1));
        assertThat(selfs.get(0), is(BASE_URL + "/collections"));

        assertThat(json.read("$.links[?(@.rel && @.type)]"), hasSize(json.read("$.links", List.class).size()));
        assertThat(json.read("$.collections"), hasSize(3));

        assertThat(json, hasJsonPath("$.collections[0].featureProperties.featureProp1", is("featureProperty_1")));
    }

    @Test
    @DisplayName("multiple collections must have correct extents")
    @Disabled("As extent is optional it was removed for performance reason #8857")
    public void testCollectionExtents() throws Exception {
        //GIVEN
        final LayerFeature f1 = buildPolygonN(1);
        final LayerFeature f2 = buildPolygonN(2);
        final LayerFeature f3 = buildPolygonN(3);
        final LayerFeature f4 = buildPolygonN(4);

        final long id1 = testDataMapper.insertLayer(buildLayerN(1));
        testDataMapper.insertFeature(id1, f1);
        testDataMapper.insertFeature(id1, f2);

        final long id2 = testDataMapper.insertLayer(buildLayerN(2));
        testDataMapper.insertFeature(id2, f3);
        testDataMapper.insertFeature(id2, f4);
        //WHEN
        final String json = mockMvc.perform(get("/collections"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        assertThat(json, hasJsonPath("$.collections", hasSize(2)));

        assertThat(json, hasJsonPath("$.collections[?(@.id=='pubId_1')].extent.spatial.bbox", contains(
                contains(contains(0, 0, 2, 2)))));
        assertThat(json, hasJsonPath("$.collections[?(@.id=='pubId_1')].extent.temporal.interval", contains(
                contains(contains(f1.getLastUpdated().toString(), f2.getLastUpdated().toString())))));

        assertThat(json, hasJsonPath("$.collections[?(@.id=='pubId_2')].extent.spatial.bbox", contains(
                contains(contains(0, 0, 4, 4)))));
        assertThat(json, hasJsonPath("$.collections[?(@.id=='pubId_2')].extent.temporal.interval", contains(
                contains(contains(f3.getLastUpdated().toString(), f4.getLastUpdated().toString())))));
    }

    @Test
    @DisplayName("collection object must match spec")
    public void testCollectionStructure() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        //WHEN
        final String jsonStr = mockMvc.perform(get("/collections"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(jsonStr);
        assertThat(json.read("$.collections"), hasSize(1));
        assertThat(json.read("$.collections[0].id"), is("pubId_1"));
        assertThat(json.read("$.collections[0].title"), is("name_1"));
        assertThat(json.read("$.collections[0].description"), is("description_1"));
        assertThat(json.read("$.collections[0].links[?(@.rel && @.type)]"),
                hasSize(json.read("$.collections[0].links", List.class).size()));
    }

    @Test
    @DisplayName("collection items link type must be geo+json")
    public void linkToItemsMustBeGeoJson() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        //WHEN
        final String response = mockMvc.perform(get("/collections"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections[0].links[?(@.rel=='items' && @.type=='application/geo+json' && @.href)]"),
                hasSize(1));
    }

    @Test
    @DisplayName("null fields must be skipped")
    public void nullFieldsMustBeSkipped() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        //WHEN
        final String json = mockMvc.perform(get("/collections"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.hreflang)]", empty()));
    }

    @Test
    @DisplayName("empty collection list should work")
    public void emptyCollection() throws Exception {
        //GIVEN
        //WHEN
        mockMvc.perform(get("/collections"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        //THEN
    }

    @Test
    @DisplayName("collection items link href must be absolute url")
    public void itemsLinkMustBeAbsolute() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        //WHEN
        final String response = mockMvc.perform(get("/collections"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        final DocumentContext json = JsonPath.parse(response);
        final JSONArray read = json.read(
                "$.collections[0].links[?(@.rel=='items' && @.type=='application/geo+json')].href");
        assertThat(read, hasSize(1));
        assertThat(read.get(0), is(BASE_URL + "/collections/pubId_1/items"));
    }

    @Test
    @DisplayName("collection items link must handle `Host` header")
    public void proxyHeadersMustBeUsed() throws Exception {
        //GIVEN
        testDataMapper.insertLayer(buildLayerN(1));
        //WHEN
        final String json = mockMvc.perform(get("/collections")
                        .header("Host", "proxyhost:123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.collections[0].links[?(@.rel=='items' && @.type=='application/geo+json')].href",
                hasJsonPath("$[0]", is("http://proxyhost:123/collections/pubId_1/items"))));
    }

//    @Test
//    @DisplayName("check `Host`+`X-Forwarded-Proto` works")
//    public void checkHostForwardedProto() throws Exception {
//        //GIVEN
//        testDataMapper.insertLayer(buildLayerN(1));
//        //WHEN
//        final String json = mockMvc.perform(get("/collections")
//                        .header( "Host", "proxyhost:2222")
//                        .header("X-Forwarded-Proto", "https"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse().getContentAsString();
//        //THEN
//        assertThat(json, hasJsonPath("$.collections[0].links[?(@.rel=='items' && @.type=='application/geo+json')].href",
//                                     hasJsonPath("$[0]", is("https://proxyhost:2222/collections/pubId_1/items"))));
//    }

//    @Test
//    @DisplayName("check `X-Forwarded-Host`+`X-Forwarded-Proto` works")
//    public void checkForwardedHostForwardedProto() throws Exception {
//        //GIVEN
//        testDataMapper.insertLayer(buildLayerN(1));
//        //WHEN
//        final String json = mockMvc.perform(get("/collections")
//                        .header( "X-Forwarded-Host", "proxyhost:2222")
//                        .header("X-Forwarded-Proto", "https"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse().getContentAsString();
//        //THEN
//        assertThat(json, hasJsonPath("$.collections[0].links[?(@.rel=='items' && @.type=='application/geo+json')].href",
//                                     hasJsonPath("$[0]", is("https://proxyhost:2222/collections/pubId_1/items"))));
//    }
//
//    @Test
//    @DisplayName("check `X-Forwarded-Host`+`X-Forwarded-Proto`+`X-Forwarded-Port` works")
//    public void checkForwardedHostForwardedProtoAndForwardedPort() {
//        //GIVEN
//        testDataMapper.insertLayer(buildLayerN(1));
//        //WHEN
//        final String json = mockMvc.perform(get("/collections")
//                        .header( "X-Forwarded-Host", "proxyhost")
//                        .header("X-Forwarded-Proto", "https")
//                        .header("X-Forwarded-Port", "1234"))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andReturn().getResponse().getContentAsString();
//        //THEN
//        assertThat(json, hasJsonPath("$.collections[0].links[?(@.rel=='items' && @.type=='application/geo+json')].href",
//                                     hasJsonPath("$[0]", is("https://proxyhost:1234/collections/pubId_1/items"))));
//    }

    @Test
    @DisplayName("pagination should work")
    public void paginationShouldWork() throws Exception {
        //GIVEN
        IntStream.rangeClosed(1, 25).forEach(i -> testDataMapper.insertLayer(buildLayerN(i)));
        //WHEN
        final String json = mockMvc.perform(get("/collections")
                        .queryParam("limit", "10")
                        .queryParam("offset", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.rel=='next' && @.type=='application/json')].href",
                contains(url(BASE_URL + "/collections?limit=10&offset=20"))));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='prev' && @.type=='application/json')].href",
                contains(url(BASE_URL + "/collections?limit=10&offset=0"))));
        assertThat(json, hasJsonPath("$.numberMatched", is(25)));
        assertThat(json, hasJsonPath("$.numberReturned", is(10)));
    }

    @Test
    @DisplayName("should not be 'next' link if no more items")
    public void noNextLinkIfNoMoreItems() throws Exception {
        //GIVEN
        IntStream.rangeClosed(1, 25).forEach(i -> testDataMapper.insertLayer(buildLayerN(i)));
        //WHEN
        final String json = mockMvc.perform(get("/collections")
                        .queryParam("limit", "10")
                        .queryParam("offset", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.rel=='next' && @.type=='application/json')].href", empty()));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='prev' && @.type=='application/json')].href",
                contains(url(BASE_URL + "/collections?limit=10&offset=10"))));
        assertThat(json, hasJsonPath("$.numberMatched", is(25)));
        assertThat(json, hasJsonPath("$.numberReturned", is(5)));
    }

    @Test
    @DisplayName("should not be 'prev' link if at start")
    public void noPrevLinkIfAtStart() throws Exception {
        //GIVEN
        IntStream.rangeClosed(1, 25).forEach(i -> testDataMapper.insertLayer(buildLayerN(i)));
        //WHEN
        final String json = mockMvc.perform(get("/collections")
                        .queryParam("limit", "10")
                        .queryParam("offset", "0"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.links[?(@.rel=='prev' && @.type=='application/json')].href", empty()));
        assertThat(json, hasJsonPath("$.links[?(@.rel=='next' && @.type=='application/json')].href",
                contains(url(BASE_URL + "/collections?limit=10&offset=10"))));
        assertThat(json, hasJsonPath("$.numberMatched", is(25)));
        assertThat(json, hasJsonPath("$.numberReturned", is(10)));
    }

    @Test
    @DisplayName("limit should be positive ")
    public void limitShouldBeGreaterThan0() throws Exception {
        //GIVEN
        //WHEN
        final String json = mockMvc.perform(get("/collections")
                        .queryParam("limit", "-10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.limit.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("offset should be positive ")
    public void offsetShouldBePositive() throws Exception {
        //GIVEN
        //WHEN
        final String json = mockMvc.perform(get("/collections")
                        .queryParam("offset", "-10"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();
        //THEN
        assertThat(json, hasJsonPath("$.fieldErrors.offset.msg", not(emptyOrNullString())));
    }

    @Test
    @DisplayName("only owner should obtain theirs collection")
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
        String response = mockMvc.perform(get("/collections"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        final DocumentContext json = JsonPath.parse(response);
        assertThat(json.read("$.collections"), hasSize(2));
        assertThat(json.read("$.collections[*].id"), containsInAnyOrder("pubId_1", "pubId_3"));
        assertThat(json.read("$.collections[0].ownedByUser"), is(false));
        assertThat(json.read("$.collections[1].ownedByUser"), is(true));
    }

    @Test
    public void numberMatchedShouldNotBe0WhenOffsetExceeded_8815() throws Exception {
        //GIVEN
        IntStream.rangeClosed(1, 7).forEach(i -> testDataMapper.insertLayer(buildLayerN(i)));

        //WHEN
        final String json = mockMvc.perform(get("/collections")
                        .queryParam("limit", "10")
                        .queryParam("offset", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse().getContentAsString();

        //THEN
        assertThat(json, hasJsonPath("$.numberMatched", is(7)));
        assertThat(json, hasJsonPath("$.numberReturned", is(0)));
    }

}