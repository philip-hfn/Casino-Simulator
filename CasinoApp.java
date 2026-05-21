import javax.swing.*;
import java.awt.*;

/**
 * Einstiegspunkt der Anwendung.
 * Erstellt das JFrame, die drei Panels und uebernimmt den Screen-Wechsel.
 */
public class CasinoApp implements HubPanel.ScreenSwitcher
{
    private JFrame    frame;
    private JPanel    container;
    private CardLayout cardLayout;

    private Spieler       spieler;
    private HubPanel      hubPanel;
    private RoulettePanel roulettePanel;
    private SlotPanel     slotPanel;

    public static void main(String[] args)
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        }
        catch (Exception e) { e.printStackTrace(); }

        SwingUtilities.invokeLater(() -> new CasinoApp(1920, 1080));
    }

    public CasinoApp(int w, int h)
    {
        spieler = new Spieler();   // Spieler() setzt kontostand bereits auf 1000

        hubPanel      = new HubPanel(spieler, this);
        roulettePanel = new RoulettePanel(spieler, this);
        slotPanel     = new SlotPanel(spieler, this);

        cardLayout = new CardLayout();
        container  = new JPanel(cardLayout);
        container.setPreferredSize(new Dimension(w, h));

        container.add(hubPanel,      "HUB");
        container.add(roulettePanel, "ROULETTE");
        container.add(slotPanel,     "SLOT");

        frame = new JFrame("Casino");
        frame.setLocation(100, 100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(container);
        frame.setResizable(true);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        switchTo(0);
    }

    @Override
    public void switchTo(int screen)
    {
        switch (screen)
        {
            case 0:
                hubPanel.refreshKontostand();
                cardLayout.show(container, "HUB");
                break;
            case 1:
                roulettePanel.refresh();
                cardLayout.show(container, "ROULETTE");
                break;
            case 2:
                slotPanel.refresh();
                cardLayout.show(container, "SLOT");
                break;
        }
    }
}