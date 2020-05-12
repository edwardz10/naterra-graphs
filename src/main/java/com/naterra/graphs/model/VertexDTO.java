package com.naterra.graphs.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@EqualsAndHashCode
@Valid
public class VertexDTO<T> {

    private UUID externalId;
    @NotNull
    private T value;
}
