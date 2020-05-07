package com.naterra.graphs.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Vertex")
@Data
public class VertexEntity {
    @Id
    @GeneratedValue
    @Column(name = "vertexId", nullable = false)
    private Long id;

    @Column(name = "Value", nullable = false)
    private String value;

    @Column(name = "externalGraphId")
    private UUID externalGraphId;
}
