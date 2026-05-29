import javax.sound.sampled.*;
import java.io.File;

/**
 * Verwaltet die Audiowiedergabe des Spiels
 * Trennt Hintergrundmusik (Loop) von Soundeffekten (Einmal-Wiedergabe)
 */
public class SoundManager
{
    private Clip musikClip;   // Clip für die dauerhafte Hintergrundmusik
    private Clip effektClip;  // Clip für kurzzeitige Soundeffekte

    /**
     * Startet eine Hintergrundmusikdatei und spielt diese in einer Endlosschleife ab
     * @param pfad Der Dateipfad zur .wav-Datei.
     */
    private void starteMusik(String pfad)
    {
        try
        {
            stoppeMusik(); // Vorherige Musik stoppen, bevor neue geladen wird
            File file = new File(pfad);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            musikClip = AudioSystem.getClip();
            musikClip.open(audio);
            setLautstaerke(musikClip, 0.8f); // 80% Lautstärke für die Hintergrundmusik
            musikClip.loop(Clip.LOOP_CONTINUOUSLY); // Musik in Endlosschleife setzen
            musikClip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace(); // Fehler im Konsolen-Log ausgeben
        }
    }

    /**
     * Spielt einen Soundeffekt einmalig ab.
     * @param pfad Der Dateipfad zur .wav-Datei.
     */
    private void starteSoundEffekt(String pfad)
    {
        try
        {
            // Vorherigen Effekt-Clip stoppen, falls dieser noch läuft
            if (effektClip != null && effektClip.isRunning())
                effektClip.stop();

            File file = new File(pfad);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            effektClip = AudioSystem.getClip(); // Neuer Clip für den Effekt
            effektClip.open(audio);
            setLautstaerke(effektClip, 1.0f); // 100% Lautstärke für klare Effekte
            effektClip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Passt die Lautstärke eines Clips an.
     * @param clip Der zu bearbeitende Audio-Clip.
     * @param lautstaerke Wert zwischen 0.0f (stumm) und 1.0f (max).
     */
    private void setLautstaerke(Clip clip, float lautstaerke)
    {
        // Zugriff auf den Master-Gain-Regler des Audio-Systems
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float min = control.getMinimum(); // Physikalisches Minimum (meist ca. -80 dB)
        float max = control.getMaximum(); // Physikalisches Maximum (meist 6 dB)
        
        // Umrechnung des linearen Wertes (0-1) in den logarithmischen Dezibel-Wert
        float dB = min + (max - min) * lautstaerke;
        control.setValue(dB);
    }

    /** 
     * Spielt die Hub-Hintergrundmusik.
     */
    public void hubMusic() 
    { 
        starteMusik("sounds/hubMusic.wav");
    }

    /** 
     * Spielt die Roulette-Hintergrundmusik.
       */
    public void rouletteMusic() 
    {
        starteMusik("sounds/rouletteMusic.wav"); 
    }

    /** 
     * Spielt die Slot-Hintergrundmusik.
     */
    public void slotMusic() 
    { 
        starteMusik("sounds/slotMusic.wav"); 
    }

    /** 
     * Spielt den Spin-Effekt (z.B. beim Slot-Start). 
     */
    public void spinEffekt() 
    {
        starteSoundEffekt("sounds/spin.wav"); 
    }

    /** 
     * Spielt Jackpot-relevante Soundeffekte ab.
     */
    public void jackpotEffekt()
    {
        starteSoundEffekt("sounds/jackpot.wav"); 
        starteSoundEffekt("sounds/coins.wav"); 
    }

    /** 
     * Stoppt die aktuelle Hintergrundmusik, falls sie läuft.
     */
    public void stoppeMusik()
    {
        if (musikClip != null && musikClip.isRunning())
            musikClip.stop();
    }
}