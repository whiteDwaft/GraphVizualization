package task3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public class CustomTriple<X,Y,Z> {

    X val1;
    Y val2;
    Z val3;

    @Override
    public String toString() {
        return "[" + val1 + ',' + val2 + ',' + val3 + ']';
    }
}
