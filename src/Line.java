import java.util.ArrayList;

public class Line {
    ArrayList<Integer> xs;
    ArrayList<Integer> ys;
    int length;

    Line() {
        this.xs = new ArrayList<Integer>();
        this.ys = new ArrayList<Integer>();
    }

    Line(ArrayList<Integer> xs, ArrayList<Integer> ys) {
        this.xs = xs;
        this.ys = ys;
        this.length = xs.size();
    }

    public void addSegment(Line addition) {
        xs.addAll(addition.xs);
        ys.addAll(addition.ys);
        this.length = xs.size();
    }
}