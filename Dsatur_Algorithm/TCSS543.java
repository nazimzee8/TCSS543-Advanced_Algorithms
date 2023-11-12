import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
//import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

public class TCSS543 {
    private static Vertex root = new Vertex();
	private static Map<Integer, List<Vertex>> map = new HashMap<Integer, List<Vertex>>();
	private static List<Vertex> graph = new ArrayList<Vertex>();
	//private static Set<Vertex> colorlessSet = new HashSet<Vertex>();
	//private static Map<Integer, PriorityQueue<Vertex>> saturLevels = new HashMap<Integer, PriorityQueue<Vertex>>();
	
	static class Vertex implements Comparable<Vertex> {
		Set<Vertex> edges; 	// child nodes connected to the parent
		Set<Vertex> neighbors; // nodes that are connected at the same depth
		List<Vertex> temp;
		int depth;
		Vertex prev;
		Vertex parent;
		int color;
		int adjacency_degree;
		Set<Integer> saturation_degree;
		int index;
		boolean connected;
		
		public Vertex() {
			this.edges = new HashSet<Vertex>();
			this.neighbors = new HashSet<Vertex>();
			this.temp = new ArrayList<Vertex>();
			this.depth = 0;
			this.parent = null;
			this.prev = null;
			this.color = -1;
			this.adjacency_degree = 0;
			this.saturation_degree = new HashSet<Integer>();
			this.index = -1;
			this.connected = false;
		}
		
		public void setColor(int newColor) {
			this.color = newColor;
		}
		
		@Override
		public int compareTo(Vertex v) {
			if (this.saturation_degree.size() == v.saturation_degree.size()) {
				if (this.adjacency_degree > v.adjacency_degree) return -1;
				else if (this.adjacency_degree < v.adjacency_degree) return 1;
				else return 0;
			}
			else if (this.saturation_degree.size() > v.saturation_degree.size()) {
				return -1;
			}
			return 1;
		}
		
		@Override
		public String toString() {
			return "vertex " + this.index + " saturation: " + this.saturation_degree.size() + " color: " + this.color;
			//return "" + this.edges + "\n" + this.parents + "\n" + this.color + "\n" + this.adjacency_degree + "\n" + this.saturation_degree; 
			//return "vertex " + this.index + " adjacency: " + this.adjacency_degree + " depth: " + this.depth;
		}
	}
    
    /* Create vertices and edges for the graph */
	public static Vertex constructEdges(Vertex root, int size, double density) {
		graph = new ArrayList<Vertex>();
		map = new HashMap<Integer, List<Vertex>>();
		//colorlessSet = new HashSet<Vertex>();
		List<Vertex> vertices = new ArrayList<Vertex>();
		vertices.add(root);
		graph.add(root);
		//colorlessSet.add(root);
		Vertex head = root;
		root.index = 0;
		map.put(root.depth, new ArrayList<Vertex>(Arrays.asList(root)));
		while (vertices.size() > 0) {
			root = vertices.remove(0);
			int adjacency = new Random().nextInt(9)+1;
			if (adjacency > size-graph.size()) adjacency = size-graph.size();
			for (int i = 0; i < adjacency; i++) {
				Vertex child = new Vertex();
				root.temp.add(child);
				child.prev = root;
				vertices.add(child);
				graph.add(child);
				//colorlessSet.add(child);
				child.depth = root.depth+1;
				child.index = graph.size()-1;
				if (map.get(child.depth) == null) map.put(child.depth, new ArrayList<Vertex>(Arrays.asList(child)));
				else map.get(child.depth).add(child);
				if (new Random().nextDouble() < density) {
					root.edges.add(child);
					root.adjacency_degree++;
					child.parent = root;
					child.adjacency_degree++;
				}
			}
			List<Vertex> neighbors = map.get(root.depth);
			for (Vertex v : neighbors) {
				if (root != v && new Random().nextDouble() < density && !root.neighbors.contains(v) && !v.neighbors.contains(root)) {
					root.neighbors.add(v);
					root.adjacency_degree++;
					v.neighbors.add(root);
					v.adjacency_degree++;
				}
			}
			if (root.edges.size() == 0 && root.temp.size() > 0) {
				Vertex child = root.temp.get(new Random().nextInt(root.temp.size()));
				root.edges.add(child);
				root.adjacency_degree++;
				child.parent = child.prev;
				child.adjacency_degree++;
			}
		}
		return head;
	}
    
