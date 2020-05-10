package com.naterra.graphs.service.impl;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.dto.GraphDTO;
import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.service.GraphService;
import com.naterra.graphs.service.VertexService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class VertexServiceImplTest {

    @Autowired
    private GraphService graphService;

    @Autowired
    private VertexService vertexService;

    @Test
    public void addVertexWorks() {
        GraphDTO graphDTO = prepareGraphDTO();
        graphDTO = graphService.createGraph(graphDTO);
        final UUID externalGraphId = graphDTO.getExternalGraphId();

        List<VertexDTO> vertices = prepareVertices();

        vertices.forEach(vertex -> vertexService.addVertex(externalGraphId, vertex));

        graphDTO = graphService.getGraphByExternalId(externalGraphId);
        assertThat(graphDTO.getVertices().size()).isEqualTo(vertices.size());
    }

    @Test
    public void addVertexWithWrongGraphIdThrowsException() {
        GraphDTO graphDTO = prepareGraphDTO();
        graphDTO = graphService.createGraph(graphDTO);

        VertexDTO dummyVertex = new VertexDTO();
        dummyVertex.setValue("Dummy vertex");

        Assertions.assertThrows(GraphException.class, () -> {
            vertexService.addVertex(UUID.randomUUID(), dummyVertex);
        });
    }

    private GraphDTO prepareGraphDTO() {
        GraphDTO graphDTO = new GraphDTO();
        graphDTO.setName("myGraph");
        return graphDTO;
    }

    private List<VertexDTO> prepareVertices() {
        String[] vertexNames = {"Bob", "Alice", "Mark", "Rob", "Maria"};

        return Arrays.asList(vertexNames).stream()
                .map(name -> {
                    VertexDTO vertexDTO = new VertexDTO();
                    vertexDTO.setValue(name);
                    return vertexDTO;
                })
                .collect(Collectors.toList());
    }
}
