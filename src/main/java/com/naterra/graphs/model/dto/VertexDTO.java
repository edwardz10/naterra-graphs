package com.naterra.graphs.model.dto;

import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Valid
public class VertexDTO {

    private Long id;
    private UUID externalId;
    @NotNull
    private String value;
    @NotNull
    private UUID externalGraphId;
}
