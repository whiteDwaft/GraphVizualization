package task1;


import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.pgm.util.io.graphml.GraphMLReader;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GraphMLParser {
    private static final String XML_FILE = "graph.xml";

    public static List<Edge> edgeList = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Graph graph = new TinkerGraph();
        GraphMLReader reader = new GraphMLReader(graph);

        InputStream is = new BufferedInputStream(new FileInputStream(XML_FILE));
        reader.inputGraph(is);

        Iterable<Vertex> vertices = graph.getVertices();
        Iterator<Vertex> verticesIterator = vertices.iterator();

        while (verticesIterator.hasNext()) {

            Vertex vertex = verticesIterator.next();
            Iterable<com.tinkerpop.blueprints.pgm.Edge> edges = vertex.getInEdges();
            Iterator<com.tinkerpop.blueprints.pgm.Edge> edgesIterator = edges.iterator();


            while (edgesIterator.hasNext()) {

                com.tinkerpop.blueprints.pgm.Edge edge = edgesIterator.next();
                Vertex outVertex = edge.getOutVertex();
                Vertex inVertex = edge.getInVertex();

                edgeList.add(new Edge(new task1.Vertex((String) outVertex.getId(),(String) outVertex.getProperty("name")),
                        new task1.Vertex((String) inVertex.getId(), (String) inVertex.getProperty("name"))));

            }
        }
        HelloJGraphT.init(edgeList);
    }
}
