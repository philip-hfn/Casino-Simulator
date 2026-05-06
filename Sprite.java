import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
public class Sprite extends Rectangle2D.Double implements Drawable, Movable
{
    private static final long serialVersionUID = 1L;
    long delay;
    Bildschirm parent;
    BufferedImage[] pics;
    int currentpic = 0;
    public Sprite (BufferedImage[] i, double x, double y, long delay, Bildschirm p)
    {
        pics = i;
        this.x = x;
        this.y = y;
        this.delay = delay;
        this.width = pics [0].getWidth();
        this.height = pics [0].getHeight();
        this.parent = p;
    }
    public void drawObjects(Graphics g)
    {
    
    }
    public void doLogic(long delta)
    {
    
    }
    public void move(long delta)
    {
    
    }
}