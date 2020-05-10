package com.naterra.graphs.service.impl;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.model.entity.GraphEntity;
import com.naterra.graphs.model.entity.VertexEntity;
import com.naterra.graphs.repository.GraphRepository;
import com.naterra.graphs.repository.VertexRepository;
import com.naterra.graphs.service.GraphTraverseService;
import com.naterra.graphs.service.VertexService;
import com.naterra.graphs.util.GraphUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VertexServiceImpl implements VertexService {

    private static final Logger LOGGER = LoggerFactory.getLogger(VertexServiceImpl.class);

    @Autowired
    private VertexRepository vertexRepository;

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private GraphTraverseService traverseService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public VertexDTO addVertex(UUID externalGraphId, VertexDTO vertex) throws GraphException {
        GraphEntity graph = graphRepository.findByExternalGraphId(externalGraphId);

        if (graph == null) {
            GraphUtil.logAndThrowException(LOGGER, "No graph by Id %s found", externalGraphId.toString());
        }

        VertexEntity vertexEntity = modelMapper.map(vertex, VertexEntity.class);

        vertexEntity.setExternalGraphId(graph.getExternalGraphId());
        vertexEntity.setExternalId(UUID.randomUUID());

        VertexDTO vertexSaved = modelMapper.map(vertexRepository.save(vertexEntity), VertexDTO.class);

        traverseService.addVertex(vertexSaved);

        return vertexSaved;
    }

}
