import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
public class Bildschirm extends JPanel implements Runnable
{
    private static final long serialVersionUID = 1L;
    JFrame frame;
    
    long delta = 0;
    long last = 0;
    long fps = 0;
    
    public static void main (String[] args)
    {
        new Bildschirm(800,600);
    }
    
    public Bildschirm(int w, int h)
    { 
        this.setPreferredSize(new Dimension(w,h));
        frame = new JFrame("GameDemo");
        frame.setLocation(100,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        
        doInitializations();
        Thread th = new Thread(this);;
        th.start();
    }
    public void doInitializations()
    {
        last = System.nanoTime();
    }
    public void run ()
    {
        while(frame.isVisible())
        {
            computeDelta();
            checkKeys();
            doLogic();
            moveObjects();
            repaint();
            try 
            {
                Thread.sleep(10);
            }
            catch (InterruptedException e){}
        }
    }
    private void computeDelta()
    {
        delta = System.nanoTime() - last;
        last = System.nanoTime();
        fps = ((long) 1e9)/delta;
    }
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.setColor(Color.red);
        g.drawString("FPS: " + Long.toString(fps), 20, 10);
    }
    private void moveObjects()
    {
    
    }
    private void doLogic()
    {
    
    }
    private void checkKeys()
    {
    
    }

}