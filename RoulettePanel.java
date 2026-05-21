import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Roulette-Panel mit Ball-Animation und Wett-Eingaben.
 */
public class RoulettePanel extends CasinoGUI implements Runnable
{
    private JTextField geradeFeld;
    private JTextField zahlFeld;
    private JTextField rotFeld;
    private JButton    spinButton;
    private JLabel     ergebnisLabel;
    private JLabel     einsatzLabel;
    private JLabel     geradeLabel;
    private JLabel     zahlLabel;
    private JLabel     rotLabel;
    private JLabel     konto;
    private JSlider    einsatz;
    private JButton    backButton;

    double  ballAngle       = 0;
    double  ballSpeed       = 0;
    int     rouletteCenterX = 330;
    int     rouletteCenterY = 400;
    int     ballRadius      = 230;
    boolean spinning        = false;
    double  targetAngle     = 0;

    private BufferedImage rouletteImage;
    private Roulette      roulette;

    private HubPanel.ScreenSwitcher screenSwitcher;

    public RoulettePanel(Spieler spieler, HubPanel.ScreenSwitcher screenSwitcher)
    {
        this.spieler        = spieler;
        this.screenSwitcher = screenSwitcher;
        this.roulette       = new Roulette(spieler);
        setLayout(null);
        initComponents();

        Thread th = new Thread(this);
        th.setDaemon(true);
        th.start();
    }

    @Override
    protected void initComponents()
    {
        rouletteImage = loadImage("pics/roulette.png");

        backButton = new JButton("Back");
        styleButton(backButton);
        addHoverEffect(backButton);
        backButton.addActionListener(e -> screenSwitcher.switchTo(0));
        add(backButton);

        // Einsatz-Slider – Maximum = aktueller Kontostand
        einsatz = new JSlider(0, spieler.getKontostand(), spieler.getKontostand() / 2);
        einsatz.setMajorTickSpacing(500);
        einsatz.setMinorTickSpacing(10);
        einsatz.setPaintTicks(true);
        einsatz.setPaintLabels(true);
        einsatz.setBackground(new Color(30, 120, 30));
        einsatz.setForeground(Color.WHITE);
        einsatz.setFont(new Font("Arial", Font.BOLD, 14));
        add(einsatz);

        einsatzLabel = new JLabel("Einsatz waehlen");
        einsatzLabel.setFont(new Font("Arial", Font.BOLD, 22));
        einsatzLabel.setForeground(Color.WHITE);
        add(einsatzLabel);

        einsatz.addChangeListener(e ->
            einsatzLabel.setText("Einsatz: " + einsatz.getValue() + " $"));

        geradeFeld = new JTextField("-");
        styleTextField(geradeFeld);
        add(geradeFeld);

        rotFeld = new JTextField("-");
        styleTextField(rotFeld);
        add(rotFeld);

        zahlFeld = new JTextField("0");
        styleTextField(zahlFeld);
        add(zahlFeld);

        geradeLabel = new JLabel("Gerade/ungerade: ");
        geradeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        geradeLabel.setForeground(Color.WHITE);
        add(geradeLabel);

        rotLabel = new JLabel("Farbe: ");
        rotLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rotLabel.setForeground(Color.WHITE);
        add(rotLabel);

        zahlLabel = new JLabel("Zahl: ");
        zahlLabel.setFont(new Font("Arial", Font.BOLD, 14));
        zahlLabel.setForeground(Color.WHITE);
        add(zahlLabel);

        konto = new JLabel("Kontostand: " + spieler.getKontostand() + "$");
        konto.setFont(new Font("Arial", Font.BOLD, 30));
        konto.setForeground(Color.WHITE);
        add(konto);

        spinButton = new JButton("SPIN");
        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);
        spinButton.setFont(new Font("Arial", Font.BOLD, 22));
        spinButton.setFocusPainted(false);
        spinButton.setBorderPainted(false);
        spinButton.setOpaque(true);
        spinButton.setContentAreaFilled(true);
        addHoverEffect(spinButton);
        add(spinButton);

