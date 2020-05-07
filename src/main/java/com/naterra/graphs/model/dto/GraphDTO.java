package com.naterra.graphs.model.dto;

import com.naterra.graphs.model.entity.VertexEntity;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;
import java.util.UUID;

@Data
public class GraphDTO {
    private Long id;

    private String name;

    private UUID externalGraphId;

    private List<VertexEntity> vertices;
}
