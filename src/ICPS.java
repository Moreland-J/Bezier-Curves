import java.util.ArrayList;
import java.util.Arrays;
import java.awt.Point;

import org.apache.commons.math3.linear.*;

public class ICPS {
    ArrayList<Point> points;
    Line line;
    int noSegs;
    int N;
    boolean empty;
    // 4 LENGTH ARRAY PER POLYNOMIAL
    double[] xMat, yMat;
    double[][] polyMatrixX, polyMatrixY;
    double[][] coefsX, coefsY;

    ICPS(int n, ArrayList<Point> points) {
        this.points = points;
        this.noSegs = n - 1;
        this.N = 4 * noSegs;
        this.coefsX = new double[noSegs][4];
        this.coefsY = new double[noSegs][4];

        /*
        for (int i = 0; i < noSegs; i++) {
            Point ps[] = new Point[4];
            if (i == 0 || i == noSegs - 1) {
                if (i == 0) {
                    ps[0] = points.get(0);
                    ps[1] = points.get(0);
                }
                else {
                    ps[0] = points.get(i - 1);
                    ps[1] = points.get(i);
                }
                if (i == noSegs - 1) {
                    ps[2] = points.get(points.size() - 1);
                    ps[3] = points.get(points.size() - 1);
                }
                else {
                    ps[2] = points.get(i + 1);
                    ps[3] = points.get(i + 2);
                }
            }
            else {matrixData
                ps[0] = points.get(i - 1);
                ps[1] = points.get(i);
                ps[2] = points.get(i + 1);
                ps[3] = points.get(i + 2);
            }
            producePolynomial(i, ps);
        }
        */

        // CALL MATRIX WORK
        formMatrices();

        for (int i = 0; i < noSegs; i++) {
            if (i == 0) {
                this.line = getLine(i);
            }
            else {
                this.line.addSegment(getLine(i));
            }
        }
        empty = false;
    }

    public void formMatrices() {
        // FORM MATRIX BY PRODUCING APPROPRIATE POLYNOMIALS
        polyMatrixX = new double[4 * noSegs][4 * noSegs];
        polyMatrixY = new double[4 * noSegs][4 * noSegs];

        for (int i = 0; i < noSegs; i++) {
            spline(i);
        }
        System.out.println("X");
        display(polyMatrixX);

        // MATRIX MATHS
        RealMatrix matX = MatrixUtils.createRealMatrix(polyMatrixX);
        RealMatrix matY = MatrixUtils.createRealMatrix(polyMatrixY);
        xMat = new double[N];
        yMat = new double[N];
        for (int i = 0; i < noSegs; i++) {
            xMat[i * 4] = (int) points.get(i).getX();
            xMat[(i * 4) + 1] = (int) points.get(i + 1).getX();

            yMat[i * 4] = (int) points.get(i).getY();
            yMat[(i * 4) + 1] = (int) points.get(i + 1).getY();
        }
        RealVector xs = new ArrayRealVector(xMat);
        RealVector ys = new ArrayRealVector(yMat);
        // APPLY SOLVER
        DecompositionSolver solverForYs = new LUDecomposition(matX).getSolver();
        DecompositionSolver solverForXs = new LUDecomposition(matY).getSolver();
        RealVector yVec = solverForYs.solve(ys);
        RealVector xVec = solverForXs.solve(xs);
        // DIVIDE THESE INTO COEFS ONCE SOLVED FOR
        double unknownsX[] = xVec.toArray();
        double unknownsY[] = yVec.toArray();

        // FROM HERE WE'LL ADD VALUES TO COEFS
        System.out.println("Coefs");
        convertCoefs(unknownsX, unknownsY);
    }

