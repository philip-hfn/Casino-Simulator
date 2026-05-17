import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;

public class CasinoGUI extends JPanel implements Runnable, MouseListener
{
    JFrame frame;
    int screen = 0;

    // Hauptmenü
    private JButton rouletteButton;
    private JButton slotButton;
    private JButton backButton;

    // Roulette
    private JTextField einsatzFeld;
    private JTextField geradeFeld;
    private JTextField zahlFeld;
    private JTextField rotFeld;
    private JButton spinButton;
    private JLabel ergebnisLabel;
    BufferedImage rouletteImage;
    BufferedImage backgroundImage;
    Roulette roulette;

    // Slot
    private SlotReel slotReel1;
    private SlotReel slotReel2;
    private SlotReel slotReel3;
    private JLabel gewinnLabel;
    private JButton drehenButton;
    private JTextField slotEinsatzFeld;
    private JLabel kontoLabel;
    BufferedImage slotImage;
    BufferedImage[] slotBilder;
    Slot slot;

    public static void main(String[] args)
    {
        new CasinoGUI(1920, 1080);
    }

    public CasinoGUI(int w, int h)
    {
        this.setPreferredSize(new Dimension(w, h));
        setLayout(null);
        frame = new JFrame("Casino");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setVisible(true);
        roulette = new Roulette();
        roulette.kontostand = 1000;
        slot = new Slot();
        slot.kontostand = 1000;
        addMouseListener(this);
        doInitializations();
        Thread th = new Thread(this);
        th.start();
    }

    // Innere Klasse für die Slot-Räder
    class SlotReel extends JPanel
    {
        private BufferedImage currentImage;

        public SlotReel()
        {
            setOpaque(false);
        }

