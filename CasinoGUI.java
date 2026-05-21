import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.net.URL;
import javax.swing.Timer;
public class CasinoGUI extends JPanel implements Runnable, MouseListener
{
    JFrame frame;

    int screen = 0;

    private JButton rouletteButton;
    private JButton slotButton;
    private JButton backButton;
    private JButton spinButton;

    private JTextField geradeFeld;
    private JTextField zahlFeld;
    private JTextField rotFeld;

    private JLabel ergebnisLabel;
    private JLabel einsatzLabel;
    private JLabel geradeLabel;
    private JLabel zahlLabel;
    private JLabel rotLabel;
    private JLabel konto;

    private JSlider einsatz;

    BufferedImage rouletteImage;
    BufferedImage slotImage;
    BufferedImage backgroundImage;

    Roulette roulette;
    Spieler spieler;

    double ballAngle = 0;
    double ballSpeed = 0;
    int rouletteCenterX = 258;
    int rouletteCenterY = 333;
    int ballRadius = 135;
    boolean spinning = false;
    double targetAngle = 0;

    public static void main(String[] args)
    {
        new CasinoGUI();
    }

    public CasinoGUI()
    {

        frame = new JFrame("Casino");
        frame.setLocation(100,100);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        frame.add(this);
        frame.setSize(1920,1080);

        spieler = new Spieler();
        roulette = new Roulette(spieler);   // Verbindung zur Logik/Roulette
        addMouseListener(this);
        doInitializations();
        frame.setVisible(true);

        Thread th = new Thread(this);
        th.start();
    }

