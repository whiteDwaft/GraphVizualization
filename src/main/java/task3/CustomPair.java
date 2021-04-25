package task3;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@Data
@AllArgsConstructor
public class CustomPair<K,V> {
    private K key;
    private V value;

    @Override
    public String toString() {
        return "[" + key + ',' + value + ']';
    }
}

