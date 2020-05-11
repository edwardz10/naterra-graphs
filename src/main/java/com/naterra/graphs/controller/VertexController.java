package com.naterra.graphs.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/graphs/{externalGraphId}/vertices")
public class VertexController {

//    @Autowired
//    private VertexServiceImpl vertexService;

//    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> addVertex(@PathVariable UUID externalGraphId, @RequestBody VertexDTO vertex) {
//        try {
//            return new ResponseEntity<VertexDTO>(vertexService.addVertex(externalGraphId, vertex), HttpStatus.CREATED);
//        } catch (Exception e) {
//            return new ResponseEntity<String>(e.getLocalizedMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
}
