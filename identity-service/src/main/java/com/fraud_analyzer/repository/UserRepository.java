package com.fraud_analyzer.repository;

import com.fraud_analyzer.domain.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    @Query("""
        SELECT u FROM UserEntity u
        LEFT JOIN FETCH u.roles r
        LEFT JOIN FETCH r.permissions
        WHERE u.email = :email
        AND u.organization.orgUuid = :orgUuid
        AND u.status = 'ACTIVE'
    """)
    Optional<UserEntity> findActiveUserWithRolesAndPermissions(
            @Param("email") String email,
            @Param("orgUuid") String orgUuid
    );

}
