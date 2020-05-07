package com.naterra.graphs.service;

import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.model.entity.GraphEntity;
import com.naterra.graphs.model.entity.VertexEntity;
import com.naterra.graphs.repository.GraphRepository;
import com.naterra.graphs.repository.VertexRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VertexService {

    @Autowired
    private VertexRepository vertexRepository;

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private ModelMapper modelMapper;

    public VertexDTO addVertexToGraph(UUID externalGraphId, VertexDTO vertex) throws Exception {
        GraphEntity graph = graphRepository.findByExternalGraphId(externalGraphId);

        if (graph == null) {
            throw new Exception("No graph by graph Id " + externalGraphId + " found");
        }

        vertex.setExternalGraphId(graph.getExternalGraphId());

        VertexEntity vertexEntity = modelMapper.map(vertex, VertexEntity.class);

        return modelMapper.map(vertexRepository.save(vertexEntity), VertexDTO.class);
    }
}
