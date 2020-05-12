package com.naterra.graphs.model;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class TraverseDTO {
    UUID externalGraphId;
    UUID rootVertexId;
    String func;
}
