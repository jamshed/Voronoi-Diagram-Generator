public class Point {
	final static double EPS = 1e-8;
	double x, y;

	public Point(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double compareTo(Point p) {
		return Math.abs(x - p.x) < EPS ? y - p.y : x - p.x;
	}

	boolean on_straight_line(Point a, Point b) {
		double c1 = a.y - b.y, c2 = b.x - a.x, c3 = a.x * (b.y - a.y) + a.y
				* (a.x - b.x);

		return Math.abs(c1 * x + c2 * y + c3) < EPS;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass())
			return false;

		Point p = (Point) obj;

		return (Math.abs(x - p.x) < EPS) && (Math.abs(y - p.y) < EPS);
	}

	@Override
	public int hashCode() {
		int prime = 1000003;

		return prime * (int) x + (int) y;
	}

	@Override
	public String toString() {
		return String.format("%f %f", x, y);
		// return String.format("(%d, %d)", x, y);
	}
}
