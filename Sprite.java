import java.awt.image.BufferedImage;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.geom.Rectangle2D;
public class Sprite extends Rectangle2D.Double implements Drawable, Movable
{
    private static final long serialVersionUID = 1L;
    long delay;
    long animation = 0;
    Bildschirm parent;
    BufferedImage[] pics;
    int currentpic = 0;
    protected double dx;
    protected double dy;
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
        g.drawImage(pics[currentpic], (int) x, (int) y, null);
    }
    public void doLogic(long delta)
    {
        animation += (delta/1000000);
        if (animation > delay)
        {
            animation = 0;
            computeAnimation();
        }
    }
    public void move(long delta)
    {
        if(dx!=0)
        {
            x += dx* (delta/1e9);
        }
        if(dy!=0)
        {
            y += dy* (delta/1e9);
        }
    }
    public void computeAnimation()
    {
        currentpic ++;
        if(currentpic >=pics.length)
        {
            currentpic = 0;
        }
    }
    
    /*
     * get und set Methoden
     */
    public double getHorizontalSpeed()
    {
        return dx;
    }
    public void setHorizontalSpeed(double dx)
    {
        this.dx = dx;
    }
    public double getVerticalSpeed()
    {
        return dy;
    }
    public void setVerticalSpeed(double dy)
    {
        this.dy = dy;
    }
}