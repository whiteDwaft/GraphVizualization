package task3;

import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.view.mxGraph;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

    List<Label> labelList = new ArrayList<>();
    List<CustomPair<CustomPair<CustomPair<Integer, Integer>, Boolean>, List<Square>>> linker = new ArrayList<>();

    public void parse() throws IOException {

        Files.lines(FileSystems.getDefault().getPath("bricks.txt")).forEach(line -> {
            List<String> props = Arrays.asList(line.split("\t"));
            CustomPair<Integer, Integer> pos = new CustomPair<>(Integer.parseInt(props.get(0).split(",")[0]),
                    Integer.parseInt(props.get(0).split(",")[1]));
            CustomPair<Integer, Integer> size = new CustomPair<>(Integer.parseInt(props.get(1).split(",")[0]),
                    Integer.parseInt(props.get(1).split(",")[1]));
            List<String> ops = Arrays.asList(props.get(2).split(" "));
            List<CustomPair<Integer, Integer>> res = new ArrayList<>();
            res.add(new CustomPair<>(Integer.parseInt(ops.get(0).split(",")[0]),
                    Integer.parseInt(ops.get(0).split(",")[1])));
            res.add(new CustomPair<>(Integer.parseInt(ops.get(1).split(",")[0]),
                    Integer.parseInt(ops.get(1).split(",")[1])));
            res.add(new CustomPair<>(Integer.parseInt(ops.get(2).split(",")[0]),
                    Integer.parseInt(ops.get(2).split(",")[1])));
            res.add(new CustomPair<>(Integer.parseInt(ops.get(3).split(",")[0]),
                    Integer.parseInt(ops.get(3).split(",")[1])));

            List<CustomTriple<CustomPair<Integer, Integer>, Square, Boolean>> posVars = new ArrayList<>();
            for (CustomPair<Integer, Integer> r : res) {
                Square s = makeSquare1(pos, r, size);
                posVars.add(new CustomTriple<>(r, s, false));
            }
            Label label = new Label(1, pos, size, posVars);
            labelList.add(label);
        });
        for (Label l1 : labelList) {
            meth(l1);
        }

        for (Label l : labelList) {
            System.out.println(l);
        }
        System.out.println("__________________");
        Map<Boolean, List<Label>> map = labelList.stream().collect(Collectors.partitioningBy(
                it -> it.posVars.stream().filter(CustomTriple::getVal3).count() > 1));
        while (!map.get(true).isEmpty()) {
            List<Square> moreSq = new ArrayList<>();
            List<Square> singSq = new ArrayList<>();
            for (Label l1 : map.get(true)) {
                List<Square> s1 = l1.posVars.stream().filter(CustomTriple::getVal3).map(CustomTriple::getVal2).collect(Collectors.toList());
                moreSq.addAll(s1);
            }

            for (Label l2 : map.get(false)) {
                List<Square> s2 = l2.posVars.stream().filter(CustomTriple::getVal3).map(CustomTriple::getVal2).collect(Collectors.toList());
                singSq.addAll(s2);
            }
            for (Square s1 : moreSq) {
                for (Square s2 : singSq) {
                    if (checkCross(s1.l1X, s1.l1Y, s1.l2X, s1.l2Y, s2.l1X, s2.l1Y, s2.l2X, s2.l2Y)) {
                        for (Label label : labelList) {
                            List<CustomTriple<CustomPair<Integer, Integer>, Square, Boolean>> l = label.posVars.stream().filter(it -> s1.equals(it.val2)).collect(Collectors.toList());
                            if (l.size() > 0) {
                                l.get(0).setVal3(false);
                                break;
                            }
                        }
                    }
                }
            }
            map = labelList.stream().collect(Collectors.partitioningBy(
                    it -> it.posVars.stream().filter(CustomTriple::getVal3).count() > 1));
        }

        for (Label l : labelList) {
            System.out.println(l);
        }
    }

    public Square makeSquare1(CustomPair<Integer, Integer> nodePos, CustomPair<Integer, Integer> posVar, CustomPair<Integer, Integer> labelSize) {
        int l1X = nodePos.getKey();
        int l1Y = nodePos.getValue();
        int l2X;
        int l2Y;
        if (posVar.getKey() == 0) {
            l2X = l1X + labelSize.getKey();
        } else {
            l2X = l1X - posVar.getKey();
        }
        if (posVar.getValue() == 0) {
            l2Y = l1Y + labelSize.getValue();
        } else {
            l2Y = l1Y - posVar.getValue();
        }
        return new Square(Math.min(l1X, l2X), Math.min(l1Y, l2Y), Math.max(l2X, l1X), Math.max(l2Y, l1Y));
    }

    public void meth(Label label) {
        int l2X = label.nodePos.getKey() + label.labelSize.getKey();
        int l2Y = label.nodePos.getValue() + label.labelSize.getValue();
        if (checkInside2(label.nodePos.getKey(), label.nodePos.getValue(), l2X, l2Y)) {
            label.posVars.get(0).setVal3(true);
        }
        int l1X = label.nodePos.getKey() + label.labelSize.getKey();
        int l1Y = label.nodePos.getValue() - label.labelSize.getValue();
        if (checkInside1(label.nodePos.getKey(), label.nodePos.getValue(), l1X, l1Y)) {
            label.posVars.get(2).setVal3(true);
        }
        int l3X = label.nodePos.getKey() - label.labelSize.getKey();
        int l3Y = label.nodePos.getValue() + label.labelSize.getValue();
        if (checkInside3(label.nodePos.getKey(), label.nodePos.getValue(), l3X, l3Y)) {
            label.posVars.get(1).setVal3(true);
        }
        int l4X = label.nodePos.getKey() - label.labelSize.getKey();
        int l4Y = label.nodePos.getValue() - label.labelSize.getValue();
        if (checkInside4(label.nodePos.getKey(), label.nodePos.getValue(), l4X, l4Y)) {
            label.posVars.get(3).setVal3(true);
        }
    }

    public boolean checkInside2(int l0X, int l0Y, int l1X, int l1Y) {
        boolean isNotInside = true;
        for (Label l2 : labelList) {
            if (!(l2.nodePos.getKey() == l0X && l2.nodePos.getValue() == l0Y)) {
                if (l2.nodePos.getKey() >= l0X
                        && l2.nodePos.getKey() <= l1X
                        && l2.nodePos.getValue() >= l0Y
                        && l2.nodePos.getValue() <= l1Y) {
                    isNotInside = false;
                    break;
                }
            }
        }
        return isNotInside;
    }

    public boolean checkInside1(int l0X, int l0Y, int l1X, int l1Y) {
        boolean isNotInside = true;
        for (Label l2 : labelList) {
            if (!(l2.nodePos.getKey() == l0X && l2.nodePos.getValue() == l0Y)) {
                if (l2.nodePos.getKey() >= l0X
                        && l2.nodePos.getKey() <= l1X
                        && l2.nodePos.getValue() >= l1Y
                        && l2.nodePos.getValue() <= l0Y) {
                    isNotInside = false;
                    break;
                }
            }
        }
        return isNotInside;
    }

    public boolean checkInside3(int l0X, int l0Y, int l1X, int l1Y) {
        boolean isNotInside = true;
        for (Label l2 : labelList) {
            if (!(l2.nodePos.getKey() == l0X && l2.nodePos.getValue() == l0Y)) {
                if (l2.nodePos.getKey() >= l1X
                        && l2.nodePos.getKey() <= l0X
                        && l2.nodePos.getValue() >= l0Y
                        && l2.nodePos.getValue() <= l1Y) {
                    isNotInside = false;
                    break;
                }
            }
        }
        return isNotInside;
    }

    public boolean checkInside4(int l0X, int l0Y, int l1X, int l1Y) {
        boolean isNotInside = true;
        for (Label l2 : labelList) {
            if (!(l2.nodePos.getKey() == l0X && l2.nodePos.getValue() == l0Y)) {
                if (l2.nodePos.getKey() >= l1X
                        && l2.nodePos.getKey() <= l0X
                        && l2.nodePos.getValue() >= l1Y
                        && l2.nodePos.getValue() <= l0Y) {
                    isNotInside = false;
                    break;
                }
            }
        }
        return isNotInside;
    }

    public boolean checkCross(int l1X1, int l1Y1, int l1X2, int l1Y2, int l2X1, int l2Y1, int l2X2, int l2Y2) {
        return (
                (
                        (
                                (l1X1 >= l2X1 && l1X1 <= l2X2) || (l1X2 >= l2X1 && l1X2 <= l2X2)
                        ) && (
                                (l1Y1 >= l2Y1 && l1Y1 <= l2Y2) || (l1Y2 >= l2Y1 && l1Y2 <= l2Y2)
                        )
                ) || (
                        (
                                (l2X1 >= l1X1 && l2X1 <= l1X2) || (l2X2 >= l1X1 && l2X2 <= l1X2)
                        ) && (
                                (l2Y1 >= l1Y1 && l2Y1 <= l1Y2) || (l2Y2 >= l1Y1 && l2Y2 <= l1Y2)
                        )
                )
        ) || (
                (
                        (
                                (l1X1 >= l2X1 && l1X1 <= l2X2) || (l1X2 >= l2X1 && l1X2 <= l2X2)
                        ) && (
                                (l2Y1 >= l1Y1 && l2Y1 <= l1Y2) || (l2Y2 >= l1Y1 && l2Y2 <= l1Y2)
                        )
                ) || (
                        (
                                (l2X1 >= l1X1 && l2X1 <= l1X2) || (l2X2 >= l1X1 && l2X2 <= l1X2)
                        ) && (
                                (l1Y1 >= l2Y1 && l1Y1 <= l2Y2) || (l1Y2 >= l2Y1 && l1Y2 <= l2Y2)
                        )
                )
        );
    }

    public void draw() {
        mxGraph graphMx = new mxGraph();
        for (Label label : labelList) {
            int x = label.posVars.stream().filter(CustomTriple::getVal3).collect(Collectors.toList()).get(0).val2.l1X;
            int y = label.posVars.stream().filter(CustomTriple::getVal3).collect(Collectors.toList()).get(0).val2.l1Y;
            int width = label.labelSize.getKey();
            int height = label.labelSize.getValue();
            int x0 = label.nodePos.getKey();
            int y0 = label.nodePos.getValue();
            graphMx.insertVertex(graphMx.getDefaultParent(), "1", 1, 10 * x, 10 * y, 10 * width, 10 * height, "rounded");
            graphMx.insertVertex(graphMx.getDefaultParent(), "1", "", 10 * x0, 10 * y0, 5, 5, "rounded");

        }
        BufferedImage image = mxCellRenderer.createBufferedImage(graphMx, null, 1, Color.WHITE, true, null);
        try {
            ImageIO.write(image, "PNG", new File("graph1.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws IOException {
        Parser parser = new Parser();
        parser.parse();
        parser.draw();
    }
}
