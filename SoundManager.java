import javax.sound.sampled.*;
import java.net.URL;

public class SoundManager
{
    private Clip musikClip;

    private void starteMusik(String pfad)
    {
        try
        {
            stoppeMusik(); // vorherige Musik stoppen
            URL url = getClass().getClassLoader().getResource(pfad);
            AudioInputStream audio = AudioSystem.getAudioInputStream(url);
            musikClip = AudioSystem.getClip();
            musikClip.open(audio);
            musikClip.loop(Clip.LOOP_CONTINUOUSLY);
            musikClip.start();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void hubMusic()
    {
        starteMusik("sounds/hubMusic");
    }

    public void rouletteMusic()
    {
        starteMusik("sounds/rouletteMusic");
    }

    public void slotMusic()
    {
        starteMusik("sounds/slotMusic");
    }

    public void stoppeMusik()
    {
        if (musikClip != null && musikClip.isRunning())
        {
            musikClip.stop();
        }
    }
}