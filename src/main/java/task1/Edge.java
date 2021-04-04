package task1;

public class Edge {
    Vertex person;
    Vertex knownPerson;

    public Edge(Vertex person, Vertex knownPerson) {
        this.person = person;
        this.knownPerson = knownPerson;
    }
}
