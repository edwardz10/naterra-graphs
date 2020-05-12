package com.naterra.graphs.service.impl;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.EdgeDTO;
import com.naterra.graphs.model.GraphDTO;
import com.naterra.graphs.model.TraverseDTO;
import com.naterra.graphs.model.VertexDTO;
import com.naterra.graphs.service.GraphTraverseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GraphTraverseServiceImplTest {

    @Autowired
    private GraphTraverseService graphTraverseService;

    @Test
    public void createGraphOfUnsupportedTypeThrowsExeption() {
        GraphDTO graphDTO = new GraphDTO();
        graphDTO.setName("Graph of BigDecimals");
        graphDTO.setType("java.math.BigDecimal");

        Exception exception = assertThrows(
                GraphException.class,
                () -> graphTraverseService.addGraph(graphDTO));

        assertTrue(exception.getMessage().contains("java.lang"));
    }

    @Test
    public void traverseGraphWorks() {
        GraphDTO graphDTO = prepareGraphOfStrings();
        graphDTO = graphTraverseService.addGraph(graphDTO);
        final UUID externalGraphId = graphDTO.getExternalGraphId();

        VertexDTO vertexLiam = addStringVertex(externalGraphId, "Liam");
        VertexDTO vertexNoel = addStringVertex(externalGraphId, "Noel");
        VertexDTO vertexBonehead = addStringVertex(externalGraphId, "Bonehead");
        VertexDTO vertexGuigsy = addStringVertex(externalGraphId, "Guigsy");
        VertexDTO vertexTony = addStringVertex(externalGraphId, "Tony");

        addEdge(externalGraphId, vertexLiam, vertexNoel);
        addEdge(externalGraphId, vertexLiam, vertexGuigsy);
        addEdge(externalGraphId, vertexNoel, vertexBonehead);
        addEdge(externalGraphId, vertexGuigsy, vertexBonehead);
        addEdge(externalGraphId, vertexNoel, vertexTony);
        addEdge(externalGraphId, vertexGuigsy, vertexTony);

        graphDTO = graphTraverseService.getGraphById(externalGraphId);

        assertThat(graphDTO.getVertices().size()).isEqualTo(5);
        assertThat(graphDTO.getEdges().size()).isEqualTo(6);

        TraverseDTO traverseDTO = TraverseDTO.builder()
                .externalGraphId(externalGraphId)
                .rootVertexId(vertexNoel.getExternalId())
                .build();

        Set<VertexDTO> vertices = graphTraverseService.traverse(traverseDTO);
        assertThat(vertices.size()).isEqualTo(5);
    }

    @Test
    public void traverseGraphWithFunctionWorks() {
        GraphDTO graphDTO = prepareGraphOfStrings();
        graphDTO = graphTraverseService.addGraph(graphDTO);
        final UUID externalGraphId = graphDTO.getExternalGraphId();

        VertexDTO vertexPapa = addStringVertex(externalGraphId, "'Papa'");
        VertexDTO vertexMama = addStringVertex(externalGraphId, "'Mama'");
        VertexDTO vertexDaughter = addStringVertex(externalGraphId, "'Daughter'");

        addEdge(externalGraphId, vertexPapa, vertexMama);
        addEdge(externalGraphId, vertexMama, vertexDaughter);

        graphDTO = graphTraverseService.getGraphById(externalGraphId);

        assertThat(graphDTO.getVertices().size()).isEqualTo(3);
        assertThat(graphDTO.getEdges().size()).isEqualTo(2);

        TraverseDTO traverseDTO = TraverseDTO.builder()
                .externalGraphId(externalGraphId)
                .rootVertexId(vertexPapa.getExternalId())
                .func(" + ' loves pizza'")
                .build();

        Set<VertexDTO> vertices = graphTraverseService.traverse(traverseDTO);

        assertThat(vertices.size()).isEqualTo(3);
        assertThat(vertices.stream()
                .filter(vertex -> vertex.getValue().toString().endsWith("loves pizza"))
                .count()).isEqualTo(3);
    }

    @Test
    public void getPathWorks() {
        GraphDTO graphDTO = prepareGraphOfStrings();
        graphDTO = graphTraverseService.addGraph(graphDTO);
        final UUID externalGraphId = graphDTO.getExternalGraphId();

        VertexDTO vertexTim = addStringVertex(externalGraphId, "Tim");
        VertexDTO vertexBrian = addStringVertex(externalGraphId, "Brian");
        VertexDTO vertexFreddy = addStringVertex(externalGraphId, "Freddy");
        VertexDTO vertexJohn = addStringVertex(externalGraphId, "John");
        VertexDTO vertexRoger = addStringVertex(externalGraphId, "Roger");

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

    @Test
    public void addVertexWithWrongValueTypeThrowsException() {
        GraphDTO graphDTO = prepareGraphOfStrings();
        graphDTO = graphTraverseService.addGraph(graphDTO);
        final UUID externalGraphId = graphDTO.getExternalGraphId();

        VertexDTO doubleTypeVertex = getDoubleTypeVertex(externalGraphId, 1.0);

        Exception exception = assertThrows(
                GraphException.class,
                () -> graphTraverseService.addVertex(externalGraphId, doubleTypeVertex));

        assertTrue(exception.getMessage().contains("not supported in graph"));
    }

    private GraphDTO prepareGraphOfStrings() {
        GraphDTO graphDTO = new GraphDTO();
        graphDTO.setName("myGraph");
        graphDTO.setType("java.lang.String");
        return graphDTO;
    }

    private VertexDTO addStringVertex(UUID externalGraphId, String value) {
        VertexDTO<String> vertexDTO = new VertexDTO<String>();
        vertexDTO.setValue(value);
        vertexDTO.setExternalId(UUID.randomUUID());
        graphTraverseService.addVertex(externalGraphId, vertexDTO);

        return vertexDTO;
    }

    private VertexDTO getDoubleTypeVertex(UUID externalGraphId, Double d) {
        VertexDTO<Double> vertexDTO = new VertexDTO<Double>();
        vertexDTO.setValue(d);
        vertexDTO.setExternalId(UUID.randomUUID());

        return vertexDTO;
    }

    private void addEdge(UUID externalGraphId, VertexDTO from, VertexDTO to) {
        EdgeDTO edge = new EdgeDTO();
        edge.setExternalGraphId(externalGraphId);
        edge.setFromVertex(from);
        edge.setToVertex(to);

        graphTraverseService.addEdge(edge);
    }
}
