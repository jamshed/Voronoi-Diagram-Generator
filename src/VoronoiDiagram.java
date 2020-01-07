import java.util.ArrayList;
import java.util.HashSet;

public class VoronoiDiagram {
	final int EXT_LEN = 10;
	int n;
	Point[] P;
	DelaunayTriangulation delaunayTriangulation;
	ArrayList<Edge> voronoiEdges;

	public VoronoiDiagram(int n, Point[] P) {
		this.n = n;
		this.P = P;

		voronoiEdges = new ArrayList<Edge>();

		compute_diagram();
	}

	HashSet<Edge> get_triangluation_edges() {
		ArrayList<Triangle> T = delaunayTriangulation.get_triangles();
		HashSet<Edge> E = new HashSet<Edge>();

		for (Triangle t : T) {
			E.add(new Edge(t.a, t.b));
			E.add(new Edge(t.b, t.c));
			E.add(new Edge(t.c, t.a));
		}

		return E;
	}

	void compute_voronoi_edges(HashSet<Edge> E) {
		for (Edge e : E) {
			TrianglePair trianglePair = delaunayTriangulation.adjacentTriangles
					.get(e);
			Triangle t1 = trianglePair.t1, t2 = trianglePair.t2;

			if (t1 != null && t2 != null)
				voronoiEdges
						.add(new Edge(t1.circumcenter(), t2.circumcenter()));
			else {
				Triangle t = (t1 == null ? t2 : t1);
				Point a = t.circumcenter(), b = e.mid_point(), c;

				double dx = (b.x - a.x), dy = (b.y - a.y), len = new Edge(a, b)
						.length();

				if (!t.contains(a)) {
					dx = -dx;
					dy = -dy;
				}

				c = new Point(a.x + dx * EXT_LEN / len, a.y + dy * EXT_LEN
						/ len);

				voronoiEdges.add(new Edge(a, c));
			}
		}
	}

	void compute_diagram() {
		delaunayTriangulation = new DelaunayTriangulation(n, P);

		HashSet<Edge> E = get_triangluation_edges();
		compute_voronoi_edges(E);
	}

	void output_points() {
		System.out.println(n);

		for (Point p : P)
			System.out.println(p);
	}

	void output_edges() {
		System.out.println(voronoiEdges.size());

		for (Edge e : voronoiEdges)
			System.out.println(e);
	}

	void output() {
		output_points();
		delaunayTriangulation.output_triangles();
		output_edges();
	}
}
