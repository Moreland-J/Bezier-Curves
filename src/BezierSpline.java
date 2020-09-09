import java.util.ArrayList;
import java.awt.Point;

public class BezierSpline {
    int n;
    ArrayList<Line> segments;
    Line line;
    ArrayList<Point> points;

    BezierSpline(int n, ArrayList<Point> points) {
        this.n = n;
        this.points = new ArrayList<Point>(points);
        this.line = formSpline(this.points);
    }

    public Line formSpline(ArrayList<Point> points) {
        Line line = new Line();
        // GET EACH SEGMENT
        for (int i = 0; i < points.size(); i += 3) {
            ArrayList<Point> spline = null;
            Point mid = null;
            int end = Integer.min(i + 4, points.size());
            if (end < points.size()) {
                // GET MIDPOINT OF SEGMENT
                int x = (points.get(end - 2).x + points.get(end - 1).x) / 2;
                int y = (points.get(end - 2).y + points.get(end - 1).y) / 2;
                mid = new Point(x, y);
                points.add(end - 1, mid);
            }
            // FORM BEZIER FROM WITH THIS NEW POINT
            spline = new ArrayList<Point>(points.subList(i, end));
            BezierCurve bez = new BezierCurve(spline.size(), spline);
            
            if (i == 0) {
                System.out.println(i + " " + spline);
                line = new Line(bez.line.xs, bez.line.ys);
            }
            else {
                System.out.println(i + " " + spline);
                line.addSegment(bez.line);
            }
        }
        return line;
    }
}