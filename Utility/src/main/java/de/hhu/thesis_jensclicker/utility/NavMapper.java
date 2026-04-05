package de.hhu.thesis_jensclicker.utility;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.stream.Collectors;

public class NavMapper {
    public static final String ADMIN = "admin";
    public static final String LOGGED_IN_TEACHER = "logged-in-teacher";
    public static final String LOGGED_IN = "logged-in";

    public static String mapRole(OAuth2User principal) {
        Collection<String> roles = principal.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet());

        if (roles.contains("ROLE_ADMIN")) {
            return ADMIN;
        }
        if (roles.contains("ROLE_TEACHER")) {
            return LOGGED_IN_TEACHER;
        }
        return LOGGED_IN;
    }
}
