import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Slot-Panel – alle Änderungen aus der alten CasinoGUI übernommen:
 * Konfetti, Jackpot-Text-Animation, Sound, transparenter Hebel-Button.
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

    // ── Bilder & Logik ────────────────────────────────────────────────────
    private BufferedImage   slotImage;
    private BufferedImage[] slotBilder;
    private Slot            slot;
    private BuffManager     buffManager;

    // ── Sound ─────────────────────────────────────────────────────────────
    private SoundManager sound;

    // ── Konfetti ──────────────────────────────────────────────────────────
    private List<int[]> konfettiList  = new ArrayList<>();
    private Timer       konfettiTimer;
    private boolean     konfettiAktiv = false;

    // ── Jackpot-Text-Animation ────────────────────────────────────────────
    private int     textGroesse = 0;
    private boolean textWaechst = true;
    private Timer   textTimer;

    private HubPanel.ScreenSwitcher screenSwitcher;

    // ─────────────────────────────────────────────────────────────────────
    public SlotPanel(Spieler spieler, BuffManager buffManager,
                     HubPanel.ScreenSwitcher screenSwitcher)
    {
        this.spieler        = spieler;
        this.buffManager    = buffManager;
        this.screenSwitcher = screenSwitcher;
        this.slot           = new Slot(spieler);
        this.sound          = new SoundManager();
        setLayout(null);
        initComponents();
    }

    // ═══════════════════════════════════════════════════════════════════════
    @Override
    protected void initComponents()
    {
        slotImage  = loadImage("pics/slot.png");
        slotBilder = new BufferedImage[10];
        for (int i = 1; i <= 9; i++)
            slotBilder[i] = loadImage("pics/slot" + i + ".png");

        // Back-Button
        backButton = new JButton("Back");
        styleButton(backButton);
        addHoverEffect(backButton);
        backButton.addActionListener(e -> screenSwitcher.switchTo(0));
        add(backButton);

        // Walzen
        slotReel1 = new SlotReel();
        slotReel1.setImage(slotBilder[1]);
        add(slotReel1);

        slotReel2 = new SlotReel();
        slotReel2.setImage(slotBilder[1]);
        add(slotReel2);

        slotReel3 = new SlotReel();
        slotReel3.setImage(slotBilder[1]);
        add(slotReel3);

        // Gewinn-Label – gold wie in der alten CasinoGUI
        gewinnLabel = new JLabel("Drücke auf den Hebel!", SwingConstants.CENTER);
        gewinnLabel.setFont(new Font("Serif", Font.BOLD, 28));
        gewinnLabel.setForeground(new Color(184, 134, 11));
        add(gewinnLabel);

        // Kontostand-Label – gold wie in der alten CasinoGUI
        kontoLabel = new JLabel("Kontostand: " + spieler.getKontostand() + "€");
        kontoLabel.setFont(new Font("Serif", Font.BOLD, 25));
        kontoLabel.setForeground(new Color(184, 134, 11));
        add(kontoLabel);

        // Einsatz-Label – gold
        slotEinsatzLabel = new JLabel("Einsatz:");
        slotEinsatzLabel.setFont(new Font("Serif", Font.BOLD, 18));
        slotEinsatzLabel.setForeground(new Color(184, 134, 11));
        add(slotEinsatzLabel);

        // Einsatz-Feld – gold
        slotEinsatzFeld = new JTextField("10");
        slotEinsatzFeld.setFont(new Font("Serif", Font.PLAIN, 18));
        slotEinsatzFeld.setBackground(new Color(184, 134, 11));
        add(slotEinsatzFeld);

        // Drehen-Button – transparent, liegt über dem Hebel im Bild
        drehenButton = new JButton();
        styleButton(drehenButton);
        drehenButton.setContentAreaFilled(false);
        drehenButton.setBorderPainted(false);
        drehenButton.setFocusPainted(false);
        drehenButton.setRolloverEnabled(false);
        drehenButton.setOpaque(false);
        add(drehenButton);

        drehenButton.addActionListener(e ->
        {
            sound.spinEffekt();

            int slotEinsatz;
            try
            {
                slotEinsatz = Integer.parseInt(slotEinsatzFeld.getText());
            }
            catch (NumberFormatException ex)
            {
                gewinnLabel.setText("Ungültige Eingabe!");
                return;
            }

            if (!slot.spielen(slotEinsatz))
            {
                gewinnLabel.setText("Ungültiger Einsatz!");
                return;
            }

            System.out.println("Slot1: " + slot.getSlot1()
                + " | Slot2: " + slot.getSlot2()
                + " | Slot3: " + slot.getSlot3()
                + " | Gewinn: " + slot.getGewinn());

            int ziel1 = slot.getSlot1();
            int ziel2 = slot.getSlot2();
            int ziel3 = slot.getSlot3();

            // Buff-Multiplikator vor dem Drehen merken
            int gewinnMultiplikator = 1;
            if (buffManager.isDoubleUpAktiv()) gewinnMultiplikator *= 2;

            drehenButton.setEnabled(false);
            gewinnLabel.setText("...");
            starteAnimation(slotReel1, ziel1,   0);
            starteAnimation(slotReel2, ziel2, 400);
            starteAnimation(slotReel3, ziel3, 800);

            int finalMulti = gewinnMultiplikator;
            Timer ergebnisTimer = new Timer(800 + 30 * 80 + 500, ev ->
            {
                // Buff-Bonus berechnen
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

                if (slot.super7IchKaufDasKasino())
                {
                    gewinnLabel.setText("🎰 JACKPOT 777 !!!" + (bonus > 0 ? " (+" + bonus + "$ Buff!)" : ""));
                    konfettiAnimation();
                    jackpotTextAnimation();
                    sound.jackpotEffekt();
                }
                else if (slot.hauptGewinn())
                {
                    gewinnLabel.setText("Großer Gewinn!" + (bonus > 0 ? " (+" + bonus + "$ Buff!)" : ""));
                    konfettiAnimation();
                    sound.jackpotEffekt();
                }
                else if (slot.kleinerGewinn())
                {
                    gewinnLabel.setText("Kleiner Gewinn!" + (bonus > 0 ? " (+" + bonus + "$ Buff!)" : ""));
                    konfettiAnimation();
                    sound.jackpotEffekt();
                }
                else
                {
                    gewinnLabel.setText("Leider verloren!");
                }

                drehenButton.setEnabled(true);
            });
            ergebnisTimer.setRepeats(false);
            ergebnisTimer.start();
        });
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Konfetti-Animation – identisch zur alten CasinoGUI
    // ═══════════════════════════════════════════════════════════════════════
    private void konfettiAnimation()
    {
        konfettiList.clear();
        for (int i = 0; i < 100; i++)
        {
            konfettiList.add(new int[]{
                (int)(Math.random() * getWidth()),
                (int)(Math.random() * -200),
                (int)(Math.random() * 5 + 3),
                (int)(Math.random() * 2),
                (int)(Math.random() * 15 + 5)
            });
        }
        konfettiAktiv = true;

        Timer stopTimer = new Timer(4000, e ->
        {
            konfettiAktiv = false;
            konfettiList.clear();
        });
        stopTimer.setRepeats(false);
        stopTimer.start();

        konfettiTimer = new Timer(30, e ->
        {
            for (int[] k : konfettiList)
            {
                k[1] += k[2];
                if (k[1] > getHeight()) k[1] = -20;
            }
        });
        konfettiTimer.start();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Jackpot-Text-Animation – identisch zur alten CasinoGUI
    // ═══════════════════════════════════════════════════════════════════════
    private void jackpotTextAnimation()
    {
        textGroesse = 10;
        textWaechst = true;

        textTimer = new Timer(20, null);
        textTimer.addActionListener(e ->
        {
            if (textWaechst)
            {
                textGroesse += 3;
                if (textGroesse >= 120) textWaechst = false;
            }
            else
            {
                textGroesse -= 3;
                if (textGroesse <= 10)
                {
                    textTimer.stop();
                    textGroesse = 0;
                }
            }
        });
        textTimer.start();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Walzen-Animation
    // ═══════════════════════════════════════════════════════════════════════
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

    // ═══════════════════════════════════════════════════════════════════════
    @Override
    protected void updateLayoutPositions(int w, int h)
    {
        backButton.setBounds(30, 25, 120, 45);

        int reelY  = (int)(h * 0.53);
        int reelW  = (int)(w * 0.08);
        int reelH  = reelW;
        int gapX   = (int)(w * 0.045);
        int totalW = 3 * reelW + 2 * gapX;
        int startX = (w - totalW) / 2;

        slotReel1.setBounds(startX,                       reelY, reelW, reelH);
        slotReel2.setBounds(startX + reelW + gapX,        reelY, reelW, reelH);
        slotReel3.setBounds(startX + 2 * (reelW + gapX),  reelY, reelW, reelH);

        kontoLabel .setBounds(w / 2 - 200, (int)(h * 0.09), 400, 40);
        gewinnLabel.setBounds(w / 2 - 200, (int)(h * 0.35), 400, 40);

        int inputY = reelY + reelH + 120;
        slotEinsatzLabel.setBounds(w / 2 - 120, inputY,  80,  35);
        slotEinsatzFeld .setBounds(w / 2 - 30,  inputY, 100,  35);

        // Transparenter Button liegt über dem Hebel im Bild
        drehenButton.setBounds((int)(w * 0.735), (int)(h * 0.22), 60, (int)(h * 0.36));
    }

    // ═══════════════════════════════════════════════════════════════════════
    @Override
    protected void drawBackground(Graphics g)
    {
        g.drawImage(slotImage, 0, 0, getWidth(), getHeight(), this);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // Konfetti und Jackpot-Text über allem zeichnen
    // ═══════════════════════════════════════════════════════════════════════
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        updateLayoutPositions(getWidth(), getHeight());
        drawBackground(g);

        // Konfetti
        if (konfettiAktiv)
        {
            Color[] farben = {Color.RED, new Color(212, 175, 55)};
            for (int[] k : konfettiList)
            {
                g.setColor(farben[k[3]]);
                g.fillRect(k[0], k[1], k[4], k[4]);
            }
        }

        // Jackpot-Text
        if (textGroesse > 0)
        {
            g.setFont(new Font("Arial", Font.BOLD, textGroesse));
            g.setColor(new Color(255, 215, 0));
            FontMetrics fm = g.getFontMetrics();
            String text = "JACKPOT 777 !!!";
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = getHeight() / 2;
            g.drawString(text, x, y);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    public void refresh()
    {
        kontoLabel.setText("Kontostand: " + spieler.getKontostand() + "€");
    }
}