import java.awt.*;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 4/4/2017.
 */
public class ObjParser {
    String filePath;
    List<Vertex> v;
    List<Polygon> p;

    ObjParser(String filePath) {
        this.filePath = filePath;
        this.v = new ArrayList<>();
        this.p = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            for(String line; (line = br.readLine()) != null; ) {
                System.out.println(line);
                if (line.startsWith("vn")) {
                    continue;
                } else if (line.startsWith("v")) {
                    String[] line_split = line.split(" ");
                    v.add(new Vertex(
                            Double.parseDouble(line_split[1])*100,
                            Double.parseDouble(line_split[2])*100,
                            Double.parseDouble(line_split[3])*100
                    ));
                } else if(line.startsWith("f")) {
                    Polygon p_temp = new Polygon(v, Color.WHITE);
                    String[] line_split = line.split(" ");
                    for(int x = 1; x < line_split.length; x++){
                        p_temp.addVertex(Integer.parseInt(line_split[x].split("//")[0]));
                    }
                    p.add(p_temp);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
