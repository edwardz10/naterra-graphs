package com.naterra.graphs.service;

import com.naterra.graphs.model.dto.GraphDTO;
import com.naterra.graphs.model.entity.GraphEntity;
import com.naterra.graphs.repository.EdgeRepository;
import com.naterra.graphs.repository.GraphRepository;
import com.naterra.graphs.repository.VertexRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GraphService {

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private VertexRepository vertexRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<GraphDTO> getAllGraphs() {
        return graphRepository.findAll()
                .stream()
                .map(graphEntity -> modelMapper.map(graphEntity, GraphDTO.class))
                .collect(Collectors.toList());
    }

    public GraphDTO createGraph(GraphDTO graph) {
        GraphEntity graphEntity = modelMapper.map(graph, GraphEntity.class);

        if (graphEntity.getExternalGraphId() == null) {
            graphEntity.setExternalGraphId(UUID.randomUUID());
        }

        return modelMapper.map(graphRepository.save(graphEntity), GraphDTO.class);
    }

    public GraphDTO getGraphByExternalId(UUID externalGraphId) {
        GraphEntity graphEntity = graphRepository.findByExternalGraphId(externalGraphId);
        GraphDTO graphDTO = modelMapper.map(graphEntity, GraphDTO.class);
        graphDTO.setVertices(vertexRepository.findAllByExternalGraphId(externalGraphId));
        graphDTO.setEdges(edgeRepository.findAllByExternalGraphId(externalGraphId));
        return graphDTO;
    }

}
