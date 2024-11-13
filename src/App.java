import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class App {
    public static void main(String[] args) throws Exception {
        try (
            //BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\cfvm0\\OneDrive\\Documents\\Proyecto Dalgo 2 OPT\\Proyecto2DalgoOpti\\bin\\P2.in"));
            FileWriter writer = new FileWriter("C:\\Users\\cfvm0\\OneDrive\\Documents\\Proyecto Dalgo 2 OPT\\Proyecto2DalgoOpti\\bin\\P2.out")
        ) {
            String line = reader.readLine(); // Número de casos
            if (line == null) {
                throw new IOException("El archivo de entrada está vacío.");
            }
            int casos = Integer.parseInt(line.trim());
            for (int caso = 1; caso <= casos; caso++) {
 //------------System.out.println("Caso -> " + caso);

                line = reader.readLine();
                if (line == null) {
                    throw new IOException("Se esperaba una línea con 'numCells' y 'maxDistance' para el caso " + caso + ".");
                }
                String[] caseInfo = line.trim().split(" ");
                if (caseInfo.length < 2) {
                    throw new IOException("Formato incorrecto en la línea de 'numCells' y 'maxDistance' para el caso " + caso + ".");
                }
                int numCells = Integer.parseInt(caseInfo[0]);
                int maxDistance = Integer.parseInt(caseInfo[1]);

                // Estructuras de datos:
                List<Cell> celulas = new ArrayList<>(numCells);  // Lista de células leídas.
                GridPartition grid = new GridPartition(maxDistance); // Tamaño de cuadrícula igual a d

                // Lista de pares que cumplen con la distancia y compatibilidad
                List<Tupla> parejasCompatibles = new ArrayList<>();

                for (int cellLine = 0; cellLine < numCells; cellLine++) {
                    line = reader.readLine();
                    if (line == null) {
                        throw new IOException("Se esperaba información de la célula " + (cellLine + 1) + " del caso " + caso + ", pero se alcanzó el final del archivo.");
                    }
                    String[] cellInfo = line.trim().split(" ");
                    if (cellInfo.length < 5) {
                        throw new IOException("Formato incorrecto en la información de la célula " + (cellLine + 1) + " del caso " + caso + ".");
                    }

                    int cellId = Integer.parseInt(cellInfo[0]);
                    int cellXPos = Integer.parseInt(cellInfo[1]);
                    int cellYPos = Integer.parseInt(cellInfo[2]);
                    int cellType = Integer.parseInt(cellInfo[3]);

                    // Añadir los péptidos a una lista
                    List<String> peptidos = new ArrayList<>();
                    for (int i = 4; i < cellInfo.length; i++) {
                        peptidos.add(cellInfo[i]);
                    }

                    Cell cell = new Cell(cellId, cellXPos, cellYPos, cellType, peptidos);

                    // Añadir la célula a la cuadrícula
                    grid.addCell(cell);
                    celulas.add(cell);
                }

                // Buscar parejas compatibles
                for (Cell cell : celulas) {
                    List<Cell> nearbyCells = grid.getNearbyCells(cell);
                    for (Cell other : nearbyCells) {
                        if (cell.getCellId() >= other.getCellId()) continue; // Evitar duplicados

                        if (cell.getCellType() == 1 && other.getCellType() == 3) continue; // Salta si son tipo 1 y tipo 3 por que una iniciadora no puede mandar a calculadora.
                        if (cell.getCellType() == 3 && other.getCellType() == 1) continue; // Salta si son tipo 3 y tipo 1 por que una iniciadora no puede mandar a calculadora.

                        double distanciaD = Math.hypot(other.getCellXPos() - cell.getCellXPos(), other.getCellYPos() - cell.getCellYPos());
                        
                        
                        if (distanciaD <= maxDistance && distanciaD != 0) {
                            
                            // Verificar compatibilidad
                            int sumaTipos = cell.getCellType() + other.getCellType();
                            if (sumaTipos == 3 || sumaTipos == 4 || sumaTipos == 5) {
                                Tupla pareja = new Tupla(cell.getCellId(), other.getCellId()); // 1-based
                                // Asignar tipo de pareja
                                if (sumaTipos == 3) pareja.type = 1;
                                else if (sumaTipos == 4) pareja.type = 2;
                                else if (sumaTipos == 5) pareja.type = 3;
                                parejasCompatibles.add(pareja);
                            }
                        }
                    }
                }

//---------------System.out.println("\nParejas que cumplen con la compatibilidad: " + parejasCompatibles.size());

                // Mapear cada célula a su nodo correspondiente en el grafo
                Map<Integer, Integer> cellToNodeIn = new HashMap<>();
                Map<Integer, Integer> cellToNodeOut = new HashMap<>();
                int nodeIdCounter = numCells + 2; // IDs para los nodos nuevos

                // Identificar todas las células calculadoras
                Set<Integer> calculadorasSet = new HashSet<>();
                for (Cell celula : celulas) {
                    if (celula.getCellType() == 2) {
                        calculadorasSet.add(celula.getCellId());
                        // Crear nodos de entrada y salida para cada célula calculadora
                        cellToNodeIn.put(celula.getCellId(), nodeIdCounter++);
                        cellToNodeOut.put(celula.getCellId(), nodeIdCounter++);
                    }
                }

                // Actualizar el grafo con el tamaño adecuado
                Dinic dinic = new Dinic(nodeIdCounter);

                // Conectar las células iniciadoras a la fuente
                for (Cell celula : celulas) {
                    if (celula.getCellType() == 1) {
                        dinic.addEdge(0, celula.getCellId(), Integer.MAX_VALUE); // Fuente -> iniciadora
                    }
                }

                // Conectar las células ejecutoras al sumidero
                for (Cell celula : celulas) {
                    if (celula.getCellType() == 3) {
                        dinic.addEdge(celula.getCellId(), numCells + 1, Integer.MAX_VALUE); // ejecutora -> sumidero
                    }
                }

                // Conectar los nodos in y out de las células calculadoras
                for (Integer cellId : calculadorasSet) {
                    int inNode = cellToNodeIn.get(cellId);
                    int outNode = cellToNodeOut.get(cellId);
                    dinic.addCalculadoraEdge(inNode, outNode, Integer.MAX_VALUE, cellId); // capacidad infinita
                }

                // Añadir las aristas entre las células según las parejas compatibles
                for (Tupla pareja : parejasCompatibles) {
                    Cell cell1 = celulas.get(pareja.first - 1); // 1-based to 0-based
                    Cell cell2 = celulas.get(pareja.second - 1);

                    int capacidadMensajes = calcularPeptidosEnComun(cell1, cell2);

                    if (capacidadMensajes > 0) {
                        if (pareja.type == 1) { // Iniciadora - Calculadora
                            if (cell1.getCellType() == 1 && cell2.getCellType() == 2) {
                                // Iniciadora -> Calculadora (in)
                                int calculadoraInNode = cellToNodeIn.get(cell2.getCellId());
                                dinic.addEdge(cell1.getCellId(), calculadoraInNode, capacidadMensajes);
                            } else if (cell2.getCellType() == 1 && cell1.getCellType() == 2) {
                                int calculadoraInNode = cellToNodeIn.get(cell1.getCellId());
                                dinic.addEdge(cell2.getCellId(), calculadoraInNode, capacidadMensajes);
                            }
                        } else if (pareja.type == 2) { // Calculadora - Calculadora
                            if (cellToNodeOut.get(cell1.getCellId()) == null) {
                                System.out.println("Error: cellToNodeOut does not contain cell1 ID.");
                                System.out.println("cell1 ID: " + cell1.getCellId());
                                //System.out.println("Current contents of cellToNodeOut: " + cellToNodeOut.keySet());
                                //System.out.println("Full cellToNodeOut map: " + cellToNodeOut);
                                throw new NullPointerException("cellToNodeOut.get(cell1.getCellId()) is null");
                            }
                            int calculadora1OutNode = cellToNodeOut.get(cell1.getCellId());
                            if (cellToNodeIn.get(cell2.getCellId()) == null) {
                                System.out.println("Error: cellToNodeIn does not contain cell2 ID.");
                                System.out.println("cell2 ID: " + cell2.getCellId());
                                System.out.println("Current contents of cellToNodeIn: " + cellToNodeIn.keySet());
                                System.out.println("Full cellToNodeIn map: " + cellToNodeIn);
                                throw new NullPointerException("cellToNodeIn.get(cell2.getCellId()) is null");
                            }
                            int calculadora2InNode = cellToNodeIn.get(cell2.getCellId());
                            dinic.addEdge(calculadora1OutNode, calculadora2InNode, capacidadMensajes);

                            if (cellToNodeOut.get(cell2.getCellId()) == null) {
                                System.out.println("Error: cellToNodeOut does not contain cell2 ID.");
                                System.out.println("cell2 ID: " + cell2.getCellId());
                                System.out.println("Current contents of cellToNodeOut: " + cellToNodeOut.keySet());
                                System.out.println("Full cellToNodeOut map: " + cellToNodeOut);
                                throw new NullPointerException("cellToNodeOut.get(cell2.getCellId()) is null");
                            }

                            calculadora1OutNode = cellToNodeOut.get(cell2.getCellId());
                            if (cellToNodeIn.get(cell1.getCellId()) == null) {
                                System.out.println("Error: cellToNodeIn does not contain cell1 ID.");
                                System.out.println("cell1 ID: " + cell1.getCellId());
                                System.out.println("Current contents of cellToNodeIn: " + cellToNodeIn.keySet());
                                System.out.println("Full cellToNodeIn map: " + cellToNodeIn);
                                throw new NullPointerException("cellToNodeIn.get(cell1.getCellId()) is null");
                            }
                            calculadora2InNode = cellToNodeIn.get(cell1.getCellId());
                            dinic.addEdge(calculadora1OutNode, calculadora2InNode, capacidadMensajes);
                        } else if (pareja.type == 3) { // Calculadora - Ejecutora
                            if (cell1.getCellType() == 2 && cell2.getCellType() == 3) {
                                int calculadoraOutNode = cellToNodeOut.get(cell1.getCellId());
                                dinic.addEdge(calculadoraOutNode, cell2.getCellId(), capacidadMensajes);
                            } else if (cell2.getCellType() == 2 && cell1.getCellType() == 3) {
                                int calculadoraOutNode = cellToNodeOut.get(cell2.getCellId());
                                dinic.addEdge(calculadoraOutNode, cell1.getCellId(), capacidadMensajes);
                            }
                        }
                    }
                }

                // Calcular el flujo máximo
//--------------System.out.println("\nConstruyendo el grafo y calculando el flujo máximo...");
                long startTime = System.currentTimeMillis();
                int flujoMaximo = dinic.maxFlow(0, numCells + 1);
                long endTime = System.currentTimeMillis();
//--------------System.out.println("El flujo máximo es: " + flujoMaximo);
                System.out.println("Tiempo de cálculo del flujo máximo: " + (endTime - startTime) + " ms");

                // Obtener el flujo que pasó por cada célula calculadora
                Map<Integer, Integer> flujoPorCalculadora = new HashMap<>();
                for (Integer cellId : calculadorasSet) {
                    Edge edge = dinic.calculadoraEdges.get(cellId);
                    int flujo = edge.originalCapacity - edge.capacity;
                    flujoPorCalculadora.put(cellId, flujo);
                }

                // Seleccionar la célula calculadora que, al eliminarse, reduce más el flujo
                int idCalculadoraMinFlow = -1;
                int maxFlowReduction = -1;

                for (Map.Entry<Integer, Integer> entry : flujoPorCalculadora.entrySet()) {
                    int cellId = entry.getKey();
                    int flowThroughCell = entry.getValue();
                    if (flowThroughCell > maxFlowReduction ||
                        (flowThroughCell == maxFlowReduction && cellId > idCalculadoraMinFlow)) {
                        maxFlowReduction = flowThroughCell;
                        idCalculadoraMinFlow = cellId;
                    }
                }

                if (idCalculadoraMinFlow == -1) {
                    System.out.println("No se encontró ninguna célula calculadora para eliminar.");
                } else {
                    int flujoTrasEliminacion = flujoMaximo - maxFlowReduction;
//------------------System.out.println("El flujo quitando " + idCalculadoraMinFlow + " es " + flujoTrasEliminacion);
                    System.out.println(idCalculadoraMinFlow+" "+flujoMaximo+" "+flujoTrasEliminacion);
                }

            }
        }
        
    }

    private static int calcularPeptidosEnComun(Cell cell1, Cell cell2) {
        Set<String> peptidosCell1 = new HashSet<>(cell1.getPeptidos());
        Set<String> peptidosCell2 = new HashSet<>(cell2.getPeptidos());
        peptidosCell1.retainAll(peptidosCell2); // Esta es la intersección de los conjuntos
        return peptidosCell1.size(); //Tamaño de la intersección => Peptidos en común

    }
    
}