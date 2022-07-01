package io.kontur.layers.config;

import com.nimbusds.jose.shaded.json.JSONArray;
import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Profile({"!(test-run | develop)"})
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Bean
    public Converter<Jwt, AbstractAuthenticationToken> jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter());
        return jwtAuthenticationConverter;
    }

    @Bean
    public Converter<Jwt, Collection<GrantedAuthority>> jwtGrantedAuthoritiesConverter() {
        JwtGrantedAuthoritiesConverter converter = new JwtGrantedAuthoritiesConverter();
        return new Converter<Jwt, Collection<GrantedAuthority>>() {
            @Override
            public Collection<GrantedAuthority> convert(Jwt jwt) {
                Collection<GrantedAuthority> grantedAuthorities = converter.convert(jwt);
                if (jwt.hasClaim("resource_access")) {
                    JSONObject resourceAccess = jwt.getClaim("resource_access");
                    if (resourceAccess.containsKey("layers-api")) {
                        JSONObject konturClient = (JSONObject) resourceAccess.get("layers-api");
                        if (konturClient.containsKey("roles")) {
                            JSONArray roles = (JSONArray) konturClient.get("roles");
                            List<SimpleGrantedAuthority> keycloakAuthorities = roles.stream()
                                    .map(role -> new SimpleGrantedAuthority((String) role))
                                    .collect(Collectors.toList());
                            grantedAuthorities.addAll(keycloakAuthorities);
                        }
                    }
                }
                return grantedAuthorities;
            }
        };
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .csrf()
                .ignoringAntMatchers("/**/search")
                .and()
                .headers().cacheControl().disable()
                .and()
                .oauth2ResourceServer(resourceServerConfigurer -> resourceServerConfigurer
                        .jwt(jwtConfigurer -> jwtConfigurer.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );
    }
}