    /* Checks if the vertex is already connected to a connected path, */
	public static boolean connected(List<Vertex> neighbors, Vertex v1) {
		for (Vertex v3 : neighbors) {
			if (v1 != v3 && v3.parent != null) {
				if (v1.neighbors.contains(v3)) return true;
			}
		}
		return false;
	}
    
    /*Checks if 2 vertices that are connected are part of a connected path. */
	public static boolean connected(List<Vertex> neighbors, Vertex v1, Vertex v2) {
		for (Vertex v3 : neighbors) {
			if (v1 != v3 && v2 != v3 && v3.parent != null) {
				if (v1.neighbors.contains(v3) || v2.neighbors.contains(v3)) return true;
			}
		}
		return false;
	}
    
    /*Retrieves a vertex that is part of a connected path. */
	public static Vertex connectedPath(List<Vertex> neighbors, Vertex u) {
		for (Vertex v: neighbors) {
			if (v.parent != null && !v.neighbors.contains(u) && !u.neighbors.contains(v)) {
				return v;
			}
		}
		return null;
	}
    
    /*Connects vertices that aren't part of a connected path in order to make a connected graph. */
	public static void connect(List<Vertex> neighbors, double density) {
		for (Vertex v1 : neighbors) {
			for (Vertex v2 : neighbors) {
				double chance = new Random().nextDouble();
				if (v1 != v2) {
					if (v1.parent == null && v2.parent != null && !v1.neighbors.contains(v2) && !v2.neighbors.contains(v1) && !connected(neighbors, v1)) {
						v1.neighbors.add(v2);
						v1.adjacency_degree++;
						v2.neighbors.add(v1);
						v2.adjacency_degree++;
					}
					else if (v1.parent == null && v2.parent == null && v1.neighbors.contains(v2) && v2.neighbors.contains(v1) && !connected(neighbors, v1, v2)) {
						Vertex v3 = connectedPath(neighbors, v2);
						if (v3 != null && !v1.neighbors.contains(v3) && !v3.neighbors.contains(v1) && !v2.neighbors.contains(v3) && !v2.neighbors.contains(v3) && chance >= density) {
							v2.neighbors.add(v3);
							v2.adjacency_degree++;
							v3.neighbors.add(v2);
							v3.adjacency_degree++;
						}
						else if (v3 != null && chance < density) {
							v2.prev.edges.add(v2);
							v2.prev.adjacency_degree++;
							v2.parent = v2.prev;
							v2.adjacency_degree++;
						}
					}
				}
				if (v1.parent == null && v1.neighbors.size() == 0) {
					Vertex v3 = connectedPath(neighbors, v1);
					if (v3 != null && chance >= density) {
						v1.neighbors.add(v3);
						v1.adjacency_degree++;
						v3.neighbors.add(v1);
						v3.adjacency_degree++;
					}
					else if (v3 != null && chance < density) {
						v1.prev.edges.add(v1);
						v1.prev.adjacency_degree++;
						v1.parent = v1.prev;
						v1.adjacency_degree++;
					}
				}
			}
		}
	}
    
    /** 
    *Iterates through all vertices in each depth and 
    *ensures that each vertex is part of a 
    *connected path in order to make a connected graph. 
    **/
	public static Vertex connectGraph(Map<Integer, List<Vertex>> map, double density) {
		for (int i = 0; i < map.keySet().size(); i++) {
			List<Vertex> neighbors = map.get(i);
			connect(neighbors, density);
		}
		return root;
	}
	
