import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.net.URL;

/**
 * Abstrakte Oberklasse fuer alle Casino-Panels.
 * Enthaelt gemeinsame Hilfsmethoden (Style, Image-Laden) und den Spieler.
 */
public abstract class CasinoGUI extends JPanel
{
    protected Spieler spieler;

    protected void styleButton(JButton button)
    {
        button.setBackground(new Color(217, 131, 53));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 22));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
    }

    protected void styleTextField(JTextField field)
    {
        field.setFont(new Font("Arial", Font.BOLD, 18));
        field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        field.setHorizontalAlignment(JTextField.CENTER);
    }

    protected BufferedImage loadImage(String path)
    {
        BufferedImage image = null;
        URL imageURL = getClass().getClassLoader().getResource(path);
        try { image = ImageIO.read(imageURL); }
        catch (Exception e) { e.printStackTrace(); }
        return image;
    }

    protected abstract void initComponents();
    protected abstract void updateLayoutPositions(int w, int h);
    protected abstract void drawBackground(Graphics g);
}