    private void doInitializations()
    {
        rouletteButton = new JButton("Roulette");
        slotButton = new JButton("Slot-Maschine");
        styleButton(rouletteButton);
        styleButton(slotButton);
        add(rouletteButton);
        add(slotButton);
        
        backButton = new JButton("Back");
        backButton.setBounds(40,35,80,30);//!!!
        backButton.setBackground(Color.WHITE);
        backButton.setForeground(Color.BLACK);
        backButton.setFont(new Font("Arial", Font.BOLD, 15));
        backButton.setFocusPainted(false);
        backButton.setBorderPainted(false);
        add(backButton);

        rouletteButton.addActionListener(e -> 
                {
                    screen = 1;
                    updateComponents();
            });

        slotButton.addActionListener(e -> 
                {
                    screen = 2;
                    updateComponents();
            });

        backButton.addActionListener(e -> 
                {
                    screen = 0;
                    updateComponents();
            });

        

        geradeFeld = new JTextField("-");
        geradeFeld.setBounds(930, 530, 100, 30);
        styleTextField(geradeFeld);
        add(geradeFeld);

        rotFeld = new JTextField("-");
        rotFeld.setBounds(930, 580, 100, 30);
        styleTextField(rotFeld);
        add(rotFeld);

        zahlFeld = new JTextField("0");
        zahlFeld.setBounds(930, 630, 100, 30);
        styleTextField(zahlFeld);
        add(zahlFeld);


        spinButton = new JButton("SPIN");
        spinButton.setBounds(1080, 540, 150, 60);
        spinButton.setBackground(Color.RED);
        spinButton.setForeground(Color.WHITE);
        spinButton.setFont(new Font("Arial", Font.BOLD, 22));
        spinButton.setFocusPainted(false);
        spinButton.setBorderPainted(false);
        add(spinButton);

        // spinButton.addActionListener(e ->
        // {
        // int gewinn = roulette.spieldurchfuehren(einsatz.getValue(), rotR(), geradeR(), zahlR());
        // if(roulette.getHauptgewinn()==true)
        // {
        // ImageIcon winGif = new ImageIcon(getClass().getResource("pics/jackpot.gif"));
        // JLabel winAnimation = new JLabel(winGif);

        // }
        // int zahl = roulette.ergebnis;

        // double anglePerField = 2 * Math.PI / 37;
        // double offset = -Math.PI / 2;

        // int index = roulette.getWinkelIndex();
        // double fullRotations = Math.PI * 10; // mind. 5 Umdrehungen
        // targetAngle = ballAngle + fullRotations
        // - (ballAngle % (2 * Math.PI))
        // + index * anglePerField
        // - Math.PI / 2          // 0 liegt bei 12 Uhr
        // + Math.toRadians(21); // Korrektur
        // ballSpeed = 0.35 + Math.random()*0.1;
        // spinning = true;
        // ergebnisLabel.setText("Zahl: " + zahl + " | Gewinn: " + gewinn + "$");
        // konto.setText("Kontostand: " + spieler.getKontostand() + "$");
        // einsatz.setMaximum(spieler.getKontostand());

        // });

        spinButton.addActionListener(e ->
                {
                    spinButton.setEnabled(false);
                    int gewinn = roulette.spieldurchfuehren(einsatz.getValue(), rotR(), geradeR(), zahlR());
                    int zahl = roulette.ergebnis;
                    int index = roulette.getWinkelIndex();

                    double anglePerField = 2 * Math.PI / 37;
                    double imageOffset = -Math.PI / 2;

                    double currentNormalized = ballAngle % (2 * Math.PI);

                    double targetNormalized = index * anglePerField + imageOffset;
                    if(targetNormalized < 0) targetNormalized += 2 * Math.PI;

                    double delta = targetNormalized - currentNormalized;
                    if(delta <= 0) delta += 2 * Math.PI;

                    targetAngle = ballAngle + (Math.PI * 10) + delta;

                    ballSpeed = 0.35 + Math.random() * 0.05;
                    spinning = true;

                    
                    int finalGewinn = gewinn;
                    int finalZahl = zahl;
                    Timer timer = new Timer(4500, event -> {
                                    ergebnisLabel.setText("Zahl: " + finalZahl + " | Gewinn: " + finalGewinn + "$");
                                    konto.setText("Kontostand: " + spieler.getKontostand() + "$");
                                    einsatz.setMaximum(spieler.getKontostand());
                                    spinButton.setEnabled(true);
                            });
                    timer.setRepeats(false);
                    timer.start();
            });  

        ergebnisLabel = new JLabel("Ergebnis: -");
        ergebnisLabel.setBounds(50, 550, 400, 40);//!!!
        ergebnisLabel.setFont(new Font("Arial", Font.BOLD, 20));
        ergebnisLabel.setForeground(Color.WHITE);
        add(ergebnisLabel);

        rotLabel = new JLabel("Farbe: ");
        geradeLabel = new JLabel("gerade/ungerade: ");
        zahlLabel = new JLabel("Zahl: ");
        rotLabel.setFont(new Font("Arial", Font.BOLD, 14));
        rotLabel.setForeground(Color.WHITE);
        geradeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        geradeLabel.setForeground(Color.WHITE);
        zahlLabel.setFont(new Font("Arial", Font.BOLD, 14));
        zahlLabel.setForeground(Color.WHITE);
        add(rotLabel);
        add(geradeLabel);
        add(zahlLabel);

        konto = new JLabel("Kontostand: " + spieler.getKontostand() + "$");
        konto.setFont(new Font("Arial", Font.BOLD, 30));
        konto.setForeground(Color.WHITE);
        add(konto);

        einsatz = new JSlider(0, spieler.getKontostand(), spieler.getKontostand()/2);
        einsatz.setBounds(920, 460, 300, 60);
        einsatz.setMajorTickSpacing(500);
        einsatz.setMinorTickSpacing(10);
        einsatz.setPaintTicks(true);
        einsatz.setPaintLabels(true);
        einsatz.setBackground(new Color(30,120,30));
        einsatz.setForeground(Color.WHITE);
        einsatz.setFont(new Font("Arial", Font.BOLD, 14));
        add(einsatz);

        einsatzLabel = new JLabel("Einsatz wählen");
        einsatzLabel.setBounds(920,420,300,40);//!!!
        einsatzLabel.setFont(new Font("Arial",Font.BOLD,22));
        einsatzLabel.setForeground(Color.WHITE);
        add(einsatzLabel);

        einsatz.addChangeListener(e -> {
                    einsatzLabel.setText("Einsatz: " + einsatz.getValue() + " $");
            });

        rouletteImage = loadImage("pics/roulette.png");
        slotImage = loadImage("pics/slot.png");
        backgroundImage = loadImage("pics/background.png");

        updateComponents();
    }

