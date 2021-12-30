package io.kontur.layers.service;

import io.kontur.layers.dto.LandingPage;
import org.springframework.stereotype.Service;

import static io.kontur.layers.service.LinkFactory.Relation.*;
import static io.kontur.layers.service.LinkFactory.Type.APPLICATION_JSON;
import static io.kontur.layers.service.LinkFactory.Type.APPLICATION_YAML;

@Service
public class LandingPageService {

    private final LinkFactory linkFactory;

    public LandingPageService(final LinkFactory linkFactory) {
        this.linkFactory = linkFactory;
    }

    public LandingPage getLandingPage() {
        final LandingPage landingPage = new LandingPage();
        landingPage.addLinksItem(linkFactory.createLocal(
                "/doc", SERVICE_DESC, APPLICATION_YAML, "OGC Features API definition"));
        landingPage.addLinksItem(linkFactory.createLocal(
                "", SELF, APPLICATION_JSON, "this document"));
        landingPage.addLinksItem(linkFactory.createLocal(
                "/conformance", CONFORMANCE, APPLICATION_JSON,
                "OGC API conformance classes implemented by this server"));
        landingPage.addLinksItem(linkFactory.createLocal(
                "/collections", DATA, APPLICATION_JSON, "Information about the feature collections"));
        return landingPage;
    }
}
