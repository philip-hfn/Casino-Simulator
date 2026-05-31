import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Hauptmenue-Panel des Casinos
 * Dient als zentrale Navigationn (Hub) zwischen dem Spielen und dem Marktplatz
 * Beinhaltet zudem einen Shop zum Aufladen von Credits
 */
public class HubPanel extends CasinoGUI
{
    // Buttons für die Hauptnavigation des Casinos
    private JButton rouletteButton;
    private JButton slotButton;
    private JButton marktplatzButton;
    private JButton infoButton;

    // Label zur permanenten Anzeige des aktuellen Kontostands oben links
    private JLabel  kontostandLabel;

    // Speicher für das geladene Hintergrundbild vom Hauptmenu
    private BufferedImage backgroundImage;

    // UI-Elemente für das Shop-System
    private JButton shopButton; // Button zum Oeffnen des Shops
    private JPanel  shopOverlay; // Halbtransparenter Vollbild-Hintergrund zur Abdunkelung
    private JPanel  shopPanel; // Das eigentliche Shop-Fenster in der Mitte

    /**
     * Interface fuer den Bildschirm-Wechsel
     * Ermoeglicht es dem Hub, dem Hauptfenster (JFrame) mitzuteilen, welches Panel angezeigt werden soll
     */
    public interface ScreenSwitcher
    {
        void switchTo(int screen);
    }
    private ScreenSwitcher screenSwitcher; // Instanz des Switchers

    /**
     * Konstruktor des Hauptmenus
     * @param spieler Das aktuelle Spieler-Objekt für den Kontostand
     * @param screenSwitcher Die Logik zum Wechseln der Ansichten
     */
    public HubPanel(Spieler spieler, ScreenSwitcher screenSwitcher)
    {
        this.spieler        = spieler; // Verknüpft den globalen Spieler
        this.screenSwitcher = screenSwitcher; // Verknüpft den Screen-Manager
        setLayout(null); // Nutzt absolutes Layout (Null-Layout) für freie Pixel-Positionierung
        initComponents(); // Initialisiert alle UI-Komponenten
    }

    /**
     * Erstellt, stylt und verdrahtet alle grafischen Komponenten des Hauptmenus
     */
    @Override
    protected void initComponents()
    {
        backgroundImage = loadImage("pics/background.png");// Laedt das Hintergrundbild ueber die Hilfsmethode der Oberklasse

        //Roulette-Button
        rouletteButton = new JButton("Roulette"); 
        styleButton(rouletteButton);// Nutzt das einheitliche Design aus CasinoGUI
        addHoverEffect(rouletteButton);// Aktiviert Hovereffekt bei Maus-Kontakt
        add(rouletteButton); // Fuegt den Button dem Panel hinzu

        //Slot-Maschine-Button
        slotButton = new JButton("Slot-Maschine");
        styleButton(slotButton);
        addHoverEffect(slotButton);
        add(slotButton);

        //Shop-Button (Geld aufladen)
        shopButton = new JButton("Geld aufladen");
        shopButton.setBackground(new Color(217, 131, 53));
        shopButton.setForeground(Color.WHITE);
        shopButton.setFont(new Font("Arial", Font.BOLD, 18));
        shopButton.setFocusPainted(false);
        shopButton.setBorderPainted(false);
        shopButton.setOpaque(true);
        shopButton.setContentAreaFilled(true);
        shopButton.addActionListener(e -> openShop());// Klick oeffnet das Shop-Overlay
        addHoverEffect(shopButton);
        add(shopButton);

        //Marktplatz-Button
        marktplatzButton = new JButton("Marktplatz");
        styleButton(marktplatzButton);
        marktplatzButton.addActionListener(e -> screenSwitcher.switchTo(3));// Leitet beim Klick 3 (Marktplatz) an den ScreenSwitcher weiter
        addHoverEffect(marktplatzButton);
        add(marktplatzButton);

        //Informations-Button
        infoButton = new JButton("Anleitung");
        styleButton(infoButton);
        addHoverEffect(infoButton);
        infoButton.addActionListener(e -> zeigeInfo());
        add(infoButton);

        //Kontostand-Anzeige
        kontostandLabel = new JLabel("Kontostand: " + spieler.getKontostand() + "$");
        kontostandLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        kontostandLabel.setForeground(Color.WHITE);
        kontostandLabel.setBackground(new Color(217, 131, 53));
        kontostandLabel.setOpaque(true);// Verhindert, dass das Label transparent ist
        add(kontostandLabel);

        //Verknuepfung der Spiel-Buttons mit dem Screen-Wechsler 
        rouletteButton.addActionListener(e -> screenSwitcher.switchTo(1)); // ID 1 = Roulette
        slotButton    .addActionListener(e -> screenSwitcher.switchTo(2)); // ID 2 = Slot-Maschine

        buildShopOverlay(); // Baut das Shop-Overlay im Hintergrund auf (bleibt vorerst unsichtbar)

    }

