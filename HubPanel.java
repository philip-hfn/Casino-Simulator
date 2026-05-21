import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Hauptmenue-Panel des Casinos.
 */
public class HubPanel extends CasinoGUI
{
    private JButton rouletteButton;
    private JButton slotButton;
    private JLabel  kontostandLabel;

    private BufferedImage backgroundImage;
    private JButton shopButton;
    private JPanel  shopOverlay;
    private JPanel  shopPanel;

    public interface ScreenSwitcher
    {
        void switchTo(int screen);
    }
    private ScreenSwitcher screenSwitcher;

    public HubPanel(Spieler spieler, ScreenSwitcher screenSwitcher)
    {
        this.spieler        = spieler;
        this.screenSwitcher = screenSwitcher;
        setLayout(null);
        initComponents();
    }

    @Override
    protected void initComponents()
    {
        backgroundImage = loadImage("pics/background.png");

        rouletteButton = new JButton("Roulette");
        styleButton(rouletteButton);
        addHoverEffect(rouletteButton);
        add(rouletteButton);

        slotButton = new JButton("Slot-Maschine");
        styleButton(slotButton);
        addHoverEffect(slotButton);
        add(slotButton);
        
        shopButton = new JButton("💰 Geld aufladen");
        shopButton.setBackground(new Color(217, 131, 53));
        shopButton.setForeground(Color.WHITE);
        shopButton.setFont(new Font("Arial", Font.BOLD, 18));
        shopButton.setFocusPainted(false);
        shopButton.setBorderPainted(false);
        shopButton.setOpaque(true);
        shopButton.setContentAreaFilled(true);
        shopButton.addActionListener(e -> openShop());
        addHoverEffect(shopButton);
        add(shopButton);

        kontostandLabel = new JLabel("Kontostand: " + spieler.getKontostand() + "€");
        kontostandLabel.setFont(new Font("Georgia", Font.BOLD, 22));
        kontostandLabel.setForeground(Color.WHITE);
        kontostandLabel.setBackground(new Color(217, 131, 53));
        kontostandLabel.setOpaque(true);
        add(kontostandLabel);

        rouletteButton.addActionListener(e -> screenSwitcher.switchTo(1));
        slotButton    .addActionListener(e -> screenSwitcher.switchTo(2));
        buildShopOverlay();
        
        
    }

    @Override
    protected void updateLayoutPositions(int w, int h)
    {
        rouletteButton.setBounds(w / 2 - 100, h / 2 - 60, 200, 55);
        slotButton.setBounds(w / 2 - 100, h / 2 + 20, 200, 55);
        shopButton.setBounds(30, 75, 300, 40);
        if (shopOverlay.isVisible())
        {
            shopOverlay.setBounds(0, 0, w, h);
            shopPanel.setBounds((w - 500) / 2, (h - 430) / 2, 500, 430);
        }
        kontostandLabel.setBounds(30, 25, 300, 40);
    }

    @Override
    protected void drawBackground(Graphics g)
    {
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    public void refreshKontostand()
    {
        kontostandLabel.setText("Kontostand: " + spieler.getKontostand() + "€");
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        updateLayoutPositions(getWidth(), getHeight());
        drawBackground(g);
    }
    
        private void buildShopOverlay()
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
        shopOverlay.setOpaque(false);
        shopOverlay.setVisible(false);
        add(shopOverlay);

        shopOverlay.addMouseListener(new java.awt.event.MouseAdapter()
        {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e)
            {
                if (!shopPanel.getBounds().contains(e.getPoint()))
                    closeShop();
            }
        });

        shopPanel = new JPanel(null);
        shopPanel.setBackground(new Color(30, 30, 30));
        shopPanel.setBorder(BorderFactory.createLineBorder(new Color(217, 131, 53), 3));
        shopOverlay.add(shopPanel);

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

        addShopOption(0, "💳  Starter Paket", "1.000$", new Color(52, 120, 200));
        addShopOption(1, "🏦  Classic Paket",  "2.500$", new Color(100, 60, 180));
        addShopOption(2, "🎁  Premium Paket",  "5.000$", new Color(40, 167, 69));
    }

    private void addShopOption(int index, String titel, String untertitel, Color farbe)
    {
        int y = 90 + index * 110;

        JPanel option = new JPanel(null);
        option.setBackground(new Color(45, 45, 45));
        option.setBorder(BorderFactory.createLineBorder(farbe, 2));
        option.setBounds(30, y, 440, 90);
        shopPanel.add(option);

        JLabel streifen = new JLabel();
        streifen.setOpaque(true);
        streifen.setBackground(farbe);
        streifen.setBounds(0, 0, 8, 90);
        option.add(streifen);

        JLabel titelLabel = new JLabel(titel);
        titelLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titelLabel.setForeground(Color.WHITE);
        titelLabel.setBounds(20, 15, 280, 25);
        option.add(titelLabel);

        JLabel subLabel = new JLabel(untertitel);
        subLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subLabel.setForeground(new Color(180, 180, 180));
        subLabel.setBounds(20, 45, 280, 20);
        option.add(subLabel);

        JButton kaufBtn = new JButton("Kaufen");
        kaufBtn.setBackground(farbe);
        kaufBtn.setForeground(Color.WHITE);
        kaufBtn.setFont(new Font("Arial", Font.BOLD, 14));
        kaufBtn.setFocusPainted(false);
        kaufBtn.setBorderPainted(false);
        kaufBtn.setOpaque(true);
        kaufBtn.setBounds(330, 28, 100, 35);
        option.add(kaufBtn);

        if (index == 0 || index == 1)
        {
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
            kaufBtn.addActionListener(e ->
            {
                spieler.changeKontostand(5000);
                refreshKontostand();
                JOptionPane.showMessageDialog(
                    this,
                    "✅ Zahlung erfolgreich!\n5.000$ wurden deinem Konto gutgeschrieben.\nNeuer Kontostand: " + spieler.getKontostand() + "$",
                    "Zahlung erfolgreich",
                    JOptionPane.INFORMATION_MESSAGE
                );
            });
        }
    }

    private void openShop()
    {
        shopOverlay.setBounds(0, 0, getWidth(), getHeight());
        shopPanel.setBounds((getWidth() - 500) / 2, (getHeight() - 430) / 2, 500, 430);
        shopOverlay.setVisible(true);
        shopOverlay.repaint();
        setComponentZOrder(shopOverlay, 0);
        shopButton.setVisible(false);
    }

    private void closeShop()
    {
        shopOverlay.setVisible(false);
        shopButton.setVisible(true);
    }
}