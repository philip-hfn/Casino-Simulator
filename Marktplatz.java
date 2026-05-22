/**
 * Logik des Marktplatzes – komplett unabhängig von der GUI.
 * Verwaltet Buff-Käufe und prüft ob der Spieler genug Geld hat.
 */
public class Marktplatz
{
    private Spieler spieler;
    private BuffManager buffManager;

    public Marktplatz(Spieler spieler, BuffManager buffManager)
    {
        this.spieler     = spieler;
        this.buffManager = buffManager;
    }

    // Buff kaufen – gibt true zurück wenn Kauf erfolgreich 
    public boolean kaufeLucky7()
    {
        if (spieler.getKontostand() < 500) 
        {
            return false;
        }
        spieler.changeKontostand(-500);
        buffManager.aktiviereLucky7(5);
        return true;
    }

    public boolean kaufeJackpotBoost()
    {
        if (spieler.getKontostand() < 800) 
        {
            return false;
        }
        spieler.changeKontostand(-800);
        buffManager.aktiviereJackpotBoost(3);
        return true;
    }

    public boolean kaufeLuckySpin()
    {
        if (spieler.getKontostand() < 600) 
        {
            return false;
        }
        spieler.changeKontostand(-600);
        buffManager.aktiviereLuckySpin(5);
        return true;
    }

    public boolean kaufeDoubleUp()
    {
        if (spieler.getKontostand() < 1000) 
        {
            return false;
        }
        spieler.changeKontostand(-1000);
        buffManager.aktiviereDoubleUp(1);
        return true;
    }

    // Preise abfragen (für Anzeige im Panel) 
    public int getPreisLucky7()      
    {
        return 500;  
    }
    public int getPreisJackpotBoost()
    {
        return 800;  
    }
    public int getPreisLuckySpin()   
    {
        return 600;  
    }
    public int getPreisDoubleUp()    
    {
        return 1000; 
    }
}