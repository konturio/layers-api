package io.kontur.layers.service;

import io.kontur.layers.ApiConstants;
import io.kontur.layers.dto.FeaturePropertiesFilter;
import io.kontur.layers.dto.Link;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Service
public class LinkFactory {

    private final HttpServletRequest servletRequest;

    public LinkFactory(HttpServletRequest servletRequest) {
        this.servletRequest = servletRequest;
    }

    public Link linkForCollectionItems(Relation rel,
                                       String collectionId,
                                       String collectionTitle,
                                       Integer limit,
                                       Integer offset,
                                       List<BigDecimal> bbox,
                                       List<FeaturePropertiesFilter> propFilterList) {
        final UriBuilder uriBuilder = UriComponentsBuilder.fromPath(ApiConstants.COLLECTION_ITEMS_ENDPOINT)
                .queryParam("limit", limit)
                .queryParam("offset", offset);
        if (!CollectionUtils.isEmpty(bbox)) {
            uriBuilder.queryParam("bbox", bbox.toArray());
        }
        propFilterList.forEach(p -> uriBuilder.queryParam(p.getFieldName(), (Object[]) p.getPattern()));
        final String url = uriBuilder.build(collectionId).toString();
        return createLocal(url, rel, Type.APPLICATION_GEO_JSON, collectionTitle);
    }

    public Link linkForCollections(Relation rel,
                                   Integer limit,
                                   Integer offset,
                                   String title) {
        final UriBuilder uriBuilder = UriComponentsBuilder.fromPath(ApiConstants.COLLECTIONS_ENDPOINT)
                .queryParam("limit", limit)
                .queryParam("offset", offset);
        return createLocal(uriBuilder.build().toString(), rel, Type.APPLICATION_JSON, title);
    }

    /**
     * Create link that refers to resource on this server
     */
    public Link createLocal(@NotNull final String relativeUrl,
                            @NotNull final Relation rel,
                            @NotNull final Type type,
                            final String title,
                            final String hreflang,
                            final Integer length

    ) {
        if (relativeUrl == null) {
            throw new IllegalArgumentException("relativeUrl must not be null");
        }
        StringBuilder builder = new StringBuilder();
        builder.append(servletRequest.getScheme()).append("://").append(servletRequest.getServerName());
        if (servletRequest.getServerPort() != 80) {
            builder.append(":").append(servletRequest.getServerPort());
        }
        builder.append(servletRequest.getContextPath());
        builder.append((relativeUrl.startsWith("/") ? relativeUrl : "/" + relativeUrl));
        final String url = builder.toString();
        return new Link().href(url).hreflang(hreflang).length(length).rel(rel.getStr()).title(title)
                .type(type.getStr());
    }

    public Link createLocal(final String relativeUrl, final Relation rel, final Type type, final String title) {
        return createLocal(relativeUrl, rel, type, title, null, null);
    }

    public Link createLocal(@NotNull final String relativeUrl, @NotNull final Relation rel, @NotNull final Type type) {
        return createLocal(relativeUrl, rel, type, null, null, null);
    }

    public enum Type {
        APPLICATION_YAML(ApiConstants.APPLICATION_YAML),
        OPEN_API_YAML(ApiConstants.OPEN_API_YAML),
        APPLICATION_JSON(MediaType.APPLICATION_JSON_VALUE),
        APPLICATION_GEO_JSON(ApiConstants.APPLICATION_GEO_JSON);
        private String str;

        Type(final String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }

    /**
     * <a href="http://docs.opengeospatial.org/is/17-069r3/17-069r3.html#_link_relations">Link relations</a>
     */
    public enum Relation {
        ALTERNATE("alternate"),
        COLLECTION("collection"),
        DESCRIBED_BY("describedBy"),
        ITEM("item"),
        NEXT("next"),
        LICENSE("license"),
        PREV("prev"),
        SELF("self"),
        SERVICE_DESC("service-desc"),
        SERVICE_DOC("service-doc"),
        ITEMS("items"),
        CONFORMANCE("conformance"),
        DATA("data");

        private String str;

        Relation(final String str) {
            this.str = str;
        }

        public String getStr() {
            return str;
        }
    }

}
