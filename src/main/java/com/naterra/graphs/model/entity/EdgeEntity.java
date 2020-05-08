package com.naterra.graphs.model.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "Edge")
@Data
public class EdgeEntity {
    @Id
    @GeneratedValue
    @Column(name = "edgeId", nullable = false)
    private Long id;

    @Column(name = "externalId", nullable = false)
    private UUID externalId;

    @Column(name = "externalGraphId")
    private UUID externalGraphId;

    @Column(name = "fromVertexId")
    private UUID fromVertexId;

    @Column(name = "toVertexId")
    private UUID toVertexId;
}
