import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SlotGUI extends JFrame implements ActionListener
{
    private JLabel label1;
    private JLabel label2;
    private JLabel label3;

    private JLabel gewinnLabel;

    private JButton drehenButton;

    private JTextField einsatzFeld;
    
    private JLabel kontoLabel;

    private Slot spiel;

    public SlotGUI()
    {
        // Slot-Objekt erstellen
        spiel = new Slot();

        // Fenster
        setTitle("Slotmaschine");
        setSize(500,300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Zahlen anzeigen
        JPanel zahlenPanel = new JPanel();

        label1 = new JLabel("0");
        label2 = new JLabel("0");
        label3 = new JLabel("0");

        Font schrift = new Font("Arial", Font.BOLD, 50);

        label1.setFont(schrift);
        label2.setFont(schrift);
        label3.setFont(schrift);

        zahlenPanel.add(label1);
        zahlenPanel.add(label2);
        zahlenPanel.add(label3);

        // Gewinnanzeige
        gewinnLabel = new JLabel("Drücke auf Drehen!", SwingConstants.CENTER);

        // Button
        drehenButton = new JButton("Drehen");
        drehenButton.addActionListener(this);

        //Textfeld
        einsatzFeld = new JTextField("10", 5);
        JPanel einsatzPanel = new JPanel();
        
        //Kontostand
        kontoLabel = new JLabel("Kontostand: " + spiel.getKontostand() + "€");

        einsatzPanel.add(new JLabel("Einsatz: "));
        einsatzPanel.add(einsatzFeld);

        // Alles ins Fenster
        add(einsatzPanel, BorderLayout.WEST);
        add(gewinnLabel, BorderLayout.NORTH);
        add(zahlenPanel, BorderLayout.CENTER);
        add(drehenButton, BorderLayout.SOUTH);
        add(kontoLabel, BorderLayout.NORTH);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent e)
    {

        // Slotmaschine drehen
        int einsatz = Integer.parseInt(einsatzFeld.getText());

        spiel.gewinnBerechnen(einsatz);

        // Zahlen anzeigen
        label1.setText("" + spiel.getSlot1());
        label2.setText("" + spiel.getSlot2());
        label3.setText("" + spiel.getSlot3());
        kontoLabel.setText("Kontostand: " + spiel.getKontostand() + "€");

        // Gewinn prüfen
        if(spiel.super7IchKaufDasKasino())
        {
            gewinnLabel.setText("🎰 JACKPOT 777 !!!");
        }
        else if(spiel.hauptGewinn())
        {
            gewinnLabel.setText("Großer Gewinn!");
        }
        else if(spiel.kleinerGewinn())
        {
            gewinnLabel.setText("Kleiner Gewinn!");
        }
        else
        {
            gewinnLabel.setText("Leider verloren!");
        }
        
    }

    public static void main(String[] args)
    {
        new SlotGUI();
    }
}