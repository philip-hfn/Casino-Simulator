import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
public class CasinoGUI extends JPanel implements Runnable, MouseListener
{
    JFrame frame;
    int screen = 0;
    private JButton rouletteButton;
    private JButton slotButton;
    private JButton backButton;
    private JTextField einsatzFeld;
    private JTextField geradeFeld;
    private JTextField zahlFeld;
    private JTextField rotFeld;
    private JButton spinButton;
    private JLabel ergebnisLabel;
    BufferedImage rouletteImage;
    BufferedImage slotImage;
    BufferedImage backgroundImage;
    Roulette roulette;
    Spieler spieler;
    
        public static void main(String[] args)
    {
    
        try
        {
            UIManager.setLookAndFeel(
            UIManager.getCrossPlatformLookAndFeelClassName()
            );
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        new CasinoGUI(1920,1080);
    }

    public CasinoGUI(int w, int h)
    {
        this.setPreferredSize(new Dimension(w,h));
        setLayout(null);
        frame = new JFrame("Casino");
        frame.setLocation(100,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setResizable(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        spieler = new Spieler();
        roulette = new Roulette(spieler);   // Verbindung zur Logik
        spieler.kontostand = 1000;  // Startgeld
        addMouseListener(this);
        doInitializations();
        Thread th = new Thread(this);
        th.start();
    }

    private void doInitializations()
    {
        rouletteButton = new JButton("Roulette");
        add(rouletteButton);
        styleButton(rouletteButton);

        slotButton = new JButton("Slot-Maschine");
        add(slotButton);
        styleButton(slotButton);

        backButton = new JButton("Back");
        add(backButton);
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 15));

        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);

        backButton.setOpaque(true);
        backButton.setContentAreaFilled(true);

        rouletteButton.addActionListener(e -> 
                {
                    screen = 1;
                    updateComponents();
            });

        slotButton.addActionListener(e -> 
                {
                    screen = 2;
                    updateComponents();
            });

        backButton.addActionListener(e -> 
                {
                    screen = 0;
                    updateComponents();
            });

        einsatzFeld = new JTextField("Einsatz");
        add(einsatzFeld);

        geradeFeld = new JTextField("gerade/ungerade");
        add(geradeFeld);

        rotFeld = new JTextField("rot/schwarz");
        add(rotFeld);

        zahlFeld = new JTextField("10");
        add(zahlFeld);

        //einsatzFeld.addActionListener(e -> einsatzR());

        spinButton = new JButton("SPIN");
        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);
        spinButton.setFont(new Font("Arial", Font.BOLD, 22));

        spinButton.setFocusPainted(false);
        spinButton.setBorderPainted(false);

        spinButton.setOpaque(true);
        spinButton.setContentAreaFilled(true);
        add(spinButton);
        //spinButton.addActionListener(e -> roulette.spieldurchfuehren(einsatzR(), rotR(), geradeR(), zahlR()));


        spinButton.addActionListener(e ->
                {
                    int gewinn = roulette.spieldurchfuehren(einsatzR(), rotR(), geradeR(), zahlR());

                    int zahl = roulette.ergebnis;

                    ergebnisLabel.setText("Zahl: " + zahl + " | Gewinn: " + gewinn + "$");
            });

        ergebnisLabel = new JLabel("Ergebnis: -");
        ergebnisLabel.setFont(new Font("Arial", Font.BOLD, 20));
        ergebnisLabel.setForeground(Color.WHITE);
        add(ergebnisLabel);

        rouletteImage = loadImage("pics/roulette.png");
        slotImage = loadImage("pics/slot.png");
        backgroundImage = loadImage("pics/background.png");

