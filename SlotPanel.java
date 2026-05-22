import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Slot-Maschinen-Panel mit drei Walzen und Animation.
 */
public class SlotPanel extends CasinoGUI
{
    // ── Inner class: Walze ────────────────────────────────────────────────
    class SlotReel extends JPanel
    {
        private BufferedImage currentImage;

        public SlotReel() { setOpaque(false); }

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
                g.drawImage(currentImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // ── Komponenten ───────────────────────────────────────────────────────
    private SlotReel   slotReel1;
    private SlotReel   slotReel2;
    private SlotReel   slotReel3;
    private JLabel     gewinnLabel;
    private JButton    drehenButton;
    private JTextField slotEinsatzFeld;
    private JLabel     slotEinsatzLabel;
    private JLabel     kontoLabel;
    private JButton    backButton;

    private BufferedImage   slotImage;
    private BufferedImage[] slotBilder;
    private Slot            slot;

    private HubPanel.ScreenSwitcher screenSwitcher;

    private BuffManager buffManager;

    public SlotPanel(Spieler spieler, BuffManager buffManager, HubPanel.ScreenSwitcher screenSwitcher)
    {
        this.spieler        = spieler;
        this.buffManager    = buffManager;
        this.screenSwitcher = screenSwitcher;
        this.slot           = new Slot(spieler);
        setLayout(null);
        initComponents();
    }
    @Override
    protected void initComponents()
    {
        slotImage  = loadImage("pics/slot.png");
        slotBilder = new BufferedImage[10];
        for (int i = 1; i <= 9; i++)
            slotBilder[i] = loadImage("pics/slot" + i + ".png");

        backButton = new JButton("Back");
        styleButton(backButton);
        addHoverEffect(backButton);
        backButton.addActionListener(e -> screenSwitcher.switchTo(0));
        add(backButton);

        slotReel1 = new SlotReel();
        slotReel1.setImage(slotBilder[1]);
        add(slotReel1);

        slotReel2 = new SlotReel();
        slotReel2.setImage(slotBilder[1]);
        add(slotReel2);

        slotReel3 = new SlotReel();
        slotReel3.setImage(slotBilder[1]);
        add(slotReel3);

        gewinnLabel = new JLabel("Druecke auf Drehen!", SwingConstants.CENTER);
        gewinnLabel.setFont(new Font("Arial", Font.BOLD, 22));
        gewinnLabel.setForeground(Color.WHITE);
        add(gewinnLabel);

        kontoLabel = new JLabel("Kontostand: " + spieler.getKontostand() + "€");
        kontoLabel.setFont(new Font("Arial", Font.BOLD, 25));
        kontoLabel.setForeground(Color.WHITE);
        add(kontoLabel);

        slotEinsatzLabel = new JLabel("Einsatz:");
        slotEinsatzLabel.setFont(new Font("Arial", Font.BOLD, 18));
        slotEinsatzLabel.setForeground(Color.WHITE);
        add(slotEinsatzLabel);

        slotEinsatzFeld = new JTextField("10");
        slotEinsatzFeld.setFont(new Font("Arial", Font.PLAIN, 18));
        add(slotEinsatzFeld);

        drehenButton = new JButton("Drehen");
        styleButton(drehenButton);
        addHoverEffect(drehenButton);
        add(drehenButton);

        drehenButton.addActionListener(e ->
        {
            int slotEinsatz;
            try
            {
                slotEinsatz = Integer.parseInt(slotEinsatzFeld.getText());
            }
            catch (NumberFormatException ex)
            {
                gewinnLabel.setText("Ungueltige Eingabe!");
                return;
            }

            // Slot.spielen(int einsatz) gibt false zurueck wenn Einsatz ungueltig
            if (!slot.spielen(slotEinsatz))
            {
                gewinnLabel.setText("Ungueltiger Einsatz!");
                return;
            }

            System.out.println("Slot1: " + slot.getSlot1()
                + " | Slot2: " + slot.getSlot2()
                + " | Slot3: " + slot.getSlot3()
                + " | Gewinn: " + slot.getGewinn());

            int ziel1 = slot.getSlot1();
            int ziel2 = slot.getSlot2();
            int ziel3 = slot.getSlot3();

            int gewinnMultiplikator = 1;
            if (buffManager.isDoubleUpAktiv()) 
            {
                    gewinnMultiplikator *= 2;
            }
            gewinnLabel.setText("...");
            starteAnimation(slotReel1, ziel1,   0);
            starteAnimation(slotReel2, ziel2, 400);
            starteAnimation(slotReel3, ziel3, 800);

            int finalMulti = gewinnMultiplikator;
        Timer ergebnisTimer = new Timer(800 + 30 * 80 + 500, ev ->
        {
            // Bonus durch Buffs berechnen
            int bonus = 0;
            if (slot.getGewinn() > 0)
            {
                if (finalMulti > 1)
                    bonus += slot.getGewinn() * (finalMulti - 1);
                if (buffManager.isLucky7Aktiv()
            && (slot.getSlot1()==7 || slot.getSlot2()==7 || slot.getSlot3()==7))
                    bonus += slot.getGewinn() * 2;
                if (buffManager.isJackpotBoostAktiv() && slot.super7IchKaufDasKasino())
                    bonus += slot.getGewinn();
            }
            if (bonus > 0) spieler.changeKontostand(bonus);
            buffManager.slotRundeGespielt();

            kontoLabel.setText("Kontostand: " + spieler.getKontostand() + "€");

            if      (slot.super7IchKaufDasKasino()) gewinnLabel.setText("🎰 JACKPOT 777 !!!" + (bonus > 0 ? " (+" + bonus + "$ Buff!)" : ""));
            else if (slot.hauptGewinn())            gewinnLabel.setText("Großer Gewinn!"      + (bonus > 0 ? " (+" + bonus + "$ Buff!)" : ""));
            else if (slot.kleinerGewinn())          gewinnLabel.setText("Kleiner Gewinn!"     + (bonus > 0 ? " (+" + bonus + "$ Buff!)" : ""));
            else                                    gewinnLabel.setText("Leider verloren!");

            drehenButton.setEnabled(true);
        }
        );
            
            ergebnisTimer.setRepeats(false);
            ergebnisTimer.start();
    
        });
    }

    private void starteAnimation(SlotReel reel, int zielZahl, int verzoegerung)
    {
        int[] counter         = {0};
        int[] geschwindigkeit = {50};

        Timer timer = new Timer(geschwindigkeit[0], null);
        timer.addActionListener(e ->
        {
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

    @Override
    protected void updateLayoutPositions(int w, int h)
    {
        backButton.setBounds(30, 25, 120, 45);

        int reelY  = (int)(h * 0.35);
        int reelW  = (int)(w * 0.08);
        int reelH  = reelW;
        int gapX   = (int)(w * 0.01);
        int totalW = 3 * reelW + 2 * gapX;
        int startX = (w - totalW) / 2;

        slotReel1.setBounds(startX,                       reelY, reelW, reelH);
        slotReel2.setBounds(startX + reelW + gapX,        reelY, reelW, reelH);
        slotReel3.setBounds(startX + 2 * (reelW + gapX),  reelY, reelW, reelH);

        kontoLabel .setBounds(w / 2 - 200, (int)(h * 0.09), 400, 40);
        gewinnLabel.setBounds(w / 2 - 200, reelY + reelH + 20, 400, 40);

        int inputY = reelY + reelH + 80;
        slotEinsatzLabel.setBounds(w / 2 - 120, inputY,      80,  35);
        slotEinsatzFeld .setBounds(w / 2 - 30,  inputY,     100,  35);
        drehenButton    .setBounds(w / 2 - 100, inputY + 55, 200,  55);
    }

    @Override
    protected void drawBackground(Graphics g)
    {
        g.drawImage(slotImage, 0, 0, getWidth(), getHeight(), this);
    }

    public void refresh()
    {
        kontoLabel.setText("Kontostand: " + spieler.getKontostand() + "€");
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        updateLayoutPositions(getWidth(), getHeight());
        drawBackground(g);
    }
}