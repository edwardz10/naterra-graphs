#naterra-graphs

<b>Naterra Graphs</b> is a Spring Boot application that provides basic operations for creating an undirected graph.
No data persistence level is available at the moment.

## REST API
<b>Naterra Graphs</b> supports REST API for the following operations:
  - Create a graph (provide name, type as a Java class (i.e. "java.lang.String)). Only types of "java.lang" package are supported;
  - Create a vertex (provide UUID of a graph, value, which type corresponds to the graphs's one);
  - Create an edge between vertices (provide UUID of a graph, vertex "from" and vertex "to");
  - Traverse a graph (provide UUID of a graph, UUID of a vertex to start from, and a function in JS format to apply to vertices' values (optional));
  - Get a path between vertices (provide UUID of a graph, UUID of the "from" vertex, UUID of the "to" vertex);
  
## Tests
For testings purposes, in <b>Naterra Graphs</b> there are:
  - unit tests;
  - a Postman collection;
  
## How to run:
`mvn clean package`
`mvn spring-boot:run`
, and the web app will be available at http://localhost:8080

## Future improvements:

1. <b>Add weighted edges support</b>.<p>
<b><i>How to solve</i></b> - add a 'Integer weight' parameter to EdgeDTO class. In this case, it would be nice to implement Dijkstra's algorithm, like this: https://www.vogella.com/tutorials/JavaAlgorithmsDijkstra/article.html

2. <b>Make the graph thread-safe</b>.<p>
<b><i>How to solve</i></b> - that would require usage of thread-safe data structures from the `java.util.concurrent' package to store adjacency matrix
as long as the vertices map + some smart locking algorithm of graph data to ensure data consistency between vertices, vertice values and edges. 

3. <b>Support directed graphs</b>.<p>
<b><i>How to solve</i></b> - add a 'boolean directed' parameter to the EdgeDTO class + reconsider 'traverse' and 'path'
operations in the `GraphTraverseServiceImpl` class.