        public void setImage(BufferedImage img)
        {
            this.currentImage = img;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (currentImage != null)
            {
                g.drawImage(currentImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    private void doInitializations()
    {
        // === BILDER LADEN ===
        rouletteImage  = loadImage("pics/roulette.png");
        slotImage      = loadImage("pics/slot.png");
        backgroundImage = loadImage("pics/background.png");

        slotBilder = new BufferedImage[10];
        for (int i = 1; i <= 9; i++)
        {
            slotBilder[i] = loadImage("pics/slot" + i + ".png");
        }

        // === HAUPTMENÜ BUTTONS ===
        rouletteButton = new JButton("Roulette");
        rouletteButton.setBounds(570, 390, 150, 60);
        rouletteButton.setBackground(new Color(217, 131, 53));
        rouletteButton.setForeground(Color.WHITE);
        rouletteButton.setFont(new Font("Arial", Font.BOLD, 22));
        rouletteButton.setFocusPainted(false);
        rouletteButton.setBorderPainted(false);
        add(rouletteButton);

        slotButton = new JButton("Slot-Maschine");
        slotButton.setBounds(535, 490, 220, 60);
        slotButton.setBackground(new Color(217, 131, 53));
        slotButton.setForeground(Color.WHITE);
        slotButton.setFont(new Font("Arial", Font.BOLD, 22));
        slotButton.setFocusPainted(false);
        slotButton.setBorderPainted(false);
        add(slotButton);

        backButton = new JButton("Back");
        backButton.setBounds(40, 35, 80, 30);
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 15));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        add(backButton);

        rouletteButton.addActionListener(e -> { screen = 1; updateComponents(); });
        slotButton.addActionListener(e ->     { screen = 2; updateComponents(); });
        backButton.addActionListener(e ->     { screen = 0; updateComponents(); });

        // === ROULETTE KOMPONENTEN ===
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

        spinButton = new JButton("SPIN");
        spinButton.setBounds(1080, 540, 150, 60);
        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);
        spinButton.setFont(new Font("Arial", Font.BOLD, 22));
        spinButton.setFocusPainted(false);
        spinButton.setBorderPainted(false);
        add(spinButton);

        spinButton.addActionListener(e -> {
            int gewinn = roulette.spieldurchfuehren(einsatzR(), rotR(), geradeR(), zahlR());
            int zahl = roulette.ergebnis;
            ergebnisLabel.setText("Zahl: " + zahl + " | Gewinn: " + gewinn + "$");
        });

        ergebnisLabel = new JLabel("Ergebnis: -");
        ergebnisLabel.setBounds(50, 550, 400, 40);
        ergebnisLabel.setFont(new Font("Arial", Font.BOLD, 20));
        ergebnisLabel.setForeground(Color.WHITE);
        add(ergebnisLabel);

        // === SLOT KOMPONENTEN ===
        slotReel1 = new SlotReel();
        slotReel1.setBounds(620, 380, 150, 150);
        slotReel1.setImage(slotBilder[1]);
        add(slotReel1);

        slotReel2 = new SlotReel();
        slotReel2.setBounds(800, 380, 150, 150);
        slotReel2.setImage(slotBilder[1]);
        add(slotReel2);

        slotReel3 = new SlotReel();
        slotReel3.setBounds(980, 380, 150, 150);
        slotReel3.setImage(slotBilder[1]);
        add(slotReel3);

        gewinnLabel = new JLabel("Drücke auf Drehen!", SwingConstants.CENTER);
        gewinnLabel.setBounds(600, 560, 400, 40);
        gewinnLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gewinnLabel.setForeground(Color.WHITE);
        add(gewinnLabel);

        kontoLabel = new JLabel("Kontostand: " + slot.getKontostand() + "€");
        kontoLabel.setBounds(600, 100, 400, 40);
        kontoLabel.setFont(new Font("Arial", Font.BOLD, 25));
        kontoLabel.setForeground(Color.WHITE);
        add(kontoLabel);

        JLabel slotEinsatzLabel = new JLabel("Einsatz:");
        slotEinsatzLabel.setBounds(660, 620, 80, 35);
        slotEinsatzLabel.setFont(new Font("Arial", Font.BOLD, 18));
        slotEinsatzLabel.setForeground(Color.WHITE);
        add(slotEinsatzLabel);

        slotEinsatzFeld = new JTextField("10");
        slotEinsatzFeld.setBounds(750, 620, 100, 35);
        slotEinsatzFeld.setFont(new Font("Arial", Font.PLAIN, 18));
        add(slotEinsatzFeld);

        drehenButton = new JButton("Drehen");
        drehenButton.setBounds(700, 680, 200, 55);
        drehenButton.setBackground(new Color(217, 131, 53));
        drehenButton.setForeground(Color.WHITE);
        drehenButton.setFont(new Font("Arial", Font.BOLD, 22));
        drehenButton.setFocusPainted(false);
        drehenButton.setBorderPainted(false);
        add(drehenButton);

        drehenButton.addActionListener(e -> {
            int einsatz = Integer.parseInt(slotEinsatzFeld.getText());
            slot.gewinnBerechnen(einsatz);

            int ziel1 = slot.getSlot1();
            int ziel2 = slot.getSlot2();
            int ziel3 = slot.getSlot3();

            drehenButton.setEnabled(false);
            gewinnLabel.setText("...");

            starteAnimation(slotReel1, ziel1, 0);
            starteAnimation(slotReel2, ziel2, 400);
            starteAnimation(slotReel3, ziel3, 800);

            // Ergebnis anzeigen nachdem alle 3 Räder gestoppt haben
            // 800ms Versatz + 30 Runden * 80ms max Delay + 500ms Puffer
            int gesamtZeit = 800 + 30 * 80 + 500;
            Timer ergebnisTimer = new Timer(gesamtZeit, ev -> {
                kontoLabel.setText("Kontostand: " + slot.getKontostand() + "€");
                if (slot.super7IchKaufDasKasino())  gewinnLabel.setText("🎰 JACKPOT 777 !!!");
                else if (slot.hauptGewinn())         gewinnLabel.setText("Großer Gewinn!");
                else if (slot.kleinerGewinn())       gewinnLabel.setText("Kleiner Gewinn!");
                else                                 gewinnLabel.setText("Leider verloren!");
                drehenButton.setEnabled(true);
            });
            ergebnisTimer.setRepeats(false);
            ergebnisTimer.start();
        });

        setVisible(true);
        updateComponents();
    }

