package com.naterra.graphs.service.impl;

import com.naterra.graphs.model.EdgeDTO;
import com.naterra.graphs.model.GraphDTO;
import com.naterra.graphs.model.VertexDTO;
import com.naterra.graphs.service.GraphTraverseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GraphTraverseServiceImplTest {

    @Autowired
    private GraphTraverseService graphTraverseService;

    @Test
    void traverseGraphWorks() {
        GraphDTO graphDTO = prepareGraphDTO();
        graphDTO = graphTraverseService.addGraph(graphDTO);
        final UUID externalGraphId = graphDTO.getExternalGraphId();

        VertexDTO vertexLiam = getSavedVertex(externalGraphId, "Liam");
        VertexDTO vertexNoel = getSavedVertex(externalGraphId, "Noel");
        VertexDTO vertexBonehead = getSavedVertex(externalGraphId, "Bonehead");
        VertexDTO vertexGuigsy = getSavedVertex(externalGraphId, "Guigsy");
        VertexDTO vertexTony = getSavedVertex(externalGraphId, "Tony");

        addEdge(externalGraphId, vertexLiam, vertexNoel);
        addEdge(externalGraphId, vertexLiam, vertexGuigsy);
        addEdge(externalGraphId, vertexNoel, vertexBonehead);
        addEdge(externalGraphId, vertexGuigsy, vertexBonehead);
        addEdge(externalGraphId, vertexNoel, vertexTony);
        addEdge(externalGraphId, vertexGuigsy, vertexTony);

        graphDTO = graphTraverseService.getGraphById(externalGraphId);

        assertThat(graphDTO.getVertices().size()).isEqualTo(5);
        assertThat(graphDTO.getEdges().size()).isEqualTo(6);

        Set<VertexDTO> vertices = graphTraverseService.traverse(externalGraphId, vertexNoel.getExternalId());
        assertThat(vertices.size()).isEqualTo(5);
    }

    @Test
    void getPath() {
        GraphDTO graphDTO = prepareGraphDTO();
        graphDTO = graphTraverseService.addGraph(graphDTO);
        final UUID externalGraphId = graphDTO.getExternalGraphId();

        VertexDTO vertexTim = getSavedVertex(externalGraphId, "Tim");
        VertexDTO vertexBrian = getSavedVertex(externalGraphId, "Brian");
        VertexDTO vertexFreddy = getSavedVertex(externalGraphId, "Freddy");
        VertexDTO vertexJohn = getSavedVertex(externalGraphId, "John");
        VertexDTO vertexRoger = getSavedVertex(externalGraphId, "Roger");

        addEdge(externalGraphId, vertexTim, vertexBrian);
        addEdge(externalGraphId, vertexTim, vertexJohn);
        addEdge(externalGraphId, vertexBrian, vertexFreddy);
        addEdge(externalGraphId, vertexJohn, vertexFreddy);
        addEdge(externalGraphId, vertexBrian, vertexRoger);
        addEdge(externalGraphId, vertexJohn, vertexRoger);

        graphDTO = graphTraverseService.getGraphById(externalGraphId);

        assertThat(graphDTO.getVertices().size()).isEqualTo(5);
        assertThat(graphDTO.getEdges().size()).isEqualTo(6);

        Set<VertexDTO> path1 = graphTraverseService.getPath(externalGraphId,
                vertexTim.getExternalId(), vertexBrian.getExternalId());
        assertThat(path1.size()).isEqualTo(2);

        Set<VertexDTO> path2 = graphTraverseService.getPath(externalGraphId,
                vertexTim.getExternalId(), vertexFreddy.getExternalId());
        assertThat(path2.size()).isEqualTo(3);

        Set<VertexDTO> path3 = graphTraverseService.getPath(externalGraphId,
                vertexBrian.getExternalId(), vertexRoger.getExternalId());
        assertThat(path3.size()).isEqualTo(2);

        Set<VertexDTO> path4 = graphTraverseService.getPath(externalGraphId,
                vertexJohn.getExternalId(), vertexBrian.getExternalId());
        assertThat(path4.size()).isEqualTo(3);
    }

    private GraphDTO prepareGraphDTO() {
        GraphDTO graphDTO = new GraphDTO();
        graphDTO.setName("myGraph");
        return graphDTO;
    }

    private VertexDTO getSavedVertex(UUID externalGraphId, String value) {
        VertexDTO vertexDTO = new VertexDTO();
        vertexDTO.setValue(value);
        vertexDTO.setExternalId(UUID.randomUUID());
//        return vertexService.addVertex(externalGraphId, vertexDTO);
        graphTraverseService.addVertex(externalGraphId, vertexDTO);

        return vertexDTO;
    }

    private void addEdge(UUID externalGraphId, VertexDTO from, VertexDTO to) {
        EdgeDTO edge = new EdgeDTO();
        edge.setExternalGraphId(externalGraphId);
        edge.setFromVertex(from);
        edge.setToVertex(to);

//        edgeService.addEdge(externalGraphId, edge);
        graphTraverseService.addEdge(edge);
    }
}
