import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Grafische Oberflaeche fuer das Roulette-Spiel.
 * Erweitert CasinoGUI um Roulette-spezifische UI-Elemente und implementiert
 * Runnable, um die Ball-Animation in einem Daemon-Thread mit ~100 FPS
 * unabhaengig vom Swing-Event-Thread zu berechnen.
 * Das eigentliche Zeichnen erfolgt weiterhin auf dem Event-Thread via repaint().
 */
public class RoulettePanel extends CasinoGUI implements Runnable
{
    // Eingabefelder fuer die drei Wettoptionen 
    private JTextField zahlFeld; // Eingabe: konkrete Zahl (0-36)

    // Steuer-Elemente
    private JButton spinButton; // startet den Dreh
    private JSlider einsatz; // Einsatzbetrag in $
    private JButton rot; //Dient zum angeben der Wette
    private JButton schwarz; //Dient zum angeben der Wette
    private JButton gerade; //Dient zum angeben der Wette
    private JButton ungerade; //Dient zum angeben der Wette
    private JButton reset; //Dient dazu die Wetten zu reseten

    // Anzeige-Labels
    private JLabel ergebnisLabel; // zeigt Zahl und Gewinn nach dem Dreh
    private JLabel einsatzLabel; // spiegelt den aktuellen Slider-Wert
    private JLabel zahlLabel; // Beschriftung fuer zahlFeld
    private JLabel konto; // aktueller Kontostand

    private JButton backButton; // zurueck zum Hub

    // Roulette-Elemente 
    private String farbeSetzen = "-";
    private String geradeSetzen = "-";

    // Ball-Animation (werden im Animations-Thread gelesen/geschrieben) 
    double ballAngle = 0; // aktueller Winkel des Balls in Radiant
    double ballSpeed = 0; // aktuelle Winkelgeschwindigkeit pro Tick
    int rouletteCenterX = 330; // X-Mittelpunkt des Rades (dynamisch berechnet)
    int rouletteCenterY = 400; // Y-Mittelpunkt des Rades (dynamisch berechnet)
    int ballRadius = 230; // Abstand Ball-Mittelpunkt in Pixeln (dynamisch berechnet)
    boolean spinning = false; // true = Animation laeuft gerade
    double targetAngle = 0; // Zielwinkel, bei dem der Ball stoppt

    // Ressourcen & Logik
    private BufferedImage rouletteImage; // Hintergrundbild des Roulette-Tisches
    private Roulette roulette; // Spiellogik-Objekt

    private HubPanel.ScreenSwitcher screenSwitcher;
    private BuffManager buffManager;

    /**
     * Erstellt das Roulette-Panel, verdrahtet alle Abhaengigkeiten und
     * startet den Animations-Thread.
     *
     * @param spieler Spieler-Profil fuer Guthabenabhaengigkeiten
     * @param buffManager Buff-System zur Pruefung aktiver Gewinnmultiplikatoren
     * @param screenSwitcher Navigator fuer den Bildschirmwechsel
     */
    public RoulettePanel(Spieler spieler, BuffManager buffManager,
    HubPanel.ScreenSwitcher screenSwitcher)
    {
        this.spieler = spieler;
        this.buffManager = buffManager;
        this.screenSwitcher = screenSwitcher;
        this.roulette = new Roulette(spieler);
        setLayout(null);
        initComponents();

        // Daemon-Thread: wird automatisch beendet, wenn die JVM beendet wird
        Thread th = new Thread(this);
        th.setDaemon(true);
        th.start();
    }

