package io.kontur.layers.controller;

import io.kontur.layers.dto.ApplicationDto;
import io.kontur.layers.dto.ApplicationUpdateDto;
import io.kontur.layers.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/apps")
public class ApplicationsApi {

    private final ApplicationService applicationService;

    public ApplicationsApi(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/{applicationId}")
    @Operation(summary = "Obtain application", tags = {"Applications"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Obtain application.", content = @Content(schema = @Schema(implementation = ApplicationDto.class)))})
    public ResponseEntity get(
            @Parameter(in = ParameterIn.PATH, description = "local identifier of an application", required = true)
            @PathVariable("applicationId") UUID applicationId,
            @Parameter(in = ParameterIn.QUERY, description = "include information about application collections")
            @RequestParam(value = "includeDefaultCollections", defaultValue = "false") boolean includeDefaultCollections) {
        ApplicationDto result = applicationService.getApplication(applicationId, includeDefaultCollections);
        return ResponseEntity.ok(result);
    }

    @PutMapping("/{applicationId}")
    @Operation(summary = "Update or create application", tags = {"Applications"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updated application.", content = @Content(schema = @Schema(implementation = ApplicationUpdateDto.class)))})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity update(
            @Parameter(in = ParameterIn.PATH, description = "local identifier of an application", required = true)
            @PathVariable("applicationId") UUID applicationId,
            @RequestBody @Valid ApplicationUpdateDto body) {
        ApplicationDto result = applicationService.updateApplication(applicationId, body);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{applicationId}")
    @Operation(summary = "Remove application", tags = {"Applications"})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Success")})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity delete(
            @Parameter(in = ParameterIn.PATH, description = "local identifier of an application", required = true)
            @PathVariable("applicationId") UUID appId) {
        applicationService.deleteApplication(appId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
