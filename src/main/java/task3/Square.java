package task3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

@AllArgsConstructor
@Data
@EqualsAndHashCode
public class Square {

    int l1X;
    int l1Y;
    int l2X;
    int l2Y;

    @Override
    public String toString() {
        return "[" + l1X + ',' + l1Y + "],[" + l2X + ',' + l2Y + ']';
    }
}