        spinButton.addActionListener(e ->
        {
            // Roulette.spieldurchfuehren(int einsatz, String farbe, String gerade, int zahl)
            int gewinn = roulette.spieldurchfuehren(
                einsatz.getValue(), rotR(), geradeR(), zahlR());

            if (roulette.getHauptgewinn())
            {
                ImageIcon winGif = new ImageIcon(
                    getClass().getClassLoader().getResource("pics/jackpot.gif"));
                JLabel winAnimation = new JLabel(winGif);
                winAnimation.setBounds(900, 500, 700, 500);
                add(winAnimation);
                winAnimation.setVisible(true);
                new Timer(3000, ev -> winAnimation.setVisible(false)).start();
            }

            int zahl = roulette.ergebnis;
            targetAngle = zahl * (2 * Math.PI / 37);
            ballSpeed   = 0.25;
            spinning    = true;

            ergebnisLabel.setText("Zahl: " + zahl + " | Gewinn: " + gewinn + "$");
            konto.setText("Kontostand: " + spieler.getKontostand() + "$");
            einsatz.setMaximum(spieler.getKontostand());
        });

        ergebnisLabel = new JLabel("Ergebnis: -");
        ergebnisLabel.setFont(new Font("Arial", Font.BOLD, 20));
        ergebnisLabel.setForeground(Color.WHITE);
        add(ergebnisLabel);
    }

    @Override
    protected void updateLayoutPositions(int w, int h)
    {
        backButton.setBounds(30, 25, 120, 45);

        einsatz     .setBounds((int)(w * 0.72), (int)(h * 0.08), 320, 60);
        einsatzLabel.setBounds((int)(w * 0.77), (int)(h * 0.15), 300, 60);

        int formX = (int)(w * 0.65);
        int label = (int)(w * 0.61);
        geradeFeld.setBounds(formX, (int)(h * 0.70), 150, 35);
        rotFeld   .setBounds(formX, (int)(h * 0.80), 150, 35);
        zahlFeld  .setBounds(formX, (int)(h * 0.90), 150, 35);

        geradeLabel.setBounds((int)(w * 0.55), (int)(h * 0.70), 150, 35);
        rotLabel   .setBounds(label,           (int)(h * 0.80), 150, 35);
        zahlLabel  .setBounds(label,           (int)(h * 0.90), 150, 35);

        spinButton   .setBounds((int)(w * 0.82), (int)(h * 0.76), 170, 70);
        ergebnisLabel.setBounds((int)(w * 0.82), (int)(h * 0.86), 500, 40);

        konto.setBounds((int)(w * 0.15), (int)(h * 0.04), 400, 50);
    }

    @Override
    protected void drawBackground(Graphics g)
    {
        g.drawImage(rouletteImage, 0, 0, getWidth(), getHeight(), this);

        int ballX = (int)(rouletteCenterX + Math.cos(ballAngle) * ballRadius);
        int ballY = (int)(rouletteCenterY + Math.sin(ballAngle) * ballRadius);
        g.setColor(Color.WHITE);
        g.fillOval(ballX - 6, ballY - 6, 12, 12);
    }

    public void refresh()
    {
        konto.setText("Kontostand: " + spieler.getKontostand() + "$");
        einsatz.setMaximum(spieler.getKontostand());
    }

    @Override
    public void run()
    {
        while (true)
        {
            if (spinning)
            {
                ballAngle += ballSpeed;
                ballSpeed *= 0.99;
                if (ballSpeed < 0.01)
                {
                    ballAngle = targetAngle;
                    spinning  = false;
                }
            }
            repaint();
            try { Thread.sleep(10); }
            catch (Exception e) {}
        }
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        updateLayoutPositions(getWidth(), getHeight());
        drawBackground(g);
    }

    private int    zahlR()   { return Integer.parseInt(zahlFeld.getText()); }
    private String geradeR() { return geradeFeld.getText(); }
    private String rotR()    { return rotFeld.getText(); }
}