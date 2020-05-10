package com.naterra.graphs.service.impl;

import com.naterra.graphs.model.dto.EdgeDTO;
import com.naterra.graphs.model.dto.GraphDTO;
import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.service.EdgeService;
import com.naterra.graphs.service.GraphService;
import com.naterra.graphs.service.VertexService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class EdgeServiceImplTest {

    @Autowired
    private GraphService graphService;

    @Autowired
    private VertexService vertexService;

    @Autowired
    private EdgeService edgeService;

    @Test
    public void addEdgeWorks() {
        GraphDTO graphDTO = prepareGraphDTO();
        graphDTO = graphService.createGraph(graphDTO);
        final UUID externalGraphId = graphDTO.getExternalGraphId();

        VertexDTO vertexOne = new VertexDTO();
        vertexOne.setValue("Batman");
        vertexOne = vertexService.addVertex(externalGraphId, vertexOne);

        VertexDTO vertexTwo = new VertexDTO();
        vertexTwo.setValue("Robin");
        vertexTwo = vertexService.addVertex(externalGraphId, vertexTwo);

        EdgeDTO edge = new EdgeDTO();
        edge.setFromVertex(vertexOne);
        edge.setToVertex(vertexTwo);

        edgeService.addEdge(externalGraphId, edge);

        graphDTO = graphService.getGraphByExternalId(externalGraphId);

        assertThat(graphDTO.getVertices().size()).isEqualTo(2);
        assertThat(graphDTO.getEdges().size()).isEqualTo(1);
    }

    private GraphDTO prepareGraphDTO() {
        GraphDTO graphDTO = new GraphDTO();
        graphDTO.setName("myGraph");
        return graphDTO;
    }

}
