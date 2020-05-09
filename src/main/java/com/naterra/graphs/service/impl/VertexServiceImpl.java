package com.naterra.graphs.service.impl;

import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.model.entity.GraphEntity;
import com.naterra.graphs.model.entity.VertexEntity;
import com.naterra.graphs.repository.GraphRepository;
import com.naterra.graphs.repository.VertexRepository;
import com.naterra.graphs.service.GraphTraverseService;
import com.naterra.graphs.service.VertexService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VertexServiceImpl implements VertexService {

    @Autowired
    private VertexRepository vertexRepository;

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private GraphTraverseService traverseService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public VertexDTO addVertex(UUID externalGraphId, VertexDTO vertex) throws Exception {
        GraphEntity graph = graphRepository.findByExternalGraphId(externalGraphId);

        if (graph == null) {
            throw new Exception("No graph by graph Id " + externalGraphId + " found");
        }

        VertexEntity vertexEntity = modelMapper.map(vertex, VertexEntity.class);

        vertexEntity.setExternalGraphId(graph.getExternalGraphId());
        vertexEntity.setExternalId(UUID.randomUUID());

        VertexDTO vertexSaved = modelMapper.map(vertexRepository.save(vertexEntity), VertexDTO.class);

        traverseService.addVertex(vertexSaved);

        return vertexSaved;
    }

}
