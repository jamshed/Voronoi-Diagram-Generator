import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class DelaunayTriangulation {
	final static double EPS = 0.05;
	final static int INF = 1000;
	int n;
	Point[] terminalPoints = new Point[3];
	Triangle DAGroot;
	ArrayList<Point> P;
	HashMap<Edge, TrianglePair> adjacentTriangles;

	public DelaunayTriangulation(int n, Point[] P) {
		this.n = n;

		this.P = new ArrayList<Point>(n);
		for (Point p : P)
			this.P.add(p);

		adjacentTriangles = new HashMap<Edge, TrianglePair>();

		shake();
		delaunay_triangulate();
	}

	void shake() {
		Random random1 = new Random();
		Random random2 = new Random();

		for (Point p : P) {
			int r1 = Math.abs(random1.nextInt()) % 3;
			if (r1 == 2)
				r1 = -1;

			int r2 = Math.abs(random2.nextInt()) % 3;
			if (r2 == 2)
				r2 = -1;

			p.x += r1 * EPS;
			p.y += r2 * EPS;
		}
	}

	double bottom_most_y_coordinate() {
		double minY = Double.MAX_VALUE;

		for (Point p : P)
			if (minY > p.y)
				minY = p.y;

		return minY;
	}

	void initialize_DAG() {
		double bottomMostY = bottom_most_y_coordinate();

		terminalPoints[0] = new Point(0, INF);
		terminalPoints[1] = new Point(-INF, bottomMostY - 1);
		terminalPoints[2] = new Point(INF, bottomMostY - 2);

		DAGroot = new Triangle(terminalPoints[0], terminalPoints[1],
				terminalPoints[2]);

		adjacentTriangles.put(new Edge(terminalPoints[0], terminalPoints[1]),
				new TrianglePair(DAGroot, null));
		adjacentTriangles.put(new Edge(terminalPoints[1], terminalPoints[2]),
				new TrianglePair(DAGroot, null));
		adjacentTriangles.put(new Edge(terminalPoints[2], terminalPoints[0]),
				new TrianglePair(DAGroot, null));
	}

	Triangle containing_tringle(Point point) {
		Triangle triangle = DAGroot;

		while (triangle.children != null) {
			for (Triangle t : triangle.children)
				if (t.contains(point)) {
					triangle = t;
					break;
				}
		}

		return triangle;
	}

	void replace_adjacent_triangle(Edge e, Triangle t_old, Triangle t_new) {
		TrianglePair trianglePair = adjacentTriangles.get(e);

		if (t_old.equals(trianglePair.t1))
			trianglePair.t1 = t_new;
		else if (t_old.equals(trianglePair.t2))
			trianglePair.t2 = t_new;
	}

	boolean is_illegal_edge(Edge e) {
		TrianglePair trianglePair = adjacentTriangles.get(e);
		Triangle t1 = trianglePair.t1, t2 = trianglePair.t2;

		if (t1 == null || t2 == null)
			return false;

		Point pi = e.a, pj = e.b, pk = t1.opposite_point(e), pl = t2
				.opposite_point(e);

		Circle circle = new Circle(pi, pj, pk);

		return circle.is_interior(pl);
	}

	void flip_edge(Edge e) {
		TrianglePair trianglePair = adjacentTriangles.get(e);
		Triangle t_old1 = trianglePair.t1, t_old2 = trianglePair.t2;

		Point pi = e.a, pj = e.b, pk = t_old1.opposite_point(e), pr = t_old2
				.opposite_point(e);

		Triangle t_new1 = new Triangle(pr, pi, pk), t_new2 = new Triangle(pr,
				pj, pk);

		t_old1.children = new ArrayList<Triangle>();
		t_old2.children = new ArrayList<Triangle>();

		t_old1.children.add(t_new1);
		t_old1.children.add(t_new2);
		t_old2.children.add(t_new1);
		t_old2.children.add(t_new2);

		adjacentTriangles.remove(e);

		replace_adjacent_triangle(new Edge(pi, pk), t_old1, t_new1);
		replace_adjacent_triangle(new Edge(pk, pj), t_old1, t_new2);
		replace_adjacent_triangle(new Edge(pj, pr), t_old2, t_new2);
		replace_adjacent_triangle(new Edge(pr, pi), t_old2, t_new1);

		adjacentTriangles.put(new Edge(pr, pk),
				new TrianglePair(t_new1, t_new2));
	}

	void legalize_edge(Point p, Edge e) {
		if (is_illegal_edge(e)) {
			TrianglePair trianglePair = adjacentTriangles.get(e);
			Triangle t1 = trianglePair.t1, t2 = trianglePair.t2;

			Point pi = e.a, pj = e.b;
			Point pk = (t1.opposite_point(e).equals(p) ? t2.opposite_point(e)
					: t1.opposite_point(e));

			flip_edge(e);

			legalize_edge(p, new Edge(pi, pk));
			legalize_edge(p, new Edge(pj, pk));
		}
	}

	void insert_interior_point(Triangle triangle, Point p) {
		Point pi = triangle.a, pj = triangle.b, pk = triangle.c;

		Triangle t1 = new Triangle(pi, p, pj), t2 = new Triangle(pj, p, pk), t3 = new Triangle(
				pk, p, pi);

		triangle.children = new ArrayList<Triangle>();

		triangle.children.add(t1);
		triangle.children.add(t2);
		triangle.children.add(t3);

		replace_adjacent_triangle(new Edge(pi, pj), triangle, t1);
		replace_adjacent_triangle(new Edge(pj, pk), triangle, t2);
		replace_adjacent_triangle(new Edge(pi, pk), triangle, t3);

		adjacentTriangles.put(new Edge(pi, p), new TrianglePair(t1, t3));
		adjacentTriangles.put(new Edge(pj, p), new TrianglePair(t1, t2));
		adjacentTriangles.put(new Edge(pk, p), new TrianglePair(t2, t3));
	}

	void insert_on_edge_point(Triangle triangle, Edge e, Point p) {
		Point pi = e.a, pj = e.b, pk = triangle.opposite_point(e), pl;

		TrianglePair trianglePair = adjacentTriangles.get(e);
		Triangle t_old1 = triangle, t_old2 = (trianglePair.t1.equals(triangle) ? trianglePair.t2
				: trianglePair.t1), t1, t2, t3, t4;

		pl = t_old2.opposite_point(e);

		t1 = new Triangle(pi, pk, p);
		t2 = new Triangle(pi, pl, p);
		t3 = new Triangle(pl, pj, p);
		t4 = new Triangle(pj, pk, p);

		t_old1.children = new ArrayList<Triangle>();
		t_old2.children = new ArrayList<Triangle>();

		t_old1.children.add(t1);
		t_old1.children.add(t4);
		t_old2.children.add(t2);
		t_old2.children.add(t3);

		adjacentTriangles.remove(e);

		replace_adjacent_triangle(new Edge(pk, pi), t_old1, t1);
		replace_adjacent_triangle(new Edge(pi, pl), t_old2, t2);
		replace_adjacent_triangle(new Edge(pl, pj), t_old2, t3);
		replace_adjacent_triangle(new Edge(pj, pk), t_old1, t4);

		adjacentTriangles.put(new Edge(pk, p), new TrianglePair(t1, t4));
		adjacentTriangles.put(new Edge(pi, p), new TrianglePair(t1, t2));
		adjacentTriangles.put(new Edge(pl, p), new TrianglePair(t2, t3));
		adjacentTriangles.put(new Edge(pj, p), new TrianglePair(t3, t4));

	}

	void insert_point(Triangle triangle, Point p) {
		if (triangle.is_interior(p)) {
			insert_interior_point(triangle, p);

			Point pi = triangle.a, pj = triangle.b, pk = triangle.c;

			legalize_edge(p, new Edge(pi, pj));
			legalize_edge(p, new Edge(pj, pk));
			legalize_edge(p, new Edge(pk, pi));
		} else {
			Edge e = triangle.coincident_edge(p);
			Point pi = e.a, pj = e.b, pk = triangle.opposite_point(e), pl;

			TrianglePair trianglePair = adjacentTriangles.get(e);
			Triangle t1 = trianglePair.t1, t2 = trianglePair.t2;

			pl = (t1.opposite_point(e).equals(pk) ? t2.opposite_point(e) : t1
					.opposite_point(e));

			insert_on_edge_point(triangle, e, p);

			legalize_edge(p, new Edge(pi, pl));
			legalize_edge(p, new Edge(pl, pj));
			legalize_edge(p, new Edge(pj, pk));
			legalize_edge(p, new Edge(pk, pi));
		}
	}

	void delaunay_triangulate() {
		initialize_DAG();

		Collections.shuffle(P);

		for (Point p : P) {
			Triangle triangle = containing_tringle(p);
			insert_point(triangle, p);
		}
	}

	void DFS(HashSet<Triangle> hashSet, Triangle triangle) {
		if (triangle.children == null)
			hashSet.add(triangle);
		else
			for (Triangle t : triangle.children)
				DFS(hashSet, t);
	}

	ArrayList<Triangle> get_triangles() {
		ArrayList<Triangle> T = new ArrayList<Triangle>();
		HashSet<Triangle> hashSet = new HashSet<Triangle>();

		DFS(hashSet, DAGroot);

		for (Triangle t : hashSet)
			if (!t.has_coincident_endpoint(terminalPoints))
				T.add(t);

		return T;
	}

	void output_triangles() {
		HashSet<Triangle> hashSet = new HashSet<Triangle>(), triangles = new HashSet<Triangle>();

		DFS(hashSet, DAGroot);

		for (Triangle t : hashSet)
			if (!t.has_coincident_endpoint(terminalPoints))
				triangles.add(t);

		System.out.println(triangles.size());
		for (Triangle t : triangles)
			System.out.println(t);
	}
}

class TrianglePair {
	Triangle t1, t2;

	public TrianglePair(Triangle t1, Triangle t2) {
		this.t1 = t1;
		this.t2 = t2;
	}
}
