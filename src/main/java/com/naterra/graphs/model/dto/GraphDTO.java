package com.naterra.graphs.model.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class GraphDTO {
    private Long id;

    private String name;

    private UUID externalGraphId;

    private List<VertexDTO> vertices;

    private List<EdgeDTO> edges;
}
