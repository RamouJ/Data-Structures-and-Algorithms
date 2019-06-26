package datastructures.concrete;

import datastructures.concrete.dictionaries.ChainedHashDictionary;
import datastructures.concrete.dictionaries.KVPair;
import datastructures.interfaces.*;
import datastructures.concrete.ArrayDisjointSet;
import misc.Sorter;
import misc.exceptions.NoPathExistsException;
import misc.exceptions.NotYetImplementedException;
import sun.security.provider.certpath.Vertex;

import java.lang.reflect.Array;

/**
 * Represents an undirected, weighted graph, possibly containing self-loops, parallel edges,
 * and unconnected components.
 *
 * Note: This class is not meant to be a full-featured way of representing a graph.
 * We stick with supporting just a few, core set of operations needed for the
 * remainder of the project.
 */
public class Graph<V, E extends IEdge<V> & Comparable<E>> {

    private IDictionary<V, ISet<E>> adjacencyList;
    private IList<E> edgesList;
    private IList<V> vertexList;
    // NOTE 1:
    //
    // Feel free to add as many fields, private helper methods, and private
    // inner classes as you want.
    //
    // And of course, as always, you may also use any of the data structures
    // and algorithms we've implemented so far.
    //
    // Note: If you plan on adding a new class, please be sure to make it a private
    // static inner class contained within this file. Our testing infrastructure
    // works by copying specific files from your project to ours, and if you
    // add new files, they won't be copied and your code will not compile.
    //
    //
    // NOTE 2:
    //
    // You may notice that the generic types of Graph are a little bit more
    // complicated than usual.
    //
    // This class uses two generic parameters: V and E.
    //
    // - 'V' is the type of the vertices in the graph. The vertices can be
    //   any type the client wants -- there are no restrictions.
    //
    // - 'E' is the type of the edges in the graph. We've constrained Graph
    //   so that E *must* always be an instance of IEdge<V> AND Comparable<E>.
    //
    //   What this means is that if you have an object of type E, you can use
    //   any of the methods from both the IEdge interface and from the Comparable
    //   interface
    //
    // If you have any additional questions about generics, or run into issues while
    // working with them, please ask ASAP either on Piazza or during office hours.
    //
    // Working with generics is really not the focus of this class, so if you
    // get stuck, let us know we'll try and help you get unstuck as best as we can.

    /**
     * Constructs a new graph based on the given vertices and edges.
     *
     * Note that each edge in 'edges' represents a unique edge. For example, if 'edges'
     * contains an entry for '(A,B)' and for '(B,A)', that means there are two parallel
     * edges between vertex 'A' and vertex 'B'.
     *
     * @throws IllegalArgumentException if any edges have a negative weight DONE
     * @throws IllegalArgumentException if any edges connect to a vertex not present in 'vertices' DONE
     * @throws IllegalArgumentException if 'vertices' or 'edges' are null or contain null DONE
     * @throws IllegalArgumentException if 'vertices' contains duplicates DONE
     */
    public Graph(IList<V> vertices, IList<E> edges) {
        this.vertexList = vertices;
        this.edgesList = edges;
        this.adjacencyList = new ChainedHashDictionary<>();
        ISet<E> set;

        if (vertices == null || edges == null) {
            throw new IllegalArgumentException();
        }

        if (vertices.contains(null) || edges.contains(null)) {
            throw new IllegalArgumentException();
        }

        for (V vertex: vertices) {
            if (adjacencyList.containsKey(vertex)) {
               throw new IllegalArgumentException();
            }
            set = new ChainedHashSet<>();
            adjacencyList.put(vertex, set);
        }

        for (E edge : edges) {
            if (edge.getWeight() < 0) {
                throw new IllegalArgumentException();
            }

            V vertex1 = edge.getVertex1();
            V vertex2 = edge.getVertex2();

            if (!vertices.contains(vertex1)) {
                throw new IllegalArgumentException();
            }

            if (!vertices.contains(vertex2)) {
                throw new IllegalArgumentException();
            }

            ISet<E> vertex1Edges = adjacencyList.get(vertex1);
            ISet<E> vertex2Edges = adjacencyList.get(vertex2);

            vertex1Edges.add(edge);
            vertex2Edges.add(edge);

            adjacencyList.put(vertex1, vertex1Edges);
            adjacencyList.put(vertex2, vertex2Edges);
        }
    }

