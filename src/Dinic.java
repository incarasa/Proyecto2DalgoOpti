// Clase Dinic para flujo máximo

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Dinic {
    private int V;
    public List<Edge>[] graph;
    private int[] level;
    private int[] ptr;
    private Queue<Integer> queue;

    // Para rastrear el flujo a través de cada célula calculadora
    private int[] flowThroughCells;

    Map<Integer, Edge> calculadoraEdges = new HashMap<>();

    @SuppressWarnings("unchecked")
    public Dinic(int V) {
        this.V = V;
        graph = new ArrayList[V];
        for (int i = 0; i < V; i++) {
            graph[i] = new ArrayList<>();
        }
        level = new int[V];
        ptr = new int[V];
        queue = new LinkedList<>();
        flowThroughCells = new int[V];
    }

    public void addEdge(int from, int to, int capacity) {
        Edge forward = new Edge(to, graph[to].size(), capacity);
        Edge backward = new Edge(from, graph[from].size(), 0);
        graph[from].add(forward);
        graph[to].add(backward);
    }

    // Añadimos un método específico para las aristas inNode -> outNode de las calculadoras
    public void addCalculadoraEdge(int from, int to, int capacity, int cellId) {
        Edge forward = new Edge(to, graph[to].size(), capacity);
        Edge backward = new Edge(from, graph[from].size(), 0);
        graph[from].add(forward);
        graph[to].add(backward);
        calculadoraEdges.put(cellId, forward);
    }

    public int maxFlow(int source, int sink) {
        int flow = 0;
        while (bfs(source, sink)) {
            Arrays.fill(ptr, 0);
            while (true) {
                int pushed = dfs(source, sink, Integer.MAX_VALUE);
                if (pushed == 0)
                    break;
                flow += pushed;
            }
        }
        return flow;
    }

    private boolean bfs(int source, int sink) {
        Arrays.fill(level, -1);
        level[source] = 0;
        queue.clear();
        queue.add(source);
        while (!queue.isEmpty()) {
            int u = queue.poll();
            for (Edge e : graph[u]) {
                if (e.capacity > 0 && level[e.to] == -1) {
                    level[e.to] = level[u] + 1;
                    queue.add(e.to);
                    if (e.to == sink)
                        return true;
                }
            }
        }
        return level[sink] != -1;
    }

    private int dfs(int u, int sink, int pushed) {
        if (u == sink)
            return pushed;
        for (; ptr[u] < graph[u].size(); ptr[u]++) {
            Edge e = graph[u].get(ptr[u]);
            if (e.capacity > 0 && level[e.to] == level[u] + 1) {
                int tr = dfs(e.to, sink, Math.min(pushed, e.capacity));
                if (tr > 0) {
                    e.capacity -= tr;
                    graph[e.to].get(e.rev).capacity += tr;

                    // Aquí asumimos que las células calculadoras están entre 1 y V-2
                    // Ajusta según tu mapeo de IDs
                    flowThroughCells[u] += tr;
                    return tr;
                }
            }
        }
        return 0;
    }

    // Método para obtener el flujo a través de una célula específica
    public int getFlowThroughCell(int cellId) {
        return flowThroughCells[cellId];
    }

    // Opcional: imprimir el grafo residual
    public void printGraph() {
        for (int u = 0; u < V; u++) {
            for (Edge e : graph[u]) {
                System.out.println(u + " -> " + e.to + " | Capacidad: " + e.capacity);
            }
        }
    }
}