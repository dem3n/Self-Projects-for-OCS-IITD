import java.util.*;

// Node class to represent each location in the delivery network
class Node {
    int id;
    List<Edge> neighbors;

    public Node(int id) {
        this.id = id;
        this.neighbors = new ArrayList<>();
    }

    public void addNeighbor(Node neighbor, int distance) {
        neighbors.add(new Edge(neighbor, distance));
    }
}

// Edge class to represent connections between nodes (locations)
class Edge {
    Node target;
    int distance;

    public Edge(Node target, int distance) {
        this.target = target;
        this.distance = distance;
    }
}

// DeliveryRoutingSystem class encapsulating the routing logic
public class DeliveryRoutingSystem {
    private Map<Integer, Node> nodes;

    public DeliveryRoutingSystem() {
        this.nodes = new HashMap<>();
    }

    // Method to add a node (location) to the delivery network
    public void addNode(int id) {
        if (!nodes.containsKey(id)) {
            nodes.put(id, new Node(id));
        }
    }

    // Method to add an edge (route) between two nodes (locations)
    public void addRoute(int from, int to, int distance) {
        if (!nodes.containsKey(from)) {
            addNode(from);
        }
        if (!nodes.containsKey(to)) {
            addNode(to);
        }
        Node fromNode = nodes.get(from);
        Node toNode = nodes.get(to);
        fromNode.addNeighbor(toNode, distance);
        toNode.addNeighbor(fromNode, distance); // Assuming bidirectional routes
    }

    // Method to find the shortest path from a starting location to all other locations using Dijkstra's algorithm
    public Map<Integer, Integer> shortestPaths(int start) {
        Map<Integer, Integer> shortestDistances = new HashMap<>();
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> shortestDistances.getOrDefault(n.id, Integer.MAX_VALUE)));
        Set<Integer> visited = new HashSet<>();

        // Initialize distances
        for (int id : nodes.keySet()) {
            shortestDistances.put(id, id == start ? 0 : Integer.MAX_VALUE);
        }

        pq.offer(nodes.get(start));

        while (!pq.isEmpty()) {
            Node current = pq.poll();
            if (visited.contains(current.id)) continue;
            visited.add(current.id);

            for (Edge neighbor : current.neighbors) {
                if (visited.contains(neighbor.target.id)) continue;

                int newDistance = shortestDistances.get(current.id) + neighbor.distance;
                if (newDistance < shortestDistances.get(neighbor.target.id)) {
                    shortestDistances.put(neighbor.target.id, newDistance);
                    pq.offer(neighbor.target);
                }
            }
        }

        return shortestDistances;
    }

    public static void main(String[] args) {
        DeliveryRoutingSystem routingSystem = new DeliveryRoutingSystem();

        // Adding nodes (locations) and routes (edges)
        routingSystem.addNode(0);
        routingSystem.addNode(1);
        routingSystem.addNode(2);
        routingSystem.addNode(3);
        routingSystem.addNode(4);

        routingSystem.addRoute(0, 1, 10);
        routingSystem.addRoute(0, 2, 15);
        routingSystem.addRoute(1, 3, 25);
        routingSystem.addRoute(1, 4, 20);
        routingSystem.addRoute(2, 3, 30);
        routingSystem.addRoute(3, 4, 5);

        // Example: Finding shortest paths from node 0
        int startNode = 0;
        Map<Integer, Integer> shortestDistances = routingSystem.shortestPaths(startNode);

        // Printing shortest distances
        System.out.println("Shortest paths from node " + startNode + ":");
        for (int id : shortestDistances.keySet()) {
            System.out.println("To node " + id + ": " + shortestDistances.get(id));
        }
    }
}
