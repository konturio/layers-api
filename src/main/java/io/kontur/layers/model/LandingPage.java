package io.kontur.layers.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LandingPage {

    @JsonProperty("title")
    private String title;

    @JsonProperty("description")
    private String description;

    @JsonProperty("links")
    private List<Link> links = new ArrayList<Link>();

    public LandingPage title(String title) {
        this.title = title;
        return this;
    }

    @JsonProperty("title")
    @Schema(example = "Buildings in Bonn", description = "")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LandingPage description(String description) {
        this.description = description;
        return this;
    }

    @JsonProperty("description")
    @Schema(example = "Access to data about buildings in the city of Bonn via a Web API that conforms to the OGC API Features specification.", description = "")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LandingPage links(List<Link> links) {
        this.links = links;
        return this;
    }

    public LandingPage addLinksItem(Link linksItem) {
        this.links.add(linksItem);
        return this;
    }

    @JsonProperty("links")
    @Schema(required = true, description = "")
    @NotNull
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        LandingPage landingPage = (LandingPage) o;
        return Objects.equals(this.title, landingPage.title) &&
                Objects.equals(this.description, landingPage.description) &&
                Objects.equals(this.links, landingPage.links);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, links);
    }
}
