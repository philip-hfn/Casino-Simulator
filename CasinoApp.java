import javax.swing.*;
import java.awt.*;

/**
 * Einstiegspunkt der Anwendung.
 * Verwaltet das Hauptfenster (JFrame), initialisiert die Spielkomponenten
 * und steuert den Wechsel zwischen den verschiedenen Menues und Spielen.
 */
public class CasinoApp implements HubPanel.ScreenSwitcher
{
    private JFrame frame;
    private JPanel container;
    private CardLayout cardLayout;

    private Spieler spieler;
    private HubPanel hubPanel;
    private RoulettePanel roulettePanel;
    private SlotPanel slotPanel;
    private BuffManager buffManager;
    private Marktplatz marktplatz;
    private MarktplatzPanel marktplatzPanel;
    public SoundManager sound;

    /**
     * Hauptmethode zum Starten der Anwendung.
     * @param args Standard-Argumente fuer die Kommandozeile.
     */
    public static void main(String[] args)
    {
        try
        {
            // Setzt den Look-and-Feel fuer ein einheitliches Design
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }

        // Startet die Applikation im Event-Dispatch-Thread der Swing-Bibliothek
        SwingUtilities.invokeLater(() -> new CasinoApp(1920, 1080));
    }

    /**
     * Konstruktor der Hauptanwendung. Initialisiert Daten, Panels und das Hauptfenster.
     * @param w Die Breite des Fensters.
     * @param h Die Hoehe des Fensters.
     */
    public CasinoApp(int w, int h)
    {
        // Spieler-Objekt anlegen; setzt Kontostand auf 1000
        spieler = new Spieler(); 
        
        // Initialisiert die Soundsteuerung
        this.sound = new SoundManager();
        
        // Bereitet die Logik-Objekte fuer Buffs und den Marktplatz vor
        buffManager = new BuffManager();
        marktplatz  = new Marktplatz(spieler, buffManager);

        // Erstellt die einzelnen grafischen Oberflaechen
        hubPanel = new HubPanel(spieler, this);
        roulettePanel = new RoulettePanel(spieler, buffManager, this);
        slotPanel = new SlotPanel(spieler, buffManager, this);
        marktplatzPanel = new MarktplatzPanel(spieler, marktplatz, buffManager, this);

        // Initialisiert das CardLayout, um zwischen den Panels zu wechseln
        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);
        container.setPreferredSize(new Dimension(w, h));

        // Fuegt alle Panels zum Container hinzu und vergibt Identifikationsnamen
        container.add(hubPanel,"HUB");
        container.add(roulettePanel,"ROULETTE");
        container.add(slotPanel,"SLOT");
        container.add(marktplatzPanel, "MARKTPLATZ");

        // Konfiguriert das Haupt-JFrame
        frame = new JFrame("Casino");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(container);
        frame.setResizable(true);
        frame.pack(); // Berechnet Groesse basierend auf Inhalten
        frame.setLocationRelativeTo(null); // Zentriert das Fenster auf dem Bildschirm
        frame.setVisible(true);

        // Startet die Anwendung beim Hub
        switchTo(0);
    }

    /**
     * Wechselt zwischen den verschiedenen Screens der Anwendung basierend auf einem Index.
     * @param screen Der Index des Ziel-Screens (0=Hub, 1=Roulette, 2=Slot, 3=Marktplatz).
     */
    @Override
    public void switchTo(int screen)
    {
        switch (screen)
        {
            case 0:
                // Aktualisiert den Kontostand im Hub, zeigt ihn an und spielt Musik
                hubPanel.refreshKontostand();
                cardLayout.show(container, "HUB");
                sound.hubMusic();
                break;
            case 1:
                // Wechsel zum Roulette-Panel
                roulettePanel.refresh();
                cardLayout.show(container, "ROULETTE");
                sound.rouletteMusic();
                break;
            case 2:
                // Wechsel zum Slot-Panel
                slotPanel.refresh();
                cardLayout.show(container, "SLOT");
                sound.slotMusic();
                break;
            case 3:
                // Wechsel zum Marktplatz-Panel
                marktplatzPanel.refresh();
                cardLayout.show(container, "MARKTPLATZ");
                sound.hubMusic();
                break;
        }
    }
}