    /**
     * Erstellt, stylt und verdrahtet alle UI-Elemente.
     * Wird einmalig aus dem Konstruktor aufgerufen.
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
        // Wertebereich: 0 bis aktueller Kontostand, Startwert in der Mitte
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

        // Label wird live aktualisiert, sobald der Slider bewegt wird
        einsatz.addChangeListener(e ->
                einsatzLabel.setText("Einsatz: " + einsatz.getValue() + " $"));

        zahlFeld = new JTextField("0");
        styleTextField(zahlFeld);
        add(zahlFeld);

        rot = new JButton("Rot");
        rot.setBackground(Color.RED);
        rot.setForeground(Color.WHITE);
        rot.setFont(new Font("Arial", Font.BOLD, 22));
        rot.setFocusPainted(false);
        rot.setBorderPainted(false);
        rot.setOpaque(true);
        rot.setContentAreaFilled(true);
        addHoverEffect(rot);
        add(rot);
        rot.addActionListener(e ->{
                    farbeSetzen = "rot";
                    rot.setEnabled(false);
                    schwarz.setEnabled(true);
            });

        schwarz = new JButton("Schwarz");
        schwarz.setBackground(Color.BLACK);
        schwarz.setForeground(Color.WHITE);
        schwarz.setFont(new Font("Arial", Font.BOLD, 22));
        schwarz.setFocusPainted(false);
        schwarz.setBorderPainted(false);
        schwarz.setOpaque(true);
        schwarz.setContentAreaFilled(true);
        addHoverEffect(schwarz);
        add(schwarz);
        schwarz.addActionListener(e ->{
                    farbeSetzen = "schwarz";
                    schwarz.setEnabled(false);
                    rot.setEnabled(true);
            });

        gerade = new JButton("gerade");
        gerade.setBackground(Color.WHITE);
        gerade.setForeground(Color.BLACK);
        gerade.setFont(new Font("Arial", Font.BOLD, 22));
        gerade.setFocusPainted(false);
        gerade.setBorderPainted(false);
        gerade.setOpaque(true);
        gerade.setContentAreaFilled(true);
        addHoverEffect(gerade);
        add(gerade);
        gerade.addActionListener(e ->{
                    geradeSetzen = "gerade";
                    gerade.setEnabled(false);
                    ungerade.setEnabled(true);
            });

        ungerade = new JButton("ungerade");
        ungerade.setBackground(Color.WHITE);
        ungerade.setForeground(Color.BLACK);
        ungerade.setFont(new Font("Arial", Font.BOLD, 22));
        ungerade.setFocusPainted(false);
        ungerade.setBorderPainted(false);
        ungerade.setOpaque(true);
        ungerade.setContentAreaFilled(true);
        addHoverEffect(ungerade);
        add(ungerade);
        ungerade.addActionListener(e -> {
                    geradeSetzen = "ungerade";
                    ungerade.setEnabled(false);
                    gerade.setEnabled(true);
            });

        reset = new JButton("Reset");
        styleButton(reset);
        addHoverEffect(reset);
        add(reset);
        reset.addActionListener(e -> {
                    geradeSetzen = "-";
                    farbeSetzen = "-";
                    rot.setEnabled(true);
                    schwarz.setEnabled(true);
                    gerade.setEnabled(true);
                    ungerade.setEnabled(true);
            });



        zahlLabel = new JLabel("Zahl: ");
        zahlLabel.setFont(new Font("Arial", Font.BOLD, 14));
        zahlLabel.setForeground(Color.WHITE);
        add(zahlLabel);

        konto = new JLabel("Kontostand: " + spieler.getKontostand() + "$");
        konto.setFont(new Font("Arial", Font.BOLD, 30));
        konto.setForeground(Color.WHITE);
        add(konto);
        
        //Spin-Button
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

        spinButton.addActionListener(e ->
                {
                    // Button waehrend der Animation sperren, um Doppelklicks zu verhindern
                    spinButton.setEnabled(false);

                    // Spiellogik ausfuehren: Einsatz abziehen, Gewinn berechnen
                    int gewinn = roulette.spieldurchfuehren(
                            einsatz.getValue(), farbeSetzen, geradeSetzen, zahlR());

                    int zahl  = roulette.ergebnis;
                    int index = roulette.getWinkelIndex(); // Index des Ergebnisfelds auf dem Rad

                    // ── Zielwinkel berechnen ──────────────────────────────────────
                    // Das Rad hat 37 Felder (0-36), gleichmaessig auf 2*PI verteilt.
                    // imageOffset korrigiert die Startposition des Bildes (-PI/2 = 12-Uhr-Position).
                    double anglePerField     = 2 * Math.PI / 37;
                    double imageOffset       = -Math.PI / 2;
                    double currentNormalized = ballAngle % (2 * Math.PI);
                    double targetNormalized  = index * anglePerField + imageOffset;
                    if (targetNormalized < 0) 
                    {
                        targetNormalized += 2 * Math.PI;
                    }

                    // Differenz stets positiv halten (Ball dreht sich immer vorwaerts)
                    double delta = targetNormalized - currentNormalized;
                    if (delta <= 0)
                    {
                        delta += 2 * Math.PI;
                    }

                    // Mindestens 5 volle Umdrehungen (10*PI) vor dem Einlaufen in die Zielposition
                    targetAngle = ballAngle + (Math.PI * 10) + delta;
                    ballSpeed = 0.35 + Math.random() * 0.05; // leichte Zufallsvariation beim Start
                    spinning = true;

                    // Jackpot-GIF anzeigen (nur bei Hauptgewinn)
                    if (roulette.getHauptgewinn())
                    {
                        ImageIcon winGif = new ImageIcon(
                                getClass().getClassLoader().getResource("pics/jackpot.gif"));
                        JLabel winAnimation = new JLabel(winGif);
                        winAnimation.setBounds(900, 500, 700, 500);
                        add(winAnimation);
                        winAnimation.setVisible(true);
                        // GIF nach 3 Sekunden automatisch ausblenden
                        new Timer(3000, ev -> winAnimation.setVisible(false)).start();
                    }

                    // Ergebnis verzoegert anzeigen
                    // 4500 ms entsprechen ungefaehr der Animations-Dauer bis zum Stillstand
                    int finalGewinn = gewinn;
                    int finalZahl   = zahl;
                    Timer timer = new Timer(4500, event ->
                                {
                                    // Buff-Bonus: LuckySpin gibt 50% des Gewinns als Bonus obendrauf
                                    int bonus = 0;
                                    if (buffManager.isLuckySpinAktiv() && finalGewinn > 0)
                                    {
                                        bonus = finalGewinn / 2;
                                        spieler.changeKontostand(bonus);
                                    }
                                    buffManager.rouletteRundeGespielt();
                                    ergebnisLabel.setText("Zahl: " + finalZahl + " | Gewinn: " + finalGewinn
                                        + (bonus > 0 ? " (+" + bonus + "$ Buff!)" : "") + "$");
                                    refresh();
                                    spinButton.setEnabled(true);
                            });
                    timer.setRepeats(false);
                    timer.start();
            });

        // Ergebnis-Label 
        ergebnisLabel = new JLabel("Ergebnis: -");
        ergebnisLabel.setFont(new Font("Arial", Font.BOLD, 20));
        ergebnisLabel.setForeground(Color.WHITE);
        add(ergebnisLabel);
    }

    /**
     * Positioniert alle Komponenten relativ zur aktuellen Fenstergroesse.
     * Wird bei jedem paintComponent()-Aufruf neu berechnet, damit das Layout bei Fenstergroessenaenderungen korrekt bleibt.
     * @param w aktuelle Panel-Breite in Pixeln
     * @param h aktuelle Panel-Hoehe in Pixeln
     */
    @Override
    protected void updateLayoutPositions(int w, int h)
    {
        backButton.setBounds(30, 25, 120, 45);

        einsatz.setBounds((int)(w * 0.72), (int)(h * 0.08), 320, 60);
        einsatzLabel.setBounds((int)(w * 0.77), (int)(h * 0.15), 300, 60);

        // Eingabefelder und zugehoerige Labels rechts ausgerichtet
        int formX = (int)(w * 0.65);
        int label = (int)(w * 0.61);
        zahlFeld.setBounds(formX, (int)(h * 0.90), 150, 35);
        zahlLabel.setBounds(label, (int)(h * 0.90), 150, 35);

        rot.setBounds((int)(w * 0.5), (int)(h * 0.70), 150, 35);
        schwarz.setBounds((int)(w * 0.65), (int)(h * 0.70), 150, 35);
        gerade.setBounds((int)(w * 0.5), (int)(h * 0.80), 150, 35);
        ungerade.setBounds((int)(w * 0.65), (int)(h * 0.80), 150, 35);
        reset.setBounds((int)(w * 0.35), (int)(h * 0.70), 150, 35);

        spinButton.setBounds((int)(w * 0.82), (int)(h * 0.76), 170, 70);
        ergebnisLabel.setBounds((int)(w * 0.78), (int)(h * 0.86), 500, 40);

        konto.setBounds((int)(w * 0.15), (int)(h * 0.04), 400, 50);

        // Ball-Kreisbahn dynamisch aus Fenstergroesse ableiten,
        // damit der Ball immer exakt auf dem gezeichneten Rad laeuft
        rouletteCenterX = (int)(w * 0.202);
        rouletteCenterY = (int)(h * 0.473);
        ballRadius      = (int)(w * 0.11);
    }

