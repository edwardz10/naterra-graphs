package com.naterra.graphs.controller;

import com.naterra.graphs.model.VertexDTO;
import com.naterra.graphs.service.GraphTraverseService;
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
    private GraphTraverseService traverseService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addVertex(@PathVariable UUID externalGraphId, @RequestBody VertexDTO vertex) {
        try {
            traverseService.addVertex(externalGraphId, vertex);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
