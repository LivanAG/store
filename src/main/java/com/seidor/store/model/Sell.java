package com.seidor.store.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Sell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "sell", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SellDetail> sellDetails = new HashSet<>();

    private Double totalSale;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;












    public Double getTotalSale() {
        return totalSale;
    }

    public void setTotalSale(Double totalSale) {
        this.totalSale = totalSale;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<SellDetail> getSellDetails() {
        return sellDetails;
    }

    public void setSellDetails(Set<SellDetail> sellDetails) {
        this.sellDetails = sellDetails;
    }
}
