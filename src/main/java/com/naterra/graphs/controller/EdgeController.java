package com.naterra.graphs.controller;

import com.naterra.graphs.model.dto.EdgeDTO;
import com.naterra.graphs.service.EdgeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/graphs/{externalGraphId}/edges")
public class EdgeController {

    @Autowired
    private EdgeService edgeService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addEdge(@PathVariable UUID externalGraphId, @RequestBody EdgeDTO edge) {
        try {
            return new ResponseEntity<EdgeDTO>(edgeService.addEdge(externalGraphId, edge), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/{values}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addEdge(@PathVariable UUID externalGraphId, @PathVariable String values) {
        try {
            return new ResponseEntity<EdgeDTO>(edgeService.addEdge(externalGraphId, values), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
