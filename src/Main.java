import java.util.*;

class Edge {
    Node from, to;
    int weight;

    public Edge(Node from, Node to, int weight) {

        this.from = from;
        this.to = to;
        this.weight = weight;
    }
    @Override
    public String toString() {
        return "Edge{" + "from=" + from.name + ", to=" + to.name + ", weight=" + weight + '}';
    }
}

class Node {
    String name;
    boolean visited;
    int ttl;

    public Node(String name) {
        this.name = name;
        this.visited = false;
        this.ttl = 0;
    }

    public void updateTTL(int baseTTL, int numNeighbors, int distanceFromStart) {
        this.ttl = Math.max(1, baseTTL + (numNeighbors * 2) - (distanceFromStart / 2));
    }
}


class Graph {
    Map<String, Node> nodes;
    Map<Node, List<Edge>> adjacencyList;

    public Graph() {
        nodes = new HashMap<>();
        adjacencyList = new HashMap<>();
    }

    public void removeEdge(String from, String to) {
        Node nodeFrom = nodes.get(from);
        Node nodeTo = nodes.get(to);

        if (nodeFrom != null && nodeTo != null) {
            adjacencyList.get(nodeFrom).removeIf(edge -> edge.to.equals(nodeTo));
            adjacencyList.get(nodeTo).removeIf(edge -> edge.from.equals(nodeFrom));
        }
    }
    public void removeNode(String name) {
        Node node = nodes.get(name);
        if (node != null) {
            adjacencyList.remove(node);
            nodes.remove(name);
            for (List<Edge> edges : adjacencyList.values()) {
                edges.removeIf(edge -> edge.to.equals(node) || edge.from.equals(node));
            }
        }//End If
    }

    public void addNode(String name) {
        Node node = new Node(name);
        nodes.put(name, node);
        adjacencyList.put(node, new ArrayList<>());
    }

    public void addEdge(String from, String to) {
        Node nodeFrom = nodes.get(from);
        Node nodeTo = nodes.get(to);

        if (nodeFrom != null && nodeTo != null) {
            int weight = calculateEdgeWeight(nodeFrom, nodeTo);
            adjacencyList.get(nodeFrom).add(new Edge(nodeFrom, nodeTo, weight));
            adjacencyList.get(nodeTo).add(new Edge(nodeTo, nodeFrom, weight));
        }
    }

    private int calculateEdgeWeight(Node from, Node to) {
        int numNeighborsFrom = adjacencyList.get(from).size();
        int numNeighborsTo = adjacencyList.get(to).size();
        int distanceFactor = Math.abs(numNeighborsFrom - numNeighborsTo) * 2;

        return Math.max(1, 1 + numNeighborsFrom + numNeighborsTo + distanceFactor);
    }

    public void assignTTL2All(String startNode, int baseTTL) {
        if (!nodes.containsKey(startNode)) return;

        Queue<Node> queue = new LinkedList<>();
        Map<Node, Integer> distanceFromStart = new HashMap<>();

        Node start = nodes.get(startNode);
        start.ttl = baseTTL;
        queue.add(start);
        distanceFromStart.put(start, 0);

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            List<Edge> neighbors = adjacencyList.getOrDefault(current, new ArrayList<>());

            for (Edge edge : neighbors) {
                Node neighbor = edge.to;
               // System.out.println("TTL is " + current.ttl);
                if (!distanceFromStart.containsKey(neighbor)) {
                    int newDistance = distanceFromStart.get(current) + edge.weight;
                    distanceFromStart.put(neighbor, newDistance);
                    queue.add(neighbor);

                    int numNeighbors = adjacencyList.get(neighbor).size();
                    neighbor.updateTTL(baseTTL, numNeighbors, newDistance);
                }//End If
            }//End Loop
        }//End Loop
    }

    public void printNodeTTLs() {
        for (Node node : nodes.values()) {
            System.out.println("Node: " + node.name + ", TTL: " + node.ttl);
        }
    }

    public void printEdges() {
        for (Node node : adjacencyList.keySet()) {
            for (Edge edge : adjacencyList.get(node)) {
                System.out.println("Edge: " + edge.from.name + " -> " + edge.to.name + ", Weight: " + edge.weight);
            }//End Loop
        } //End Loop
    }//End Function
} //End Class





public class Main {
    public static void main(String[] args) {
        Graph graph = new Graph();

        int hopCount = 17; //Default is 1 for A->B;  2 for A->C, etc.
        for (char c = 'A'; c <= 'Z'; c++) {
            graph.addNode(String.valueOf(c));
        }
        for (char c = 'A';  c <= 'Z'; c++) {

            char b = (char) (c + hopCount); // Adjust hopCount above as needed, e.g., 1 for A->B, 2 for A->C, etc.

            if( b > 'Z') {
                b = (char) ('A' + (b - 'Z' - 1)); // Wrap around if beyond Z
            }
            graph.addEdge((String.valueOf(c)), String.valueOf(b));
            System.out.println("Added edge from " + c + " to " + b);
        }
        //graph.addNode("A");
       // graph.addNode("B");
       // graph.addNode("C");
       // graph.addNode("D");

        graph.addEdge("A", "B");
        graph.addEdge("B", "C");
        graph.addEdge("C", "D");
        graph.addEdge("A", "D");

        graph.assignTTL2All("A", 100);
        graph.printNodeTTLs();
        graph.printEdges();


    }//End Main
}
/*
*   public Edge(int weight, Node  from, Node to) {
        Optional ln = Optional.ofNullable(weight);
        if(ln==null) this.weight=1;

        this.from = from;
        this.to = to;
    }
 */