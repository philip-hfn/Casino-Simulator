import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;
import javax.swing.ImageIcon;
public class CasinoGUI extends JPanel implements Runnable, MouseListener
{
    JFrame frame;
    int screen = 0;

    // Hauptmenü
    private JButton rouletteButton;
    private JButton slotButton;
    private JButton backButton;
    private JLabel kontostandLabel;
    private JLabel gifLabel;

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
    Spieler spieler;

    // Slot
    private SlotReel slotReel1;
    private SlotReel slotReel2;
    private SlotReel slotReel3;
    private JLabel gewinnLabel;
    private JButton drehenButton;
    private JTextField slotEinsatzFeld;
    private JLabel kontoLabel;
    private JLabel slotEinsatzLabel;
    BufferedImage slotImage;
    BufferedImage[] slotBilder;
    Slot slot;

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName()
            );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
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
        frame.setResizable(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        spieler = new Spieler();
        roulette = new Roulette(spieler);
        spieler.kontostand = 1000;

        slot = new Slot(spieler);
        slot.kontostand = 1000;

        addMouseListener(this);
        doInitializations();
        Thread th = new Thread(this);
        th.start();
    }

    // ─── Innere Klasse für die Slot-Räder ────────────────────────────────────
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

    // ─── Style-Hilfsmethode (aus Version 1) ──────────────────────────────────
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

    // ─── Dynamisches Layout (aus Version 1) ──────────────────────────────────
    private void updateLayoutPositions()
    {
        int w = getWidth();
        int h = getHeight();

        // Hauptmenü-Buttons
        rouletteButton.setBounds(w / 2 - 90,  h / 2 - 60, 180, 65);
        slotButton.setBounds    (w / 2 - 130, h / 2 + 40, 260, 65);
        kontostandLabel.setBounds(30, 25, 300, 40);
        // GIF unten am Rand
        gifLabel.setBounds(w / 2 - 100, h - 220, 200, 200);
        

        // Back-Button
        backButton.setBounds(40, 35, 80, 30);

        // ── Roulette-Formular ─────────────────────────────────────────────────
        int formX = (int)(w * 0.58);
        int formY = (int)(h * 0.68);

        einsatzFeld.setBounds(formX + 130, formY,       150, 35);
        geradeFeld .setBounds(formX + 130, formY + 60,  150, 35);
        rotFeld    .setBounds(formX + 130, formY + 120, 150, 35);
        zahlFeld   .setBounds(formX + 130, formY + 180, 150, 35);

        spinButton.setBounds(formX + 330, formY + 65, 170, 70);

        ergebnisLabel.setBounds(w - 740, h - 90, 500, 40);

        // ── Slot-Komponenten ─────────────────────────────────────────────────
        // Roulette-Räder: zentriert, etwas über Bildmitte
        int reelY  = (int)(h * 0.35);
        int reelW  = (int)(w * 0.08);
        int reelH  = reelW;
        int gapX   = (int)(w * 0.01);
        int totalW = 3 * reelW + 2 * gapX;
        int startX = (w - totalW) / 2;

        slotReel1.setBounds(startX,              reelY, reelW, reelH);
        slotReel2.setBounds(startX + reelW + gapX,       reelY, reelW, reelH);
        slotReel3.setBounds(startX + 2 * (reelW + gapX), reelY, reelW, reelH);

        // Kontostand-Label oben mittig
        kontoLabel.setBounds(w / 2 - 200, (int)(h * 0.09), 400, 40);

        // Gewinn-Label unter den Rädern
        gewinnLabel.setBounds(w / 2 - 200, reelY + reelH + 20, 400, 40);

        // Einsatz-Label und -Feld
        int inputY = reelY + reelH + 80;
        slotEinsatzLabel.setBounds(w / 2 - 120, inputY, 80,  35);
        slotEinsatzFeld .setBounds(w / 2 - 30,  inputY, 100, 35);

        // Drehen-Button
        drehenButton.setBounds(w / 2 - 100, inputY + 55, 200, 55);
    }

    // ─── Initialisierung aller Komponenten ───────────────────────────────────
    private void doInitializations()
    {
        // Bilder laden
        rouletteImage   = loadImage("pics/roulette.png");
        slotImage       = loadImage("pics/slot.png");
        backgroundImage = loadImage("pics/background.png");

        slotBilder = new BufferedImage[10];
        for (int i = 1; i <= 9; i++)
        {
            slotBilder[i] = loadImage("pics/slot" + i + ".png");
        }

        // === Hauptmenü ===
        rouletteButton = new JButton("Roulette");
        add(rouletteButton);
        styleButton(rouletteButton);
        
        ImageIcon gifIcon = new ImageIcon(getClass().getClassLoader().getResource("pics/Character.gif"));
        gifLabel = new JLabel(gifIcon);  // <-- kein JLabel davor
        add(gifLabel);

        slotButton = new JButton("Slot-Maschine");
        add(slotButton);
        styleButton(slotButton);

        backButton = new JButton("Back");
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 15));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        backButton.setOpaque(true);
        backButton.setContentAreaFilled(true);
        add(backButton);

        rouletteButton.addActionListener(e -> { screen = 1; updateComponents(); });
        slotButton    .addActionListener(e -> { screen = 2; updateComponents(); });
        backButton    .addActionListener(e -> { screen = 0; updateComponents(); });

        // === Roulette ===
        einsatzFeld = new JTextField("Einsatz");
        add(einsatzFeld);

        geradeFeld = new JTextField("gerade/ungerade");
        add(geradeFeld);

        rotFeld = new JTextField("rot/schwarz");
        add(rotFeld);

        zahlFeld = new JTextField("10");
        add(zahlFeld);

        spinButton = new JButton("SPIN");
        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);
        spinButton.setFont(new Font("Arial", Font.BOLD, 22));
        spinButton.setFocusPainted(false);
        spinButton.setBorderPainted(false);
        spinButton.setOpaque(true);
        spinButton.setContentAreaFilled(true);
        add(spinButton);

        spinButton.addActionListener(e -> {
            int gewinn = roulette.spieldurchfuehren(einsatzR(), rotR(), geradeR(), zahlR());
            int zahl   = roulette.ergebnis;
            ergebnisLabel.setText("Zahl: " + zahl + " | Gewinn: " + gewinn + "$");
        });

        ergebnisLabel = new JLabel("Ergebnis: -");
        ergebnisLabel.setFont(new Font("Arial", Font.BOLD, 24));
        ergebnisLabel.setForeground(Color.WHITE);
        add(ergebnisLabel);

        // === Slot ===
        slotReel1 = new SlotReel();
        slotReel1.setImage(slotBilder[1]);
        add(slotReel1);

        slotReel2 = new SlotReel();
        slotReel2.setImage(slotBilder[1]);
        add(slotReel2);

        slotReel3 = new SlotReel();
        slotReel3.setImage(slotBilder[1]);
        add(slotReel3);

        gewinnLabel = new JLabel("Drücke auf Drehen!", SwingConstants.CENTER);
        gewinnLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gewinnLabel.setForeground(Color.WHITE);
        add(gewinnLabel);

        kontoLabel = new JLabel("Kontostand: " + spieler.getKontostand() + "€");
        kontoLabel.setFont(new Font("Arial", Font.BOLD, 25));
        kontoLabel.setForeground(Color.WHITE);
        add(kontoLabel);
        
        kontostandLabel = new JLabel("Kontostand: " + spieler.getKontostand() + "€");
        kontostandLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        kontostandLabel.setForeground(Color.WHITE);
        kontostandLabel.setBackground(new Color(217, 131, 53));
        kontostandLabel.setOpaque(true);
        add(kontostandLabel);

        slotEinsatzLabel = new JLabel("Einsatz:");
        slotEinsatzLabel.setFont(new Font("Arial", Font.BOLD, 18));
        slotEinsatzLabel.setForeground(Color.WHITE);
        add(slotEinsatzLabel);

        slotEinsatzFeld = new JTextField("10");
        slotEinsatzFeld.setFont(new Font("Arial", Font.PLAIN, 18));
        add(slotEinsatzFeld);

        drehenButton = new JButton("Drehen");
        add(drehenButton);
        styleButton(drehenButton);

        drehenButton.addActionListener(e -> {
            int einsatz = Integer.parseInt(slotEinsatzFeld.getText());
            slot.spielen(einsatz);

            int ziel1 = slot.getSlot1();
            int ziel2 = slot.getSlot2();
            int ziel3 = slot.getSlot3();

            drehenButton.setEnabled(false);
            gewinnLabel.setText("...");

            starteAnimation(slotReel1, ziel1, 0);
            starteAnimation(slotReel2, ziel2, 400);
            starteAnimation(slotReel3, ziel3, 800);

            int gesamtZeit = 800 + 30 * 80 + 500;
            Timer ergebnisTimer = new Timer(gesamtZeit, ev -> {
                kontoLabel.setText("Kontostand: " + spieler.getKontostand() + "€");
                if      (slot.super7IchKaufDasKasino()) gewinnLabel.setText("🎰 JACKPOT 777 !!!");
                else if (slot.hauptGewinn())             gewinnLabel.setText("Großer Gewinn!");
                else if (slot.kleinerGewinn())           gewinnLabel.setText("Kleiner Gewinn!");
                else                                     gewinnLabel.setText("Leider verloren!");
                drehenButton.setEnabled(true);
            });
            ergebnisTimer.setRepeats(false);
            ergebnisTimer.start();
        });

        updateComponents();
    }

    // ─── Slot-Animations-Methode (aus Version 2) ─────────────────────────────
    private void starteAnimation(SlotReel reel, int zielZahl, int verzoegerung)
    {
        int[] counter       = {0};
        int[] geschwindigkeit = {50};

        Timer timer = new Timer(geschwindigkeit[0], null);
        timer.addActionListener(e -> {
            int zufallsZahl = (int)(Math.random() * 9 + 1);
            reel.setImage(slotBilder[zufallsZahl]);
            counter[0]++;

            if (counter[0] > 20)
            {
                geschwindigkeit[0] += 15;
                timer.setDelay(geschwindigkeit[0]);
            }

            if (counter[0] > 30)
            {
                reel.setImage(slotBilder[zielZahl]);
                timer.stop();
            }
        });

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
        updateLayoutPositions();   // dynamisches Layout bei jedem Frame
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
        int w = getWidth();
        int h = getHeight();
        g.drawImage(rouletteImage, 0, 0, w, h, this);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        g.drawString("Kontostand: " + spieler.kontostand + " $", 200, 60);

        g.setFont(new Font("Arial", Font.BOLD, 15));
        int formX = (int)(w * 0.58);
        int formY = (int)(h * 0.68);
        g.drawString("Einsatz: ",          formX,       formY + 22);
        g.drawString("Gerade/ungerade: ",  formX - 70,  formY + 82);
        g.drawString("Rot/Schwarz: ",      formX - 40,  formY + 142);
        g.drawString("Zahl: ",             formX + 65,  formY + 202);
    }

    private void drawSlot(Graphics g)
    {
        g.drawImage(slotImage, 0, 0, getWidth(), getHeight(), this);
    }

    private void updateComponents()
    {
        boolean isHub     = screen == 0;
        boolean isRoulette = screen == 1;
        boolean isSlot    = screen == 2;

        rouletteButton.setVisible(isHub);
        slotButton    .setVisible(isHub);
        kontostandLabel.setVisible(isHub);
        backButton    .setVisible(!isHub);
        // GIF unten am Rand   
        gifLabel.setVisible(isHub);
        
        // Roulette
        einsatzFeld  .setVisible(isRoulette);
        rotFeld      .setVisible(isRoulette);
        geradeFeld   .setVisible(isRoulette);
        zahlFeld     .setVisible(isRoulette);
        spinButton   .setVisible(isRoulette);
        ergebnisLabel.setVisible(isRoulette);

        // Slot
        slotReel1       .setVisible(isSlot);
        slotReel2       .setVisible(isSlot);
        slotReel3       .setVisible(isSlot);
        gewinnLabel     .setVisible(isSlot);
        drehenButton    .setVisible(isSlot);
        slotEinsatzFeld .setVisible(isSlot);
        slotEinsatzLabel.setVisible(isSlot);
        kontoLabel      .setVisible(isSlot);
    }

    public void mousePressed(MouseEvent e)  {}
    public void mouseClicked(MouseEvent e)  {}
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e)  {}
    public void mouseExited(MouseEvent e)   {}

    private int    einsatzR() { return Integer.parseInt(einsatzFeld.getText()); }
    private int    zahlR()    { return Integer.parseInt(zahlFeld.getText()); }
    private String geradeR()  { return geradeFeld.getText(); }
    private String rotR()     { return rotFeld.getText(); }
}