	public static Vertex Graph(Vertex root, int size, double density) {
		root = constructEdges(root, size, density);
		root = connectGraph(map, density);
		return root;
	}
	
	public static void Simulation(int group) {
		int size = 0;
		double min = 0;
		double  max = 0;
		if (group == 1) {
			min = 0.73;
			max = 0.82;
		}
		else if (group == 2) {
			min = 0.61;
			max = 0.72;
		}
		else if (group == 3) {
			min = 0.44;
			max = 0.59;
		}
		else if (group == 4) {
			min = 0.26;
			max = 0.34;
		}
		else throw new IllegalArgumentException("Enter a value between 1 and 4");
		double density = min + (new Random().nextDouble() * (max-min));
		if (group < 3) {
			size = 10;
			while (size <= 105) {
				double totalTime = 0;
				int color = 0;
				for (int i = 0; i < 100; i++) {
					if (size <= 100) root = Graph(new Vertex(), size, density);
					else root = Graph(new Vertex(), 100, density);
					long startTime = System.nanoTime();
					color += Dsatur(graph);
					long endTime = System.nanoTime();
					totalTime += (double)(endTime-startTime)/1000000;
				}
				System.out.println("Size: " + graph.size() + " Density: " + density + " Run Time: " + totalTime/100 + " Minimum Colors: " + color/100);
				if (size >= 60) size+=5;
				else size+=10;
			}
		}
		else {
			size = 35;
			while (size <= 100) {
				double totalTime = 0;
				int color = 0;
				for (int i = 0; i < 100; i++) {
					root = Graph(new Vertex(), size, density);
					long startTime = System.nanoTime();
					color += Dsatur(graph);
					long endTime = System.nanoTime();
					totalTime += (double) (endTime-startTime)/1000000;
				}
				System.out.println("Size: " + graph.size() + " Density: " + density + " Run Time: " + totalTime/100 + " Minimum Colors: " + color/100);
				int delta = 0;
				if (size >= 70) delta = 5;
				else if (size >= 35 && size < 55) delta = 10;
				else delta = 15;
				size += delta;
			}
		}
	}
	
	public static Set<Vertex> getEdges(Vertex vertex) {
		Set<Vertex> edges = vertex.edges;
		edges.addAll(vertex.neighbors);
		if (vertex.parent != null) edges.add(vertex.parent);
		return edges;
	}
	
	/*public static PriorityQueue<Vertex> colorlessEdges(Vertex vertex) {
		PriorityQueue<Vertex> colorless = new PriorityQueue<Vertex>();
		Set<Vertex> edges = getEdges(vertex);
		for (Vertex v : edges) {
			if (v.color < 0) colorless.add(v);
		}
		return colorless;
	}*/
	
	public static Set<Vertex> colorlessEdges(Set<Vertex> edges) {
		Set<Vertex> colorless = new HashSet<Vertex>();
		for (Vertex v : edges) {
			if (v.color < 0) 
				colorless.add(v);
		}
		return colorless;
	}
	
	/*public static PriorityQueue<Vertex> selectedEdges(Vertex vertex) {
		PriorityQueue<Vertex> selected = new PriorityQueue<Vertex>();
		Set<Vertex> edges = getEdges(vertex);
		for (Vertex v : edges) {
			if (colorlessSet.contains(v)) {
				selected.add(v);
			}
		}
		return selected;
	}*/
	
	public static Vertex getMaximalAdjacency(List<Vertex> graph) {
		int max = Integer.MIN_VALUE;
		Vertex node = graph.get(0);
		for (Vertex v : graph) {
			if (v.adjacency_degree > max) {
				max = v.adjacency_degree;
				node = v;
			}
		}
		return node;
	}
	
	public static Vertex getMinimalAdjacency(List<Vertex> graph) {
		int min = Integer.MAX_VALUE;
		Vertex node = graph.get(0);
		for (Vertex v : graph) {
			if (v.adjacency_degree < min) {
				min = v.adjacency_degree;
				node = v;
			}
		}
		return node;
	}
	
