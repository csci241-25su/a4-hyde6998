package graph;
/*
 * Author: Kevon Hyde
 * Date: 07/30/2025
 * Purpose: This program will take a text file listing paths between nodes with an edge weight e in the
 *          format "A B e", listing shortest paths to each reachable node from a given node if only one
 *          node is given or listing the full path to a second given node if two nodes are given. */

import heap.Heap;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;

/** Provides an implementation of Dijkstra's single-source shortest paths
 * algorithm.
 * Sample usage:
 *   Graph g = // create your graph
 *   ShortestPaths sp = new ShortestPaths();
 *   Node a = g.getNode("A");
 *   sp.compute(a);
 *   Node b = g.getNode("B");
 *   LinkedList<Node> abPath = sp.getShortestPath(b);
 *   double abPathLength = sp.getShortestPathLength(b);
 *   */
public class ShortestPaths {
    // stores auxiliary data associated with each node for the shortest
    // paths computation:
    private HashMap<Node,PathData> paths;

    /** Compute the shortest path to all nodes from origin using Dijkstra's
     * algorithm. Fill in the paths field, which associates each Node with its
     * PathData record, storing total distance from the source, and the
     * backpointer to the previous node on the shortest path.
     * Precondition: origin is a node in the Graph.*/
    public void compute(Node origin) {
        paths = new HashMap<Node,PathData>();
        double distance = 0.0;  //starting distance
        Heap<Node, Double> frontier = new Heap<Node, Double>(); //store nodes and edge leading to node
        PathData pathData = new PathData(0, null);  //keep track of recorded shortest paths to node
        frontier.add(origin, 0.0);
        paths.put(origin, pathData); //update this node with given pathdata
        while (frontier.size() > 0) {
            //starting from origin node, dequeue node of lowest edge and get map of neighbors
            Node f = frontier.poll();
            HashMap<Node, Double> neighbors = f.getNeighbors();
            for (Node w : neighbors.keySet()) {
                //if a node in neighbors is undiscovered, enqueue the node;
                //pathdata will contain the sum of distance traveled and previous node
                //otherwise, if the sum of distance traveled to get to this neighbor
                //is less than recorded distance, update the pathdata
                if (frontier.contains(w) == false && paths.containsKey(w) == false) {
                    distance = paths.get(f).distance + neighbors.get(w);
                    pathData = new PathData(distance, f);
                    frontier.add(w, neighbors.get(w));
                    paths.put(w, pathData);
                } else if (paths.get(f).distance + neighbors.get(w) < shortestPathLength(w)) {
                    pathData = new PathData((paths.get(f).distance + neighbors.get(w)), f);
                    paths.put(w, pathData);
                }
            }
        }
    }

    /** Return recorded paths laid out by compute.
      * Precondition: compute(origin) has been called. */
    public HashMap<Node, PathData> getPaths() {
        return paths;
    }

    /** Returns the length of the shortest path from the origin to destination.
     * If no path exists, return Double.POSITIVE_INFINITY.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. */
    public double shortestPathLength(Node destination) {
        if (paths.containsKey(destination)) {
            return paths.get(destination).distance;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    /** Returns a LinkedList of the nodes along the shortest path from origin
     * to destination. This path includes the origin and destination. If origin
     * and destination are the same node, it is included only once.
     * If no path to it exists, return null.
     * Precondition: destination is a node in the graph, and compute(origin)
     * has been called. */
    public LinkedList<Node> shortestPath(Node destination) {
        LinkedList<Node> path = new LinkedList<>();
        Node node = destination;
        //if shortestPathLength returns positive infinity, no path exists
        if (shortestPathLength(destination) != Double.POSITIVE_INFINITY) {
            while (node != null) {
                path.addFirst(node);
                node = paths.get(node).previous;
            }
        }
        return path;
    }


    /** Inner class representing data used by Dijkstra's algorithm in the
     * process of computing shortest paths from a given source node. */
    class PathData {
        double distance; // distance of the shortest path from source
        Node previous; // previous node in the path from the source

        /** constructor: initialize distance and previous node */
        public PathData(double dist, Node prev) {
            distance = dist;
            previous = prev;
        }
    }


    /** Static helper method to open and parse a file containing graph
     * information. Can parse either a basic file or a DB1B CSV file with
     * flight data. See GraphParser, BasicParser, and DB1BParser for more.*/
    protected static Graph parseGraph(String fileType, String fileName) throws
        FileNotFoundException {
        // create an appropriate parser for the given file type
        GraphParser parser;
        if (fileType.equals("basic")) {
            parser = new BasicParser();
        } else if (fileType.equals("db1b")) {
            parser = new DB1BParser();
        } else {
            throw new IllegalArgumentException(
                    "Unsupported file type: " + fileType);
        }

        // open the given file
        parser.open(new File(fileName));

        // parse the file and return the graph
        return parser.parse();
    }

    public static void main(String[] args) {
      // read command line args
      String fileType = args[0];
      String fileName = args[1];
      String origCode = args[2];

      String destCode = null;
      if (args.length == 4) {
          destCode = args[3];
      }

      // parse a graph with the given type and filename
      Graph graph;
      try {
          graph = parseGraph(fileType, fileName);
      } catch (FileNotFoundException e) {
          System.out.println("Could not open file " + fileName);
          return;
      }
      graph.report();

      Node origNode = graph.getNode(origCode);
      ShortestPaths sp = new ShortestPaths();
      sp.compute(origNode);

      HashMap<Node, PathData> paths = sp.getPaths();
      if (destCode == null) {
          //print list of reachable nodes and their shortest path lengths
          for (Node node : paths.keySet()) {
              System.out.println("Node: " + node.toString() + " " + paths.get(node).distance);
          }
      } else {
          //print nodes in the shortest path from origin to destination, followed by path length
          Node destNode = graph.getNode(destCode);
          LinkedList<Node> destPath = sp.shortestPath(destNode);
          if (destPath.size() > 0) {
              while (destPath.size() > 0) {
                  System.out.print(destPath.pop() + " ");
              }
              System.out.println("\nTotal path length is: " + sp.shortestPathLength(destNode));
          } else {
              System.out.println("No path exists.");
          }
       }
    }
}
