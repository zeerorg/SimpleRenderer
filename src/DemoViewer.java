import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Asus on 4/3/2017.
 */
public class DemoViewer {

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        Container pane = frame.getContentPane();
        pane.setLayout(new BorderLayout());

        JSlider headingSlider = new JSlider(0, 360, 180);
        pane.add(headingSlider, BorderLayout.SOUTH);

        JSlider pitchSlider = new JSlider(SwingConstants.VERTICAL, -90, 90, 0);
        pane.add(pitchSlider, BorderLayout.EAST);

        ObjParser figure = new ObjParser("C:\\Users\\Asus\\IdeaProjects\\renderer\\assets\\wavefront\\cone.obj");

        JPanel renderPanel = new JPanel() {
            public void paintComponent(Graphics g) {

                BufferedImage img =
                        new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

                double heading = Math.toRadians(headingSlider.getValue());
                double heading2 = Math.toRadians(pitchSlider.getValue());
                Matrix3 transform_temp = new Matrix3(new double[]{
                        Math.cos(heading), 0, -Math.sin(heading),
                        0, 1, 0,
                        Math.sin(heading), 0, Math.cos(heading)
                });
                Matrix3 transform2 = new Matrix3(new double[]{
                        1, 0, 0,
                        0, Math.cos(heading2), Math.sin(heading2),
                        0, -Math.sin(heading2), Math.cos(heading2)
                });
                Matrix3 transform = transform_temp.multiply(transform2);

                Graphics2D g2D = (Graphics2D) g;
                g2D.setColor(Color.BLACK);
                g2D.fillRect(0, 0, getWidth(), getHeight());
                g2D.setColor(Color.WHITE);
                double[] zBuffer = new double[img.getWidth() * img.getHeight()];
                for (int q = 0; q < zBuffer.length; q++) {
                    zBuffer[q] = Double.NEGATIVE_INFINITY;
                }
                Vertex light = new Vertex(5, 5, 3);
                for(Polygon p: figure.p) {
                    List<Triangle> triangles = p.triangulate();
                    for (Triangle t : triangles) {
                        Vertex v1 = transform.transform(t.v1);
                        Vertex v2 = transform.transform(t.v2);
                        Vertex v3 = transform.transform(t.v3);

                        Vertex normal = (new Triangle(v1, v2, v3, t.color)).getNormal();
                        double shading = Math.abs(normal.dot_product(light) / (normal.length() * light.length()));

                        // since we are not using Graphics2D anymore,
                        // we have to do translation manually
                        v1.x += getWidth() / 2;
                        v1.y += getHeight() / 2;
                        v2.x += getWidth() / 2;
                        v2.y += getHeight() / 2;
                        v3.x += getWidth() / 2;
                        v3.y += getHeight() / 2;

                        int minX = (int) Math.max(0, Math.ceil(Math.min(v1.x, Math.min(v2.x, v3.x))));
                        int maxX = (int) Math.min(img.getWidth() - 1,
                                Math.floor(Math.max(v1.x, Math.max(v2.x, v3.x))));
                        int minY = (int) Math.max(0, Math.ceil(Math.min(v1.y, Math.min(v2.y, v3.y))));
                        int maxY = (int) Math.min(img.getHeight() - 1,
                                Math.floor(Math.max(v1.y, Math.max(v2.y, v3.y))));

                        double triangleArea =
                                (v1.y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - v1.x);

                        for (int y = minY; y <= maxY; y++) {
                            for (int x = minX; x <= maxX; x++) {
                                double b1 =
                                        ((y - v3.y) * (v2.x - v3.x) + (v2.y - v3.y) * (v3.x - x)) / triangleArea;
                                double b2 =
                                        ((y - v1.y) * (v3.x - v1.x) + (v3.y - v1.y) * (v1.x - x)) / triangleArea;
                                double b3 =
                                        ((y - v2.y) * (v1.x - v2.x) + (v1.y - v2.y) * (v2.x - x)) / triangleArea;
                                if (b1 >= 0 && b1 <= 1 && b2 >= 0 && b2 <= 1 && b3 >= 0 && b3 <= 1) {
                                    // handle rasterization...
                                    // for each rasterized pixel:
                                    double depth = b1 * v1.z + b2 * v2.z + b3 * v3.z;
                                    int zIndex = y * img.getWidth() + x;
                                    if (zBuffer[zIndex] < depth) {

                                        img.setRGB(x, y, setColorWithLight(t.color, shading).getRGB());
                                        zBuffer[zIndex] = depth;
                                    }
                                }
                            }
                        }


                        g2D.drawImage(img, 0, 0, null);
                    }
                }
            }
        };

        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e -> renderPanel.repaint());
        pane.add(renderPanel, BorderLayout.CENTER);
        frame.setSize(400, 400);
        frame.setVisible(true);
    }

    private static List<Triangle> getTetrahedron() {
        List tris = new ArrayList<Triangle>();
        tris.add(new Triangle(new Vertex(100, 100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(-100, 100, -100),
                Color.BLUE));
        tris.add(new Triangle(new Vertex(100, 100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(100, -100, -100),
                Color.GREEN));
        tris.add(new Triangle(new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100),
                new Vertex(100, 100, 100),
                Color.RED));
        tris.add(new Triangle(new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100),
                new Vertex(-100, -100, 100),
                Color.WHITE));
        return tris;
    }

    public static Color setColorWithLight(Color color, double shade){
        double redLinear = Math.pow(color.getRed(), 2.4) * shade;
        double greenLinear = Math.pow(color.getGreen(), 2.4) * shade;
        double blueLinear = Math.pow(color.getBlue(), 2.4) * shade;

        int red = (int) Math.pow(redLinear, 1/2.4);
        int green = (int) Math.pow(greenLinear, 1/2.4);
        int blue = (int) Math.pow(blueLinear, 1/2.4);

        return new Color(red, green, blue);
    }
}