    /**
     * Berechnet und setzt die Positionen und Groeßen aller Komponenten dynamisch
     * Wird bei jedem Neuzeichnen aufgerufen
     */
    @Override
    protected void updateLayoutPositions(int w, int h)
    {
        rouletteButton.setBounds(w / 2 - 100, h / 2 - 60, 200, 55);
        slotButton.setBounds(w / 2 - 100, h / 2 + 20, 200, 55);
        shopButton.setBounds(30, 75, 300, 40);
        marktplatzButton.setBounds(w / 2 - 100, h / 2 + 100, 200, 55);
        infoButton.setBounds(w / 2 - 100, h / 2 + 180, 200, 55);
        if (shopOverlay.isVisible())
        {
            shopOverlay.setBounds(0, 0, w, h);
            shopPanel.setBounds((w - 500) / 2, (h - 430) / 2, 500, 430);
        }
        kontostandLabel.setBounds(30, 25, 300, 40);
    }

    /**
     * Zeichnet das geladene Hintergrundbild ueber die gesamte Flaeche des Panels
     */
    @Override
    protected void drawBackground(Graphics g)
    {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    /**
     * Aktualisiert den Text des Kontostand-Labels mit dem aktuellen Wert aus dem Spieler-Objekt
     * Muss von außen aufgerufen werden, wenn der Spieler Geld gewinnt oder verliert
     */
    public void refreshKontostand()
    {
        kontostandLabel.setText("Kontostand: " + spieler.getKontostand() + "$");
    }

    /**
     * Ueberschreibt die standardmaeßige Zeichenmethode von Swing
     * Sorgt fuer die korrekte Reihenfolge: Positionen updaten -> Hintergrund zeichnen -> Komponenten vermitteln
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);// Führt Standard-Zeichenoperationen aus
        updateLayoutPositions(getWidth(), getHeight());// Aktualisiert Positionsdaten basierend auf aktueller Fenstergroeße
        drawBackground(g); // Zeichnet das Casino-Hintergrundbild
    }

    private void zeigeInfo()
    {
        JDialog dialog = new JDialog();
        dialog.setTitle("Info");
        dialog.setSize(600, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setModal(true);
        dialog.setLayout(new BorderLayout());

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Arial", Font.BOLD, 15));

        // Tab 1: Spielanleitung
        JTextArea anleitung = new JTextArea(
                "Willkommen im Casino!\n" +
                "─────────────────────────────\n\n" +
                "KONTO\n" +
                "Du startest mit 1.000$. Ueber 'Geld aufladen'\n" +
                "im Hub kannst du dein Konto aufladen.\n\n" +
                "ROULETTE\n" +
                "1. Einsatz per Regler waehlen.\n" +
                "2. Wette eingeben:\n" +
                "   - Über die Button kann deine Wette ausgewählt werden\n" +
                "   - Über den Reset Button lassen sich die Wettoptionen zurücksetzen\n" +
                "   - Zahl: 1-36 eingeben -> Gewinn x36\n" +
                "   - Nicht setzen: Zahl auf 0 lassen\n" +
                "   Achtung: Zahl und Farbe/Paritaet koennen nicht kombiniert werden.\n" +
                "3. 'SPIN' druecken.\n\n" +
                "SLOT-MASCHINE\n" +
                "1. Einsatz per Regler waehlen.\n" +
                "2. 'SPIN' druecken und Glueck haben!\n" +
                "   - 2 gleiche Symbole -> Gewinn x3\n" +
                "   - 3 gleiche Symbole -> Gewinn x50\n" +
                "   - Siebener-Kombos -> Sondergewinne!\n\n" +
                "MARKTPLATZ\n" +
                "Kaufe Buffs, die deine Gewinne boosten:\n" +
                "   - Lucky 7 - 7er Gewinn x3 (5 Runden, 500$)\n" +
                "   - Jackpot Boost - Jackpot x2 (3 Runden, 800$)\n" +
                "   - Lucky Spin - Roulette +50% (5 Runden, 600$)\n" +
                "   - Double Up - naechster Slot x2 (1 Runde, 1000$)\n\n" +
                "Mit 'Back' kommst du jederzeit zum Hub zurueck."
        );
        anleitung.setFont(new Font("Arial", Font.PLAIN, 14));
        anleitung.setEditable(false);
        anleitung.setLineWrap(true);
        anleitung.setWrapStyleWord(true);
        anleitung.setMargin(new Insets(12, 14, 12, 14));
        tabs.addTab("Spielanleitung", new JScrollPane(anleitung));

        // Tab 2: Hinweis
        JTextArea hinweis = new JTextArea(
                "Wichtiger Hinweis zum Glücksspiel\n" +
                "─────────────────────────────────────\n\n" +
                "Zufall hat kein Gedächtnis.\n\n" +
                "Nur weil eine Farbe oder Zahl mehrmals hintereinander\n" +
                "nicht kam, wird sie beim nächsten Mal nicht automatisch\n" +
                "wahrscheinlicher. Dieser Denkfehler heisst:\n\n" +
                "   Gambler's Fallacy\n\n" +
                "Er kann dazu führen, dass man zu lange weiterspielt\n" +
                "oder Verluste unbedingt zurückholen will - obwohl\n" +
                "die Gewinnchance jede Runde gleich bleibt.\n\n" +
                "─────────────────────────────────────\n\n" +
                "Gerade für Junge Menschen gilt zu beachten:\n\n" +
                "Glücksspiel ist keine sichere Möglichkeit,\n" +
                "Geld zu gewinnen - sondern riskante Unterhaltung\n" +
                "mit realer Verlustgefahr.\n\n" +
                "Spiele verantwortungsvoll."
        );
        hinweis.setFont(new Font("Arial", Font.PLAIN, 14));
        hinweis.setEditable(false);
        hinweis.setLineWrap(true);
        hinweis.setWrapStyleWord(true);
        hinweis.setMargin(new Insets(12, 14, 12, 14));
        tabs.addTab("Hinweis", new JScrollPane(hinweis));

        JButton schliessen = new JButton("Schliessen");
        schliessen.addActionListener(e -> dialog.dispose());

        dialog.add(tabs, BorderLayout.CENTER);
        dialog.add(schliessen, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    /**
     * Erstellt das versteckte Shop-Overlay 
     */ private void buildShopOverlay()
    {
        shopOverlay = new JPanel(null)
        {
            @Override
            protected void paintComponent(Graphics g)
            {
                g.setColor(new Color(0, 0, 0, 160));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        shopOverlay.setOpaque(false);   // Verhindert, dass das Panel standardmaeßig grau gezeichnet wird
        shopOverlay.setVisible(false); // Startet standardmaeßig unsichtbar
        add(shopOverlay);   // Fuegt es dem Hauptpanel hinzu

        // Schließt den Shop, wenn der Benutzer auf die abgedunkelte Flaeche außerhalb des ShopPanels klickt
        shopOverlay.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e)
            {
                if (!shopPanel.getBounds().contains(e.getPoint()))
                {
                    closeShop();
                }
            }
        });

        //Shopfenster
        shopPanel = new JPanel(null);
        shopPanel.setBackground(new Color(30, 30, 30));
        shopPanel.setBorder(BorderFactory.createLineBorder(new Color(217, 131, 53), 3));
        shopOverlay.add(shopPanel);// Wird auf dem abgedunkelten Overlay platziert

        //Shop-Titel
        JLabel titel = new JLabel("💳  Geld aufladen", SwingConstants.CENTER);
        titel.setFont(new Font("Georgia", Font.BOLD, 26));
        titel.setForeground(new Color(217, 131, 53));
        titel.setBounds(0, 20, 500, 40);
        shopPanel.add(titel);

        JLabel linie = new JLabel();
        linie.setOpaque(true);
        linie.setBackground(new Color(217, 131, 53));
        linie.setBounds(30, 70, 440, 2);
        shopPanel.add(linie);

        //Schließen-Button
        JButton closeBtn = new JButton("✕");
        closeBtn.setBackground(new Color(200, 50, 50));
        closeBtn.setForeground(Color.WHITE);
        closeBtn.setFont(new Font("Arial", Font.BOLD, 16));
        closeBtn.setFocusPainted(false);
        closeBtn.setBorderPainted(false);
        closeBtn.setOpaque(true);
        closeBtn.setBounds(450, 10, 40, 35);
        closeBtn.addActionListener(e -> closeShop());
        shopPanel.add(closeBtn);

        //Drei Kauf-Optionen im Shop-Fenster
        addShopOption(0, "💳  Starter Paket", "1.000$", new Color(217, 131, 53));
        addShopOption(1, "💸  Classic Paket",  "2.500$", new Color(217, 131, 53));
        addShopOption(2, "🏦  Premium Paket",  "5.000$", new Color(217, 131, 53));
    }

