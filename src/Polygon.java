import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 4/4/2017.
 */
public class Polygon {
    List<Vertex> all_vertex;
    List<Integer> pts;
    Color c;

    Polygon(List<Vertex> all_vertex, Color c){
        this.all_vertex = new ArrayList<>();
        this.all_vertex.add(new Vertex(0, 0, 0));
        this.all_vertex.addAll(all_vertex);
        pts = new ArrayList<>();
        this.c = c;
    }

    Polygon(List<Vertex> all_vertex, List<Integer> pts, Color c){
        this.all_vertex = all_vertex;
        this.pts = pts;
        this.c = c;
    }

    public void addVertex(int vertex){
        pts.add(vertex);
    }

    public List<Triangle> triangulate(){
        return triangulate_polygon(this);
    }

    private List<Triangle> triangulate_polygon(Polygon p) {
        if(p.pts.size() == 3){
            System.out.println("Base case");
            List<Triangle> t = new ArrayList<>();
            t.add(new Triangle(p.all_vertex.get(p.pts.get(0)), p.all_vertex.get(p.pts.get(1)), p.all_vertex.get(p.pts.get(2)), p.c));
            return t;
        }
        List<Integer> firstPoints = new ArrayList<>(p.pts.subList(0, (p.pts.size()/2)+1));
        List<Integer> secondList = new ArrayList<>(
                p.pts.subList(p.pts.size()/2, p.pts.size()));
        secondList.add(p.pts.get(0));
        List<Triangle> firstPart = triangulate_polygon(new Polygon(p.all_vertex, firstPoints, p.c));
        List<Triangle> secondPart = triangulate_polygon(new Polygon(p.all_vertex, secondList, p.c));
        firstPart.addAll(secondPart);
        return firstPart;
    }
}
