package io.kontur.layers.util;

import io.kontur.layers.controller.exceptions.Error;
import io.kontur.layers.controller.exceptions.WebApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

public class AuthorizationUtils {

    private static final Logger LOG = LoggerFactory.getLogger(AuthorizationUtils.class);

    public static String getAuthenticatedUserName() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        switch (authentication.getPrincipal().getClass().getSimpleName()) {
            case "User":
            case "Jwt":
                return authentication.getName();
            default:
                LOG.error(String.format("Can't authorize user with %s principal.",
                        authentication.getPrincipal().getClass().getName()));
                throw new WebApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, Error.error("Can't authorize"));
        }
    }

    public static boolean hasRole(String role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (role.equalsIgnoreCase(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isSuperAdmin() {
        return hasRole("superadmin") || hasRole("ROLE_SUPERADMIN");
    }

}
