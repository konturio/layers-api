package io.kontur.layers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.Objects;

public class Link {

    @JsonProperty("href")
    private String href;

    @JsonProperty("rel")
    private String rel;

    @JsonProperty("type")
    private String type;

    @JsonProperty("hreflang")
    private String hreflang;

    @JsonProperty("title")
    private String title;

    @JsonProperty("length")
    private Integer length;

    @JsonProperty("apiTag")
    private String apiTag;

    @JsonProperty("apiKey")
    private String apiKey;

    public Link href(String href) {
        this.href = href;
        return this;
    }

    public Link apiTag(String apiTag) {
        this.apiTag = apiTag;
        return this;
    }

    @JsonProperty("apiTag")
    @Schema(description = "Url tag to send api key.")
    public String getApiTag() {
        return apiTag;
    }

    public void setApiTag(String apiTag) {
        this.apiTag = apiTag;
    }

    public Link apiKey(String apiKey) {
        this.apiKey = apiKey;
        return this;
    }

    @JsonProperty("apiKey")
    @Schema(description = "Api key of layer")
    @NotNull
    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiTag = apiKey;
    }

    @JsonProperty("href")
    @Schema(example = "http://data.example.com/buildings/123", required = true, description = "")
    @NotNull
    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public Link rel(String rel) {
        this.rel = rel;
        return this;
    }

    /**
     * Get rel
     *
     * @return rel
     **/
    @JsonProperty("rel")
    @Schema(example = "alternate", description = "")
    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public Link type(String type) {
        this.type = type;
        return this;
    }

    @JsonProperty("type")
    @Schema(example = "application/geo+json", description = "")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Link hreflang(String hreflang) {
        this.hreflang = hreflang;
        return this;
    }

    @JsonProperty("hreflang")
    @Schema(example = "en", description = "")
    public String getHreflang() {
        return hreflang;
    }

    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    public Link title(String title) {
        this.title = title;
        return this;
    }

    @JsonProperty("title")
    @Schema(example = "Trierer Strasse 70, 53115 Bonn", description = "")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Link length(Integer length) {
        this.length = length;
        return this;
    }

    @JsonProperty("length")
    @Schema(description = "")
    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(this.href, link.href) &&
                Objects.equals(this.rel, link.rel) &&
                Objects.equals(this.type, link.type) &&
                Objects.equals(this.hreflang, link.hreflang) &&
                Objects.equals(this.apiTag, link.apiTag) &&
                Objects.equals(this.apiKey, link.apiKey) &&
                Objects.equals(this.title, link.title) &&
                Objects.equals(this.length, link.length);
    }

    @Override
    public int hashCode() {
        return Objects.hash(href, rel, type, hreflang, apiTag, apiKey, title, length);
    }
}