        updateComponents();
    }
    
    private void updateLayoutPositions()
    {
        int w = getWidth();
        int h = getHeight();

        rouletteButton.setBounds(w / 2 - 75, h / 2 - 120, 150, 60);

        slotButton.setBounds(w / 2 - 110, h / 2, 220, 60);

        backButton.setBounds(40, 35, 80, 30);

        einsatzFeld.setBounds(w / 2 + 250, h / 2 - 60, 100, 30);

        geradeFeld.setBounds(w / 2 + 250, h / 2 - 10, 100, 30);

        rotFeld.setBounds(w / 2 + 250, h / 2 + 40, 100, 30);

        zahlFeld.setBounds(w / 2 + 250, h / 2 + 90, 100, 30);

        spinButton.setBounds(w / 2 + 400, h / 2, 150, 60);

        ergebnisLabel.setBounds(50, h / 2 + 80, 400, 40);
    }

    private BufferedImage loadImage(String path)
    {
        BufferedImage image = null;
        URL imageURL = getClass().getClassLoader().getResource(path);
        try
        {
            image = ImageIO.read(imageURL);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return image;
    }

    public void run()
    {
        while(frame.isVisible())
        {
            repaint();

            try
            {
                Thread.sleep(10);
            }
            catch(Exception e){}
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        updateLayoutPositions();


        if(screen == 0)
        {
            drawStartscreen(g);
        }

        if(screen == 1)
        {
            drawRoulette(g);
        }

        if(screen == 2)
        {
            drawSlot(g);
        }
    }

    private void drawStartscreen(Graphics g)
    {
        g.drawImage(backgroundImage,0,0,getWidth(),getHeight(),this);

        // g.setColor(Color.WHITE);
        // g.setFont(new Font("Arial",Font.BOLD,30));

        // g.drawRect(rouletteButton.x,rouletteButton.y,
        // rouletteButton.width,rouletteButton.height);
        // g.drawString("Roulette",530,350);

        // g.drawRect(slotButton.x,slotButton.y,
        // slotButton.width,slotButton.height);
        // g.drawString("Slot",570,470);
    }

    private void drawRoulette(Graphics g)
    {
        g.drawImage(rouletteImage,0,0,getWidth(),getHeight(),this);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD,30));
        String konto = "Kontostand: " + spieler.kontostand + " $";
        g.drawString(konto,200,60);
        g.setFont(new Font("Arial",Font.BOLD,15));
        String einsatz = "Einsatz: ";
        g.drawString(einsatz,855,500);
        String gerade = "Gerade/ungerade: ";
        g.drawString(gerade,785,550);
        String rot = "Rot/Schwarz: ";
        g.drawString(rot,815,600);
        String zahl = "Zahl: ";
        g.drawString(zahl,880,650);
        // // g.drawRect(backButton.x,backButton.y,
        // // backButton.width,backButton.height);
        // g.drawString("Back",50,55);
    }

    private void drawSlot(Graphics g)
    {
        g.drawImage(slotImage,0,0,getWidth(),getHeight(),this);
        // g.setColor(Color.WHITE);
        // g.setFont(new Font("Arial",Font.BOLD,30));
        // g.drawRect(backButton.x,backButton.y,
        // backButton.width,backButton.height);
        // g.drawString("Back",50,55);
    }

    private void styleButton(JButton button)
    {
        button.setBackground(new Color(217, 131, 53));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 22));

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }
    
    public void mousePressed(MouseEvent e)
    {
        
    }

    private void updateComponents()
    {
        if(screen == 0)//Hub
        {
            einsatzFeld.setVisible(false);
            rotFeld.setVisible(false);
            geradeFeld.setVisible(false);
            zahlFeld.setVisible(false);
            spinButton.setVisible(false);
            rouletteButton.setVisible(true);
            slotButton.setVisible(true);
            backButton.setVisible(false);
            ergebnisLabel.setVisible(false);
        }
        else if(screen == 1)//Roulette
        {
            einsatzFeld.setVisible(true);
            rotFeld.setVisible(true);
            geradeFeld.setVisible(true);
            zahlFeld.setVisible(true);
            spinButton.setVisible(true);
            rouletteButton.setVisible(false);
            slotButton.setVisible(false);
            backButton.setVisible(true);
            ergebnisLabel.setVisible(true);
        }
        else if(screen == 2)//Slot
        {
            einsatzFeld.setVisible(false);
            rotFeld.setVisible(false);
            geradeFeld.setVisible(false);
            zahlFeld.setVisible(false);
            spinButton.setVisible(false);
            rouletteButton.setVisible(false);
            slotButton.setVisible(false);
            backButton.setVisible(true);
            ergebnisLabel.setVisible(false);
        }
    }

    public void mouseClicked(MouseEvent e){}

    public void mouseReleased(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseExited(MouseEvent e){}

    private int einsatzR()//Einsatz Roulette
    {
        int einsatz = Integer.parseInt(einsatzFeld.getText());
        return einsatz;
    }

    private int zahlR()//Einsatz Roulette
    {
        int zahl = Integer.parseInt(zahlFeld.getText());
        return zahl;
    }

    private String geradeR()//Einsatz Roulette
    {
        String gerade = geradeFeld.getText();
        return gerade;
    }

    private String  rotR()//Einsatz Roulette
    {
        String rot = rotFeld.getText();
        return rot;
    }
}