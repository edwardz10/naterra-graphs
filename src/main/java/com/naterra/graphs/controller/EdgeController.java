package com.naterra.graphs.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/graphs/{externalGraphId}/edges")
public class EdgeController {

//    @Autowired
//    private EdgeService edgeService;

//    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> addEdge(@PathVariable UUID externalGraphId, @RequestBody EdgeDTO edge) {
//        try {
//            return new ResponseEntity<EdgeDTO>(edgeService.addEdge(externalGraphId, edge), HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @PostMapping(value = "/{values}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> addEdge(@PathVariable UUID externalGraphId, @PathVariable String values) {
//        try {
//            return new ResponseEntity<EdgeDTO>(edgeService.addEdge(externalGraphId, values), HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
