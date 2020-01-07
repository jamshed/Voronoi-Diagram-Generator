public class Edge {
	Point a, b;

	public Edge(Point a, Point b) {
		if (a.compareTo(b) < 0) {
			this.a = a;
			this.b = b;
		} else {
			this.a = b;
			this.b = a;
		}
	}

	Point mid_point() {
		return new Point((a.x + b.x) / 2, (a.y + b.y) / 2);
	}

	double length() {
		return Math.sqrt((a.x - b.x) * (a.x - b.x) + (a.y - b.y) * (a.y - b.y));
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass())
			return false;

		Edge e = (Edge) obj;

		return a.equals(e.a) && b.equals(e.b);
	}

	@Override
	public int hashCode() {
		int prime1 = 89, prime2 = 97;

		return (prime1 * a.hashCode()) ^ (prime2 * b.hashCode());
	}

	@Override
	public String toString() {
		return a + " " + b;
	}
}
