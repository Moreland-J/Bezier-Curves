import java.util.ArrayList;
import java.awt.Point;
import java.util.Random;
import org.apache.commons.math3.analysis.*;

public class BezierCurve {
    int n;
    Line line;
    ArrayList<Point> points;
    ArrayList<Integer> pascals;
    ArrayList<Integer> ts;
    float t;
    ArrayList<Point> splineInts;
    Point random;
    Point perpendicular;

    BezierCurve(int n, ArrayList<Point> points) {
        this.n = n;
        this.points = points;
        this.ts = new ArrayList<Integer>();
        this.line = formCurve(points);
    }

    public Line formCurve(ArrayList<Point> controls) {
        ArrayList<Integer> xs = new ArrayList<Integer>();
        ArrayList<Integer> ys = new ArrayList<Integer>();
        pascals = pascals(n);

        for (int t = 0; t < 100; t++) {
            ts.add(t);
            double sumX = 0, sumY = 0;
            float s = (float) t / 100;
            int m = n - 1;
            for (int i = 0; i < n; i++) {
                // CLASSIC BINOMIAL THEOREM
                sumX += pascals.get(i) * Math.pow(s, i) * Math.pow((1 - s), m - i) * controls.get(i).getX();
                sumY += pascals.get(i) * Math.pow(s, i) * Math.pow((1 - s), m - i) * controls.get(i).getY();
            }
            xs.add((int) Math.round(sumX));
            ys.add((int) Math.round(sumY));
        }   
        return new Line(xs, ys);
    }

    // Pascals Triangle production inspired by:
    // https://www.java-programs.thiyagaraaj.com/pascal_triangle_java_example_program.html
    public ArrayList<Integer> pascals(int n) {
        ArrayList<Integer> row = new ArrayList<Integer>();
		for(int i = 0; i < n; i++){
			int num = 1;
			for(int j = 0; j <= i; j++) {
                if (i == n - 1) {
                    row.add(num);
                }
                num = num * (i - j) / (j + 1);
            }
        }
        return row;
    }

    public void findRandPoint(ICPS icps) {
        splineInts = new ArrayList<Point>();
        int num = (int)(Math.random() * line.length);
        random = new Point(line.xs.get(num), line.ys.get(num));
        t = (float) ts.get(num) / 100;
        double[] derivs = derivative();
        double m = derivs[1] / derivs[0];
        double c = -((m * random.x) - random.y);
        double nm = -1 / m;  // GRADIENT OF THE NORMAL VECTOR
        double nc = -((nm * random.x) - random.y);

        perpendicular = new Point(random.x - 100, (int)(nm * (random.x - 100) + nc));

        Solver solver = new Solver();
        ArrayList<Double> res = new ArrayList<Double>();
        for (int i = 0; i < icps.noSegs; i++) {
            // ADD ALL X AND Y COEFS AND ADD C TO LAST COEF
            double[] sum = new double[4];
            sum[0] = nm * icps.coefsX[i][3] - icps.coefsY[i][3];
            sum[1] = nm * icps.coefsX[i][2] - icps.coefsY[i][2];
            sum[2] = nm * icps.coefsX[i][1] - icps.coefsY[i][1];
            sum[3] = nm * icps.coefsX[i][0] - icps.coefsY[i][0] + nc;
            solver.solve(sum[0], sum[1], sum[2], sum[3]);
            if (solver.x1 >= 0 && solver.x1 <= 1) {
                res.add(solver.x1);
                splinePoint(icps.coefsX, icps.coefsY, i, solver.x1);
            }
            if (solver.nRoots == 3) {
                if (solver.x2 >= 0 && solver.x2 <= 1) {
                    res.add(solver.x2);
                    splinePoint(icps.coefsX, icps.coefsY, i, solver.x2);
                }
                if (solver.x3 >= 0 && solver.x3 <= 1) {
                    res.add(solver.x3);
                    splinePoint(icps.coefsX, icps.coefsY, i, solver.x3);
                }

            }
        }
        System.out.println("\nRoot Results");
        System.out.println(res + "\n");
    }

    public double[] derivative() {
        double[] deriv = new double[2];
        for (int i = 0; i < n - 1; i++) {
            deriv[0] += sideDerive(i, n - 1) * (points.get(i + 1).getX() - points.get(i).getX());
            deriv[1] += sideDerive(i, n - 1) * (points.get(i + 1).getY() - points.get(i).getY());
        }
        deriv[0] = deriv[0] * n;
        deriv[1] = deriv[1] * n;
        return deriv;
    }

    public double sideDerive(int i, int n) {
        return (pascals.get(i) * Math.pow(t, i) * Math.pow((1 - t), n - i));
    }

    public void splinePoint(double[][] coefsX, double[][] coefsY, int seg, double t) {
        int x = ((int)(coefsX[seg][0] + coefsX[seg][1] * t + coefsX[seg][2] * Math.pow(t, 2) + coefsX[seg][3] * Math.pow(t, 3)));
        int y = ((int)(coefsY[seg][0] + coefsY[seg][1] * t + coefsY[seg][2] * Math.pow(t, 2) + coefsY[seg][3] * Math.pow(t, 3)));
        splineInts.add(new Point(x, y));
    }
}