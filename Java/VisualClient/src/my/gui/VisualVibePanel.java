package my.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import javax.swing.JPanel;
import my.data.Config;

/**
 *
 * @author torstein
 */
public class VisualVibePanel extends JPanel {
    private static final int rowscols = (int)Math.ceil(Math.sqrt(Config.vibs));
    private Point[] points = new Point[Config.vibs];
    private Color pColour = Color.BLACK;
    
    public VisualVibePanel() {
        super();
        for (int i=0; i<Config.vibs; i++) {
            int xi = i / rowscols;
            int yi = i % rowscols;
            int x = (xi+1)*this.getWidth()/(rowscols+1);
            int y = (yi+1)*this.getHeight()/(rowscols+1);
            points[i] = new Point(x, y);
        }
    }
     
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Point p:this.points) {
            if (p != null) {
                g.setColor(pColour);
                g.fillOval(p.x-Config.r, p.y-Config.r, 8, 8);
            }
        }
    }
    
    public void setPoint(Point p) {
        this.repaint();
    }
    
    public void setPointColour(Color c) {
        this.pColour = c;
        this.repaint();
    }
}
