package com.fraud_analyzer.repository;

import com.fraud_analyzer.domain.entity.ApiKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKeyEntity, Long> {

    @Query("""
        SELECT a FROM ApiKeyEntity a
        JOIN FETCH a.organization
        WHERE a.keyHash = :keyHash
        AND a.status = 'ACTIVE'
    """)
    Optional<ApiKeyEntity> findActiveKeyWithOrg(
            @Param("keyHash") String keyHash
    );

}
