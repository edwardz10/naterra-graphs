package com.naterra.graphs.service.impl;

import com.naterra.graphs.exception.GraphException;
import com.naterra.graphs.model.EdgeDTO;
import com.naterra.graphs.model.GraphDTO;
import com.naterra.graphs.model.VertexDTO;
import com.naterra.graphs.service.GraphTraverseService;
import com.naterra.graphs.util.GraphUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GraphTraverseServiceImpl implements GraphTraverseService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphTraverseServiceImpl.class);

    private Map<UUID, Map<VertexDTO, List<VertexDTO>>> adjacentMatrixes = new HashMap<>();

    @Override
    public GraphDTO addGraph(GraphDTO graphDTO) {
        graphDTO.setExternalGraphId(UUID.randomUUID());
        adjacentMatrixes.put(graphDTO.getExternalGraphId(), new HashMap<>());
        return graphDTO;
    }

    @Override
    public void addVertex(UUID externalGraphId, VertexDTO vertexDTO) {
        adjacentMatrixes.get(externalGraphId).put(vertexDTO, new ArrayList<>());
    }

    @Override
    public void addEdge(EdgeDTO edgeDTO) {
        Map<VertexDTO, List<VertexDTO>> matrix = adjacentMatrixes.get(edgeDTO.getExternalGraphId());
        matrix.get(edgeDTO.getFromVertex()).add(edgeDTO.getToVertex());
        matrix.get(edgeDTO.getToVertex()).add(edgeDTO.getFromVertex());
    }

    @Override
    public void removeVertex(VertexDTO vertexDTO) {

    }

    @Override
    public void removeEdge(EdgeDTO edgeDTO) {

    }

    @Override
    public GraphDTO getGraphById(UUID externalGraphId) {
        if (adjacentMatrixes.get(externalGraphId) == null) {
            LOGGER.warn("Graph by id {} not found", externalGraphId);
            return null;
        }

        Map<VertexDTO, List<VertexDTO>> matrix = adjacentMatrixes.get(externalGraphId);

        GraphDTO graphDTO = new GraphDTO();
        graphDTO.setExternalGraphId(externalGraphId);
        graphDTO.setVertices(matrix.keySet());

        final Set<EdgeDTO> edges = new HashSet<>();
        final Set<String> edgeValues = new HashSet<>();

        matrix.forEach((key, value) -> value.forEach(vertex -> {
            String cacheValue = calculateValueCache(key, vertex);

            if (!edgeValues.contains(cacheValue)) {
                edgeValues.add(cacheValue);
                EdgeDTO newEdge = new EdgeDTO();
                newEdge.setExternalGraphId(externalGraphId);
                newEdge.setFromVertex(key);
                newEdge.setToVertex(vertex);
                edges.add(newEdge);
            }
        }));

        graphDTO.setEdges(edges);

        return graphDTO;
    }

    @Override
    public Set<VertexDTO> traverse(UUID externalGraphId, UUID rootVertexId) throws GraphException {
        VertexDTO rootVertex = validateAndFindRoot(externalGraphId, rootVertexId);
        Map<VertexDTO, List<VertexDTO>> matrix = adjacentMatrixes.get(externalGraphId);

        Set<VertexDTO> visited = new LinkedHashSet<>();
        Queue<VertexDTO> queue = new LinkedList<>();

        queue.add(rootVertex);
        visited.add(rootVertex);

        while (!queue.isEmpty()) {
            VertexDTO vertex = queue.poll();

            for (VertexDTO v : matrix.get(vertex)) {
                if (!visited.contains(v)) {
                    visited.add(v);
                    queue.add(v);
                }
            }
        }

        return visited;
    }

    @Override
    public Set<VertexDTO> getPath(UUID externalGraphId, UUID fromVertexId, UUID toVertexId) throws GraphException {
        Set<VertexDTO> path = new LinkedHashSet<>();
        VertexDTO rootVertex = validateAndFindRoot(externalGraphId, fromVertexId, toVertexId);
        Map<VertexDTO, List<VertexDTO>> matrix = adjacentMatrixes.get(externalGraphId);

        VertexDTO fromVertex = matrix.keySet()
                .stream()
                .filter(vertex -> vertex.getExternalId().equals(fromVertexId))
                .findFirst()
                .get();

        Optional<VertexDTO> toVertex = matrix.get(fromVertex)
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

                for (VertexDTO v : matrix.get(vertex)) {
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

                    if (matrix.get(currentVertex).contains(nextVertex)) {
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
                .filter(vertex -> vertex.getExternalId().equals(rootVertexId))
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
                .filter(vertex -> vertex.getExternalId().equals(fromVertexId))
                .findFirst()
                .get();

    }

    private boolean isGraphContainVertex(UUID externalGraphId, UUID rootVertexId) {
        return adjacentMatrixes.get(externalGraphId).keySet().stream()
                .anyMatch(vertex -> vertex.getExternalId().equals(rootVertexId));
    }

    private boolean containsKey(Map<VertexDTO, VertexDTO> matrix, UUID externalId) {
        return matrix.keySet()
                .stream()
                .anyMatch(vertex -> vertex.getExternalId().equals(externalId));
    }

    private String calculateValueCache(VertexDTO from, VertexDTO to) {
        String[] values = {from.getValue(), to.getValue()};
        Arrays.sort(values);
        return values[0] + values[1];
    }
}
