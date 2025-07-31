package graph;

import static org.junit.Assert.*;
import org.junit.FixMethodOrder;

import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.net.URL;
import java.io.FileNotFoundException;

import java.util.LinkedList;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShortestPathsTest {

    /* Performs the necessary gradle-related incantation to get the
       filename of a graph text file in the src/test/resources directory at
       test time.*/
    private String getGraphResource(String fileName) {
        ClassLoader classLoader = getClass().getClassLoader();
        URL resource = classLoader.getResource(fileName);
        System.out.println(resource.getPath());
        return resource.getPath();
    }

    /* Returns the Graph loaded from the file with filename fn located in
     * src/test/resources at test time. */
    private Graph loadBasicGraph(String fn) {
        Graph result = null;
        String filePath = getGraphResource(fn);
        try {
          result = ShortestPaths.parseGraph("basic", filePath);
        } catch (FileNotFoundException e) {
          fail("Could not find graph " + fn);
        }
        return result;
    }

    /** Dummy test case demonstrating syntax to create a graph from scratch.
     * Write your own tests below. */
    @Test
    public void test00Nothing() {
        Graph g = new Graph();
        Node a = g.getNode("A");
        Node b = g.getNode("B");
        g.addEdge(a, b, 1);

        // sample assertion statements:
        assertTrue(true);
        assertEquals(2+2, 4);
    }

    /** Minimal test case to check the path from A to B in Simple0.txt */
    @Test
    public void test01Simple0() {
        Graph g = loadBasicGraph("Simple0.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node b = g.getNode("B");
        LinkedList<Node> abPath = sp.shortestPath(b);
        assertEquals(abPath.size(), 2);
        assertEquals(abPath.getFirst(), a);
        assertEquals(abPath.getLast(),  b);
        assertEquals(sp.shortestPathLength(b), 1.0, 1e-6);
    }

    @Test
    public void test02Simple1() {
        Graph g = loadBasicGraph("Simple1.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node a = g.getNode("A");
        sp.compute(a);
        Node c = g.getNode("C");
        Node s = g.getNode("S");
        LinkedList<Node> acPath = sp.shortestPath(c);
        LinkedList<Node> asPath = sp.shortestPath(s);
        //test path from a to c
        assertEquals(acPath.size(), 2);
        assertEquals(acPath.get(0), a);
        assertEquals(acPath.get(1), c);
        assertEquals(sp.shortestPathLength(c), 2.0, 1e-6);
        //test path from a to s
        String[] nodes = {"A", "C", "D", "S"};
        for (int i = 0; i < nodes.length; i++) {
            assertEquals(asPath.get(i).toString(), nodes[i]);
        }
        assertEquals(sp.shortestPathLength(s), 5.0, 1e-6);
    }

    @Test
    public void test03Simple2() {
        Graph g = loadBasicGraph("Simple2.txt");
        g.report();
        ShortestPaths sp = new ShortestPaths();
        Node d = g.getNode("D");
        Node c = g.getNode("C");
        Node ii = g.getNode("I");
        Node f = g.getNode("F");
        Node gg = g.getNode("G");
        //test node d
        sp.compute(d);
        LinkedList<Node> diPath = sp.shortestPath(ii);
        String[] nodes = {"D", "A", "E", "F", "I"};
        for (int i = 0; i < nodes.length; i++) {
            assertEquals(diPath.get(i).toString(), nodes[i]);
        }
        assertEquals(sp.shortestPathLength(ii), 9.0, 1e-6);
        assertEquals(sp.getPaths().size(), 10);
        //test node c
        sp.compute(c);
        LinkedList<Node> cfPath = sp.shortestPath(f);
        assertEquals(sp.shortestPath(f).size(), 0);
        assertEquals(sp.shortestPathLength(f), Double.POSITIVE_INFINITY, 1e-6);
        assertEquals(sp.getPaths().size(), 1);
        //test node f
        sp.compute(f);
        assertEquals(sp.getPaths().size(), 6);
        LinkedList<Node> ffPath = sp.shortestPath(f);
        assertEquals(ffPath.getFirst().toString(), "F");
        assertEquals(ffPath.getLast().toString(), "F");
    }

    /* Pro tip: unless you include @Test on the line above your method header,
     * gradle test will not run it! This gets me every time. */
}
