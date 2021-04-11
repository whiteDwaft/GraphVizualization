package task2;


import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;
import com.tinkerpop.blueprints.pgm.Graph;
import com.tinkerpop.blueprints.pgm.Vertex;
import com.tinkerpop.blueprints.pgm.impls.tg.TinkerGraph;
import com.tinkerpop.blueprints.pgm.util.io.graphml.GraphMLReader;
import javafx.util.Pair;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;


public class GraphMLParser {
    private static final String XML_FILE = "graph.xml";



    public static List<Vertex> vertexList = new ArrayList<>();

    public static List<CustomVertex> customVertexList = new ArrayList<>();
    public static List<Vertex> vertexListForChosen = new ArrayList<>();

    public static Set<Pair<Vertex, List<Vertex>>> innerVertexListForChosen = new HashSet<>();

    public static List<List<Integer>> resMatrix = new ArrayList<>();
    static int count = 0;

    public static void getJudgeVertex(Vertex vertex) {
        List<Vertex> innerVertexList = new ArrayList<>();
        Iterable<com.tinkerpop.blueprints.pgm.Edge> edges = vertex.getOutEdges();
        for (com.tinkerpop.blueprints.pgm.Edge edge : edges) {
            if (getInEdges(edge.getInVertex()).size() == 1) {
                innerVertexList.add(edge.getInVertex());
                customVertexList.add(new CustomVertex(edge.getInVertex(), String.valueOf(++count)));
            } else {
                vertexListForChosen.add(vertex);
                innerVertexListForChosen.add(new Pair<>(edge.getInVertex(), getInEdges(edge.getInVertex())));
                innerVertexList.add(edge.getInVertex());
            }
        }
        System.out.println("from " + vertex + " to " + innerVertexList);
        System.out.println("______________________________");
        vertexList.addAll(innerVertexList);
    }

    public static void filter() {
        if (!innerVertexListForChosen.isEmpty())
            choose();
        System.out.println("______________________________");
    }

    public static List<Vertex> getInEdges(Vertex vertex) {
        List<Vertex> innerVertexList = new ArrayList<>();
        Iterable<com.tinkerpop.blueprints.pgm.Edge> edges = vertex.getInEdges();
        for (com.tinkerpop.blueprints.pgm.Edge edge : edges) {
            innerVertexList.add(edge.getOutVertex());
        }
        return innerVertexList;
    }

    public static List<Vertex> getOutEdges(Vertex vertex) {
        List<Vertex> innerVertexList = new ArrayList<>();
        Iterable<com.tinkerpop.blueprints.pgm.Edge> edges = vertex.getOutEdges();
        for (com.tinkerpop.blueprints.pgm.Edge edge : edges) {
            innerVertexList.add(edge.getInVertex());
        }
        return innerVertexList;
    }

    public static void choose() {
        List<Pair<List<Vertex>, List<Integer>>> l = new ArrayList<>();
        List<List<Integer>> intSuperList = new ArrayList<>();
        for (Pair<Vertex, List<Vertex>> vList : innerVertexListForChosen) {
            List<Integer> intList = fromVertexToIndex(vList.getValue());
            if (!intList.contains(null)) {
                intList.sort(Collections.reverseOrder());
                l.add(new Pair<>(vList.getValue(), intList));
                intSuperList.add(intList);
            }
        }
        System.out.println(intSuperList);
        List<Integer> resList = findBest(intSuperList);
        System.out.println(resList);


        List<Vertex> v2 = l.stream().filter(it -> it.getValue().equals(resList)).collect(Collectors
                .toList()).get(0).getKey();
        Vertex s = innerVertexListForChosen.stream().filter(it -> it.getValue().toString()
                .equals(v2.toString())).collect(Collectors.toList()).get(0).getKey();
        if (!customVertexList.contains(new CustomVertex(s, String.valueOf(count)))) {
            innerVertexListForChosen.removeIf(it -> {
                List<Integer> intList = fromVertexToIndex(it.getValue());
                if (!intList.contains(null)) {
                    intList.sort(Collections.reverseOrder());
                    return intList.equals(resList);
                }
                return false;
            });
            customVertexList.add(new CustomVertex(s, String.valueOf(++count)));
        }


    }

