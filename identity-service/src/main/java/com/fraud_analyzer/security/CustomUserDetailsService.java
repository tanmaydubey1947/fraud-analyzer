package com.fraud_analyzer.security;

import com.fraud_analyzer.domain.entity.UserEntity;
import com.fraud_analyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetails loadUserByEmailAndOrg(String email, String orgUuid) {

        UserEntity user = userRepository
                .findActiveUserWithRolesAndPermissions(email, orgUuid)
                .orElseThrow(() -> new UsernameNotFoundException("Invalid credentials"));

        return new CustomUserPrincipal(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        throw new UnsupportedOperationException("Use email + orgUuid login");
    }
}
