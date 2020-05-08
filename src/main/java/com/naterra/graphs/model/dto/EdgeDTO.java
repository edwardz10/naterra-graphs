package com.naterra.graphs.model.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class EdgeDTO {

    private Long id;
    private UUID externalId;
    @NotNull
    private UUID externalGraphId;
    @NotNull
    private UUID fromVertexId;
    @NotNull
    private UUID toVertexId;
}
