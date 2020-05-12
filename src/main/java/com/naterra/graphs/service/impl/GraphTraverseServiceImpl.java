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

    private Map<UUID, Map<VertexDTO<?>, List<VertexDTO<?>>>> adjacentMatrixes = new HashMap<>();
    private Map<UUID, GraphDTO> graphs = new HashMap<>();

    @Override
    public GraphDTO addGraph(GraphDTO graphDTO) {
        if (graphDTO.getType() == null) {
            GraphUtil.logAndThrowException(LOGGER, "Graph type is not set");
        }

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

        adjacentMatrixes.get(externalGraphId).put(vertexDTO, new ArrayList<>());
    }

    @Override
    public void addEdge(EdgeDTO edgeDTO) throws GraphException {
        Map<VertexDTO<?>, List<VertexDTO<?>>> matrix = adjacentMatrixes.get(edgeDTO.getExternalGraphId());

        if (matrix == null) {
            GraphUtil.logAndThrowException(LOGGER, "Graph with id %s doesn't exist",
                    edgeDTO.getExternalGraphId().toString());
        }

        matrix.get(edgeDTO.getFromVertex()).add(edgeDTO.getToVertex());
        matrix.get(edgeDTO.getToVertex()).add(edgeDTO.getFromVertex());
    }

    @Override
    public void addEdge(UUID externalGraphId, String values) throws GraphException {
        Map<VertexDTO<?>, List<VertexDTO<?>>> matrix = adjacentMatrixes.get(externalGraphId);

        if (matrix == null) {
            GraphUtil.logAndThrowException(LOGGER, "Graph with id %s doesn't exist", externalGraphId.toString());
        }

        String[] tokens = values.split("\\.\\.");

        if (tokens.length != 2) {
            GraphUtil.logAndThrowException(LOGGER, "Vertices values '%s' are incorrect", values);
        }

        VertexDTO vertexFrom = GraphUtil.getVertexByValue(matrix, tokens[0]);

        if (vertexFrom == null) {
            GraphUtil.logAndThrowException(LOGGER, "Vertex with value %s not found", tokens[0]);
        }

        VertexDTO vertexTo = GraphUtil.getVertexByValue(matrix, tokens[1]);

        if (vertexTo == null) {
            GraphUtil.logAndThrowException(LOGGER, "Vertex with value %s not found", tokens[1]);
        }

        matrix.get(vertexFrom).add(vertexTo);
        matrix.get(vertexTo).add(vertexFrom);
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

        Map<VertexDTO<?>, List<VertexDTO<?>>> matrix = adjacentMatrixes.get(externalGraphId);

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
        Map<VertexDTO<?>, List<VertexDTO<?>>> matrix = adjacentMatrixes.get(externalGraphId);

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
        Map<VertexDTO<?>, List<VertexDTO<?>>> matrix = adjacentMatrixes.get(externalGraphId);

        VertexDTO fromVertex = matrix.keySet()
                .stream()
                .filter(vertex -> vertex.getExternalId().equals(fromVertexId))
                .findFirst()
                .get();

        Optional<VertexDTO<?>> toVertex = matrix.get(fromVertex)
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

    private String calculateValueCache(VertexDTO from, VertexDTO to) {
        String[] values = {from.getValue().toString(), to.getValue().toString()};
        Arrays.sort(values);
        return values[0] + values[1];
    }
}
