package com.naterra.graphs.controller;

import com.naterra.graphs.model.dto.GraphDTO;
import com.naterra.graphs.model.entity.GraphEntity;
import com.naterra.graphs.service.GraphService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/graphs")
public class GraphController {

    @Autowired
    private GraphService graphService;

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

}
