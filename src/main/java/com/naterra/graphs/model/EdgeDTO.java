package com.naterra.graphs.model;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class EdgeDTO {

    @NotNull
    private UUID externalGraphId;
    @NotNull
    private VertexDTO fromVertex;
    @NotNull
    private VertexDTO toVertex;
}
