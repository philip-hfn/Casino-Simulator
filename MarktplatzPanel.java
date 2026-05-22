import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Marktplatz-Panel – nur GUI, keine Logik.
 * Alle Käufe werden an Marktplatz.java delegiert.
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

    // ─────────────────────────────────────────────────────────────────────
    public MarktplatzPanel(Spieler spieler, Marktplatz marktplatz,
                           BuffManager buffManager,
                           HubPanel.ScreenSwitcher screenSwitcher)
    {
        this.spieler        = spieler;
        this.marktplatz     = marktplatz;
        this.buffManager    = buffManager;
        this.screenSwitcher = screenSwitcher;
        setLayout(null);
        initComponents();
    }

    // ═══════════════════════════════════════════════════════════════════════
    @Override
    protected void initComponents()
    {
        backgroundImage = loadImage("pics/background.png");

        // Back-Button
        backButton = new JButton("Back");
        styleButton(backButton);
        addHoverEffect(backButton);
        backButton.addActionListener(e -> screenSwitcher.switchTo(0));
        add(backButton);

        // Kontostand
        kontoLabel = new JLabel("Kontostand: " + spieler.getKontostand() + "$");
        kontoLabel.setFont(new Font("Georgia", Font.BOLD, 26));
        kontoLabel.setForeground(Color.WHITE);
        kontoLabel.setBackground(new Color(217, 131, 53));
        kontoLabel.setOpaque(true);
        add(kontoLabel);

        // Titel
        JLabel titel = new JLabel("Marktplatz", SwingConstants.CENTER);
        titel.setFont(new Font("Georgia", Font.BOLD, 36));
        titel.setForeground(Color.WHITE);
        add(titel);

        // ── 4 Buff-Karten ─────────────────────────────────────────────────
        lucky7StatusLabel = addBuffKarte(
            0,
            "🎰  Lucky 7",
            "7er Gewinn wird verdreifacht",
            "x3 Gewinn bei 7",
            marktplatz.getPreisLucky7(),
            5,
            new Color(217, 131, 53),
            () -> marktplatz.kaufeLucky7()
        );

        jackpotStatusLabel = addBuffKarte(
            1,
            "💰  Jackpot Boost",
            "777 Jackpot Gewinn wird verdoppelt",
            "x2 Jackpot Gewinn",
            marktplatz.getPreisJackpotBoost(),
            3,
            new Color(217, 131, 53),
            () -> marktplatz.kaufeJackpotBoost()
        );

        luckySpinStatusLabel = addBuffKarte(
            2,
            "🍀  Lucky Spin",
            "Roulette Gewinn wird um 50% erhöht",
            "+50% Roulette Gewinn",
            marktplatz.getPreisLuckySpin(),
            5,
            new Color(217, 131, 53),
            () -> marktplatz.kaufeLuckySpin()
        );

        doubleUpStatusLabel = addBuffKarte(
            3,
            "⚡  Double Up",
            "Nächster Slot-Gewinn wird verdoppelt",
            "x2 nächster Gewinn",
            marktplatz.getPreisDoubleUp(),
            1,
            new Color(217, 131, 53),
            () -> marktplatz.kaufeDoubleUp()
        );
    }

    // ═══════════════════════════════════════════════════════════════════════
    /**
     * Erstellt eine Buff-Karte.
     * onKauf ist ein Runnable das die Kauf-Methode aus Marktplatz.java aufruft.
     * Gibt das Status-Label zurück damit refresh() es aktualisieren kann.
     */
    private JLabel addBuffKarte(int index, String name, String beschreibung,
                                 String effekt, int preis, int runden,
                                 Color farbe, java.util.function.BooleanSupplier onKauf)
    {
        JPanel karte = new JPanel(null);
        karte.setBackground(new Color(30, 30, 30));
        karte.setBorder(BorderFactory.createLineBorder(farbe, 2));
        karte.setName("karte" + index);
        add(karte);

        // Farbstreifen links
        JLabel streifen = new JLabel();
        streifen.setOpaque(true);
        streifen.setBackground(farbe);
        streifen.setBounds(0, 0, 8, 120);
        karte.add(streifen);

        // Name
        JLabel nameLabel = new JLabel(name);
        nameLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(20, 10, 400, 30);
        karte.add(nameLabel);

        // Beschreibung
        JLabel beschLabel = new JLabel(beschreibung);
        beschLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        beschLabel.setForeground(new Color(180, 180, 180));
        beschLabel.setBounds(20, 45, 400, 20);
        karte.add(beschLabel);

        // Effekt
        JLabel effektLabel = new JLabel(effekt);
        effektLabel.setFont(new Font("Arial", Font.BOLD, 13));
        effektLabel.setForeground(farbe);
        effektLabel.setBounds(20, 70, 400, 20);
        karte.add(effektLabel);

        // Dauer
        JLabel dauerLabel = new JLabel("Dauer: " + runden + " Runden");
        dauerLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        dauerLabel.setForeground(new Color(150, 150, 150));
        dauerLabel.setBounds(20, 92, 200, 20);
        karte.add(dauerLabel);

        // Status-Label
        JLabel statusLabel = new JLabel("Nicht aktiv");
        statusLabel.setFont(new Font("Arial", Font.BOLD, 13));
        statusLabel.setForeground(new Color(120, 120, 120));
        statusLabel.setBounds(230, 92, 200, 20);
        karte.add(statusLabel);

        // Preis
        JLabel preisLabel = new JLabel(preis + "$");
        preisLabel.setFont(new Font("Georgia", Font.BOLD, 20));
        preisLabel.setForeground(new Color(217, 131, 53));
        preisLabel.setBounds(560, 20, 120, 30);
        karte.add(preisLabel);

        // Kaufen-Button
        JButton kaufBtn = new JButton("Kaufen");
        kaufBtn.setBackground(farbe);
        kaufBtn.setForeground(Color.WHITE);
        kaufBtn.setFont(new Font("Arial", Font.BOLD, 15));
        kaufBtn.setFocusPainted(false);
        kaufBtn.setBorderPainted(false);
        kaufBtn.setOpaque(true);
        kaufBtn.setBounds(560, 60, 120, 40);
        karte.add(kaufBtn);

        // Kauf-Aktion – GUI fragt Logik, Logik entscheidet
        kaufBtn.addActionListener(e ->
        {
            boolean erfolgreich = onKauf.getAsBoolean();
            if (!erfolgreich)
            {
                JOptionPane.showMessageDialog(
                    this,
                    "Nicht genug Geld!\nDu brauchst " + preis + "$.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                );
                return;
            }
            kontoLabel.setText("Kontostand: " + spieler.getKontostand() + "$");
            statusLabel.setForeground(farbe);
            refresh();
            JOptionPane.showMessageDialog(
                this,
                "✅ " + name + " wurde aktiviert!\nAktiv für " + runden + " Runden.",
                "Buff gekauft",
                JOptionPane.INFORMATION_MESSAGE
            );
        });

        return statusLabel;
    }

    // ═══════════════════════════════════════════════════════════════════════
    /** Wird aufgerufen wenn man zum Marktplatz wechselt */
    public void refresh()
    {
        kontoLabel.setText("Kontostand: " + spieler.getKontostand() + "$");

        lucky7StatusLabel.setText(
            buffManager.getLucky7Runden() > 0
            ? "✅ Aktiv: " + buffManager.getLucky7Runden() + " Runden"
            : "Nicht aktiv");

        jackpotStatusLabel.setText(
            buffManager.getJackpotBoostRunden() > 0
            ? "✅ Aktiv: " + buffManager.getJackpotBoostRunden() + " Runden"
            : "Nicht aktiv");

        luckySpinStatusLabel.setText(
            buffManager.getLuckySpinRunden() > 0
            ? "✅ Aktiv: " + buffManager.getLuckySpinRunden() + " Runden"
            : "Nicht aktiv");

        doubleUpStatusLabel.setText(
            buffManager.getDoubleUpRunden() > 0
            ? "✅ Aktiv: " + buffManager.getDoubleUpRunden() + " Runden"
            : "Nicht aktiv");
    }

    // ═══════════════════════════════════════════════════════════════════════
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
        for (Component c : getComponents())
        {
            if (c instanceof JPanel && ((JPanel)c).getName() != null
                && ((JPanel)c).getName().startsWith("karte"))
            {
                c.setBounds(startX,
                    startY + kartenIndex * (karteH + gapY),
                    karteW, karteH);

                // Preis und Button innerhalb der Karte rechts ausrichten
                for (Component k : ((JPanel)c).getComponents())
                {
                    if (k instanceof JLabel)
                    {
                        JLabel lbl = (JLabel) k;
                        if (lbl.getFont().getName().equals("Georgia")
                            && lbl.getText().contains("$"))
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

            // Titel zentrieren
            if (c instanceof JLabel
                && ((JLabel)c).getText().contains("Marktplatz"))
                c.setBounds(0, 90, w, 50);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    @Override
    protected void drawBackground(Graphics g)
    {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        updateLayoutPositions(getWidth(), getHeight());
        drawBackground(g);
    }
}