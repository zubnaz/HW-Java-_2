package org.example.models;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name="tbl_photos")
public class Photo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name",length = 255,nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "product_id",nullable = false)
    private Product product;
}
