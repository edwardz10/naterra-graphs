package com.naterra.graphs.controller;

import com.naterra.graphs.model.GraphDTO;
import com.naterra.graphs.model.TraverseDTO;
import com.naterra.graphs.model.VertexDTO;
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
    private GraphTraverseService traverseService;

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public GraphDTO createGraph(@RequestBody GraphDTO graph) {
        return traverseService.addGraph(graph);
    }

    @GetMapping(value = "/{externalGraphId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public GraphDTO getGraphByName(@PathVariable UUID externalGraphId) {
        return traverseService.getGraphById(externalGraphId);
    }

    @PostMapping(value = "/traverse", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> traverse(@RequestBody TraverseDTO traverseDTO) {
        try {
            return new ResponseEntity<Set<VertexDTO>>(traverseService.traverse(traverseDTO), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping(value = "/{externalGraphId}/path", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> path(@PathVariable UUID externalGraphId,
                                  @RequestParam UUID from, @RequestParam UUID to) {
        try {
            return new ResponseEntity<Set<VertexDTO>>(traverseService.getPath(externalGraphId, from, to), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
