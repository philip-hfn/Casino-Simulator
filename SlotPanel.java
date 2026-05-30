import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Grafische Oberflaeche fuer den Spielautomaten.
 *
 * Erweitert CasinoGUI um drei animierte Walzen, Konfetti-Effekte,
 * eine Jackpot-Textanimation und Sound-Feedback. Das Layout wird bei jedem
 * Paint-Zyklus relativ zur aktuellen Fenstergroesse neu berechnet.
 */
public class SlotPanel extends CasinoGUI
{
    //Innere Klasse: einzelne Walzenanzeige
    /**
     * Anzeigekomponente fuer eine einzelne Slot-Walze.
     * Zeichnet ein Bild skaliert auf die volle Panel-Flaeche.
     * setOpaque(false) laesst das Hintergrundbild des SlotPanels durchscheinen.
     */
    class SlotReel extends JPanel
    {
        //Das aktuell angezeigte Slot-Symbol
        private BufferedImage currentImage;
        /** 
         * Erstellt eine leere, transparente Walzenanzeige. 
         */
        public SlotReel() 
        { 
            setOpaque(false); 
        }

        /**
         * Setzt ein neues Slot-Symbol und loest sofort eine Neuzeichnung aus.
         * Wird waehrend der Animation in schneller Folge mit Zufallsbildern und abschliessend mit dem Ergebnisbild aufgerufen.
         * @param img das anzuzeigende Symbolbild
         */
        public void setImage(BufferedImage img)
        {
            this.currentImage = img;
            repaint();
        }

