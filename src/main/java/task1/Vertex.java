package task1;

public class Vertex {
    String id;
    String name;

    public Vertex(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public Vertex() {
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