    /**
     * Zeichnet Hintergrundbild und Ball.
     * Wird aus paintComponent() heraus aufgerufen.
     *
     * @param g das Graphics-Objekt des aktuellen Paint-Zyklus
     */
    @Override
    protected void drawBackground(Graphics g)
    {
        g.drawImage(rouletteImage, 0, 0, getWidth(), getHeight(), this);
        // Ball-Position aus Winkel und Radius berechnen (Polarkoordinaten)
        int ballX = (int)(rouletteCenterX + Math.cos(ballAngle) * ballRadius);
        int ballY = (int)(rouletteCenterY + Math.sin(ballAngle) * ballRadius);
        g.setColor(Color.WHITE);
        g.fillOval(ballX - 6, ballY - 6, 12, 12); // 12x12 Pixel grosser Ball
    }

    /**
     * Aktualisiert Kontostand-Label und Einsatz-Slider nach jeder Spielrunde.
     * Wird vom Ergebnis-Timer aufgerufen.
     */
    public void refresh()
    {
        int max = spieler.getKontostand();

        konto.setText("Kontostand: " + max + "$");
        // Slider-Maximum an neuen Kontostand anpassen
        einsatz.setMaximum(max);

        // Tick-Abstaende gleichmaessig auf 5 Abschnitte verteilen
        int schritt = Math.max(1, max / 5);
        einsatz.setMajorTickSpacing(schritt);
        einsatz.setLabelTable(einsatz.createStandardLabels(schritt));

        // Slider-Wert auf neues Maximum begrenzen, falls er darueber liegt
        if (einsatz.getValue() > max)
            einsatz.setValue(max);

        einsatz.repaint();
    }

