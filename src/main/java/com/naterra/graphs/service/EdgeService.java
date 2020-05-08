package com.naterra.graphs.service;

import com.naterra.graphs.model.dto.EdgeDTO;
import com.naterra.graphs.model.entity.EdgeEntity;
import com.naterra.graphs.model.entity.VertexEntity;
import com.naterra.graphs.repository.EdgeRepository;
import com.naterra.graphs.repository.VertexRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EdgeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EdgeService.class);

    @Autowired
    private VertexRepository vertexRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public EdgeDTO addEdge(UUID externalGraphId, EdgeDTO edgeDTO) throws Exception {
        validate(externalGraphId, edgeDTO);

        edgeDTO.setExternalGraphId(externalGraphId);
        edgeDTO.setExternalId(UUID.randomUUID());

        EdgeEntity edgeEntity = modelMapper.map(edgeDTO, EdgeEntity.class);

        return modelMapper.map(edgeRepository.save(edgeEntity), EdgeDTO.class);
    }

    public EdgeDTO addEdge(UUID externalGraphId, String values) throws Exception {
        EdgeDTO edgeDTO = valuesToEdge(values);
        validate(externalGraphId, edgeDTO);

        edgeDTO.setExternalGraphId(externalGraphId);
        edgeDTO.setExternalId(UUID.randomUUID());

        EdgeEntity edgeEntity = modelMapper.map(edgeDTO, EdgeEntity.class);

        return modelMapper.map(edgeRepository.save(edgeEntity), EdgeDTO.class);
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
        edgeDTO.setFromVertexId(vertexFrom.getExternalId());
        edgeDTO.setToVertexId(vertexTo.getExternalId());

        return edgeDTO;
    }

    private void validate(UUID externalGraphId, EdgeDTO edgeDTO) throws Exception {
        UUID fromVertexId = edgeDTO.getFromVertexId();
        UUID toVertexId = edgeDTO.getToVertexId();

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
