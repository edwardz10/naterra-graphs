package com.naterra.graphs.service.impl;

import com.naterra.graphs.model.dto.GraphDTO;
import com.naterra.graphs.service.GraphService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class GraphServiceImplTest {

    @Autowired
    private GraphService graphService;

    @Test
    public void createGraphWorks() {
        GraphDTO graphDTO = prepareGraphDTO();

        graphDTO = graphService.createGraph(graphDTO);

        assertThat(graphDTO.getId()).isNotNull();
        assertThat(graphDTO.getExternalGraphId()).isNotNull();
    }

    @Test
    public void getGraphByExternalIdWorks() {
        GraphDTO graphDTO = prepareGraphDTO();

        graphDTO = graphService.createGraph(graphDTO);

        assertThat(graphService.getGraphByExternalId(graphDTO.getExternalGraphId())).isNotNull();
    }

    private GraphDTO prepareGraphDTO() {
        GraphDTO graphDTO = new GraphDTO();
        graphDTO.setName("myGraph");
        return graphDTO;
    }
}
