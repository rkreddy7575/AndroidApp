package com.trailbook.experience;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "transportations")
public class Transportation {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "experience_id", nullable = false) private Experience experience;
    @Column(nullable = false, length = 100) private String mode;
    @Column(columnDefinition = "TEXT") private String details;
    private BigDecimal cost;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Experience getExperience() { return experience; }
    public void setExperience(Experience experience) { this.experience = experience; }
    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    public BigDecimal getCost() { return cost; }
    public void setCost(BigDecimal cost) { this.cost = cost; }
}