	public static Vertex getMaximumSaturation(List<Vertex> graph) { 
		int max = Integer.MIN_VALUE;
		Vertex node = null;
		for (Vertex v : graph) { 
			if (v.color < 0 && max < v.saturation_degree.size()) {
				max = v.saturation_degree.size();
				node = v;
			}
			else if (v.color < 0 && max == v.saturation_degree.size()) {
				if (v.adjacency_degree > node.adjacency_degree) node = v;
			}
		}
		return node;
	}
	
	/*public static Vertex getMaximumSaturation(Set<Vertex> colorless) { 
		int max = Integer.MIN_VALUE;
		Vertex node = null;
		for (Vertex v : colorless) { 
			if (max < v.saturation_degree.size()) {
				max = v.saturation_degree.size();
				node = v;
			}
			else if (max == v.saturation_degree.size()) {
				if (v.adjacency_degree > node.adjacency_degree) node = v;
			}
		}
		return node;
	}*/
	
	public static int computeColor(Vertex vertex) { 
		Set<Vertex> vertices = getEdges(vertex);
		Set<Integer> colors = new HashSet<Integer>();
		for (Vertex v : vertices) {
			colors.add(v.color);
		}
		return colors.size()-1;
	}
	
	public static void updateSaturation(Vertex vertex) {
		Set<Vertex> vertices = getEdges(vertex);
		for (Vertex v : vertices) {
			v.saturation_degree.add(vertex.color);
		}
	}
	
	public static int Dsatur(List<Vertex> graph) {
		List<Vertex> vertices = new ArrayList<Vertex>();
		Set<Integer> uniqueColors = new HashSet<Integer>();
		Vertex max;
		Vertex vertex = getMaximalAdjacency(graph);
		vertices.add(vertex);
		int color = 0;
		vertex.setColor(0);
		updateSaturation(vertex);
		while (vertices.size() > 0) {
			vertex = vertices.remove(0);
			max = getMaximumSaturation(graph);
			if (max == null) break;
			color = computeColor(max);
			uniqueColors.add(color);
			max.setColor(color);
			updateSaturation(max);
			vertices.add(max);
		}
		return uniqueColors.size();
	}
	
	/*public static int Dsatur(PriorityQueue<Vertex> graph) {
		PriorityQueue<Vertex> vertices = graph;
		PriorityQueue<Vertex> colorless;
		Vertex vertex = vertices.poll();
		Set<Integer> uniqueColors = new HashSet<Integer>();
		int color = 0;
		vertex.setColor(color);
		colorlessSet.remove(vertex);
		updateSaturation(vertex);
		colorless = colorlessEdges(vertex);
		vertices.add(colorless.peek());
		colorlessSet.remove(colorless.peek());
		saturLevels.put(vertex.saturation_degree.size(), new PriorityQueue<Vertex>(Arrays.asList(vertex)));
		saturLevels.put(colorless.peek().saturation_degree.size(), new PriorityQueue<Vertex>(colorless));
		while (vertices.size() > 0) {
			vertex = vertices.poll();
			color = computeColor(vertex);
			uniqueColors.add(color);
			vertex.setColor(color);
			colorlessSet.remove(vertex);
			updateSaturation(vertex);
			PriorityQueue<Vertex> selected = selectedEdges(vertex);
			if (selected.size() > 0) {
				vertices.add(selected.peek());
				colorlessSet.remove(selected.peek());
			}
			else {
				Vertex max = null;
				if (colorlessSet.isEmpty()) break;
				else 
					max = getMaximumSaturation(colorlessSet);
					vertices.add(max);
					colorlessSet.remove(max);
			}
			//if (vertices.size() > 100) break;
		}
		return uniqueColors.size();
	}*/
	
	public static void main(String[] args) {
		Simulation(1);
		System.out.println("\n");
		Simulation(2);
		System.out.println("\n");
		Simulation(3);
		System.out.println("\n");
		Simulation(4);
	}
}