    // p(x) = b3.x^3 + b2.x^2 + b1.x + b0
    public void spline(int i) {
        // THEN FINAL 2 LINES ARE 0 2 0 -2
        // GOING FROM 0 TO 1
        // FILL IN AS 0 0 0 1 && 1 1 1 1
        polyMatrixX[i * 4][(i * 4)] = 0;
        polyMatrixX[i * 4][(i * 4) + 1] = 0;
        polyMatrixX[i * 4][(i * 4) + 2] = 0;
        polyMatrixX[i * 4][(i * 4) + 3] = 1;

        polyMatrixY[i * 4][(i * 4)] = 0;
        polyMatrixY[i * 4][(i * 4) + 1] = 0;
        polyMatrixY[i * 4][(i * 4) + 2] = 0;
        polyMatrixY[i * 4][(i * 4) + 3] = 1;

        polyMatrixX[(i * 4) + 1][(i * 4)] = 1;
        polyMatrixX[(i * 4) + 1][(i * 4) + 1] = 1;
        polyMatrixX[(i * 4) + 1][(i * 4) + 2] = 1;
        polyMatrixX[(i * 4) + 1][(i * 4) + 3] = 1;

        polyMatrixY[(i * 4) + 1][(i * 4)] = 1;
        polyMatrixY[(i * 4) + 1][(i * 4) + 1] = 1;
        polyMatrixY[(i * 4) + 1][(i * 4) + 2] = 1;
        polyMatrixY[(i * 4) + 1][(i * 4) + 3] = 1;

        if (i == noSegs - 1) {
            condition(i);
        }
        else {
            derivatives(i);
        }
    }

    
    // p'(x) = 3.b3.x^2 + 2.b2.x + b1
    // p''(x) = 2.3.b3.x + 1.2.b2 
    //        = 6.b3.x + 2.b2
    public void derivatives(int i) {
        // ADD AFTER 2n ROWS, TWO ROWS, ONE FOR FIRST DERIVE, ONE FOR SECOND        
        // 0 TO 1
        polyMatrixX[(i * 4) + 2][(i * 4)] = 3;
        polyMatrixX[(i * 4) + 2][(i * 4) + 1] = 2;
        polyMatrixX[(i * 4) + 2][(i * 4) + 2] = 1;
        polyMatrixX[(i * 4) + 2][(i * 4) + 3] = 0;
        polyMatrixX[(i * 4) + 2][2 + (i * 4) + 4] = -1;

        polyMatrixY[(i * 4) + 2][(i * 4)] = 3;
        polyMatrixY[(i * 4) + 2][(i * 4) + 1] = 2;
        polyMatrixY[(i * 4) + 2][(i * 4) + 2] = 1;
        polyMatrixY[(i * 4) + 2][(i * 4) + 3] = 0;
        polyMatrixY[(i * 4) + 2][(i * 4) + 6] = -1;

        polyMatrixX[(i * 4) + 3][(i * 4)] = 6;
        polyMatrixX[(i * 4) + 3][(i * 4) + 1] = 2;
        polyMatrixX[(i * 4) + 3][(i * 4) + 4] = 0;
        polyMatrixX[(i * 4) + 3][(i * 4) + 5] = -polyMatrixX[(i * 4) + 3][(i * 4) + 1];

        polyMatrixY[(i * 4) + 3][(i * 4)] = 6;
        polyMatrixY[(i * 4) + 3][(i * 4) + 1] = 2;
        polyMatrixY[(i * 4) + 3][(i * 4) + 2] = 0;
        polyMatrixY[(i * 4) + 3][(i * 4) + 5] = -polyMatrixY[(i * 4) + 3][(i * 4) + 1];
    }

    // p''(x) = 2.3.b3.x + 1.2.b2 
    //        = 6.b3.x + 2.b2
    public void condition(int i) {
        // ADD IN LAST 2 ROWS
        // USE FIRST AND LAST x VALUES IN SECOND DERIVATIVE
        int j = (4 * noSegs) - 4;
        int k = 4 * noSegs;

        polyMatrixX[j][k - 4] = 0;
        polyMatrixX[j][k - 3] = 0;
        polyMatrixX[j][k - 2] = 0;
        polyMatrixX[j][k - 1] = 1;

        polyMatrixY[j][k - 4] = 0;
        polyMatrixY[j][k - 3] = 0;
        polyMatrixY[j][k - 2] = 0;
        polyMatrixY[j][k - 1] = 1;

        j++;
        polyMatrixX[j][k - 4] = 1;
        polyMatrixX[j][k - 3] = 1;
        polyMatrixX[j][k - 2] = 1;
        polyMatrixX[j][k - 1] = 1;

        polyMatrixY[j][k - 4] = 1;
        polyMatrixY[j][k - 3] = 1;
        polyMatrixY[j][k - 2] = 1;
        polyMatrixY[j][k - 1] = 1;

        j++;
        polyMatrixX[j][0] = 0;
        polyMatrixX[j][1] = 2;
        polyMatrixX[j][2] = 0;
        polyMatrixX[j][3] = 0;

        polyMatrixY[j][0] = 0;
        polyMatrixY[j][1] = 2;
        polyMatrixY[j][2] = 0;
        polyMatrixY[j][3] = -2;

        j++;
        polyMatrixX[j][k - 4] = -6;
        polyMatrixX[j][k - 3] = -2;
        polyMatrixX[j][k - 2] = 0;
        polyMatrixX[j][k - 1] = 0;

        polyMatrixY[j][k - 4] = -6;
        polyMatrixY[j][k - 3] = -2;
        polyMatrixY[j][k - 2] = 0;
        polyMatrixY[j][k - 1] = 0;
    }

