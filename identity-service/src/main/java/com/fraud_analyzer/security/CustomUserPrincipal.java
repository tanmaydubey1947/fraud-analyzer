package com.fraud_analyzer.security;

import com.fraud_analyzer.domain.entity.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomUserPrincipal implements UserDetails {

    private final Long userId;
    private final String userUuid;
    private final String orgUuid;
    private final String email;
    private final String password;
    private final Set<GrantedAuthority> authorities;

    public CustomUserPrincipal(UserEntity user) {
        this.userId = user.getId();
        this.userUuid = user.getUserUuid();
        this.orgUuid = user.getOrganization().getOrgUuid();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();

        this.authorities = user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(p -> new SimpleGrantedAuthority(p.getPermissionKey()))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public String getOrgUuid() {
        return orgUuid;
    }

    public String getUserUuid() {
        return userUuid;
    }
}
