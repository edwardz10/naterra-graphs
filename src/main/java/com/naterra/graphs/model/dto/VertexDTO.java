package com.naterra.graphs.model.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class VertexDTO {

    private Long id;
    private String value;
    private UUID externalGraphId;
}