        /**
         * Zeichnet das aktuelle Symbol skaliert auf die gesamte Panel-Flaeche
         * @param g das Graphics-Objekt des aktuellen Paint-Zyklus
         */
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            if (currentImage != null)
                g.drawImage(currentImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    //Walzen-Anzeigen 
    private SlotReel slotReel1; //linke Walze
    private SlotReel slotReel2; //mittlere Walze
    private SlotReel slotReel3; //rechte Walze

    //Steuer- und Anzeige-Elemente 
    private JLabel gewinnLabel; // zeigt Spielergebnis oder Hinweistext
    private JButton drehenButton;// transparenter Button ueber dem Hebel im Bild
    private JTextField slotEinsatzFeld; // aktuell durch Slider ersetzt, nicht sichtbar
    private JSlider slotEinsatz; // Einsatzbetrag in $
    private JLabel slotEinsatzLabel; // spiegelt den aktuellen Slider-Wert
    private JLabel kontoLabel;       // aktueller Kontostand
    private JButton backButton;       // zurueck zum Hub

    //Bilder & Spiellogik
    private BufferedImage slotImage;// Hintergrundbild des Automaten
    private BufferedImage[] slotBilder; // Slot-Symbole, Index 1-9
    private Slot slot; // kapselt die Spiellogik und Gewinnberechnung
    private BuffManager buffManager; // prueft aktive Gewinn-Buffs

    // Sound
    private SoundManager sound; // spielt Dreh- und Jackpot-Sounds ab

    // Konfetti-Animation

    //Jedes Konfetti-Teilchen wird als int[5] gespeichert:
    //[0] x-Position, [1] y-Position, [2] Fallgeschwindigkeit,
    // [3] Farb-Index (0=rot, 1=gold), [4] Groesse in Pixeln
    private List<int[]> konfettiList  = new ArrayList<>();
    private Timer konfettiTimer; // bewegt Teilchen alle 30 ms nach unten
    private boolean konfettiAktiv = false; // steuert das Zeichnen in paintComponent

    //Jackpot-Text-Animation
    private int textGroesse = 0; // aktuelle Schriftgroesse (0 = nicht sichtbar)
    private boolean textWaechst = true; // true = waechst, false = schrumpft
    private Timer textTimer; // aktualisiert textGroesse alle 20 ms

    private HubPanel.ScreenSwitcher screenSwitcher;

    
    /**
     * Erstellt das Slot-Panel und verdrahtet alle Abhaengigkeiten.
     * @param spieler Spieler-Profil fuer Guthabenaenderungen
     * @param buffManager Buff-System zur Pruefung aktiver Gewinnmultiplikatoren
     * @param screenSwitcher Navigator fuer den Bildschirmwechsel
     */
    public SlotPanel(Spieler spieler, BuffManager buffManager, HubPanel.ScreenSwitcher screenSwitcher)
    {
        this.spieler = spieler;
        this.buffManager = buffManager;
        this.screenSwitcher = screenSwitcher;
        this.slot = new Slot(spieler);
        this.sound = new SoundManager();
        setLayout(null);
        initComponents();
    }

    
    /**
     * Erstellt, stylt und verdrahtet alle UI-Elemente.
     * Wird einmalig aus dem Konstruktor aufgerufen.
     */
    @Override
    protected void initComponents()
    {
        // Hintergrundbild und alle 9 Slot-Symbole laden (Index 1-9)
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

        // Walzen (alle starten mit Symbol 1
        slotReel1 = new SlotReel();
        slotReel1.setImage(slotBilder[1]);
        add(slotReel1);

        slotReel2 = new SlotReel();
        slotReel2.setImage(slotBilder[1]);
        add(slotReel2);

        slotReel3 = new SlotReel();
        slotReel3.setImage(slotBilder[1]);
        add(slotReel3);

        // Gewinn-Label (goldfarben)
        gewinnLabel = new JLabel("Druecke auf den Hebel!", SwingConstants.CENTER);
        gewinnLabel.setFont(new Font("Serif", Font.BOLD, 28));
        gewinnLabel.setForeground(new Color(184, 134, 11));
        add(gewinnLabel);

        // Kontostand-Label (goldfarben)
        kontoLabel = new JLabel("Kontostand: " + spieler.getKontostand() + "$");
        kontoLabel.setFont(new Font("Serif", Font.BOLD, 25));
        kontoLabel.setForeground(new Color(255, 255, 255));
        add(kontoLabel);

        // Einsatz-Label
        slotEinsatzLabel = new JLabel("Einsatz:");
        slotEinsatzLabel.setFont(new Font("Serif", Font.BOLD, 18));
        slotEinsatzLabel.setForeground(Color.WHITE);
        add(slotEinsatzLabel);

        // Einsatz-Textfeld (aktuell durch Slider ersetzt, nicht sichtbar)
        slotEinsatzFeld = new JTextField("10");
        slotEinsatzFeld.setFont(new Font("Serif", Font.PLAIN, 18));
        slotEinsatzFeld.setBackground(new Color(184, 134, 11));
        add(slotEinsatzFeld);

        // Einsatz-Slider 
        // Wertebereich: 0 bis aktueller Kontostand, Startwert in der Mitte
        slotEinsatz = new JSlider(0, spieler.getKontostand(), spieler.getKontostand() / 2);
        slotEinsatz.setMajorTickSpacing(500);
        slotEinsatz.setMinorTickSpacing(10);
        slotEinsatz.setPaintTicks(true);
        slotEinsatz.setPaintLabels(true);
        slotEinsatz.setBackground(Color.WHITE);
        slotEinsatz.setForeground(Color.BLACK);
        slotEinsatz.setFont(new Font("Arial", Font.BOLD, 14));
        add(slotEinsatz);

        // Drehen-Button (unsichtbar, liegt exakt ueber dem Hebel im Bild)
        drehenButton = new JButton();
        styleButton(drehenButton);
        drehenButton.setContentAreaFilled(false); // kein Hintergrundfill
        drehenButton.setBorderPainted(false); // kein Rahmen
        drehenButton.setFocusPainted(false); // kein Fokus-Rechteck
        drehenButton.setRolloverEnabled(false); // kein Hover-Effekt
        drehenButton.setOpaque(false); // vollstaendig transparent
        add(drehenButton);

        // Einsatz-Label live aktualisieren, wenn Slider bewegt wird
        slotEinsatz.addChangeListener(e ->
                slotEinsatzLabel.setText("Einsatz: " + slotEinsatz.getValue() + " $"));

        // Drehen-Button ActionListener
        drehenButton.addActionListener(e ->
        {
            sound.spinEffekt();
            
            // Einsatz aus Slider lesen (try-catch fuer zukuenftige Textfeld-Nutzung)
            int slotEinsatzTemp;
            try
            {
                slotEinsatzTemp = slotEinsatz.getValue();
            }
            catch (NumberFormatException ex)
            {
                gewinnLabel.setText("Ungueltige Eingabe!");
                return;
            }

            // Spiellogik ausfuehren: prueft Einsatz und berechnet Ergebnis
            if (!slot.spielen(slotEinsatzTemp))
            {
                gewinnLabel.setText("Ungueltiger Einsatz!");
                return;
            }

            // Debug-Ausgabe der Slot-Ergebnisse in der Konsole
            System.out.println("Slot1: " + slot.getSlot1()
                    + " | Slot2: " + slot.getSlot2()
                    + " | Slot3: " + slot.getSlot3()
                    + " | Gewinn: " + slot.getGewinn());

            int ziel1 = slot.getSlot1();
            int ziel2 = slot.getSlot2();
            int ziel3 = slot.getSlot3();

            // Buff-Multiplikator VOR der Animation merken, da Buffs sich
            // waehrend der Laufzeit aendern koennten
            int gewinnMultiplikator = 1;
            if (buffManager.isDoubleUpAktiv()) gewinnMultiplikator *= 2;

            // Button sperren und Animation der drei Walzen starten
            // (Walze 2 verzoegert um 400 ms, Walze 3 um 800 ms)
            drehenButton.setEnabled(false);
            gewinnLabel.setText("...");
            starteAnimation(slotReel1, ziel1, 0);
            starteAnimation(slotReel2, ziel2, 400);
            starteAnimation(slotReel3, ziel3, 800);

            // Ergebnis-Timer: feuert wenn alle drei Walzen zum Stillstand
            // gekommen sind (800 ms Offset + 30 Ticks x 80 ms + 500 ms Puffer)
            int finalMulti = gewinnMultiplikator;
            Timer ergebnisTimer = new Timer(800 + 30 * 80 + 500, ev ->
            {
                // Buff-Boni berechnen und gutschreiben
                int bonus = 0;
                if (slot.getGewinn() > 0)
                {
                    // DoubleUp: verdoppelt den Grundgewinn
                    if (finalMulti > 1)
                        bonus += slot.getGewinn() * (finalMulti - 1);

                    // Lucky7: dreifacher Bonus wenn eine 7 auf einer Walze liegt
                    if (buffManager.isLucky7Aktiv()
                            && (slot.getSlot1() == 7 || slot.getSlot2() == 7 || slot.getSlot3() == 7))
                        bonus += slot.getGewinn() * 2;

                    // JackpotBoost: verdoppelt bei echtem 777-Jackpot
                    if (buffManager.isJackpotBoostAktiv() && slot.super7IchKaufDasKasino())
                        bonus += slot.getGewinn();
                }
                if (bonus > 0) spieler.changeKontostand(bonus);
                buffManager.slotRundeGespielt();

                // UI nach Runde aktualisieren 
                kontoLabel.setText("Kontostand: " + spieler.getKontostand() + "$");
                // Slider-Maximum und Tick-Abstaende an neuen Kontostand anpassen
                slotEinsatz.setMaximum(spieler.getKontostand());
                slotEinsatz.setMajorTickSpacing(Math.max(1, spieler.getKontostand() / 5));
                slotEinsatz.setLabelTable(slotEinsatz.createStandardLabels(Math.max(1, spieler.getKontostand() / 5)));

                // Ergebnis-Feedback (Text + Sound + Effekte)
                if (slot.super7IchKaufDasKasino())
                {
                    // Drei 7er: Jackpot mit allen Effekten
                    gewinnLabel.setText("JACKPOT 777 !!!"+(bonus>0?"(+"+ bonus +"$ Buff!)":""));
                    konfettiAnimation();
                    jackpotTextAnimation();
                    sound.jackpotEffekt();
                }
                else if (slot.hauptGewinn())
                {
                    // Drei gleiche Symbole
                    gewinnLabel.setText("Grosser Gewinn!" +(bonus>0?"(+"+ bonus +"$ Buff!)":""));
                    konfettiAnimation();
                    sound.jackpotEffekt();
                }
                else if (slot.kleinerGewinn())
                {
                    // Zwei gleiche Symbole
                    gewinnLabel.setText("Kleiner Gewinn!" +(bonus>0?"(+"+ bonus +"$ Buff!)":""));
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

    
    /**
     * Startet die Konfetti-Animation bei einem Gewinn.
     * Erzeugt 100 Teilchen mit zufaelliger x-Position, Fallgeschwindigkeit, Farbe und Groesse
     * Ein Stop-Timer beendet die Animation nach 4 Sekunden
     * Der Bewegungs-Timer aktualisiert alle 30 ms die y-Position jedes Teilchens, Teilchen die unten herausfallen werden oben neu eingesetzt.
     */
    private void konfettiAnimation()
    {
        konfettiList.clear();
        for (int i = 0; i < 100; i++)
        {
            // int[]{x, y, Geschwindigkeit, Farbindex, Groesse}
            konfettiList.add(new int[]{
                    (int)(Math.random() * getWidth()), // zufaellige x-Startposition
                    (int)(Math.random() * -200), // startet oberhalb des sichtbaren Bereichs
                    (int)(Math.random() * 5 + 3), // Fallgeschwindigkeit: 3-7 px pro Tick
                    (int)(Math.random() * 2), // Farbindex: 0=rot, 1=gold
                    (int)(Math.random() * 15 + 5) // Groesse: 5-19 px
            });
        }
        konfettiAktiv = true;

        // Konfetti nach 4 Sekunden automatisch deaktivieren
        Timer stopTimer = new Timer(4000, e ->
        {
            konfettiAktiv = false;
            konfettiList.clear();
        });
        stopTimer.setRepeats(false);
        stopTimer.start();

        // Bewegungs-Timer: bewegt jedes Teilchen nach unten und setzt es
        // bei Unterschreitung der Panel-Hoehe oben wieder ein
        konfettiTimer = new Timer(30, e ->
        {
            for (int[] k : konfettiList)
            {
                k[1] += k[2]; // y-Position um Geschwindigkeit erhoehen
                if (k[1] > getHeight()) k[1] = -20; // oben wieder einsetzen
            }
        });
        konfettiTimer.start();
    }

    
    /**
     * Startet die pulsierende Jackpot-Textanimation (nur bei 777).
     *
     * Die Schriftgroesse waechst in 3er-Schritten von 10 auf 120 px
     * und schrumpft dann wieder auf 0. Der Text wird direkt in
     * paintComponent() gezeichnet.
     */
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
                if (textGroesse >= 120)
                {    
                 textWaechst = false; // Wendepunkt: schrumpfen
                }
            }
            else
            {
                textGroesse -= 3;
                if (textGroesse <= 10)
                {
                    textTimer.stop();
                    textGroesse = 0; // Text ausblenden
                }
            }
        });
        textTimer.start();
    }

    
    /**
     * Startet die Dreh-Animation einer einzelnen Walze.
     *
     * Ablauf: 20 Ticks mit schnellen Zufallsbildern bei gleichbleibender
     * Geschwindigkeit, danach Abbremsen (Delay +15 ms pro Tick),
     * ab Tick 30 wird das Zielbild gesetzt und der Timer gestoppt.
     * Die Verzoegerung staffelt die drei Walzen zeitlich.
     * @param reel die Walzen-Komponente, die animiert werden soll
     * @param zielZahl Index (1-9) des Ergebnis-Symbols
     * @param verzoegerung Startverzoegerung in ms (0 / 400 / 800 fuer Walze 1-3)
     */
    private void starteAnimation(SlotReel reel, int zielZahl, int verzoegerung)
    {
        int[] counter = {0};  // Tick-Zaehler (als Array, da in Lambda verwendet)
        int[] geschwindigkeit = {50}; // aktuelles Timer-Intervall in ms

        Timer timer = new Timer(geschwindigkeit[0], null);
        timer.addActionListener(e ->
        {
            // Zufaelliges Symbol anzeigen (Spinning-Effekt)
            int zufallsZahl = (int)(Math.random() * 9 + 1);
            reel.setImage(slotBilder[zufallsZahl]);
            counter[0]++;

            // Ab Tick 21: schrittweise abbremsen
            if (counter[0] > 20)
            {
                geschwindigkeit[0] += 15;
                timer.setDelay(geschwindigkeit[0]);
            }
            // Ab Tick 31: Zielbild einrasten und Animation beenden
            if (counter[0] > 30)
            {
                reel.setImage(slotBilder[zielZahl]);
                timer.stop();
            }
        });

        // Start der Animation um verzoegerung ms verschieben
        Timer startTimer = new Timer(verzoegerung, e -> timer.start());
        startTimer.setRepeats(false);
        startTimer.start();
    }

    
    /**
     * Positioniert alle Komponenten relativ zur aktuellen Fenstergroesse.
     * Die drei Walzen werden zentriert mit gleichmaessigem Abstand platziert.
     * @param w aktuelle Panel-Breite in Pixeln
     * @param h aktuelle Panel-Hoehe in Pixeln
     */
    @Override
    protected void updateLayoutPositions(int w, int h)
    {
        backButton.setBounds(30, 25, 120, 45);

        // Walzen: quadratisch (reelW x reelW), zentriert mit gapX Abstand
        int reelY = (int)(h * 0.53);
        int reelW = (int)(w * 0.08);
        int reelH = reelW;
        int gapX = (int)(w * 0.045);
        int totalW = 3 * reelW + 2 * gapX;
        int startX = (w - totalW) / 2; // linker Rand fuer zentrierte Gruppe

        slotReel1.setBounds(startX,reelY, reelW, reelH);
        slotReel2.setBounds(startX + reelW + gapX, reelY, reelW, reelH);
        slotReel3.setBounds(startX + 2 * (reelW + gapX), reelY, reelW, reelH);

        kontoLabel .setBounds(w/ 2 - 100, (int)(h * 0.05), 400, 40);
        gewinnLabel.setBounds(w/ 2 - 300, (int)(h * 0.35), 600, 40);

        int inputY = reelY + reelH + 120; // Slider unterhalb der Walzen
        slotEinsatzLabel.setBounds((int)(w * 0.46), (int)(h * 0.725), 200, 35);
        slotEinsatz.setBounds((int)(w * 0.40), inputY, 250, 60);

        // Transparenter Button exakt ueber dem Hebel im Hintergrundbild
        drehenButton.setBounds((int)(w * 0.735), (int)(h * 0.22), 60, (int)(h * 0.36));
    }

    
    /**
     * Zeichnet das Hintergrundbild des Spielautomaten.
     * @param g das Graphics-Objekt des aktuellen Paint-Zyklus
     */
    @Override
    protected void drawBackground(Graphics g)
    {
        g.drawImage(slotImage, 0, 0, getWidth(), getHeight(), this);
    }

    
    /**
     * Einstiegspunkt des Swing-Paint-Zyklus.
     * Zeichnet zusaetzlich zum Hintergrund Konfetti-Teilchen und den
     * pulsierenden Jackpot-Text, die beide ueber allen anderen Komponenten liegen.
     *
     * @param g das Graphics-Objekt des aktuellen Paint-Zyklus
     */
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        updateLayoutPositions(getWidth(), getHeight());
        drawBackground(g);

        //Konfetti ueber allem zeichnen 
        if (konfettiAktiv)
        {
            Color[] farben = {Color.RED, new Color(212, 175, 55)}; // rot und gold
            for (int[] k : konfettiList)
            {
                g.setColor(farben[k[3]]);// Farbe aus Index
                g.fillRect(k[0], k[1], k[4], k[4]); // quadratisches Teilchen
            }
        }

        // Jackpot-Text zentriert in Bildschirmmitte
        if (textGroesse > 0)
        {
            g.setFont(new Font("Arial", Font.BOLD, textGroesse));
            g.setColor(new Color(255, 215, 0)); // Gold
            FontMetrics fm = g.getFontMetrics();
            String text = "JACKPOT 777 !!!";
            int x = (getWidth() - fm.stringWidth(text)) / 2; // horizontal zentrieren
            int y = getHeight() / 2;
            g.drawString(text, x, y);
        }
    }

    /**
     * Aktualisiert Kontostand-Label und Einsatz-Slider nach jeder Spielrunde
     * sowie beim Wechsel auf dieses Panel.
     * Stellt sicher, dass der Slider-Wert das aktuelle Maximum nicht uebersteigt.
     */
    public void refresh()
    {
        int max = spieler.getKontostand();
        kontoLabel.setText("Kontostand: " + max + "$");

        // Slider-Maximum und Tick-Abstaende an neuen Kontostand anpassen
        slotEinsatz.setMaximum(max);
        int schritt = Math.max(1, max / 5);
        slotEinsatz.setMajorTickSpacing(schritt);
        slotEinsatz.setLabelTable(slotEinsatz.createStandardLabels(schritt));

        // Slider-Wert auf neues Maximum begrenzen, falls er darueber liegt
        if (slotEinsatz.getValue() > max)
            slotEinsatz.setValue(max);
    }
}