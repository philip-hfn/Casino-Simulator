import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import javax.imageio.ImageIO;
import java.util.Vector;
import java.util.ListIterator;
public class Bildschirm extends JPanel implements Runnable
{
    private static final long serialVersionUID = 1L;
    JFrame frame;
    
    long delta = 0;
    long last = 0;
    long fps = 0;
    Sprite symbol;
    Vector<Sprite> actors;
    Vector<Sprite> painter;
    BufferedImage background;
    public static void main (String[] args)
    {
        new Bildschirm(1200,800);
    }
    
    public Bildschirm(int w, int h)
    { 
        this.setPreferredSize(new Dimension(w,h));
        frame = new JFrame("Casino Royal");
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
        BufferedImage[] casinoLeuchte = loadPics ("pics/casinoLeuchte.png", 6);
        background = loadPics("pics/background.jpg",1)[0];
        actors = new Vector<Sprite>();
        painter = new Vector<Sprite>();
        symbol = new Sprite(casinoLeuchte, 200, 100, 100, this);
        actors.add(symbol);
    }
    public void run ()
    {
        while(frame.isVisible())
        {
            computeDelta();
            checkKeys();
            doLogic();
            moveObjects();
            cloneVectors();
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
        g.drawImage(background, 0, 0, this);
        g.setColor(Color.red);
        g.drawString("FPS: " + Long.toString(fps), 20, 10);
        if(painter!=null)
        {
            for(ListIterator<Sprite> it = painter.listIterator();it.hasNext();)
            {
                Sprite r = it.next();
                r.drawObjects(g);
            }
        }
    }
    private void moveObjects()
    {
        for(ListIterator<Sprite> it = actors.listIterator();it.hasNext();)
        {
            Sprite r = it.next();
            r.move(delta);
        }
    }
    private void doLogic()
    {
        for(ListIterator<Sprite> it =actors.listIterator();it.hasNext();)
        {
            Sprite r = it.next();
            r.doLogic(delta);
        }
    }
    private void checkKeys()
    {
    
    }
    private BufferedImage[] loadPics(String path, int pics)
    {
        BufferedImage[] anim = new BufferedImage[pics];
        BufferedImage source = null;
        URL pic_url = getClass().getClassLoader().getResource(path);
        
        try 
        {
            source = ImageIO.read(pic_url);
        }
        catch (IOException e) {}
        
        for(int x=0; x<pics; x++)
        {
            anim[x] = source.getSubimage(x*source.getWidth()/pics, 0,source.getWidth()/pics, source.getHeight());
        }
        return anim;
    }
    public void cloneVectors()
    {
        painter = (Vector<Sprite>) actors.clone();
    }
}