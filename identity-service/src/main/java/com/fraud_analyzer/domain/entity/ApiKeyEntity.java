package com.fraud_analyzer.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "key_uuid", unique = true)
    private String keyUuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private OrganizationEntity organization;

    @Column(name = "key_hash", nullable = false)
    private String keyHash;

    private String status;   // ACTIVE, REVOKED

    @Column(name = "rate_limit_per_min")
    private Integer rateLimitPerMin;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

