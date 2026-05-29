import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Marktplatz-Panel – reine GUI-Klasse fuer die Darstellung des Shops.
 * Alle Kaeufe werden zur Verarbeitung an die Klasse Marktplatz.java delegiert.
 */
public class MarktplatzPanel extends CasinoGUI
{
    private JButton backButton;
    private JLabel  kontoLabel;

    private JLabel lucky7StatusLabel;
    private JLabel jackpotStatusLabel;
    private JLabel luckySpinStatusLabel;
    private JLabel doubleUpStatusLabel;

    private BufferedImage backgroundImage;

    private Marktplatz  marktplatz;
    private BuffManager buffManager;
    private HubPanel.ScreenSwitcher screenSwitcher;

    /**
     * Initialisiert das Marktplatz-Panel mit den notwendigen Referenzen.
     * @param spieler Der aktuelle Spieler.
     * @param marktplatz Die Logik-Instanz fuer Kaufvorgaenge.
     * @param buffManager Der Manager zur Verwaltung der aktiven Effekte.
     * @param screenSwitcher Steuerung zum Wechseln zwischen den Menues.
     */
    public MarktplatzPanel(Spieler spieler, Marktplatz marktplatz,BuffManager buffManager,HubPanel.ScreenSwitcher screenSwitcher)
    {
        this.spieler = spieler;
        this.marktplatz = marktplatz;
        this.buffManager = buffManager;
        this.screenSwitcher = screenSwitcher;
        setLayout(null); // Absolute Positionierung nutzen
        initComponents();
    }

    /**
     * Erstellt und konfiguriert alle grafischen Komponenten fuer den Marktplatz.
     */
    @Override
    protected void initComponents()
    {
        backgroundImage = loadImage("pics/background.png");

        // Back-Button: Fuehrt den Spieler zurueck zum Hauptmenue
        backButton = new JButton("Back");
        styleButton(backButton);
        addHoverEffect(backButton);
        backButton.addActionListener(e -> screenSwitcher.switchTo(0));
        add(backButton);

        // Kontostand-Label: Zeigt dem Spieler sein aktuelles Guthaben an
        kontoLabel = new JLabel("Kontostand: " + spieler.getKontostand() + "$");
        kontoLabel.setFont(new Font("Georgia", Font.BOLD, 26));
        kontoLabel.setForeground(Color.WHITE);
        kontoLabel.setBackground(new Color(217, 131, 53));
        kontoLabel.setOpaque(true);
        add(kontoLabel);

        // Ueberschrift: Marktplatz-Titel mittig zentriert
        JLabel titel = new JLabel("Marktplatz", SwingConstants.CENTER);
        titel.setFont(new Font("Georgia", Font.BOLD, 36));
        titel.setForeground(Color.WHITE);
        add(titel);

        // ── Erstellung der 4 Buff-Karten ──────────────────────────────────
        // Jede Karte erhaelt einen Callback an die Marktplatz-Logik
        lucky7StatusLabel = addBuffKarte(
            0, "🎰  Lucky 7", "7er Gewinn wird verdreifacht", "x3 Gewinn bei 7",
            marktplatz.getPreisLucky7(), 5, new Color(217, 131, 53),
            () -> marktplatz.kaufeLucky7()
        );

        jackpotStatusLabel = addBuffKarte(
            1, "💰  Jackpot Boost", "777 Jackpot Gewinn wird verdoppelt", "x2 Jackpot Gewinn",
            marktplatz.getPreisJackpotBoost(), 3, new Color(217, 131, 53),
            () -> marktplatz.kaufeJackpotBoost()
        );

        luckySpinStatusLabel = addBuffKarte(
            2, "🍀  Lucky Spin", "Roulette Gewinn wird um 50% erhoeht", "+50% Roulette Gewinn",
            marktplatz.getPreisLuckySpin(), 5, new Color(217, 131, 53),
            () -> marktplatz.kaufeLuckySpin()
        );

        doubleUpStatusLabel = addBuffKarte(
            3, "⚡  Double Up", "Naechster Slot-Gewinn wird verdoppelt", "x2 naechster Gewinn",
            marktplatz.getPreisDoubleUp(), 1, new Color(217, 131, 53),
            () -> marktplatz.kaufeDoubleUp()
        );
    }

