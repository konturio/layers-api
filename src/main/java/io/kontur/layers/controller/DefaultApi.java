package io.kontur.layers.controller;

import io.kontur.layers.dto.LandingPage;
import io.kontur.layers.service.LandingPageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class DefaultApi {

    private final LandingPageService landingPageService;
    private final Logger log = LoggerFactory.getLogger(DefaultApi.class);

    public DefaultApi(LandingPageService landingPageService) {
        this.landingPageService = landingPageService;
    }

    @GetMapping
    @Operation(summary = "landing page", description = "The landing page provides links to the API definition, the conformance statements and to the feature collections in this dataset.", tags = {"Capabilities"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The landing page provides links to the API definition (link relations `service-desc` and `service-doc`), the Conformance declaration (path `/conformance`, link relation `conformance`), and the Feature Collections (path `/collections`, link relation `data`).", content = @Content(schema = @Schema(implementation = LandingPage.class))),
            @ApiResponse(responseCode = "500", description = "A server error occurred.", content = @Content(schema = @Schema(implementation = Exception.class)))})
    public ResponseEntity<LandingPage> getLandingPage(HttpServletRequest request) {
        logRequestHeaders(request);
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON)
                .body(landingPageService.getLandingPage());
    }

    private void logRequestHeaders(HttpServletRequest request) {
        log.info("X-Forwarded-For: {}", request.getHeader("X-Forwarded-For"));
        log.info("X-Forwarded-Host: {}", request.getHeader("X-Forwarded-Host"));
        log.info("X-Forwarded-Proto: {}", request.getHeader("X-Forwarded-Proto"));
        log.info("Host: {}", request.getHeader("Host"));
    }
}
