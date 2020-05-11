package com.naterra.graphs.model;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class GraphDTO {
    private Long id;

    private String name;

    private UUID externalGraphId;

    private Set<VertexDTO> vertices;

    private Set<EdgeDTO> edges;
}
