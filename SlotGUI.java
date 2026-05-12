import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;

public class SlotGUI extends JPanel implements ActionListener
{
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;

    private JLabel gewinnLabel;
    private JLabel kontoLabel;

    private JButton drehenButton;

    private JTextField einsatzFeld;

    private Slot spiel;

    private BufferedImage backgroundImage;
    private BufferedImage slotImage;

    public SlotGUI()
    {
        spiel = new Slot();

        setLayout(null);

        // =========================
        // BILDER LADEN
        // =========================

        backgroundImage = loadImage("pics/background.png");
        slotImage = loadImage("pics/slot.png");

        // =========================
        // SLOT ZAHLEN
        // =========================

        Font schrift = new Font("Arial", Font.BOLD, 70);

        label1 = new JLabel("0");
        label2 = new JLabel("0");
        label3 = new JLabel("0");

        label1.setFont(schrift);
        label2.setFont(schrift);
        label3.setFont(schrift);

        label1.setForeground(Color.WHITE);
        label2.setForeground(Color.WHITE);
        label3.setForeground(Color.WHITE);

        label1.setBounds(520,260,100,100);
        label2.setBounds(670,260,100,100);
        label3.setBounds(820,260,100,100);

        add(label1);
        add(label2);
        add(label3);

        // =========================
        // GEWINN LABEL
        // =========================

        gewinnLabel = new JLabel("Drücke auf Drehen!");
        gewinnLabel.setBounds(500,150,500,50);
        gewinnLabel.setFont(new Font("Arial", Font.BOLD, 30));
        gewinnLabel.setForeground(Color.WHITE);

        add(gewinnLabel);

        // =========================
        // KONTOSTAND
        // =========================

        kontoLabel = new JLabel(
            "Kontostand: " +
            spiel.getKontostand() +
            " €"
        );

        kontoLabel.setBounds(50,50,400,40);
        kontoLabel.setFont(new Font("Arial", Font.BOLD, 30));
        kontoLabel.setForeground(Color.WHITE);

        add(kontoLabel);

        // =========================
        // EINSATZ
        // =========================

        JLabel einsatzLabel = new JLabel("Einsatz:");
        einsatzLabel.setBounds(500,500,150,40);
        einsatzLabel.setFont(new Font("Arial", Font.BOLD, 25));
        einsatzLabel.setForeground(Color.WHITE);

        add(einsatzLabel);

        einsatzFeld = new JTextField("10");
        einsatzFeld.setBounds(650,500,120,40);
        einsatzFeld.setFont(new Font("Arial", Font.BOLD, 20));

        add(einsatzFeld);

        // =========================
        // BUTTON
        // =========================

        drehenButton = new JButton("DREHEN");
        drehenButton.setBounds(800,500,180,50);

        drehenButton.setBackground(Color.RED);
        drehenButton.setForeground(Color.WHITE);
        drehenButton.setFont(new Font("Arial", Font.BOLD, 25));

        drehenButton.addActionListener(this);

        add(drehenButton);
    }

    // =========================
    // BILDER LADEN
    // =========================

    private BufferedImage loadImage(String path)
    {
        BufferedImage image = null;

        try
        {
            URL imageURL =
                getClass().getClassLoader().getResource(path);

            image = ImageIO.read(imageURL);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return image;
    }

    // =========================
    // HINTERGRUND ZEICHNEN
    // =========================

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if(backgroundImage != null)
        {
            g.drawImage(
                backgroundImage,
                0,
                0,
                getWidth(),
                getHeight(),
                this
            );
        }

        if(slotImage != null)
        {
            g.drawImage(
                slotImage,
                350,
                120,
                800,
                500,
                this
            );
        }
    }

    // =========================
    // BUTTON KLICK
    // =========================

    public void actionPerformed(ActionEvent e)
    {
        int einsatz;

        try
        {
            einsatz =
                Integer.parseInt(einsatzFeld.getText());
        }
        catch(Exception ex)
        {
            gewinnLabel.setText("Ungültige Zahl!");
            return;
        }

        spiel.gewinnBerechnen(einsatz);

        // Fehlercodes
        if(spiel.getGewinn() == -1)
        {
            gewinnLabel.setText("Einsatz muss > 0 sein!");
            return;
        }

        if(spiel.getGewinn() == -2)
        {
            gewinnLabel.setText("Nicht genug Geld!");
            return;
        }

        // Zahlen anzeigen
        label1.setText("" + spiel.getSlot1());
        label2.setText("" + spiel.getSlot2());
        label3.setText("" + spiel.getSlot3());

        // Kontostand aktualisieren
        kontoLabel.setText(
            "Kontostand: " +
            spiel.getKontostand() +
            " €"
        );

        // Gewinntext
        if(spiel.super7IchKaufDasKasino())
        {
            gewinnLabel.setText("🎰 JACKPOT 777 !!!");
        }
        else if(spiel.strasse())
        {
            gewinnLabel.setText("Straße!");
        }
        else if(spiel.hauptGewinn())
        {
            gewinnLabel.setText("Hauptgewinn!");
        }
        else if(spiel.mega7())
        {
            gewinnLabel.setText("Mega 7!");
        }
        else if(spiel.kleinerGewinn())
        {
            gewinnLabel.setText("Kleiner Gewinn!");
        }
        else if(spiel.super7())
        {
            gewinnLabel.setText("Eine 7!");
        }
        else
        {
            gewinnLabel.setText("Leider verloren!");
        }
    }
}