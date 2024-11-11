import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class EdmondsKarp {
    private int V;      //Numero de vertices
    private List<Edge>[] graph;

    @SuppressWarnings("unchecked")
    public EdmondsKarp(int V) {
        this.V = V;
        graph = new ArrayList[V];
        for(int i = 0; i < V; i++) {
            graph[i] = new ArrayList<>();
        }
    }

    // Metodo para añadir una arista al grafo
    public void addEdge(int from, int to, int capacity) {
        Edge forward = new Edge(to, graph[to].size(), capacity);
        Edge backward = new Edge(from, graph[from].size(), 0);
        graph[from].add(forward);
        graph[to].add(backward);
    }

    //Metodo principal para calular el flujo máximo
    public int maxFlow(int source, int sink) {
        int flow = 0;
        int[] parent = new int[V];
        Edge[] path = new Edge[V];

        while(bfs(source, sink, parent, path)) {
            // Encontrar la capacidad mínima en la ruta aumentante (cuello de botella)
            int pathFlow = Integer.MAX_VALUE;
            int current = sink;
            
            while(current != source) {
                Edge e = path[current];
                pathFlow = Math.min(pathFlow, e.capacity);
                current = parent[current];
            }

            // Actualizar las capacidades residuales de las aristas y las aristas inversas
            // añadiendo el cuello de botella.
            current = sink;
            while(current != source) {
                Edge e = path[current];
                e.capacity -= pathFlow;
                graph[current].get(e.rev).capacity += pathFlow;
                current = parent[current];
            }

            flow += pathFlow;

        }

        return flow;
        
    }

    private boolean bfs(int source, int sink, int[] parent, Edge[] path) {
        Arrays.fill(parent, -1);
        Queue<Integer> queue = new LinkedList<>();
        queue.add(source);
        parent[source] = source;

        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (Edge e : graph[u]) {
                if (parent[e.to] == -1 && e.capacity > 0) {  // Nodo no visitado y capacidad residual > 0
                    parent[e.to] = u;
                    path[e.to] = e;
                    queue.add(e.to);
                    if (e.to == sink) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    // Método para imprimir el grafo
    public void printGraph() {
        for(int u = 0; u < V; u++) {
            for(Edge e : graph[u]) {
                System.out.println(u + " -> " + e.to + " | Capacidad: " + e.capacity);
            }
        }
    }
    
}