    /**
     * Erstellt ein grafisches Panel fuer einen Buff-Kauf.
     * @param index Eindeutige ID fuer das Layout.
     * @param name Name des Buffs.
     * @param beschreibung Textbeschreibung des Effekts.
     * @param effekt Kurzer Text zum Bonus-Effekt.
     * @param preis Kosten in $.
     * @param runden Laufzeit in Runden.
     * @param farbe Designfarbe der Karte.
     * @param onKauf Lambda-Funktion, die die Logik aufruft.
     * @return Das Status-Label, um es bei Aenderungen zu aktualisieren.
     */
    private JLabel addBuffKarte(int index, String name, String beschreibung,
                                String effekt, int preis, int runden,
                                Color farbe, java.util.function.BooleanSupplier onKauf)
    {
        JPanel karte = new JPanel(null);
        karte.setBackground(new Color(30, 30, 30)); // Dunkler Hintergrund fuer die Karte
        karte.setBorder(BorderFactory.createLineBorder(farbe, 2)); // Farbiger Rahmen
        karte.setName("karte" + index);
        add(karte);

        // Farbstreifen links zur optischen Kennzeichnung
        JLabel streifen = new JLabel();
        streifen.setOpaque(true);
        streifen.setBackground(farbe);
        streifen.setBounds(0, 0, 8, 120);
        karte.add(streifen);

        // Name des Buffs
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(20, 10, 400, 30);
        karte.add(nameLabel);

        // Beschreibung des Effekts
        JLabel beschLabel = new JLabel(beschreibung);
        beschLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        beschLabel.setForeground(new Color(180, 180, 180));
        beschLabel.setBounds(20, 45, 400, 20);
        karte.add(beschLabel);

        // Kurze Effekt-Zusammenfassung
        JLabel effektLabel = new JLabel(effekt);
        effektLabel.setFont(new Font("Arial", Font.BOLD, 13));
        effektLabel.setForeground(farbe);
        effektLabel.setBounds(20, 70, 400, 20);
        karte.add(effektLabel);

        // Dauer-Anzeige
        JLabel dauerLabel = new JLabel("Dauer: " + runden + " Runden");
        dauerLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        dauerLabel.setForeground(new Color(150, 150, 150));
        dauerLabel.setBounds(20, 92, 200, 20);
        karte.add(dauerLabel);

        // Status-Label (aktiv oder inaktiv)
        JLabel statusLabel = new JLabel("Nicht aktiv");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statusLabel.setForeground(new Color(120, 120, 120));
        statusLabel.setBounds(230, 92, 200, 20);
        karte.add(statusLabel);

        // Preis-Anzeige
        JLabel preisLabel = new JLabel(preis + "$");
        preisLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        preisLabel.setForeground(new Color(217, 131, 53));
        preisLabel.setBounds(560, 20, 120, 30);
        karte.add(preisLabel);

        // Kaufen-Button zur Ausloesung der Logik
        JButton kaufBtn = new JButton("Kaufen");
        kaufBtn.setBackground(farbe);
        kaufBtn.setForeground(Color.WHITE);
        kaufBtn.setFont(new Font("Arial", Font.BOLD, 15));
        kaufBtn.setFocusPainted(false);
        kaufBtn.setBorderPainted(false);
        kaufBtn.setOpaque(true);
        kaufBtn.setBounds(560, 60, 120, 40);
        karte.add(kaufBtn);

        // Action-Listener: Bindet die GUI an die Logik-Methode
        kaufBtn.addActionListener(e ->
        {
            boolean erfolgreich = onKauf.getAsBoolean(); // Aufruf der Logik-Methode
            if (!erfolgreich)
            {
                // Fehleranzeige bei zu wenig Guthaben
                JOptionPane.showMessageDialog(this, "Nicht genug Geld!\nDu brauchst " + preis + "$.",
                    "Fehler", JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Aktualisiert UI und Kontostand bei Erfolg
            kontoLabel.setText("Kontostand: " + spieler.getKontostand() + "$");
            statusLabel.setForeground(farbe);
            refresh();
            JOptionPane.showMessageDialog(this, "✅ " + name + " wurde aktiviert!\nAktiv fuer " + runden + " Runden.",
                "Buff gekauft", JOptionPane.INFORMATION_MESSAGE);
        });

        return statusLabel;
    }

    /**
     * Aktualisiert die Status-Anzeigen aller Buff-Karten basierend auf dem BuffManager.
     */
    public void refresh()
    {
        kontoLabel.setText("Kontostand: " + spieler.getKontostand() + "$");

        // Prueft den Status im BuffManager und setzt den Text entsprechend
        lucky7StatusLabel.setText(buffManager.getLucky7Runden() > 0 ? "✅ Aktiv: " + buffManager.getLucky7Runden() + " Runden" : "Nicht aktiv");
        jackpotStatusLabel.setText(buffManager.getJackpotBoostRunden() > 0 ? "✅ Aktiv: " + buffManager.getJackpotBoostRunden() + " Runden" : "Nicht aktiv");
        luckySpinStatusLabel.setText(buffManager.getLuckySpinRunden() > 0 ? "✅ Aktiv: " + buffManager.getLuckySpinRunden() + " Runden" : "Nicht aktiv");
        doubleUpStatusLabel.setText(buffManager.getDoubleUpRunden() > 0 ? "✅ Aktiv: " + buffManager.getDoubleUpRunden() + " Runden" : "Nicht aktiv");
    }

    /**
     * Berechnet die Positionen aller UI-Elemente neu, um bei Fenstergroessenanpassung korrekt zu bleiben.
     * @param w Aktuelle Breite.
     * @param h Aktuelle Hoehe.
     */
    @Override
    protected void updateLayoutPositions(int w, int h)
    {
        backButton.setBounds(30, 25, 120, 45);
        kontoLabel.setBounds(w - 370, 25, 340, 45);

        int karteW = (int)(w * 0.45);
        int karteH = 120;
        int startY = 160;
        int gapY   = 20;
        int startX = (w - karteW) / 2;

        int kartenIndex = 0;
        // Durchlaeuft alle Komponenten und passt deren Bounds (Position/Groesse) an
        for (Component c : getComponents())
        {
            if (c instanceof JPanel && ((JPanel)c).getName() != null && ((JPanel)c).getName().startsWith("karte"))
            {
                c.setBounds(startX, startY + kartenIndex * (karteH + gapY), karteW, karteH);
                for (Component k : ((JPanel)c).getComponents())
                {
                    if (k instanceof JLabel)
                    {
                        JLabel lbl = (JLabel) k;
                        if (lbl.getFont().getName().equals("Georgia") && lbl.getText().contains("$"))
                            lbl.setBounds(karteW - 160, 20, 120, 30);
                        if (lbl.getText().startsWith("✅") || lbl.getText().equals("Nicht aktiv"))
                            lbl.setBounds(230, 92, 200, 20);
                    }
                    if (k instanceof JButton)
                        k.setBounds(karteW - 160, 55, 120, 40);
                    if (k instanceof JLabel && ((JLabel)k).isOpaque())
                        k.setBounds(0, 0, 8, karteH);
                }
                kartenIndex++;
            }
            if (c instanceof JLabel && ((JLabel)c).getText().contains("Marktplatz"))
                c.setBounds(0, 90, w, 50);
        }
    }

    /**
     * Zeichnet das Hintergrundbild auf das Panel.
     * @param g Grafik-Kontext zum Zeichnen.
     */
    @Override
    protected void drawBackground(Graphics g)
    {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    /**
     * Zeichnet die Komponente und sorgt fuer ein aktualisiertes Layout.
     * @param g Grafik-Kontext zum Zeichnen.
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g); // Standard-Panel-Zeichnung
        updateLayoutPositions(getWidth(), getHeight()); // Layout dynamisch aktualisieren
        drawBackground(g); // Hintergrundbild rendern
    }
}