    /**
     * Sometimes, we store vertices and edges as sets instead of lists, so we
     * provide this extra constructor to make converting between the two more
     * convenient.
     *
     * @throws IllegalArgumentException if any of the edges have a negative weight
     * @throws IllegalArgumentException if one of the edges connects to a vertex not
     *                                  present in the 'vertices' list
     * @throws IllegalArgumentException if vertices or edges are null or contain null
     */
    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

    // You shouldn't need to call this helper method -- it only needs to be used
    // in the constructor above.
    private static <T> IList<T> setToList(ISet<T> set) {
        if (set == null) {
            throw new IllegalArgumentException();
        }
        IList<T> output = new DoubleLinkedList<>();
        for (T item : set) {
            output.add(item);
        }
        return output;
    }

    /**
     * Returns the number of vertices contained within this graph.
     */
    public int numVertices() {
        return adjacencyList.size();
    }

    /**
     * Returns the number of edges contained within this graph.
     */
    public int numEdges() {
        ISet<E> edgesSet = new ChainedHashSet<>();
        for (KVPair<V, ISet<E>> vertex: adjacencyList) {
            for (E edges: vertex.getValue()) {
                edgesSet.add(edges);
            }
        }
        return edgesSet.size();
    }

    /**
     * Returns the set of all edges that make up the minimum spanning tree of
     * this graph.
     *
     * If there exists multiple valid MSTs, return any one of them.
     *
     * Precondition: the graph does not contain any unconnected components.
     */
    public ISet<E> findMinimumSpanningTree() {
        ISet<E> setToReturn = new ChainedHashSet<>();
        ArrayDisjointSet<V> set = new ArrayDisjointSet();

        for (KVPair<V, ISet<E>> vertex : adjacencyList) {
            set.makeSet(vertex.getKey());
        }

        IList<E> sortedList = Sorter.topKSort(edgesList.size(), edgesList);

        for (E edge: sortedList) {
            V vertex1 = edge.getVertex1();
            V vertex2 = edge.getVertex2();
            if (!(set.findSet(vertex1) == set.findSet(vertex2))) {
                set.union(vertex1, vertex2);
                setToReturn.add(edge);
            }
        }

        return setToReturn;
    }

    /**
     * Returns the edges that make up the shortest path from the start
     * to the end.
     *
     * The first edge in the output list should be the edge leading out
     * of the starting node; the last edge in the output list should be
     * the edge connecting to the end node.
     *
     * Return an empty list if the start and end vertices are the same.
     *
     * @throws NoPathExistsException  if there does not exist a path from the start to the end
     * @throws IllegalArgumentException if start or end is null or not in the graph
     */

    private static class ExampleComparableVertex<V, E> implements Comparable<ExampleComparableVertex<V, E>> {

        private final V vertex;
        private final double cost;
        private V preVer;
        private  E preEdge;

        public ExampleComparableVertex(V vertex , double cost){
            this.vertex = vertex;
            this.cost = cost;
        }

        @Override
        public int compareTo(ExampleComparableVertex other) {
            if(this.cost < other.cost){
                return -1;
            }
            else if (this.cost > other.cost) {
                return 1;
            }
            else {
                return 0;
            }
        }
    }