    private static List<Integer> fromVertexToIndex(List<Vertex> vList) {
        List<Integer> intList = new ArrayList<>();
        for (Vertex v : vList) {
            List<CustomVertex> res = customVertexList.stream().filter(vert -> vert.vertex.equals(v))
                    .collect(Collectors.toList());
            if (res.isEmpty())
                intList.add(null);
            else
                intList.add(Integer.parseInt(res.get(0).num));
        }
        return intList;
    }

    public static List<Integer> findBest(List<List<Integer>> lists) {
        for (List<Integer> l : lists) {
            l.sort(Collections.reverseOrder());
        }
        List<Integer> resList = new ArrayList<>();
        if (!lists.isEmpty()) {
            resList.addAll(lists.get(0));
            for (int i = 1; i < lists.size(); i++) {
                if (lists.get(i).toString().compareTo(resList.toString()) < 0) {
                    resList.clear();
                    resList.addAll(lists.get(i));
                }
            }
        }
        System.out.println(lists);
        return resList;
    }

    public static void getAlone(Iterator<Vertex> iter) {
        while (iter.hasNext()) {
            Vertex vertex = iter.next();
            Iterable<com.tinkerpop.blueprints.pgm.Edge> edge1 = vertex.getOutEdges();
            Iterable<com.tinkerpop.blueprints.pgm.Edge> edge2 = vertex.getInEdges();
            if (!edge1.iterator().hasNext() && !edge2.iterator().hasNext())
                customVertexList.add(new CustomVertex(vertex, String.valueOf(++count)));
        }
    }

    public static int iter(int lvlSize) {
        Map<Integer, Integer> lvlCounter = new HashMap() {{
            put(1, 0);
        }};
        customVertexList.sort((it1, it2) -> Integer.parseInt(it2.num) - Integer.parseInt(it1.num));
        for (CustomVertex v : customVertexList) {
            List<Vertex> inV = getOutEdges(v.vertex);
            List<CustomVertex> cV = customVertexList.stream().filter(it -> inV.contains(it.vertex)).collect(Collectors.toList());
            if (!cV.isEmpty()) {
                List<Integer> lvlList = cV.stream().map(it -> it.lvl).collect(Collectors.toList());
                if (cV.stream().map(it -> it.lvl).allMatch(it -> it > 0)) {
                    int minLvl = lvlList.stream().max(Comparator.comparingInt(it -> it)).get();
                    v.lvl = minLvl + 1;
                    while (lvlCounter.getOrDefault(v.lvl, 0) > lvlSize)
                        v.lvl++;
                    v.count = lvlCounter.getOrDefault(v.lvl, 0) + 1;
                    lvlCounter.put(v.lvl, lvlCounter.getOrDefault(v.lvl, 0) + 1);
                }
            } else {
                v.lvl = 1;
                v.count = lvlCounter.get(1) + 1;
                lvlCounter.put(1, lvlCounter.get(1) + 1);
            }
        }
        for (CustomVertex v : customVertexList) {
            v.count = (v.count - 1) * lvlCounter.size();
            v.lvl = lvlCounter.size() - v.lvl;
        }
        return lvlCounter.size();
    }

    public static void minimize(int[][] res) {
        for (CustomVertex v1 : customVertexList) {
            List<Vertex> vList = getOutEdges(v1.vertex);
            if(!vList.isEmpty()) {
                List<CustomVertex> cV1 = customVertexList.stream().filter(it -> vList.contains(it.vertex)).collect(Collectors.toList());
                int sum = cV1.stream().mapToInt(it -> it.count).sum();
                int newX = sum / vList.size();
                while (res[v1.lvl][newX] == 1)
                    newX++;
                res[v1.lvl][v1.count] = 0;
                v1.count = newX;
                res[v1.lvl][v1.count] = 1;
            }
        }
    }

