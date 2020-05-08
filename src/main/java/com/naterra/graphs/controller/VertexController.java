package com.naterra.graphs.controller;

import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.service.VertexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/graphs/{externalGraphId}/vertices")
public class VertexController {

    @Autowired
    private VertexService vertexService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addVertex(@PathVariable UUID externalGraphId, @RequestBody VertexDTO vertex) {
        try {
            return new ResponseEntity<VertexDTO>(vertexService.addVertex(externalGraphId, vertex), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
