package com.naterra.graphs.service.impl;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.dto.EdgeDTO;
import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.model.entity.EdgeEntity;
import com.naterra.graphs.model.entity.VertexEntity;
import com.naterra.graphs.repository.EdgeRepository;
import com.naterra.graphs.repository.VertexRepository;
import com.naterra.graphs.service.EdgeService;
import com.naterra.graphs.service.GraphTraverseService;
import com.naterra.graphs.util.GraphUtil;
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
    public EdgeDTO addEdge(UUID externalGraphId, EdgeDTO edgeDTO) throws GraphException {
        validate(externalGraphId, edgeDTO);

        return saveEdge(externalGraphId, edgeDTO);
    }

    @Override
    public EdgeDTO addEdge(UUID externalGraphId, String values) throws GraphException {
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

    private EdgeDTO valuesToEdge(String values) throws GraphException {
        String[] tokens = values.split("\\.\\.");

        if (tokens.length != 2) {
            GraphUtil.logAndThrowException(LOGGER, "Incorrect vertices: %s", values);
        }

        VertexEntity vertexFrom = vertexRepository.findVertexEntityByValue(tokens[0]);
        VertexEntity vertexTo = vertexRepository.findVertexEntityByValue(tokens[1]);

        EdgeDTO edgeDTO = new EdgeDTO();
        edgeDTO.setFromVertex(modelMapper.map(vertexFrom, VertexDTO.class));
        edgeDTO.setToVertex(modelMapper.map(vertexTo, VertexDTO.class));

        return edgeDTO;
    }

    private void validate(UUID externalGraphId, EdgeDTO edgeDTO) throws GraphException {
        UUID fromVertexId = edgeDTO.getFromVertex().getExternalId();
        UUID toVertexId = edgeDTO.getToVertex().getExternalId();

        VertexEntity vertexFrom = vertexRepository.findVertexEntityByExternalId(fromVertexId);

        if (vertexFrom == null) {
            GraphUtil.logAndThrowException(LOGGER, "No vertex by Id %s found", fromVertexId.toString());
        }

        if (!vertexFrom.getExternalGraphId().equals(externalGraphId)) {
            GraphUtil.logAndThrowException(LOGGER, "Vertex %s doesn't belong to graph %s",
                    vertexFrom.getExternalGraphId().toString(), externalGraphId.toString());
        }

        VertexEntity vertexTo = vertexRepository.findVertexEntityByExternalId(toVertexId);

        if (vertexTo == null) {
            GraphUtil.logAndThrowException(LOGGER, "No vertex by Id %s found", toVertexId.toString());
        }

        if (!vertexTo.getExternalGraphId().equals(externalGraphId)) {
            GraphUtil.logAndThrowException(LOGGER, "Vertex %s doesn't belong to graph %s",
                    vertexTo.getExternalGraphId().toString(), externalGraphId.toString());
        }

        if (!vertexFrom.getExternalGraphId().equals(vertexTo.getExternalGraphId())) {
            GraphUtil.logAndThrowException(LOGGER, "Vertices %s and %s belong to different graphs",
                    fromVertexId.toString(), toVertexId.toString());
        }
    }

}
