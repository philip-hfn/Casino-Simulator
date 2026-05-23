import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Grafische Oberflaeche fuer das Roulette-Spiel
 * Implementiert Runnable, um eine fluessige Ball-Animation (60 FPS) in einem separaten Hintergrund-Thread zu berechnen
 */
public class RoulettePanel extends CasinoGUI implements Runnable
{
    private JTextField geradeFeld;
    private JTextField zahlFeld;
    private JTextField rotFeld;
    private JButton    spinButton;
    private JLabel     ergebnisLabel;
    private JLabel     einsatzLabel;
    private JLabel     geradeLabel;
    private JLabel     zahlLabel;
    private JLabel     rotLabel;
    private JLabel     konto;
    private JSlider    einsatz;
    private JButton    backButton;

    // ── Ball-Animation ────────────────────────────────────────────────────
    double  ballAngle       = 0;
    double  ballSpeed       = 0;
    // NEU: werden dynamisch in updateLayoutPositions berechnet
    int     rouletteCenterX = 330;
    int     rouletteCenterY = 400;
    int     ballRadius      = 230;
    boolean spinning        = false;
    double  targetAngle     = 0;

    private BufferedImage rouletteImage;
    private Roulette      roulette;

    private HubPanel.ScreenSwitcher screenSwitcher;

    // ─────────────────────────────────────────────────────────────────────
    private BuffManager buffManager;

    /**
     * Konstruktor des Roulette-Panels
     * Initialisiert alle Verknuepfungen, baut das UI auf und startet den Animations-Thread
     * @param spieler Das Spieler-Profil fuer Guthaben-Aenderungen
     * @param buffManager Das Buff-System zur Pruefung aktiver Gewinn-Multiplikatoren
     * @param screenSwitcher Der Navigator für den Bildschirmwechsel
     */
    public RoulettePanel(Spieler spieler, BuffManager buffManager, HubPanel.ScreenSwitcher screenSwitcher)
    {
        this.spieler        = spieler;
        this.buffManager    = buffManager;
        this.screenSwitcher = screenSwitcher;
        this.roulette       = new Roulette(spieler);
        setLayout(null);
        initComponents();
        Thread th = new Thread(this);
        th.setDaemon(true);
        th.start();
    }   

    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Erstellt, stylt und verdrahtet alle UI-Elemente wie Buttons, Slider und Textfelder
     */
    @Override
    protected void initComponents()
    {
        rouletteImage = loadImage("pics/roulette.png");

        // Back-Button
        backButton = new JButton("Back");
        styleButton(backButton);
        addHoverEffect(backButton);
        backButton.addActionListener(e -> screenSwitcher.switchTo(0));
        add(backButton);

        // Einsatz-Slider
        einsatz = new JSlider(0, spieler.getKontostand(), spieler.getKontostand() / 2);
        einsatz.setMajorTickSpacing(500);
        einsatz.setMinorTickSpacing(10);
        einsatz.setPaintTicks(true);
        einsatz.setPaintLabels(true);
        einsatz.setBackground(new Color(30, 120, 30));
        einsatz.setForeground(Color.WHITE);
        einsatz.setFont(new Font("Arial", Font.BOLD, 14));
        add(einsatz);

        einsatzLabel = new JLabel("Einsatz waehlen");
        einsatzLabel.setFont(new Font("Arial", Font.BOLD, 22));
        einsatzLabel.setForeground(Color.WHITE);
        add(einsatzLabel);

        einsatz.addChangeListener(e ->
            einsatzLabel.setText("Einsatz: " + einsatz.getValue() + " $"));

        // Wett-Felder
        geradeFeld = new JTextField("-");
        styleTextField(geradeFeld);
        add(geradeFeld);

        rotFeld = new JTextField("-");
        styleTextField(rotFeld);
        add(rotFeld);

        zahlFeld = new JTextField("0");
        styleTextField(zahlFeld);
        add(zahlFeld);

        // Labels
        geradeLabel = new JLabel("Gerade/ungerade: ");
        geradeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        geradeLabel.setForeground(Color.WHITE);
        add(geradeLabel);

        rotLabel = new JLabel("Farbe: ");
        rotLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rotLabel.setForeground(Color.WHITE);
        add(rotLabel);

        zahlLabel = new JLabel("Zahl: ");
        zahlLabel.setFont(new Font("Arial", Font.BOLD, 14));
        zahlLabel.setForeground(Color.WHITE);
        add(zahlLabel);

        konto = new JLabel("Kontostand: " + spieler.getKontostand() + "$");
        konto.setFont(new Font("Arial", Font.BOLD, 30));
        konto.setForeground(Color.WHITE);
        add(konto);

        // Spin-Button
        spinButton = new JButton("SPIN");
        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);
        spinButton.setFont(new Font("Arial", Font.BOLD, 22));
        spinButton.setFocusPainted(false);
        spinButton.setBorderPainted(false);
        spinButton.setOpaque(true);
        spinButton.setContentAreaFilled(true);
        addHoverEffect(spinButton);
        add(spinButton);

