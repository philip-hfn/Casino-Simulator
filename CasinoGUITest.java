
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;

public class CasinoGUITest extends JPanel implements Runnable
{
    JFrame frame;

    BufferedImage rouletteImage;
    BufferedImage slotImage;
    BufferedImage backgroundImage;

    JButton rouletteButton;
    JButton slotButton;
    JButton backButton;
    JButton spinButton;

    JTextField geradeFeld;
    JTextField rotFeld;
    JTextField zahlFeld;

    JLabel ergebnisLabel;
    JLabel einsatzLabel;

    JSlider einsatz;

    Roulette roulette;
    Spieler spieler;

    int screen = 0;

    double ballAngle = 0;
    double ballSpeed = 0;
    double targetAngle = 0;

    int rouletteCenterX = 330;
    int rouletteCenterY = 400;
    int ballRadius = 230;

    boolean spinning = false;

    public static void main(String[] args)
    {
        new CasinoGUITest();
    }

    public CasinoGUITest()
    {
        frame = new JFrame("Casino");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

        setLayout(new BorderLayout());

        frame.add(this);
        frame.setVisible(true);

        spieler = new Spieler();
        roulette = new Roulette(spieler);

        doInitializations();

        Thread th = new Thread(this);
        th.start();
    }

    private void doInitializations()
    {
        rouletteButton = new JButton("Roulette");
        slotButton = new JButton("Slot Maschine");
        backButton = new JButton("Back");
        spinButton = new JButton("SPIN");

        styleButton(rouletteButton);
        styleButton(slotButton);

        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);
        spinButton.setFont(new Font("Arial",Font.BOLD,22));

        rouletteButton.addActionListener(e ->
        {
            screen = 1;
            rebuildLayout();
        });

        slotButton.addActionListener(e ->
        {
            screen = 2;
            rebuildLayout();
        });

        backButton.addActionListener(e ->
        {
            screen = 0;
            rebuildLayout();
        });

        geradeFeld = new JTextField("-");
        rotFeld = new JTextField("rot");
        zahlFeld = new JTextField("0");

        ergebnisLabel = new JLabel("Zahl: - | Gewinn: -");
        ergebnisLabel.setFont(new Font("Arial",Font.BOLD,18));
        ergebnisLabel.setForeground(Color.WHITE);

        einsatzLabel = new JLabel("Einsatz");
        einsatzLabel.setForeground(Color.WHITE);

        einsatz = new JSlider(1,spieler.getKontostand(),spieler.getKontostand()/2);
        einsatz.setMajorTickSpacing(100);
        einsatz.setPaintTicks(true);
        einsatz.setPaintLabels(true);

        spinButton.addActionListener(e ->
        {
            int gewinn = roulette.spieldurchfuehren(
                einsatz.getValue(),
                rotFeld.getText(),
                geradeFeld.getText(),
                Integer.parseInt(zahlFeld.getText())
            );

            int zahl = roulette.ergebnis;

            targetAngle = zahl * (2*Math.PI/37);

            ballSpeed = 0.25;
            spinning = true;

            ergebnisLabel.setText("Zahl: "+zahl+" | Gewinn: "+gewinn+"$");
        });

        rouletteImage = loadImage("pics/roulette.png");
        slotImage = loadImage("pics/slot.png");
        backgroundImage = loadImage("pics/background.png");

        rebuildLayout();
    }

    private JPanel createControlPanel()
    {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(350,400));

        panel.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10,10,10,10);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;

        c.gridy = 0;
        panel.add(einsatzLabel,c);

        c.gridy = 1;
        panel.add(einsatz,c);

        c.gridy = 2;
        panel.add(new JLabel("Gerade/Ungerade"),c);

        c.gridy = 3;
        panel.add(geradeFeld,c);

        c.gridy = 4;
        panel.add(new JLabel("Rot/Schwarz"),c);

        c.gridy = 5;
        panel.add(rotFeld,c);

        c.gridy = 6;
        panel.add(new JLabel("Zahl"),c);

        c.gridy = 7;
        panel.add(zahlFeld,c);

        c.gridy = 8;
        panel.add(spinButton,c);

        c.gridy = 9;
        panel.add(ergebnisLabel,c);

        return panel;
    }

    private void rebuildLayout()
    {
        removeAll();//!!!

        if(screen == 0)
        {
            JPanel start = new JPanel();
            start.setOpaque(false);
            start.setLayout(new GridLayout(2,1,20,20));

            start.add(rouletteButton);
            start.add(slotButton);

            add(start,BorderLayout.CENTER);
        }

        if(screen == 1)
        {
            add(backButton,BorderLayout.NORTH);
            add(createControlPanel(),BorderLayout.EAST);
        }

        if(screen == 2)
        {
            add(backButton,BorderLayout.NORTH);
        }

        revalidate();
        repaint();
    }

    private void styleButton(JButton button)
    {
        button.setBackground(new Color(217,131,53));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial",Font.BOLD,22));
        button.setFocusPainted(false);
    }

    private BufferedImage loadImage(String path)
    {
        try
        {
            URL url = getClass().getClassLoader().getResource(path);
            return ImageIO.read(url);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void run()
    {
        while(frame.isVisible())
        {
            if(spinning)
            {
                ballAngle += ballSpeed;

                ballSpeed *= 0.99;

                if(ballSpeed < 0.01)
                {
                    ballAngle = targetAngle;
                    spinning = false;
                }
            }

            repaint();

            try{Thread.sleep(10);}catch(Exception e){}
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        if(screen == 0)
        {
            g.drawImage(backgroundImage,0,0,getWidth(),getHeight(),this);
        }

        if(screen == 1)
        {
            g.drawImage(rouletteImage,0,0,getWidth(),getHeight(),this);

            int ballX = (int)(rouletteCenterX + Math.cos(ballAngle)*ballRadius);
            int ballY = (int)(rouletteCenterY + Math.sin(ballAngle)*ballRadius);

            g.setColor(Color.WHITE);
            g.fillOval(ballX-6,ballY-6,12,12);
        }

        if(screen == 2)
        {
            g.drawImage(slotImage,0,0,getWidth(),getHeight(),this);
        }
    }
}
