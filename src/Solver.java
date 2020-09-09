/* The formulas for the roots of a cubic equation come from:
 * E. Weisstein. "Cubic formula." From <I>MathWorld</I>--A Wolfram Web Resource.
    http://mathworld.wolfram.com/CubicFormula.html
 *
 * @author Alan Kaminsky
 * @version 02-Feb-2008
 */
public class Solver {
    // Hidden constants.
    private static final double TWO_PI = 2.0 * Math.PI;
    private static final double FOUR_PI = 4.0 * Math.PI;
    // Exported fields.
    public int nRoots;
    public double x1;
    public double x2;
    public double x3;

//Exported constructors.

    /**
     * Construct a new Cubic object.
     */
    public Solver() {}

//Exported operations.

    /**
     * Solve the cubic equation with the given coefficients. The results are
     * stored in this Cubic object's fields.
     *
     * @exception  DomainException
     *     (unchecked exception) Thrown if <TT>a</TT> is 0; in other words, the
     *     coefficients do not represent a cubic equation.
     */
    public void solve(double a, double b, double c, double d) {
        // Verify preconditions.
        if (a == 0.0) {
            throw new RuntimeException ("Cubic.solve(): a = 0");
        }

        // Normalize coefficients.
        double denom = a;
        a = b/denom;
        b = c/denom;
        c = d/denom;

        // Commence solution.
        double a_over_3 = a / 3.0;
        double Q = (3*b - a*a) / 9.0;
        double Q_CUBE = Q*Q*Q;
        double R = (9*a*b - 27*c - 2*a*a*a) / 54.0;
        double R_SQR = R*R;
        double D = Q_CUBE + R_SQR;

        if (D < 0.0) {
            // Three unequal real roots.
            nRoots = 3;
            double theta = Math.acos (R / Math.sqrt (-Q_CUBE));
            double SQRT_Q = Math.sqrt (-Q);
            x1 = 2.0 * SQRT_Q * Math.cos (theta/3.0) - a_over_3;
            x2 = 2.0 * SQRT_Q * Math.cos ((theta+TWO_PI)/3.0) - a_over_3;
            x3 = 2.0 * SQRT_Q * Math.cos ((theta+FOUR_PI)/3.0) - a_over_3;
            sortRoots();
        }
        else if (D > 0.0){
            // One real root.
            nRoots = 1;
            double SQRT_D = Math.sqrt (D);
            double S = Math.cbrt (R + SQRT_D);
            double T = Math.cbrt (R - SQRT_D);
            x1 = (S + T) - a_over_3;
            x2 = Double.NaN;
            x3 = Double.NaN;
        }
        else {
            // Three real roots, at least two equal.
            nRoots = 3;
            double CBRT_R = Math.cbrt (R);
            x1 = 2*CBRT_R - a_over_3;
            x2 = x3 = CBRT_R - a_over_3;
            sortRoots();
        }
    }

    // Hidden operations
    // Sort the roots into descending order
    private void sortRoots() {
        if (x1 < x2) {
            double tmp = x1; x1 = x2; x2 = tmp;
        }
        if (x2 < x3) {
            double tmp = x2; x2 = x3; x3 = tmp;
        }
        if (x1 < x2) {
            double tmp = x1; x1 = x2; x2 = tmp;
        }
    }
}