    /**
     * Hilfsmethode, um eine einzelne Produktreihe (Paket) im Shop zu generieren
     * @param index Reihenfolge-Index (0, 1, 2) zur Berechnung der Y-Koordinate
     * @param titel Name des Pakets
     * @param untertitel Geldbetrag als Text
     * @param farbe Akzentfarbe für Ränder und Knöpfe
     */
    private void addShopOption(int index, String titel, String untertitel, Color farbe)
    {
        // Berechnet die Y-Position basierend auf dem Index
        int y = 90 + index * 110;

        // Erstellt die Container-Box für dieses Paket
        JPanel option = new JPanel(null);
        option.setBackground(new Color(45, 45, 45));
        option.setBorder(BorderFactory.createLineBorder(farbe, 2));
        option.setBounds(30, y, 440, 90);
        shopPanel.add(option);

        // Ein Deko-Streifen ganz links in der Produktbox
        JLabel streifen = new JLabel();
        streifen.setOpaque(true);
        streifen.setBackground(farbe);
        streifen.setBounds(0, 0, 8, 90);
        option.add(streifen);

        // Produktname anzeigen
        JLabel titelLabel = new JLabel(titel);
        titelLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titelLabel.setForeground(Color.WHITE);
        titelLabel.setBounds(20, 15, 280, 25);
        option.add(titelLabel);

        // Beschreibung und Wert anzeigen
        JLabel subLabel = new JLabel(untertitel);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subLabel.setForeground(new Color(180, 180, 180));
        subLabel.setBounds(20, 45, 280, 20);
        option.add(subLabel);

        // Der Kaufen-Button für dieses Paket
        JButton kaufBtn = new JButton("Kaufen");
        kaufBtn.setBackground(farbe);
        kaufBtn.setForeground(Color.WHITE);
        kaufBtn.setFont(new Font("Arial", Font.BOLD, 14));
        kaufBtn.setFocusPainted(false);
        kaufBtn.setBorderPainted(false);
        kaufBtn.setOpaque(true);
        kaufBtn.setBounds(330, 28, 100, 35);
        option.add(kaufBtn);

        // Logik-Zuweisung für die Buttons
        if (index == 0 || index == 1)
        {
            // Starter- und Classic-Paket werfen absichtlich eine Fehlermeldung aus
            kaufBtn.addActionListener(e ->
                JOptionPane.showMessageDialog(
                    this,
                    "Keine Karte hinterlegt!\nBitte hinterlege zuerst eine Zahlungsmethode.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
            );
        }
        else
        {
            // Das Premium Paket (Index 2) funktioniert als "Cheat" und gibt dem Spieler sofort 5000$ 
            kaufBtn.addActionListener(e ->
            {
                spieler.changeKontostand(5000);// Erhöht den Kontostand des Spielers um 5000
                refreshKontostand();// Aktualisiert die Anzeige direkt im Menü
                JOptionPane.showMessageDialog(
                    this,
                    "✅ Zahlung erfolgreich!\n5.000$ wurden deinem Konto gutgeschrieben.\nNeuer Kontostand: " + spieler.getKontostand() + "$",
                    "Zahlung erfolgreich",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
                );
        }
    }

    /**
     * Macht das Shop-Overlay sichtbar und berechnet die Positionen neu
     * Schiebt das Overlay in der Tiefenordnung ganz nach vorne
     */
    private void openShop()
    {
        shopOverlay.setBounds(0, 0, getWidth(), getHeight()); // Streckt Overlay auf aktuelle Fenstergröße
        shopPanel.setBounds((getWidth() - 500) / 2, (getHeight() - 430) / 2, 500, 430); // Zentriert das Fenster
        shopOverlay.setVisible(true);// Macht das Overlay sichtbar
        shopOverlay.repaint(); //Neuzeichnen
        setComponentZOrder(shopOverlay, 0);// Schiebt das Overlay auf Layer 0
        shopButton.setVisible(false);// Versteckt den ursprueglichen Shop-Button
    }

    /**
     * Schließt das Shop-Overlay und bringt den normalen Shop-Button wieder zum Vorschein
     */
    private void closeShop()
    {
        shopOverlay.setVisible(false);
        shopButton.setVisible(true);
    }
}