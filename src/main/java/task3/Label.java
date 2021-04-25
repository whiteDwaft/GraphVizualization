package task3;
import  javafx.util.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class Label {

    int id;
    CustomPair<Integer,Integer> nodePos;
    CustomPair<Integer,Integer> labelSize;
    List<CustomTriple<CustomPair<Integer,Integer>,Square,Boolean>> posVars;

    @Override
    public String toString() {
        List<CustomPair<Integer,Integer>> l = new ArrayList<>();
        for (CustomTriple<CustomPair<Integer, Integer>, Square,Boolean> posVar : posVars) {
            if (posVar.getVal3()) {
                l.add(posVar.getVal1());
            }
        }
        return "Label{" +
                ", nodePos=" + nodePos +
                ", posVars=" + l +
                '}';
    }
}
