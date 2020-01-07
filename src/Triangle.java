import java.util.ArrayList;

public class Triangle {
	final static double EPS = 1e-8;
	Point a, b, c;
	ArrayList<Triangle> children;

	public Triangle(Point a, Point b, Point c) {
		this.a = a;
		this.b = b;
		this.c = c;

		sort_points();

		children = null;
	}

	void sort_points() {
		Point[] P = new Point[3];

		P[0] = a;
		P[1] = b;
		P[2] = c;

		for (int i = 0; i < 3; ++i)
			for (int j = i + 1; j < 3; ++j)
				if (P[i].compareTo(P[j]) > 0) {
					Point temp = P[i];
					P[i] = P[j];
					P[j] = temp;
				}

		a = P[0];
		b = P[1];
		c = P[2];
	}

	double area() {
		return Math.abs(a.x * (b.y - c.y) + b.x * (c.y - a.y) + c.x
				* (a.y - b.y)) / 2;
	}

	boolean contains(Point p) {
		Triangle t1 = new Triangle(a, p, b), t2 = new Triangle(b, p, c), t3 = new Triangle(
				c, p, a);

		return Math.abs(area() - (t1.area() + t2.area() + t3.area())) < EPS;
	}

	boolean is_interior(Point p) {
		return !p.on_straight_line(a, b) && !p.on_straight_line(b, c)
				&& !p.on_straight_line(c, a);
	}

	Point opposite_point(Edge e) {
		Point pi = e.a, pj = e.b;

		if (!a.equals(pi) && !a.equals(pj))
			return a;

		if (!b.equals(pi) && !b.equals(pj))
			return b;

		return c;
	}

	Edge coincident_edge(Point p) {
		if (p.on_straight_line(a, b))
			return new Edge(a, b);

		if (p.on_straight_line(b, c))
			return new Edge(b, c);

		return new Edge(c, a);
	}

	boolean has_coincident_endpoint(Point[] P) {
		for (Point p : P)
			if (p.equals(a) || p.equals(b) || p.equals(c))
				return true;

		return false;
	}

	Point circumcenter() {
		Circle circle = new Circle(a, b, c);

		return new Point(circle.h, circle.k);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass())
			return false;

		Triangle t = (Triangle) obj;

		return a.equals(t.a) && b.equals(t.b) && c.equals(t.c);
	}

	@Override
	public int hashCode() {
		int prime1 = 83, prime2 = 89, prime3 = 97;

		return (prime1 * a.hashCode()) ^ (prime2 * b.hashCode())
				^ (prime3 * c.hashCode());
	}

	@Override
	public String toString() {
		return a + " " + b + " " + c;
		// return String.format("%f %f %f %f %f %f", a.x, a.y, b.x, b.y, c.x,
		// c.y);
		/*
		 * return String.format("(%d, %d), (%d, %d), (%d, %d)", a.x, a.y, b.x,
		 * b.y, c.x, c.y);
		 */
	}
}