    /**
     * Animations-Loop, laeuft im Daemon-Thread (~100 FPS, 10 ms Schlaf pro Tick).
     * Solange spinning == true, wird der Ballwinkel schrittweise an targetAngle
     * angenaehert. Die Geschwindigkeit nimmt mit der Wurzel der verbleibenden
     * Distanz ab, was eine physikalisch glaubwuerdige Abbremsung erzeugt.
     */
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
                    // Zielposition erreicht: Ball einrasten lassen
                    ballAngle = targetAngle;
                    ballSpeed = 0;
                    spinning  = false;
                }
                else
                {
                    // Abbremsen: Geschwindigkeit proportional zur Wurzel der Restdistanz
                    double naturalSpeed = Math.sqrt(remaining) * 0.04;
                    if (naturalSpeed < 0.005) naturalSpeed = 0.005; // Mindestgeschwindigkeit
                    if (naturalSpeed > ballSpeed) naturalSpeed = ballSpeed; // nie beschleunigen
                    ballSpeed  = naturalSpeed;
                    ballAngle += ballSpeed;
                }
            }

            // Neuzeichnen anfordern (thread-safe)
            repaint();
            try { Thread.sleep(10); }
            catch (Exception e) { /* Unterbrechung ignorieren, Loop laeuft weiter */ }
        }
    }

    // ═════════════════════════════════════════════════════════════════════
    /**
     * Einstiegspunkt des Swing-Paint-Zyklus.
     * Delegiert Layout-Berechnung und Hintergrund-Rendering an die jeweiligen Methoden.
     *
     * @param g das Graphics-Objekt des aktuellen Paint-Zyklus
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        updateLayoutPositions(getWidth(), getHeight());
        drawBackground(g);
    }

    // ── Eingabe-Hilfsmethoden ─────────────────────────────────────────────

    /** 
     * Liest die gewettete Zahl aus dem Textfeld (wirft NumberFormatException bei ungueltiger Eingabe).
     */
    private int zahlR()  
    {
        return Integer.parseInt(zahlFeld.getText()); 
    }

 
}