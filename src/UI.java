import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class UI extends JPanel {
    ArrayList<Point> points = new ArrayList<Point>();
    boolean drawn = false;
    boolean intersect = false;

    UI() {
        JFrame frame = new JFrame();
        frame.setSize(600, 600);        // HAS TO BE BIG ENOUGH TO FIT JPANEL
        // frame.setVisible(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    points.add(new Point(e.getX(), e.getY()));
                    System.out.println(e.getX() + " " + e.getY());
                    repaint();
                } else if (e.getButton() == MouseEvent.BUTTON3 && !drawn) {
                    drawn = true;
                    repaint();
                }
                else if (drawn && e.getButton() == MouseEvent.BUTTON2) {
                    intersect = true;
                    repaint();
                }
                else if (drawn) {
                    points = new ArrayList<Point>();
                    drawn = false;
                    intersect = false;
                    repaint();
                }
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(Color.black);
        for (Point point : points) {
            g2.fillOval(point.x - 5, point.y - 5, 10, 10);
        }

        if (drawn) {
            // CURVEE
            g.setColor(Color.BLACK);
            BezierCurve curve = new BezierCurve(points.size(), points);
            int[] xs = curve.line.xs.stream().mapToInt(i -> i).toArray();
            int[] ys = curve.line.ys.stream().mapToInt(i -> i).toArray();            
            g.drawPolyline(xs, ys, curve.line.length);
            System.out.println("Drawn Curve");

            // SPLINE TIME
            g.setColor(Color.BLUE);
            BezierSpline splineCurve = new BezierSpline(points.size(), points);
            int[] x2s = splineCurve.line.xs.stream().mapToInt(i -> i).toArray();
            int[] y2s = splineCurve.line.ys.stream().mapToInt(i -> i).toArray();            
            g.drawPolyline(x2s, y2s, splineCurve.line.length);
            System.out.println("Drawn Spline");

            // INTERPOLATION
            g.setColor(Color.RED);
            ICPS icps = new ICPS(points.size(), points);
            if (!icps.empty) {
                int[] x3s = icps.line.xs.stream().mapToInt(i -> i).toArray();
                int[] y3s = icps.line.ys.stream().mapToInt(i -> i).toArray();            
                g.drawPolyline(x3s, y3s, icps.line.length);
                System.out.println("Drawn Interpolation");
            }

            if (intersect) {
                g.setColor(Color.GREEN);
                curve.findRandPoint(icps);
//                g2.drawLine(curve.random.x, curve.random.y, curve.perpendicular.x, curve.perpendicular.y);
                g2.fillOval(curve.random.x - 5, curve.random.y - 5, 10, 10);
                for (int i = 0; i < curve.splineInts.size(); i++) {
                    g2.fillOval(curve.splineInts.get(i).x - 5, curve.splineInts.get(i).y - 5, 10, 10);
                }
            }
        }
    }

    public static void main(String args[]) {
        JFrame frame = new JFrame();
        frame.setSize(600, 600);
        frame.add(new UI());
        frame.setVisible(true);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}