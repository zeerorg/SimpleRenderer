import java.awt.*;

/**
 * Created by Asus on 4/3/2017.
 */
public class Triangle {
    Vertex v1;
    Vertex v2;
    Vertex v3;
    Color color;

    Triangle(Vertex v1, Vertex v2, Vertex v3, Color color){
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.color = color;
    }

    Vertex getNormal(){
        Vertex line_ab = v2.subtract(v1);
        Vertex line_ac = v3.subtract(v1);

        Vertex norm = line_ab.cross_product(line_ac);
        double normLength = norm.length();
        norm.x /= normLength;
        norm.y /= normLength;
        norm.z /= normLength;

        return norm;
    }
}
