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

public class Graph<V, E extends IEdge<V> & Comparable<E>> {

    private IDictionary<V, ISet<E>> adjacencyList;
    private IList<E> edgesList;
    private IList<V> vertexList;

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

    public Graph(ISet<V> vertices, ISet<E> edges) {
        // You do not need to modify this method.
        this(setToList(vertices), setToList(edges));
    }

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

    public int numEdges() {
        ISet<E> edgesSet = new ChainedHashSet<>();
        for (KVPair<V, ISet<E>> vertex: adjacencyList) {
            for (E edges: vertex.getValue()) {
                edgesSet.add(edges);
            }
        }
        return edgesSet.size();
    }

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
}