    private void starteAnimation(SlotReel reel, int zielZahl, int verzoegerung)
    {
        int[] counter      = {0};
        int[] geschwindigkeit = {50};

        Timer timer = new Timer(geschwindigkeit[0], null);
        timer.addActionListener(e -> {
            // Zufälliges Bild während der Animation
            int zufallsZahl = (int)(Math.random() * 9 + 1);
            reel.setImage(slotBilder[zufallsZahl]);
            counter[0]++;

            // Nach 20 Runden langsamer werden
            if (counter[0] > 20)
            {
                geschwindigkeit[0] += 15;
                timer.setDelay(geschwindigkeit[0]);
            }

            // Nach 30 Runden auf Zielbild stoppen
            if (counter[0] > 30)
            {
                reel.setImage(slotBilder[zielZahl]);
                timer.stop();
            }
        });

        // Versetzter Start für jedes Rad
        Timer startTimer = new Timer(verzoegerung, e -> timer.start());
        startTimer.setRepeats(false);
        startTimer.start();
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
        while (frame.isVisible())
        {
            repaint();
            try { Thread.sleep(10); }
            catch (Exception e) {}
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        if (screen == 0) drawStartscreen(g);
        if (screen == 1) drawRoulette(g);
        if (screen == 2) drawSlot(g);
    }

    private void drawStartscreen(Graphics g)
    {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void drawRoulette(Graphics g)
    {
        g.drawImage(rouletteImage, 0, 0, getWidth(), getHeight(), this);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Kontostand: " + roulette.kontostand + " $", 200, 60);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Einsatz: ", 855, 500);
        g.drawString("Gerade/ungerade: ", 785, 550);
        g.drawString("Rot/Schwarz: ", 815, 600);
        g.drawString("Zahl: ", 880, 650);
    }

    private void drawSlot(Graphics g)
    {
        g.drawImage(slotImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void updateComponents()
    {
        if (screen == 0) // Hauptmenü
        {
            rouletteButton.setVisible(true);
            slotButton.setVisible(true);
            backButton.setVisible(false);
            einsatzFeld.setVisible(false);
            rotFeld.setVisible(false);
            geradeFeld.setVisible(false);
            zahlFeld.setVisible(false);
            spinButton.setVisible(false);
            ergebnisLabel.setVisible(false);
            slotReel1.setVisible(false);
            slotReel2.setVisible(false);
            slotReel3.setVisible(false);
            gewinnLabel.setVisible(false);
            drehenButton.setVisible(false);
            slotEinsatzFeld.setVisible(false);
            kontoLabel.setVisible(false);
        }
        else if (screen == 1) // Roulette
        {
            rouletteButton.setVisible(false);
            slotButton.setVisible(false);
            backButton.setVisible(true);
            einsatzFeld.setVisible(true);
            rotFeld.setVisible(true);
            geradeFeld.setVisible(true);
            zahlFeld.setVisible(true);
            spinButton.setVisible(true);
            ergebnisLabel.setVisible(true);
            slotReel1.setVisible(false);
            slotReel2.setVisible(false);
            slotReel3.setVisible(false);
            gewinnLabel.setVisible(false);
            drehenButton.setVisible(false);
            slotEinsatzFeld.setVisible(false);
            kontoLabel.setVisible(false);
        }
        else if (screen == 2) // Slot
        {
            rouletteButton.setVisible(false);
            slotButton.setVisible(false);
            backButton.setVisible(true);
            einsatzFeld.setVisible(false);
            rotFeld.setVisible(false);
            geradeFeld.setVisible(false);
            zahlFeld.setVisible(false);
            spinButton.setVisible(false);
            ergebnisLabel.setVisible(false);
            slotReel1.setVisible(true);
            slotReel2.setVisible(true);
            slotReel3.setVisible(true);
            gewinnLabel.setVisible(true);
            drehenButton.setVisible(true);
            slotEinsatzFeld.setVisible(true);
            kontoLabel.setVisible(true);
        }
    }

    public void mousePressed(MouseEvent e)
    {
        int x = e.getX();
        int y = e.getY();
        if (screen == 0)
        {
            if (rouletteButton.contains(x, y)) { screen = 1; updateComponents(); }
            if (slotButton.contains(x, y))     { screen = 2; updateComponents(); }
        }
        else
        {
            if (backButton.contains(x, y)) { screen = 0; updateComponents(); }
        }
    }

    public void mouseClicked(MouseEvent e)  {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e)  {}
    public void mouseExited(MouseEvent e)   {}

    private int    einsatzR() { return Integer.parseInt(einsatzFeld.getText()); }
    private int    zahlR()    { return Integer.parseInt(zahlFeld.getText()); }
    private String geradeR()  { return geradeFeld.getText(); }
    private String rotR()     { return rotFeld.getText(); }
}