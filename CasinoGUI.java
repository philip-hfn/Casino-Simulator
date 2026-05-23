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

    /**
     * Methode um Button ein einheitliches Design zu verleihen
     * @param button Der zu stylende JButton
     */
    protected void styleButton(JButton button)
    {
        button.setBackground(new Color(180, 100, 30));//Hintergrund Braun/Orange
        button.setForeground(Color.WHITE);// Weiße Schriftfarbe
        button.setFont(new Font("Georgia", Font.BOLD, 20));//Schriftart Georgia, Fett, Groeße 20
        button.setFocusPainted(false);// Entfernt Fokus-Rahmen
        //Erstellt Rand: Außen hellorange Linie, innen unsichtbarer Abstand (Padding)
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 200, 100), 2), // Aeußerer Rand
            BorderFactory.createEmptyBorder(6, 18, 6, 18) //innerer Abstand 
        ));
        button.setOpaque(true); //Hintergrundfarbe wird gezeichnet 
        button.setContentAreaFilled(true); //Ausfuellen des Buttons mit der Hintergrundfarbe
    }

     /**
     * Verleiht Eingabefeldern (JTextFields) ein einheitliches Aussehen
     * @param field Das zu stylende JTextField
     */
    protected void styleTextField(JTextField field)
    {
        field.setFont(new Font("Arial", Font.BOLD, 18)); //Schriftart Arial, Fett, Groeße 18
        field.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2)); // Schwarzer Rand 2 Pixel dick
        field.setHorizontalAlignment(JTextField.CENTER); // Zentriert den eingegebenen Text oder Zahlen im Feld
    }

    /**
     * laed ein Bild anhand des angegebenen Dateipfads
     *  @param path Der relative Pfad zum Bild (z.B. "pics/slot.png")
     *  @return Das geladene BufferedImage oder null, falls das Bild nicht gefunden wurde
     */
    protected BufferedImage loadImage(String path)
    {
        BufferedImage image = null;// Initialisiert das Bild-Objekt mit null
        URL imageURL = getClass().getClassLoader().getResource(path); // Sucht den exakten URL-Pfad der Datei über den ClassLoader (sucht im Ressourcen-Ordner des Projekts)
        try { image = ImageIO.read(imageURL); } // Versucht, Bilddatei über ermittelte URL einzulesen
        catch (Exception e) { e.printStackTrace(); } // Fängt Fehler ab (z.B. Datei nicht gefunden) und gibt den Fehlerpfad in der Konsole aus
        return image; // Gibt das geladene Bild (oder wenn ein Fehler auftritt null) zurück
    }

    /**
     * Abstrakt: Muss in den Unterklassen implementiert werden, um GUI-Elemente zu erstellen
     */
    protected abstract void initComponents();
    
    /**
     * Abstrakt: Muss in den Unterklassen implementiert werden, um die Positionen der Elemente anzupassen
     * @param w Die Breite des Fensters/Panels
     * @param h Die Höhe des Fensters/Panels
     */ 
    protected abstract void updateLayoutPositions(int w, int h);
    
     /**
     * Abstrakt: Muss in den Unterklassen implementiert werden, um den Hintergrund zu zeichnen
     * @param g Das Graphics-Objekt, das für die Zeichenoperationen genutzt wird
     */
    protected abstract void drawBackground(Graphics g);
    
        
    /**
     * Fuegt einem Button einen Hover-Effekt (Farbwechsel bei Mauskontakt) hinzu
     * @param button Der JButton, der den Effekt erhalten soll
     */
    protected void addHoverEffect(JButton button)
    {
        Color normal = button.getBackground(); // Speichert die urspruengliche Hintergrundfarbe des Buttons ab
        Color bright = normal.brighter(); // Berechnet eine hellere Variante dieser Farbe
        button.addMouseListener(new java.awt.event.MouseAdapter() // Registriert einen MouseListener am Button über eine anonyme Adapter-Klasse
        {
            public void mouseEntered(java.awt.event.MouseEvent e) // Wird aufgerufen, sobald der Mauszeiger die Flaeche des Buttons betritt
            {
                button.setBackground(bright); // Hintergrundfarbe auf die hellere Variante umstellen
            }
            public void mouseExited(java.awt.event.MouseEvent e) // Wird aufgerufen, sobald der Mauszeiger die Flaeche des Buttons wieder verlaesst
            {
                button.setBackground(normal); // Hintergrundfarbe wird wieder auf die Originalfarbe zurueckgesetzt
            }
        });
    }
}