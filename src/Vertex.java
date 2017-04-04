/**
 * Created by Asus on 4/3/2017.
 */
public class Vertex {
    double x;
    double y;
    double z;

    Vertex(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    Vertex subtract(Vertex subtraend){
        return new Vertex(
                x - subtraend.x,
                y - subtraend.y,
                z - subtraend.z
        );
    }

    Vertex cross_product(Vertex v2) {
        return new Vertex (
                y * v2.z - z * v2.y,
                z * v2.x - x * v2.z,
                x * v2.y - y * v2.x
        );
    }

    double length(){
        return Math.sqrt(x*x + y*y + z*z);
    }

    double dot_product(Vertex v) {
        return x*v.x + y*v.y + z*v.z;
    }
}
