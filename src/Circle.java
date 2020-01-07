public class Circle {
	double h, k, r;

	public Circle(Point p1, Point p2, Point p3) {
		double a1 = (p2.y - p3.y)
				* (p1.x * p1.x - p2.x * p2.x + p1.y * p1.y - p2.y * p2.y)
				- (p1.y - p2.y)
				* (p2.x * p2.x - p3.x * p3.x + p2.y * p2.y - p3.y * p3.y);
		double b1 = 2 * ((p1.x - p2.x) * (p2.y - p3.y) - (p2.x - p3.x)
				* (p1.y - p2.y));
		double a2 = (p2.x - p3.x)
				* (p1.x * p1.x - p2.x * p2.x + p1.y * p1.y - p2.y * p2.y)
				- (p1.x - p2.x)
				* (p2.x * p2.x - p3.x * p3.x + p2.y * p2.y - p3.y * p3.y);
		double b2 = 2 * ((p2.x - p3.x) * (p1.y - p2.y) - (p1.x - p2.x)
				* (p2.y - p3.y));

		h = a1 / b1;
		k = a2 / b2;
		r = Math.sqrt(p1.x * p1.x - 2 * p1.x * h + h * h + p1.y * p1.y - 2
				* p1.y * k + k * k);
	}

	boolean is_interior(Point p) {
		return (p.x - h) * (p.x - h) + (p.y - k) * (p.y - k) < r * r;
	}
}