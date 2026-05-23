import javax.sound.sampled.*;
import java.io.File;

public class SoundManager
{
    private Clip musikClip;
    private Clip effektClip; // <-- eigener Clip für Effekte

    private void starteMusik(String pfad)
    {
        try
        {
            stoppeMusik();
            File file = new File(pfad);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            musikClip = AudioSystem.getClip();
            musikClip.open(audio);
            setLautstaerke(musikClip, 0.8f); // 50% Lautstärke
            musikClip.loop(Clip.LOOP_CONTINUOUSLY);
            musikClip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void starteSoundEffekt(String pfad)
    {
        try
        {
            if (effektClip != null && effektClip.isRunning())
                effektClip.stop();

            File file = new File(pfad);
            AudioInputStream audio = AudioSystem.getAudioInputStream(file);
            effektClip = AudioSystem.getClip(); // <-- musikClip wird nicht mehr überschrieben
            effektClip.open(audio);
            setLautstaerke(effektClip, 1.0f); // 100% Lautstärke
            effektClip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Lautstärke zwischen 0.0f (stumm) und 1.0f (max)
    private void setLautstaerke(Clip clip, float lautstaerke)
    {
        FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float min = control.getMinimum();   // meist ca. -80 dB
        float max = control.getMaximum();   // meist 6 dB
        float dB  = min + (max - min) * lautstaerke;
        control.setValue(dB);
    }

    public void hubMusic()      { starteMusik("sounds/hubMusic.wav"); }

    public void rouletteMusic() { starteMusik("sounds/rouletteMusic.wav"); }

    public void slotMusic()     { starteMusik("sounds/slotMusic.wav"); }

    public void spinEffekt()    { starteSoundEffekt("sounds/spin.wav"); }

    public void jackpotEffekt()
    {
        starteSoundEffekt("sounds/jackpot.wav"); 
        starteSoundEffekt("sounds/coins.wav"); 

    }

    public void stoppeMusik()
    {
        if (musikClip != null && musikClip.isRunning())
            musikClip.stop();
    }
}