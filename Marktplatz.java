/**
 * Logik des Marktplatzes – komplett unabhängig von der GUI.
 * Verwaltet Buff-Käufe und prüft ob der Spieler genug Geld hat.
 */
public class Marktplatz
{
    private Spieler spieler;// Referenz auf das Spieler-Objekt, um den Kontostand abzufragen und zu veraendern
    private BuffManager buffManager; // Referenz auf den BuffManager, um die gekauften Effekte im Spiel freizuschalten

    /**
     * Konstruktor, Verknuepft den Markt direkt mit dem Spieler und dem Buffmanager
     * @param spieler Der Spieler, der einkaufen moechte
     * @param buffManager Der Manager, der die Laufzeiten der Effekte verwaltet
     */
    public Marktplatz(Spieler spieler, BuffManager buffManager)
    {
        this.spieler     = spieler; // Speichert den uebergebenen Spieler
        this.buffManager = buffManager; // Speichert den uebergebenen BuffManager 
    }

    /**
     * "Lucky 7"-Buff kaufen
     * @return true, wenn der Spieler genug Geld hatte und der Kauf erfolgreich war, sonst false
     */
    public boolean kaufeLucky7()
    {
        if (spieler.getKontostand() < 500) // Prueft, ob das aktuelle Guthaben des Spielers kleiner ist als 500$
        {
            return false; 
        }
        spieler.changeKontostand(-500); // Zieht dem Spieler 500$ vom Konto ab
        buffManager.aktiviereLucky7(5); // Schaltet den "Lucky 7"-Buff im Manager frei (fuer die naechsten 5 Runden) 
        return true;
    }

    /**
     * "Jackpot-Boost"-Buff kaufen
     * @return true, wenn der Spieler genug Geld hatte und der Kauf erfolgreich war, sonst false
     */
    public boolean kaufeJackpotBoost()
    {
        if (spieler.getKontostand() < 800) // Prueft, ob das aktuelle Guthaben des Spielers kleiner ist als 800$
        {
            return false;
        }
        spieler.changeKontostand(-800); // Zieht dem Spieler 800$ vom Konto ab
        buffManager.aktiviereJackpotBoost(3);// Schaltet den Jackpot-Boost im Manager frei (fuer die naechsten 3 Runden) 
        return true;
    }

    /**
     * "Lucky-Spin"-Buff kaufen
     * @return true, wenn der Spieler genug Geld hatte und der Kauf erfolgreich war, sonst false
     */
    public boolean kaufeLuckySpin()
    {
        if (spieler.getKontostand() < 600) // Prueft, ob das aktuelle Guthaben des Spielers kleiner ist als 600$
        {
            return false;
        }
        spieler.changeKontostand(-600);// Zieht dem Spieler 600$ vom Konto ab
        buffManager.aktiviereLuckySpin(5);// Schaltet den Luck-Spin Buff im Manager frei (fuer die naechsten 5 Runden) 
        return true;
    }

    /**
     * "Lucky-Spin"-Buff kaufen
     * @return true, wenn der Spieler genug Geld hatte und der Kauf erfolgreich war, sonst false
     */
    public boolean kaufeDoubleUp()
    {
        if (spieler.getKontostand() < 1000) // Prueft, ob das aktuelle Guthaben des Spielers kleiner ist als 1000$
        {
            return false;
        }
        spieler.changeKontostand(-1000);// Zieht dem Spieler 1000$ vom Konto ab
        buffManager.aktiviereDoubleUp(1);// Schaltet den Double-Up-Buff im Manager frei (fuer die naechste Runde) 
        return true;
    }

    /**
     * Gibt den Preis fuer den "Lucky-7"-Buff zurueck
     * @return Der feste Preis von 500$
     */
    public int getPreisLucky7()      
    {
        return 500;  
    }

    /**
     * Gibt den Preis fuer den "Jackpot-Boost"-Buff zurueck
     * @return Der feste Preis von 800$
     */
    public int getPreisJackpotBoost()
    {
        return 800;  
    }

    /**
     * Gibt den Preis fuer den "Lucky-Spin"-Buff zurueck
     * @return Der feste Preis von 600$
     */
    public int getPreisLuckySpin()   
    {
        return 600;  
    }

    /**
     * Gibt den Preis fuer den "Double-Up"-Buff zurueck
     * @return Der feste Preis von 1000$
     */
    public int getPreisDoubleUp()    
    {
        return 1000; 
    }
}