    public IList<E> findShortestPathBetween(V start, V end) {

        IDictionary<V,ExampleComparableVertex> vertices = new ChainedHashDictionary<>();

        ExampleComparableVertex vertexObj;
        ExampleComparableVertex newVertexObj;

        for(V vertex: vertexList){
            vertexObj = new ExampleComparableVertex(vertex,Double.POSITIVE_INFINITY);
            vertices.put(vertex,vertexObj);
        }

        newVertexObj = new ExampleComparableVertex(start,0.0);
        vertices.put(start,newVertexObj);

        IPriorityQueue<ExampleComparableVertex<V,E>> MPQueue = new ArrayHeap<>();
        //add source
        MPQueue.add(vertices.get(start));

        ISet<ExampleComparableVertex> checker = new ChainedHashSet<>();

        while (!MPQueue.isEmpty()){
            ExampleComparableVertex<V,E> minVertex = MPQueue.removeMin();
            ISet<E> edges = adjacencyList.get(minVertex.vertex);

            if(!checker.contains(minVertex)) {
                for (E edge : edges) {
                    ExampleComparableVertex vertexInDic = vertices.get(edge.getOtherVertex(minVertex.vertex));
                    double oldCost = vertexInDic.cost;
                    double newCost = vertexInDic.cost + edge.getWeight();
                    if (newCost < oldCost){
                        vertexInDic.preVer = minVertex.vertex;
                        vertexInDic.preEdge = edge;
                        if(oldCost == Double.POSITIVE_INFINITY){
                            MPQueue.add(vertexInDic);
                        } else {
                            MPQueue.replace(vertexInDic,new ExampleComparableVertex(vertexInDic,newCost));
                        }
                    }
                }
                checker.add(minVertex);
            }
        }
        IList<E> shortestPath = new DoubleLinkedList<>();

        ExampleComparableVertex<V,E> lastVertex = vertices.get(end);

        while (lastVertex.preVer != null) {
            shortestPath.insert(0,lastVertex.preEdge);
            lastVertex = vertices.get(lastVertex.preVer);
        }

       return shortestPath;
    }

    // public IList<E> findShortestPathBetween(V start, V end) {
    //     if (start == null || end == null || !adjacencyList.containsKey(start) || !adjacencyList.containsKey(end)) {
    //         throw new IllegalArgumentException();
    //     }
    //
    //     IList<E> list = new DoubleLinkedList<>();
    //
    //     if (start == end) {
    //         return list;
    //     }
    //
    //     IDictionary<V, ExampleComparableVertex<V, E>> vertices = new ChainedHashDictionary<>();
    //
    //     ExampleComparableVertex<V, E> vertexObj;
    //     ExampleComparableVertex<V, E> newVertexObj;
    //
    //     for (V vertex: vertexList){
    //         vertexObj = new ExampleComparableVertex<>(vertex, Double.POSITIVE_INFINITY, null, null);
    //         vertices.put(vertex, vertexObj);
    //     }
    //
    //     newVertexObj = new ExampleComparableVertex<>(start, 0.0, null, null);
    //     vertices.put(start, newVertexObj);
    //
    //     IPriorityQueue<ExampleComparableVertex<V, E>> queue = new ArrayHeap<>();
    //     //add source
    //     queue.add(vertices.get(start));
    //
    //     ISet<ExampleComparableVertex> checker = new ChainedHashSet<>();
    //
    //     while (!queue.isEmpty()){
    //         ExampleComparableVertex<V, E> minVert = queue.removeMin();
    //
    //         ISet<E> edges = adjacencyList.get(minVert.vertex);
    //
    //         if (!checker.contains(minVert)) {
    //             for (E edge : edges) {
    //                 ExampleComparableVertex<V, E> vert = vertices.get(edge.getOtherVertex(minVert.vertex));
    //                 double oldCost = vert.cost;
    //                 double newCost = vert.cost + edge.getWeight();
    //                 if (newCost < oldCost){
    //                     ExampleComparableVertex<V, E> newVertObject = new ExampleComparableVertex<>(vert.vertex,
    //                             newCost, minVert.vertex, edge);
    //                     queue.replace(vert, newVertObject);
    //
    //                 }
    //
    //             }
    //         }
    //
    //     }
    //
    //     //Go stuck on creating the final lsit to return...
    //
    //     ExampleComparableVertex<V, E> target = new ExampleComparableVertex<>(end, 0, null, null);
    //
    //     while (target.vertex != start) {
    //         target = new ExampleComparableVertex<>(target.preVer, target.cost, null, null);
    //         list.add(target.vertex);
    //     }
    //
    //     return list;
    // }
}
