package com.naterra.graphs.service.impl;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.EdgeDTO;
import com.naterra.graphs.model.GraphDTO;
import com.naterra.graphs.model.TraverseDTO;
import com.naterra.graphs.model.VertexDTO;
import com.naterra.graphs.service.GraphTraverseService;
import com.naterra.graphs.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class GraphTraverseServiceImpl implements GraphTraverseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphTraverseServiceImpl.class);

    private Map<UUID, Map<UUID, List<VertexDTO>>> adjacentMatrixes = new HashMap<>();
    private Map<UUID, GraphDTO> graphs = new HashMap<>();
    private Map<UUID, VertexDTO> vertices = new HashMap<>();

    @Override
    public GraphDTO addGraph(GraphDTO graphDTO) throws GraphException {
        if (graphDTO.getType() == null) {
            GraphUtil.logAndThrowException(LOGGER, "Graph type is not set");
        }

        GraphUtil.validateType(graphDTO.getType());

        graphDTO.setExternalGraphId(UUID.randomUUID());
        adjacentMatrixes.put(graphDTO.getExternalGraphId(), new HashMap<>());
        graphs.put(graphDTO.getExternalGraphId(), graphDTO);
        return graphDTO;
    }

    @Override
    public void addVertex(UUID externalGraphId, VertexDTO vertexDTO) throws GraphException {
        if (adjacentMatrixes.get(externalGraphId) == null) {
            GraphUtil.logAndThrowException(LOGGER, "Graph with id %s doesn't exist", externalGraphId.toString());
        }

        String vertexValueType = vertexDTO.getValue().getClass().getName();

        if (!vertexValueType.equals(graphs.get(externalGraphId).getType())) {
            GraphUtil.logAndThrowException(LOGGER, "Value of type %s not supported in graph %s",
                    vertexValueType, externalGraphId.toString());
        }

        if (vertexDTO.getExternalId() == null) {
            vertexDTO.setExternalId(UUID.randomUUID());
        }

        vertices.put(vertexDTO.getExternalId(), vertexDTO);
        adjacentMatrixes.get(externalGraphId).put(vertexDTO.getExternalId(), new ArrayList<>());
    }

    @Override
    public void addEdge(EdgeDTO edgeDTO) throws GraphException {
        Map<UUID, List<VertexDTO>> matrix = adjacentMatrixes.get(edgeDTO.getExternalGraphId());

        if (matrix == null) {
            GraphUtil.logAndThrowException(LOGGER, "Graph with id %s doesn't exist",
                    edgeDTO.getExternalGraphId().toString());
        }

        matrix.get(edgeDTO.getFromVertex().getExternalId()).add(edgeDTO.getToVertex());
        matrix.get(edgeDTO.getToVertex().getExternalId()).add(edgeDTO.getFromVertex());
    }

    @Override
    public GraphDTO getGraphById(UUID externalGraphId) {
        if (adjacentMatrixes.get(externalGraphId) == null) {
            LOGGER.warn("Graph by id {} not found", externalGraphId);
            return null;
        }

        Map<UUID, List<VertexDTO>> matrix = adjacentMatrixes.get(externalGraphId);

        GraphDTO graphDTO = new GraphDTO();
        graphDTO.setExternalGraphId(externalGraphId);
        graphDTO.setVertices(matrix.keySet()
                .stream()
                .map(vertexId -> vertices.get(vertexId))
                .collect(Collectors.toSet()));

        final Set<EdgeDTO> edges = new HashSet<>();
        final Set<String> edgeValues = new HashSet<>();

        matrix.forEach((key, value) -> value.forEach(vertex -> {
            String cacheValue = GraphUtil.calculateValueCache(vertices.get(key), vertex);

            if (!edgeValues.contains(cacheValue)) {
                edgeValues.add(cacheValue);
                EdgeDTO newEdge = new EdgeDTO();
                newEdge.setExternalGraphId(externalGraphId);
                newEdge.setFromVertex(vertices.get(key));
                newEdge.setToVertex(vertex);
                edges.add(newEdge);
            }
        }));

        graphDTO.setEdges(edges);

        return graphDTO;
    }

    @Override
    public Set<VertexDTO> traverse(TraverseDTO traverseDTO) throws GraphException {
        UUID externalGraphId = traverseDTO.getExternalGraphId();
        UUID rootVertexId = traverseDTO.getRootVertexId();
        VertexDTO rootVertex = validateAndFindRoot(externalGraphId, rootVertexId);
        Map<UUID, List<VertexDTO>> matrix = adjacentMatrixes.get(externalGraphId);

        Set<VertexDTO> visited = new LinkedHashSet<>();
        Queue<VertexDTO> queue = new LinkedList<>();

        queue.add(rootVertex);
        visited.add(rootVertex);

        while (!queue.isEmpty()) {
            VertexDTO vertex = queue.poll();

            for (VertexDTO v : matrix.get(vertex.getExternalId())) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    queue.add(v);
                }
            }
        }

        if (traverseDTO.getFunc() != null) {
            visited.forEach(v -> {
                Object updatedValue = new FunctionCalculatorImpl().calculate(v.getValue(), traverseDTO.getFunc());
                v.setValue(updatedValue);
                updateValueInMatrix(matrix, v.getExternalId(), updatedValue);
            });
        }

        return visited;
    }

    @Override
    public Set<VertexDTO> getPath(UUID externalGraphId, UUID fromVertexId, UUID toVertexId) throws GraphException {
        Set<VertexDTO> path = new LinkedHashSet<>();
        VertexDTO rootVertex = validateAndFindRoot(externalGraphId, fromVertexId, toVertexId);
        Map<UUID, List<VertexDTO>> matrix = adjacentMatrixes.get(externalGraphId);

        VertexDTO fromVertex = matrix.keySet()
                .stream()
                .filter(vertexId -> vertexId.equals(fromVertexId))
                .map(vertexId -> vertices.get(vertexId))
                .findFirst()
                .get();

        Optional<VertexDTO> toVertex = matrix.get(fromVertex.getExternalId())
                .stream()
                .filter(vertex -> vertex.getExternalId().equals(toVertexId))
                .findAny();

        if (toVertex.isPresent()) {
            path.add(fromVertex);
            path.add(toVertex.get());
        } else {
            boolean found = false;
            Set<VertexDTO> visited = new LinkedHashSet<>();
            Queue<VertexDTO> queue = new LinkedList<>();

            queue.add(rootVertex);
            visited.add(rootVertex);

            while (!queue.isEmpty()) {
                VertexDTO vertex = queue.poll();

                for (VertexDTO v : matrix.get(vertex.getExternalId())) {
                    if (v.getExternalId().equals(toVertexId)) {
                        visited.add(v);
                        found = true;
                        break;
                    }

                    if (!visited.contains(v)) {
                        visited.add(v);
                        queue.add(v);
                    }
                }
            }


            if (found) {
                path.add(rootVertex);

                Iterator<VertexDTO> it = visited.iterator();
                VertexDTO nextVertex = it.next();
                VertexDTO currentVertex = null;

                while (it.hasNext()) {
                    currentVertex = nextVertex;
                    nextVertex = it.next();

                    if (matrix.get(currentVertex.getExternalId()).contains(nextVertex)) {
                        path.add(nextVertex);
                    }
                }
            }
        }

        return path;
    }

    private VertexDTO validateAndFindRoot(UUID externalGraphId, UUID rootVertexId) throws GraphException {
        if (adjacentMatrixes.get(externalGraphId) == null) {
            GraphUtil.logAndThrowException(LOGGER, "Graph %s not found", externalGraphId.toString());
        }

        if (!isGraphContainVertex(externalGraphId, rootVertexId)) {
            GraphUtil.logAndThrowException(LOGGER, "Graph %s does not contain vertex %s",
                    externalGraphId.toString(), rootVertexId.toString());
        }

        return adjacentMatrixes.get(externalGraphId).keySet()
                .stream()
                .filter(vertexId -> vertexId.equals(rootVertexId))
                .map(vertexId -> vertices.get(vertexId))
                .findFirst()
                .get();

    }

    private VertexDTO validateAndFindRoot(UUID externalGraphId, UUID fromVertexId, UUID toVertexId) throws GraphException {
        if (adjacentMatrixes.get(externalGraphId) == null) {
            GraphUtil.logAndThrowException(LOGGER, "Graph %s not found", externalGraphId.toString());
        }

        if (!isGraphContainVertex(externalGraphId, fromVertexId)) {
            GraphUtil.logAndThrowException(LOGGER, "Graph %s does not contain vertex %s",
                    externalGraphId.toString(), fromVertexId.toString());
        }

        if (!isGraphContainVertex(externalGraphId, toVertexId)) {
            GraphUtil.logAndThrowException(LOGGER, "Graph %s does not contain vertex %s",
                    externalGraphId.toString(), toVertexId.toString());
        }

        return adjacentMatrixes.get(externalGraphId).keySet()
                .stream()
                .filter(vertexId -> vertexId.equals(fromVertexId))
                .map(vertexId -> vertices.get(vertexId))
                .findFirst()
                .get();

    }

    private boolean isGraphContainVertex(UUID externalGraphId, UUID rootVertexId) {
        return adjacentMatrixes.get(externalGraphId).keySet().contains(rootVertexId);
    }

    private void updateValueInMatrix(Map<UUID, List<VertexDTO>> matrix,
                                     UUID externalId,
                                     Object updatedValue) {
        Object newUpdatedValue = (updatedValue instanceof String) ?
                updatedValue = "'" + updatedValue + "'" :
                updatedValue;

        vertices.get(externalId).setValue(newUpdatedValue);

        for (Map.Entry<UUID, List<VertexDTO>> entry : matrix.entrySet()) {
            for (VertexDTO v : entry.getValue()) {
                if (v.getExternalId().equals(externalId)) {
                    v.setValue(newUpdatedValue);
                }
            }
        }
    }
}
