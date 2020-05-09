package com.naterra.graphs.service.impl;

import com.naterra.graphs.model.dto.EdgeDTO;
import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.model.entity.EdgeEntity;
import com.naterra.graphs.model.entity.VertexEntity;
import com.naterra.graphs.repository.EdgeRepository;
import com.naterra.graphs.repository.VertexRepository;
import com.naterra.graphs.service.EdgeService;
import com.naterra.graphs.service.GraphTraverseService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EdgeServiceImpl implements EdgeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeServiceImpl.class);

    @Autowired
    private VertexRepository vertexRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private GraphTraverseService traverseService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public EdgeDTO addEdge(UUID externalGraphId, EdgeDTO edgeDTO) throws Exception {
        validate(externalGraphId, edgeDTO);

        return saveEdge(externalGraphId, edgeDTO);
    }

    @Override
    public EdgeDTO addEdge(UUID externalGraphId, String values) throws Exception {
        EdgeDTO edgeDTO = valuesToEdge(values);
        validate(externalGraphId, edgeDTO);

        return saveEdge(externalGraphId, edgeDTO);
    }

    @Override
    public EdgeDTO convert(EdgeEntity edgeEntity) {
        EdgeDTO edgeDTO = modelMapper.map(edgeEntity, EdgeDTO.class);
        edgeDTO.setFromVertex(
                modelMapper.map(
                        vertexRepository.findVertexEntityByExternalId(edgeEntity.getFromVertexId()), VertexDTO.class));
        edgeDTO.setToVertex(
                modelMapper.map(
                        vertexRepository.findVertexEntityByExternalId(edgeEntity.getToVertexId()), VertexDTO.class));
        return edgeDTO;
    }

    private EdgeDTO saveEdge(UUID externalGraphId, EdgeDTO edgeDTO) {
        EdgeEntity edgeEntity = modelMapper.map(edgeDTO, EdgeEntity.class);
        edgeEntity.setFromVertexId(edgeDTO.getFromVertex().getExternalId());
        edgeEntity.setToVertexId(edgeDTO.getToVertex().getExternalId());

        edgeEntity.setExternalGraphId(externalGraphId);
        edgeEntity.setExternalId(UUID.randomUUID());

        EdgeDTO edgeSaved = modelMapper.map(edgeRepository.save(edgeEntity), EdgeDTO.class);
        edgeSaved.setFromVertex(edgeDTO.getFromVertex());
        edgeSaved.setToVertex(edgeDTO.getToVertex());

        traverseService.addEdge(edgeSaved);

        return edgeSaved;
    }

    private EdgeDTO valuesToEdge(String values) throws Exception {
        String[] tokens = values.split("\\.\\.");

        if (tokens.length != 2) {
            LOGGER.error("Incorrect vertices: {}", values);
            throw new Exception("Incorrect vertices: " + values);
        }

        VertexEntity vertexFrom = vertexRepository.findVertexEntityByValue(tokens[0]);
        VertexEntity vertexTo = vertexRepository.findVertexEntityByValue(tokens[1]);

        EdgeDTO edgeDTO = new EdgeDTO();
        edgeDTO.setFromVertex(modelMapper.map(vertexFrom, VertexDTO.class));
        edgeDTO.setToVertex(modelMapper.map(vertexTo, VertexDTO.class));

        return edgeDTO;
    }

    private void validate(UUID externalGraphId, EdgeDTO edgeDTO) throws Exception {
        UUID fromVertexId = edgeDTO.getFromVertex().getExternalId();
        UUID toVertexId = edgeDTO.getToVertex().getExternalId();

        VertexEntity vertexFrom = vertexRepository.findVertexEntityByExternalId(fromVertexId);

        if (vertexFrom == null) {
            LOGGER.error("No vertex by Id {} found", fromVertexId);
            throw new Exception("Wrong vertex Id: " + fromVertexId);
        }

        if (!vertexFrom.getExternalGraphId().equals(externalGraphId)) {
            LOGGER.error("Vertex {} doesn't belong to graph {}", vertexFrom.getExternalGraphId(), externalGraphId);
            throw new Exception("Vertex " + vertexFrom.getExternalGraphId() + " doesn't belong to graph " + externalGraphId);
        }

        VertexEntity vertexTo = vertexRepository.findVertexEntityByExternalId(toVertexId);

        if (vertexTo == null) {
            LOGGER.error("No vertex by Id {} found", toVertexId);
            throw new Exception("Wrong vertex Id: " + toVertexId);
        }

        if (!vertexTo.getExternalGraphId().equals(externalGraphId)) {
            LOGGER.error("Vertex {} doesn't belong to graph {}", vertexTo.getExternalGraphId(), externalGraphId);
            throw new Exception("Vertex " + vertexTo.getExternalGraphId() + " doesn't belong to graph " + externalGraphId);
        }

        if (!vertexFrom.getExternalGraphId().equals(vertexTo.getExternalGraphId())) {
            LOGGER.error("Vertices {} and {} belong to different graphs!", fromVertexId, toVertexId);
            throw new Exception("Vertices " + fromVertexId + " and " + toVertexId + " belong to different graphs!");
        }
    }

}