    private void styleButton(JButton button)
    {
        button.setBackground(new Color(217, 131, 53));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 22));

        button.setFocusPainted(false);
        button.setBorderPainted(false);

        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }

    private void styleTextField(JTextField field)
    {
        field.setFont(new Font("Arial", Font.BOLD, 18));
        field.setBorder(BorderFactory.createLineBorder(Color.BLACK,2));
        field.setHorizontalAlignment(JTextField.CENTER);
    }

    private void updateLayoutPositions()
    {
        int w = getWidth();
        int h = getHeight();

        // Startscreen Buttons
        rouletteButton.setBounds(w / 2 - 90, h / 2 - 60, 180, 65);
        slotButton.setBounds(w / 2 - 130, h / 2 + 40, 260, 65);

        // Back Button
        backButton.setBounds(40, 35, 80, 30);

        // Eingaben UNTER dem Zahlenfeld
        int formX = (int)(w * 0.65);
        int formY = (int)(h * 0.70);
        int label = (int)(w * 0.61);

        //einsatzFeld.setBounds(formX + 130, formY, 150, 35);
        geradeFeld.setBounds(formX, formY, 150, 35);
        rotFeld.setBounds(formX, (int)(h * 0.80), 150, 35);
        zahlFeld.setBounds(formX, (int)(h * 0.90), 150, 35);

        // Spin Button rechts neben den Eingaben
        spinButton.setBounds((int)(w * 0.82), (int)(h * 0.76), 170, 70);

        // Ergebnis unten rechts
        ergebnisLabel.setBounds((int)(w * 0.82), (int)(h * 0.86), 500, 40);

        //Schieberegler für den Einsatz
        einsatz.setBounds((int)(w * 0.72), (int)(h * 0.08), 320, 60);

        //Zeigt unter dem Regler den Einsatz an
        einsatzLabel.setBounds((int)(w * 0.77), (int)(h * 0.15), 300, 60);

        //Lables die anzeigen in welchem Feld man auf was setzt
        geradeLabel.setBounds((int)(w * 0.55), formY, 150, 35);
        rotLabel.setBounds(label, (int)(h * 0.80), 150, 35);
        zahlLabel.setBounds(label, (int)(h * 0.90), 150, 35);

        //Kontostandsanzeige
        konto.setBounds((int)(w * 0.15), (int)(h * 0.04), 400, 50);

        //Spielkugel im Roulette
        rouletteCenterX = (int) (w * 0.202);
        rouletteCenterY = (int) (h * 0.473);
        ballRadius = (int) (w * 0.11);
    }

    private BufferedImage loadImage(String path)
    {
        BufferedImage image = null;
        URL imageURL = getClass().getClassLoader().getResource(path);
        try
        {
            image = ImageIO.read(imageURL);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return image;
    }

    public void run()
    {
        while(frame.isVisible())
        {
            if(spinning)
            {
                // Wie weit noch bis zum Ziel?
                double remaining = targetAngle - ballAngle;

                if(remaining <= 0.01)
                {
                    // Ziel erreicht - sauber einrasten
                    ballAngle = targetAngle;
                    ballSpeed = 0;
                    spinning = false;
                }
                else
                {
                    // Geschwindigkeit abhängig von verbleibender Distanz
                    // Je näher am Ziel, desto langsamer
                    double naturalSpeed = Math.sqrt(remaining) * 0.04;

                    // Minimum damit es nicht ewig kriecht
                    if(naturalSpeed < 0.005) naturalSpeed = 0.005;

                    // Nie schneller als Startgeschwindigkeit
                    if(naturalSpeed > ballSpeed) naturalSpeed = ballSpeed;

                    ballSpeed = naturalSpeed;
                    ballAngle += ballSpeed;
                }
            }

            repaint();
            try { Thread.sleep(10); } catch(Exception e){}
        }
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        updateLayoutPositions();

        if(screen == 0)
        {
            drawStartscreen(g);
        }

        if(screen == 1)
        {
            drawRoulette(g);
        }

        if(screen == 2)
        {
            drawSlot(g);
        }
    }

    private void drawStartscreen(Graphics g)
    {
        g.drawImage(backgroundImage,0,0,getWidth(),getHeight(),this);
    }

    private void drawRoulette(Graphics g)
    {
        g.drawImage(rouletteImage,0,0,getWidth(),getHeight(),this);

        int ballX = (int)(rouletteCenterX + Math.cos(ballAngle) * ballRadius);
        int ballY = (int)(rouletteCenterY + Math.sin(ballAngle) * ballRadius);

        g.setColor(Color.WHITE);
        g.fillOval(ballX-6, ballY-6, 12, 12);
    }

    private void drawSlot(Graphics g)
    {
        g.drawImage(slotImage,0,0,getWidth(),getHeight(),this);
    }

    public void mousePressed(MouseEvent e)
    {
        
    }

    private void updateComponents()
    {
        if(screen == 0)//Hub
        {
            rotFeld.setVisible(false);
            geradeFeld.setVisible(false);
            zahlFeld.setVisible(false);
            spinButton.setVisible(false);
            backButton.setVisible(false);
            ergebnisLabel.setVisible(false);
            einsatz.setVisible(false);
            rotLabel.setVisible(false);
            zahlLabel.setVisible(false);
            geradeLabel.setVisible(false);
            einsatzLabel.setVisible(false);
            konto.setVisible(false);

            rouletteButton.setVisible(true);
            slotButton.setVisible(true);
        }
        else if(screen == 1)//Roulette
        {
            rouletteButton.setVisible(false);
            slotButton.setVisible(false);

            rotFeld.setVisible(true);
            geradeFeld.setVisible(true);
            zahlFeld.setVisible(true);
            spinButton.setVisible(true);
            backButton.setVisible(true);
            ergebnisLabel.setVisible(true);
            einsatz.setVisible(true);
            rotLabel.setVisible(true);
            zahlLabel.setVisible(true);
            geradeLabel.setVisible(true);
            einsatzLabel.setVisible(true);
            konto.setVisible(true);

        }
        else if(screen == 2)//Slot
        {
            rotFeld.setVisible(false);
            geradeFeld.setVisible(false);
            zahlFeld.setVisible(false);
            spinButton.setVisible(false);
            ergebnisLabel.setVisible(false);
            einsatz.setVisible(false);
            rotLabel.setVisible(false);
            zahlLabel.setVisible(false);
            geradeLabel.setVisible(false);
            rouletteButton.setVisible(false);
            slotButton.setVisible(false);
            einsatzLabel.setVisible(false);

            backButton.setVisible(true);
            konto.setVisible(true);
        }
    }

    public void mouseClicked(MouseEvent e){}

    public void mouseReleased(MouseEvent e){}

    public void mouseEntered(MouseEvent e){}

    public void mouseExited(MouseEvent e){}

    private int zahlR()//Einsatz Roulette
    {
        int zahl = Integer.parseInt(zahlFeld.getText());
        return zahl;
    }

    private String geradeR()//Einsatz Roulette
    {
        String gerade = geradeFeld.getText();
        return gerade;
    }

    private String  rotR()//Einsatz Roulette
    {
        String rot = rotFeld.getText();
        return rot;
    }

}
