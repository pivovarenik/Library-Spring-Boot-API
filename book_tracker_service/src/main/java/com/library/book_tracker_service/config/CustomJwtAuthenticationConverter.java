package com.library.book_tracker_service.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class CustomJwtAuthenticationConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        Object authoritiesClaim = jwt.getClaims().get("authorities");

        if (authoritiesClaim instanceof List<?>) {
            return ((List<?>) authoritiesClaim).stream()
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role)) // Добавляем ROLE_
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