    public void display(double A[][]) { 
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.print(A[i][j] + " ");
            }
            System.out.println();
        } 
    } 
    public void display(float A[][]) { 
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                System.out.printf("%.6f ", A[i][j]);
            }
            System.out.println();
        }
    }

    public void convertCoefs(double[] unkX, double[] unkY) {
        for (int i = 0; i < coefsX.length; i++) {
            coefsX[i][0] = unkX[(i * 4) + 3];
            coefsX[i][1] = unkX[(i * 4) + 2];
            coefsX[i][2] = unkX[(i * 4) + 1];
            coefsX[i][3] = unkX[i * 4];

            coefsY[i][0] = unkY[(i * 4) + 3];
            coefsY[i][1] = unkY[(i * 4) + 2];
            coefsY[i][2] = unkY[(i * 4) + 1];
            coefsY[i][3] = unkY[i * 4];
        }
    }

    /*
    // NON-SPLINE BASED INTERPOLATION
    public void producePolynomial(int seg, Point[] ps) {
        // X COEFFICIENTS PRODUCED
        double f1, f2;
        f1 = 0.5 * (ps[2].getX() - ps[0].getX());
        f2 = 0.5 * (ps[3].getX() - ps[1].getX());

        System.out.println(ps[0].getX() + " " + ps[1].getX() + " " + ps[2].getX() + " " + ps[3].getX());
        coefsX[seg][0] = (int) ps[1].getX();
        coefsX[seg][1] = (int) f1;
        coefsX[seg][2] = (int) (3.0 * (ps[2].getX() - ps[1].getX()) - (2.0 * f1) - f2);
        coefsX[seg][3] = (int) (f1 + f2 + (2.0 * (-ps[2].getX() + ps[1].getX())));

        // Y COEFFICIENTS PRODUCED
        double g1, g2;
        g1 = 0.5 * (ps[2].getY() - ps[0].getY());
        g2 = 0.5 * (ps[3].getY() - ps[1].getY());

        coefsY[seg][0] = (int) ps[1].getY();
        coefsY[seg][1] = (int) g1;
        coefsY[seg][2] = (int) (3.0 * (ps[2].getY() - ps[1].getY()) - (2.0 * g1) - g2);
        coefsY[seg][3] = (int) (g1 + g2 + (2.0 * (-ps[2].getY() + ps[1].getY())));
    }
    */

    // PLEASE NOTE SEG MUST BE SENT IN AS >= 0, SO EACH SEGMENT HAS AN INDEX STARTING FROM 0
    public Line getLine(int seg) {
        ArrayList<Integer> xs = new ArrayList<Integer>();
        ArrayList<Integer> ys = new ArrayList<Integer>();
        for (double t = 0; t < 1.0; t += 0.01) {
            xs.add((int)(coefsX[seg][0] + coefsX[seg][1] * t + coefsX[seg][2] * Math.pow(t, 2) + coefsX[seg][3] * Math.pow(t, 3)));
            ys.add((int)(coefsY[seg][0] + coefsY[seg][1] * t + coefsY[seg][2] * Math.pow(t, 2) + coefsY[seg][3] * Math.pow(t, 3)));
        }
        return new Line(xs, ys);
    }
}