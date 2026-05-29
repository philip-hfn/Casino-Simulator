/**
 * Verwaltet alle aktiven Buffs des Spielers.
 * Jeder Buff hat einen Zaehler, der angibt, wie viele Runden er noch aktiv ist.
 */
public class BuffManager
{
    //Zaehler fuer die verbleibenden Runden der einzelnen Buffs
    private int lucky7Runden = 0; //7er Gewinn x3
    private int jackpotBoostRunden = 0; //Jackpot Gewinn x2
    private int luckySpinRunden = 0; //Roulette +50%
    private int doubleUpRunden = 0; //Naechster Slot x2

    //Buffs aktivieren (wird vom Marktplatz aufgerufen)

    /**
     * Aktiviert den Lucky-7-Buff fuer die angegebene Anzahl an Runden.
     * @param runden Anzahl der Runden, die hinzugefuegt werden sollen.
     */
    public void aktiviereLucky7(int runden)      
    {
        lucky7Runden += runden; // Addiert die Runden zum vorhandenen Puffer
    }

    /**
     * Aktiviert den Jackpot-Boost-Buff fuer die angegebene Anzahl an Runden.
     * @param runden Anzahl der Runden, die hinzugefuegt werden sollen.
     */
    public void aktiviereJackpotBoost(int runden)
    {
        jackpotBoostRunden += runden; // Addiert die Runden zum vorhandenen Puffer
    }

    /**
     * Aktiviert den Lucky-Spin-Buff fuer die angegebene Anzahl an Runden.
     * @param runden Anzahl der Runden, die hinzugefuegt werden sollen.
     */
    public void aktiviereLuckySpin(int runden)   
    {
        luckySpinRunden += runden; // Addiert die Runden zum vorhandenen Puffer
    }

    /**
     * Aktiviert den Double-Up-Buff fuer die angegebene Anzahl an Runden.
     * @param runden Anzahl der Runden, die hinzugefuegt werden sollen.
     */
    public void aktiviereDoubleUp(int runden)    
    {
        doubleUpRunden += runden; // Addiert die Runden zum vorhandenen Puffer
    }

    // Abfragen ob ein Buff aktiv ist 

    /**
     * Prueft, ob der Lucky-7-Buff aktuell aktiv ist.
     * @return true, wenn noch Runden verbleiben.
     */
    public boolean isLucky7Aktiv()      
    {
        return lucky7Runden > 0;
    }

    /**
     * Prueft, ob der Jackpot-Boost-Buff aktuell aktiv ist.
     * @return true, wenn noch Runden verbleiben.
     */
    public boolean isJackpotBoostAktiv()
    { 
        return jackpotBoostRunden > 0; 
    }

    /**
     * Prueft, ob der Lucky-Spin-Buff aktuell aktiv ist.
     * @return true, wenn noch Runden verbleiben.
     */
    public boolean isLuckySpinAktiv()   
    {
        return luckySpinRunden > 0; 
    }

    /**
     * Prueft, ob der Double-Up-Buff aktuell aktiv ist.
     * @return true, wenn noch Runden verbleiben.
     */
    public boolean isDoubleUpAktiv()    
    {
        return doubleUpRunden > 0; 
    }

    /**
     * Reduziert die Rundenzaehler fuer Slot-relevante Buffs um eins.
     * Sollte nach jedem Spiel am Slot aufgerufen werden.
     */
    public void slotRundeGespielt()
    {
        // Reduziert den Zaehler, falls der Buff aktiv ist
        if (lucky7Runden > 0) 
        {
            lucky7Runden--;    
        }
        if (jackpotBoostRunden > 0) 
        {
            jackpotBoostRunden--;
        }
        if (doubleUpRunden > 0) 
        {
            doubleUpRunden--;
        }
    }

    /**
     * Reduziert den Rundenzaehler fuer Roulette-relevante Buffs um eins.
     * Sollte nach jedem Roulette-Spiel aufgerufen werden.
     */
    public void rouletteRundeGespielt()
    {
        //Reduziert den Zaehler, falls der Buff aktiv ist
        if (luckySpinRunden > 0) 
        {
            luckySpinRunden--;
        }
    }

    //Fuer die Anzeige im Marktplatz

    /** 
     * @return Verbleibende Runden des Lucky-7-Buffs.
     */
    public int getLucky7Runden()       
    {
        return lucky7Runden;       
    }

    /** 
    * @return Verbleibende Runden des Jackpot-Boost-Buffs. 
    */
    public int getJackpotBoostRunden() 
    { 
        return jackpotBoostRunden; 
    }

    /**
     * @return Verbleibende Runden des Lucky-Spin-Buffs.
     */
    public int getLuckySpinRunden()    
    { 
        return luckySpinRunden;    
    }

    /**
     * @return Verbleibende Runden des Double-Up-Buffs.
     */
    public int getDoubleUpRunden()     
    { 
        return doubleUpRunden;     
    }
}