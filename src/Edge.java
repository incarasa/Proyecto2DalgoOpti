public class Edge {
    int to;
    int rev;
    int capacity;
    int originalCapacity; // Nueva variable para almacenar la capacidad original

    public Edge(int to, int rev, int capacity) {
        this.to = to;
        this.rev = rev;
        this.capacity = capacity;
        this.originalCapacity = capacity; // Inicializamos con la capacidad original
    }
}
