package com.naterra.graphs.model;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.UUID;

@Data
@Valid
public class GraphDTO {
    private String name;

    private UUID externalGraphId;

    @NotNull
    private String type;

    private Set<VertexDTO<?>> vertices;

    private Set<EdgeDTO> edges;
}