    public static void combine(mxGraph graphMx, int[][] res) {
        customVertexList.sort((it1, it2) -> Integer.parseInt(it2.num) - Integer.parseInt(it1.num));
        for (CustomVertex v1 : customVertexList) {
            Object o1 = graphMx.insertVertex(graphMx.getDefaultParent(), (String) v1.vertex.getId(), v1.vertex.getId(), 100 * (v1.count), 60 * (v1.lvl), 50.0, 30.0, "rounded");
            List<Vertex> v = getOutEdges(v1.vertex);
            List<CustomVertex> cV1 = customVertexList.stream().filter(it -> v.contains(it.vertex)).collect(Collectors.toList());
            List<CustomVertex> cv2 = cV1.stream().filter(it -> it.lvl - v1.lvl > 1).collect(Collectors.toList());
            if (!cv2.isEmpty()) {
                for (CustomVertex v2 : cv2) {
                    int y = (v1.lvl + v2.lvl) / 2;
                    int x = (v1.count + v2.count) / 2;
                    while (res[y][x] == 1)
                        x++;
                    res[y][x] = 1;
                    Object o2 = graphMx.insertVertex(graphMx.getDefaultParent(), "", "", 100 * (x), 60 * (y), 5, 5, "rounded");
                    graphMx.insertEdge(graphMx.getDefaultParent(), null, "", o2, ((mxGraphModel) graphMx.getModel()).getCell((String) v2.vertex.getId()));
                    graphMx.insertEdge(graphMx.getDefaultParent(), null, "", ((mxGraphModel) graphMx.getModel()).getCell((String) v1.vertex.getId()), o2);
                }
            }
            List<CustomVertex> cv3 = cV1.stream().filter(it -> it.lvl - v1.lvl == 1).collect(Collectors.toList());
            for (CustomVertex v2 : cv3) {
                Object o2 = graphMx.insertVertex(graphMx.getDefaultParent(), (String) v2.vertex.getId(), v2.vertex.getId(), 100 * (v2.count), 60 * (v2.lvl), 50.0, 30.0, "rounded");
                graphMx.insertEdge(graphMx.getDefaultParent(), null, "", o1, o2);
            }

        }
    }

    public static void main(String[] args) throws Exception {
        Graph graph = new TinkerGraph();
        GraphMLReader reader = new GraphMLReader(graph);

        InputStream is = new BufferedInputStream(new FileInputStream(XML_FILE));
        reader.inputGraph(is);

        Iterable<Vertex> vertices = graph.getVertices();
        Iterator<Vertex> verticesIterator = vertices.iterator();

        Vertex firstV = verticesIterator.next();

        customVertexList.add(new CustomVertex(firstV, String.valueOf(++count)));
        getAlone(verticesIterator);
        getJudgeVertex(firstV);


        for (int i = 0; i < vertexList.size(); i++) {
            getJudgeVertex(vertexList.get(i));
        }
        System.out.println("__________________________________________");
        for (int i = 0; i < vertexListForChosen.stream().distinct().collect(Collectors.toList()).size(); i++) {
            filter();
        }

        System.out.println(vertexList);
        System.out.println(vertexListForChosen.stream().distinct().collect(Collectors.toList()));
        System.out.println(customVertexList);

        int lvlCount = iter(Integer.parseInt(args[0]));
        System.out.println(customVertexList);

        int[][] res = new int[lvlCount][3 + 2 * (lvlCount)];
        customVertexList.forEach(it -> res[it.lvl][it.count] = 1);


        mxGraph graphMx = new mxGraph();
        customVertexList.sort(Comparator.comparingInt(it -> it.lvl));
//        minimize(res);
//        customVertexList.sort((it1, it2) -> it2.lvl - it1.lvl);
//        minimize(res);
        combine(graphMx, res);

        for (int i = 0; i < lvlCount; i++) {
            for (int j = 0; j < Integer.parseInt(args[0]) + 2 * (lvlCount - 1); j++) {
                System.out.print(res[i][j] + " ");
            }
            System.out.println("");
        }

        BufferedImage image = mxCellRenderer.createBufferedImage(graphMx, null, 1, Color.WHITE, true, null);
        try {
            ImageIO.write(image, "PNG", new File("graph.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
