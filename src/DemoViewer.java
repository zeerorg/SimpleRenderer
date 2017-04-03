import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

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

        JPanel renderPanel = new JPanel() {
          public void paintComponent(Graphics g){

              BufferedImage img =
                      new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);

              double heading = Math.toRadians(headingSlider.getValue());
              double heading2 = Math.toRadians(pitchSlider.getValue());
              Matrix3 transform_temp = new Matrix3(new double[] {
                      Math.cos(heading), 0, -Math.sin(heading),
                      0, 1, 0,
                      Math.sin(heading), 0, Math.cos(heading)
              });
              Matrix3 transform2 = new Matrix3(new double[] {
                      1, 0, 0,
                      0, Math.cos(heading2), Math.sin(heading2),
                      0, -Math.sin(heading2), Math.cos(heading2)
              });
              Matrix3 transform = transform_temp.multiply(transform2);

              Graphics2D g2D = (Graphics2D) g;
              g2D.setColor(Color.BLACK);
              g2D.fillRect(0, 0, getWidth(), getHeight());
              g2D.translate(getWidth()/2, getHeight()/2);
              g2D.setColor(Color.WHITE);
              for(Triangle t: getTetrahedron()) {
                  Vertex v1 = transform.transform(t.v1);
                  Vertex v2 = transform.transform(t.v2);
                  Vertex v3 = transform.transform(t.v3);

                  Path2D path = new Path2D.Double();
                  path.moveTo(v1.x, v1.y);
                  path.lineTo(v2.x, v2.y);
                  path.lineTo(v3.x, v3.y);
                  path.closePath();
                  g2D.draw(path);
              }
              // Main Rendering here
          }
        };

        headingSlider.addChangeListener(e -> renderPanel.repaint());
        pitchSlider.addChangeListener(e -> renderPanel.repaint());
        pane.add(renderPanel, BorderLayout.CENTER);
        frame.setSize(400, 400);
        frame.setVisible(true);
    }

    private static ArrayList<Triangle> getTetrahedron() {
        ArrayList tris = new ArrayList<Triangle>();
        tris.add(new Triangle(new Vertex(100, 100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(-100, 100, -100),
                Color.WHITE));
        tris.add(new Triangle(new Vertex(100, 100, 100),
                new Vertex(-100, -100, 100),
                new Vertex(100, -100, -100),
                Color.RED));
        tris.add(new Triangle(new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100),
                new Vertex(100, 100, 100),
                Color.GREEN));
        tris.add(new Triangle(new Vertex(-100, 100, -100),
                new Vertex(100, -100, -100),
                new Vertex(-100, -100, 100),
                Color.BLUE));
        return tris;
    }
}
