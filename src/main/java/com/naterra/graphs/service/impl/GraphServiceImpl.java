package com.naterra.graphs.service.impl;

import com.naterra.graphs.model.dto.GraphDTO;
import com.naterra.graphs.model.dto.VertexDTO;
import com.naterra.graphs.model.entity.GraphEntity;
import com.naterra.graphs.repository.EdgeRepository;
import com.naterra.graphs.repository.GraphRepository;
import com.naterra.graphs.repository.VertexRepository;
import com.naterra.graphs.service.EdgeService;
import com.naterra.graphs.service.GraphService;
import com.naterra.graphs.service.GraphTraverseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GraphServiceImpl implements GraphService {

    @Autowired
    private GraphRepository graphRepository;

    @Autowired
    private VertexRepository vertexRepository;

    @Autowired
    private EdgeRepository edgeRepository;

    @Autowired
    private EdgeService edgeService;

    @Autowired
    private GraphTraverseService traverseService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<GraphDTO> getAllGraphs() {
        return graphRepository.findAll()
                .stream()
                .map(graphEntity -> modelMapper.map(graphEntity, GraphDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public GraphDTO createGraph(GraphDTO graph) {
        GraphEntity graphEntity = modelMapper.map(graph, GraphEntity.class);

        if (graphEntity.getExternalGraphId() == null) {
            graphEntity.setExternalGraphId(UUID.randomUUID());
        }

        GraphDTO graphSaved = modelMapper.map(graphRepository.save(graphEntity), GraphDTO.class);

        traverseService.addGraph(graphSaved);

        return graphSaved;
    }

    public GraphDTO getGraphByExternalId(UUID externalGraphId) {
        GraphEntity graphEntity = graphRepository.findByExternalGraphId(externalGraphId);
        GraphDTO graphDTO = modelMapper.map(graphEntity, GraphDTO.class);

        graphDTO.setVertices(vertexRepository.findAllByExternalGraphId(externalGraphId)
                .stream()
                .map(vertex -> modelMapper.map(vertex, VertexDTO.class))
                .collect(Collectors.toList()));

        graphDTO.setEdges(edgeRepository.findAllByExternalGraphId(externalGraphId)
                .stream()
                .map(edge -> edgeService.convert(edge))
                .collect(Collectors.toList()));

        return graphDTO;
    }

}
