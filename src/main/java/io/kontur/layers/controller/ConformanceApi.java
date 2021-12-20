package io.kontur.layers.controller;

import io.kontur.layers.model.ConfClasses;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/conformance")
public class ConformanceApi {

    @GetMapping
    @Operation(summary = "information about specifications that this API conforms to", description = "A list of all conformance classes specified in a standard that the server conforms to.", tags = {"Capabilities"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The URIs of all conformance classes supported by the server.  To support \"generic\" clients that want to access multiple OGC API Features implementations - and not \"just\" a specific API / server, the server declares the conformance classes it implements and conforms to.", content = @Content(schema = @Schema(implementation = ConfClasses.class))),
            @ApiResponse(responseCode = "500", description = "A server error occurred.", content = @Content(schema = @Schema(implementation = Exception.class)))})
    public ResponseEntity getConformanceDeclaration() {
        return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body("""
                {"conformsTo": [
                    "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/core",
                    "http://docs.opengeospatial.org/is/17-069r3/17-069r3.html#_conformance_class_openapi_3_0",
                    "http://www.opengis.net/spec/ogcapi-features-1/1.0/conf/geojson"]
                }""");
    }
}
