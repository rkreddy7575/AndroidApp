package com.trailbook.experience;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "packing_items")
public class PackingItem {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "experience_id", nullable = false) private Experience experience;
    @Column(nullable = false, length = 200) private String item;
    @Column(nullable = false) private boolean checked = false;
    @PrePersist void prePersist() { if (id == null) id = UUID.randomUUID(); }
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Experience getExperience() { return experience; }
    public void setExperience(Experience experience) { this.experience = experience; }
    public String getItem() { return item; }
    public void setItem(String item) { this.item = item; }
    public boolean isChecked() { return checked; }
    public void setChecked(boolean checked) { this.checked = checked; }
}