        // NEU: verbesserter Spin-ActionListener aus CasinoGUI
        spinButton.addActionListener(e ->
        {
            // Button sperren während Animation läuft
            spinButton.setEnabled(false);

            int gewinn = roulette.spieldurchfuehren(
                einsatz.getValue(), rotR(), geradeR(), zahlR());

            int zahl  = roulette.ergebnis;
            int index = roulette.getWinkelIndex();   // Position auf dem Rad

            // NEU: Zielwinkel genau auf das Roulette-Feld berechnen
            double anglePerField   = 2 * Math.PI / 37;
            double imageOffset     = -Math.PI / 2;
            double currentNormalized = ballAngle % (2 * Math.PI);
            double targetNormalized  = index * anglePerField + imageOffset;
            if (targetNormalized < 0) targetNormalized += 2 * Math.PI;

            double delta = targetNormalized - currentNormalized;
            if (delta <= 0) delta += 2 * Math.PI;

            // Mindestens 5 volle Umdrehungen + exakte Zielposition
            targetAngle = ballAngle + (Math.PI * 10) + delta;
            ballSpeed   = 0.35 + Math.random() * 0.05;
            spinning    = true;

            // Jackpot-GIF
            if (roulette.getHauptgewinn())
            {
                ImageIcon winGif = new ImageIcon(
                    getClass().getClassLoader().getResource("pics/jackpot.gif"));
                JLabel winAnimation = new JLabel(winGif);
                winAnimation.setBounds(900, 500, 700, 500);
                add(winAnimation);
                winAnimation.setVisible(true);
                new Timer(3000, ev -> winAnimation.setVisible(false)).start();
            }

            // NEU: Ergebnis erst nach 4,5 Sek. anzeigen (wenn Ball gestoppt hat)
            int finalGewinn = gewinn;
            int finalZahl   = zahl;
            Timer timer = new Timer(4500, event ->
            {
                int bonus = 0;
                if (buffManager.isLuckySpinAktiv() && finalGewinn > 0)
                {
                    bonus = finalGewinn / 2;
                    spieler.changeKontostand(bonus);
                }
                buffManager.rouletteRundeGespielt();

                ergebnisLabel.setText("Zahl: " + finalZahl + " | Gewinn: " + finalGewinn
                  + (bonus > 0 ? " (+" + bonus + "$ Buff!)" : "") + "$");
                konto.setText("Kontostand: " + spieler.getKontostand() + "$");
                einsatz.setMaximum(spieler.getKontostand());
                spinButton.setEnabled(true);
            }
            );
            timer.setRepeats(false);
            timer.start();
        }
        );

        ergebnisLabel = new JLabel("Ergebnis: -");
        ergebnisLabel.setFont(new Font("Arial", Font.BOLD, 20));
        ergebnisLabel.setForeground(Color.WHITE);
        add(ergebnisLabel);
    }

    // ═══════════════════════════════════════════════════════════════════════
    @Override
    protected void updateLayoutPositions(int w, int h)
    {
        backButton.setBounds(30, 25, 120, 45);

        einsatz     .setBounds((int)(w * 0.72), (int)(h * 0.08), 320, 60);
        einsatzLabel.setBounds((int)(w * 0.77), (int)(h * 0.15), 300, 60);

        int formX = (int)(w * 0.65);
        int label = (int)(w * 0.61);
        geradeFeld.setBounds(formX, (int)(h * 0.70), 150, 35);
        rotFeld   .setBounds(formX, (int)(h * 0.80), 150, 35);
        zahlFeld  .setBounds(formX, (int)(h * 0.90), 150, 35);

        geradeLabel.setBounds((int)(w * 0.55), (int)(h * 0.70), 150, 35);
        rotLabel   .setBounds(label,           (int)(h * 0.80), 150, 35);
        zahlLabel  .setBounds(label,           (int)(h * 0.90), 150, 35);

        spinButton   .setBounds((int)(w * 0.82), (int)(h * 0.76), 170, 70);
        ergebnisLabel.setBounds((int)(w * 0.82), (int)(h * 0.86), 500, 40);

        konto.setBounds((int)(w * 0.15), (int)(h * 0.04), 400, 50);

        // NEU: Ball-Position dynamisch aus Fenstergröße berechnen (wie in CasinoGUI)
        rouletteCenterX = (int)(w * 0.202);
        rouletteCenterY = (int)(h * 0.473);
        ballRadius      = (int)(w * 0.11);
    }

    // ═══════════════════════════════════════════════════════════════════════
    @Override
    protected void drawBackground(Graphics g)
    {
        g.drawImage(rouletteImage, 0, 0, getWidth(), getHeight(), this);

        int ballX = (int)(rouletteCenterX + Math.cos(ballAngle) * ballRadius);
        int ballY = (int)(rouletteCenterY + Math.sin(ballAngle) * ballRadius);
        g.setColor(Color.WHITE);
        g.fillOval(ballX - 6, ballY - 6, 12, 12);
    }

    // ═══════════════════════════════════════════════════════════════════════
    public void refresh()
    {
        konto.setText("Kontostand: " + spieler.getKontostand() + "$");
        einsatz.setMaximum(spieler.getKontostand());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NEU: verbesserte Ball-Animation aus CasinoGUI
    @Override
    public void run()
    {
        while (true)
        {
            if (spinning)
            {
                double remaining = targetAngle - ballAngle;

                if (remaining <= 0.01)
                {
                    ballAngle = targetAngle;
                    ballSpeed = 0;
                    spinning  = false;
                }
                else
                {
                    // Geschwindigkeit abhängig von verbleibender Distanz
                    double naturalSpeed = Math.sqrt(remaining) * 0.04;
                    if (naturalSpeed < 0.005) naturalSpeed = 0.005;
                    if (naturalSpeed > ballSpeed) naturalSpeed = ballSpeed;
                    ballSpeed  = naturalSpeed;
                    ballAngle += ballSpeed;
                }
            }

            repaint();
            try { Thread.sleep(10); }
            catch (Exception e) {}
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        updateLayoutPositions(getWidth(), getHeight());
        drawBackground(g);
    }

    // ─── Input-Helpers ────────────────────────────────────────────────────
    private int    zahlR()   { return Integer.parseInt(zahlFeld.getText()); }
    private String geradeR() { return geradeFeld.getText(); }
    private String rotR()    { return rotFeld.getText(); }
}
