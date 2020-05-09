package com.naterra.graphs.controller;

import com.naterra.graphs.model.dto.GraphDTO;
import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.service.GraphService;
import com.naterra.graphs.service.GraphTraverseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/graphs")
public class GraphController {

    @Autowired
    private GraphService graphService;

    @Autowired
    private GraphTraverseService traverseService;

    @GetMapping
    public Iterable<GraphDTO> getAllGraphs() {
        return graphService.getAllGraphs();
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public GraphDTO createGraph(@RequestBody GraphDTO graph) {
        return graphService.createGraph(graph);
    }

    @GetMapping(value = "/{externalGraphId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GraphDTO getGraphByName(@PathVariable UUID externalGraphId) {
        return graphService.getGraphByExternalId(externalGraphId);
    }

    @GetMapping(value = "/{externalGraphId}/traverse", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> traverse(@PathVariable UUID externalGraphId, @RequestParam UUID rootId) {
        try {
            return new ResponseEntity<Set<VertexDTO>>(traverseService.traverse(externalGraphId, rootId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
