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
    BufferedImage backgroundImage;
    Roulette roulette;

    private JLabel slot1;
    private JLabel slot2;
    private JLabel slot3;
    private JLabel gewinnLabel;
    private JButton drehenButton;
    private JTextField slotEinsatzFeld;
    private JLabel kontoLabel;
    BufferedImage slotImage;
    Slot slot;

    public static void main(String[] args)
    {
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
        frame.pack();
        frame.setVisible(true);
        roulette = new Roulette();   // Verbindung zur Logik
        roulette.kontostand = 1000;  // Startgeld
        slot = new Slot();   // Verbindung zur Logik
        slot.kontostand = 1000;  // Startgeld
        addMouseListener(this);
        doInitializations();
        Thread th = new Thread(this);
        th.start();
    }

    private void doInitializations()
    {
        //setLayout(new BorderLayout());  möglichkeit für positionierung in unterschiedlichen fenstern

        rouletteButton = new JButton("Roulette");
        rouletteButton.setBounds(570,390,150,60);
        add(rouletteButton);
        rouletteButton.setBackground(new Color(217, 131, 53));
        rouletteButton.setForeground(Color.WHITE);
        rouletteButton.setFont(new Font("Arial", Font.BOLD, 22));
        rouletteButton.setFocusPainted(false);
        rouletteButton.setBorderPainted(false);

        slotButton = new JButton("Slot-Maschine");
        slotButton.setBounds(535,490,220,60);
        add(slotButton);
        slotButton.setBackground(new Color(217, 131, 53));
        slotButton.setForeground(Color.WHITE);
        slotButton.setFont(new Font("Arial", Font.BOLD, 22));
        slotButton.setFocusPainted(false);
        slotButton.setBorderPainted(false);

        backButton = new JButton("Back");
        backButton.setBounds(40,35,80,30);
        add(backButton);
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 15));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);

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
        einsatzFeld.setBounds(930, 480, 100, 30);
        add(einsatzFeld);

        geradeFeld = new JTextField("gerade/ungerade");
        geradeFeld.setBounds(930, 530, 100, 30);
        add(geradeFeld);

        rotFeld = new JTextField("rot/schwarz");
        rotFeld.setBounds(930, 580, 100, 30);
        add(rotFeld);

        zahlFeld = new JTextField("10");
        zahlFeld.setBounds(930, 630, 100, 30);
        add(zahlFeld);

        //einsatzFeld.addActionListener(e -> einsatzR());

        spinButton = new JButton("SPIN");
        spinButton.setBounds(1080, 540, 150, 60);
        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);
        spinButton.setFont(new Font("Arial", Font.BOLD, 22));
        spinButton.setFocusPainted(false);
        spinButton.setBorderPainted(false);
        add(spinButton);
        //spinButton.addActionListener(e -> roulette.spieldurchfuehren(einsatzR(), rotR(), geradeR(), zahlR()));

        spinButton.addActionListener(e ->
                {
                    int gewinn = roulette.spieldurchfuehren(einsatzR(), rotR(), geradeR(), zahlR());

                    int zahl = roulette.ergebnis;

                    ergebnisLabel.setText("Zahl: " + zahl + " | Gewinn: " + gewinn + "$");
            });

        ergebnisLabel = new JLabel("Ergebnis: -");
        ergebnisLabel.setBounds(50, 550, 400, 40);
        ergebnisLabel.setFont(new Font("Arial", Font.BOLD, 20));
        ergebnisLabel.setForeground(Color.WHITE);
        add(ergebnisLabel);

        rouletteImage = loadImage("pics/roulette.png");
        slotImage = loadImage("pics/slot.png");
        backgroundImage = loadImage("pics/background.png");

        JPanel zahlenPanel = new JPanel();
        slot1 = new JLabel("0");
        slot2 = new JLabel("0");
        slot3 = new JLabel("0");

        Font schrift = new Font("Arial", Font.BOLD, 50);

        slot1.setFont(schrift);
        slot2.setFont(schrift);
        slot3.setFont(schrift);

        zahlenPanel.add(slot1);
        zahlenPanel.add(slot2);
        zahlenPanel.add(slot3);

        // Gewinnanzeige
        gewinnLabel = new JLabel("Drücke auf Drehen!", SwingConstants.CENTER);

        // Button
        drehenButton = new JButton("Drehen");
        drehenButton.addActionListener(e -> 
                {
                    int einsatz = Integer.parseInt(einsatzFeld.getText());

                    slot.gewinnBerechnen(einsatz);

                    // Zahlen anzeigen
                    slot1.setText("" + slot.getSlot1());
                    slot2.setText("" + slot.getSlot2());
                    slot3.setText("" + slot.getSlot3());
                    kontoLabel.setText("Kontostand: " + slot.getKontostand() + "€");

                    // Gewinn prüfen
                    if(slot.super7IchKaufDasKasino())
                    {
                        gewinnLabel.setText("🎰 JACKPOT 777 !!!");
                    }
                    else if(slot.hauptGewinn())
                    {
                        gewinnLabel.setText("Großer Gewinn!");
                    }
                    else if(slot.kleinerGewinn())
                    {
                        gewinnLabel.setText("Kleiner Gewinn!");
                    }
                    else
                    {
                        gewinnLabel.setText("Leider verloren!");
                    }
            });

        //Textfeld
        einsatzFeld = new JTextField("10", 5);
        JPanel einsatzPanel = new JPanel();

        //Kontostand
        kontoLabel = new JLabel("Kontostand: " + slot.getKontostand() + "€");

        einsatzPanel.add(new JLabel("Einsatz: "));
        einsatzPanel.add(einsatzFeld);

        // Alles ins Fenster
        add(einsatzPanel, BorderLayout.WEST);
        add(gewinnLabel, BorderLayout.NORTH);
        add(zahlenPanel, BorderLayout.CENTER);
        add(drehenButton, BorderLayout.SOUTH);
        add(kontoLabel, BorderLayout.NORTH);

        setVisible(true);
        updateComponents();
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
        String konto = "Kontostand: " + roulette.kontostand + " $";
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

    public void mousePressed(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();

        if(screen == 0)
        {
            if(rouletteButton.contains(x,y))
            {
                screen = 1;
                updateComponents();
            }

            if(slotButton.contains(x,y))
            {
                screen = 2;
                updateComponents();
            }
        }
        else
        {
            if(backButton.contains(x,y))
            {
                screen = 0;
                updateComponents();
            }
        }
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
            slot1.setVisible(false);
            slot2.setVisible(false);
            slot3.setVisible(false);
            gewinnLabel.setVisible(false);
            drehenButton.setVisible(false);
            einsatzFeld.setVisible(false);
            kontoLabel.setVisible(false);
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
            slot1.setVisible(true);
            slot2.setVisible(true);
            slot3.setVisible(true);
            gewinnLabel.setVisible(true);
            drehenButton.setVisible(true);
            einsatzFeld.setVisible(true);
            kontoLabel.setVisible(true);
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