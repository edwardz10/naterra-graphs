package com.naterra.graphs.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "Graph")
@Data
public class GraphEntity {

    @Id
    @GeneratedValue
    @Column(name = "graphId", nullable = false)
    private Long id;

    @Column(name = "Name", nullable = false)
    private String name;

    @Column(name = "ExternalGraphId", nullable = false)
    private UUID externalGraphId;

}
