package task2;



import com.tinkerpop.blueprints.pgm.Vertex;

import java.util.Objects;

public class CustomVertex {
    Vertex vertex;
    String num;
    int lvl;
    int count;

    public CustomVertex(Vertex vertex, String num) {
        this.vertex = vertex;
        this.num = num;
    }

    @Override
    public String toString() {
        return "CustomVertex{" +
                "vertex=" + vertex +
                ", num='" + num + '\'' +
                ", lvl=" + lvl +
                ", coint=" + count +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomVertex that = (CustomVertex) o;
        return Objects.equals(vertex, that.vertex);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertex, num);
    }
}
