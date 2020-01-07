import java.io.File;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws Exception {
		int n;
		double x, y;
		Point[] P;
		Scanner input = new Scanner(new File("input.txt"));
		// Scanner input = new Scanner(System.in);

		n = input.nextInt();
		P = new Point[n];

		for (int i = 0; i < n; ++i) {
			x = input.nextDouble();
			y = input.nextDouble();

			P[i] = new Point(x, y);
		}

		VoronoiDiagram voronoiDiagram = new VoronoiDiagram(n, P);

		voronoiDiagram.output();

		input.close();
	}
}
