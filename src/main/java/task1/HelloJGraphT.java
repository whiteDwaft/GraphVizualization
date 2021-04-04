package task1;

import com.mxgraph.layout.*;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


public final class HelloJGraphT {

    static int count = 0;
    static int leftLvl = -1;
    static int rightLvl = -1;
    static int safeCount = 0;

    private static Object iter(Vertex vertex, List<Edge> edgeList, mxGraph graphMx, int lvl) {
        List<Edge> start = edgeList.stream().filter(it -> it.person.id.equals(vertex.id)).collect(Collectors.toList());
        if (!start.isEmpty()) {
            Object o1 = iter(start.get(0).knownPerson, edgeList, graphMx, lvl + 1);
            Object o2 = graphMx.insertVertex(graphMx.getDefaultParent(), vertex.id, vertex.id, 100*(++count), 60*(lvl + 1), 50.0, 30.0, "rounded");
            graphMx.insertEdge(graphMx.getDefaultParent(), null, "", o2, o1);
            System.out.println("++++++" + vertex);
            if (start.size() > 1) {
                Object o3 = iter(start.get(1).knownPerson, edgeList, graphMx, lvl + 1);
                graphMx.insertEdge(graphMx.getDefaultParent(), null, "", o2, o3);
            }
            return o2;
        } else {
//            if (leftLvl == -1){
//                leftLvl = lvl;
//                safeCount = count + 1;
                return graphMx.insertVertex(graphMx.getDefaultParent(), vertex.id, vertex.id, 100*(++count), 60*(lvl + 1), 50.0, 30.0, "rounded");
//            }
//            else{
//                rightLvl = lvl;
//                count = safeCount + 1 - (rightLvl - leftLvl);
//                leftLvl = rightLvl;
//                safeCount = count;
//                return graphMx.insertVertex(graphMx.getDefaultParent(), vertex.id, vertex.id, 100*(count), 60*(lvl + 1), 50.0, 30.0, "rounded");
//            }
        }
    }

    public static void init(List<Edge> edgeList) {

        Vertex parentVertex = new Vertex();

        mxGraph graphMx = new mxGraph();

        for (Edge edge : edgeList) {
            if (edgeList.stream().allMatch(it -> edge.person.id != it.knownPerson.id)) {
                parentVertex = edge.person;
                break;
            }
        }
        iter(parentVertex, edgeList, graphMx,1);


        BufferedImage image = mxCellRenderer.createBufferedImage(graphMx, null, 1, Color.WHITE, true, null);
        try {
            ImageIO.write(image, "PNG", new File("graph.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
