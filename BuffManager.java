/**
 * Verwaltet alle aktiven Buffs des Spielers.
 * Jeder Buff hat einen Zähler wie viele Runden er noch aktiv ist.
 */
public class BuffManager
{
    private int lucky7Runden      = 0;  // 7er Gewinn x3
    private int jackpotBoostRunden = 0; // Jackpot Gewinn x2
    private int luckySpinRunden   = 0;  // Roulette +50%
    private int doubleUpRunden    = 0;  // nächster Slot x2

    // ── Buffs aktivieren (wird vom Marktplatz aufgerufen) ─────────────────
    public void aktiviereLucky7(int runden)      
    {
        lucky7Runden       += runden; 
    }
    public void aktiviereJackpotBoost(int runden)
    {
        jackpotBoostRunden += runden; 
    }
    public void aktiviereLuckySpin(int runden)   
    {
        luckySpinRunden    += runden; 
    }
    public void aktiviereDoubleUp(int runden)    
    {
        doubleUpRunden     += runden; 
    }

    // ── Abfragen ob ein Buff aktiv ist ────────────────────────────────────
    public boolean isLucky7Aktiv()      
    {
        return lucky7Runden       > 0;
    }
    public boolean isJackpotBoostAktiv()
    { 
        return jackpotBoostRunden > 0; 
    }
    public boolean isLuckySpinAktiv()   
    {
        return luckySpinRunden    > 0; 
    }
    public boolean isDoubleUpAktiv()    
    {
        return doubleUpRunden     > 0; 
    }

    //Nach jedem Slot-Spiel aufrufen 
    public void slotRundeGespielt()
    {
        if (lucky7Runden       > 0) 
        {
            lucky7Runden--;    
        }
        if (jackpotBoostRunden > 0) 
        {
            jackpotBoostRunden--;
        }
        if (doubleUpRunden     > 0) 
        {
            doubleUpRunden--;
        }
    }

    // ── Nach jedem Roulette-Spiel aufrufen ───────────────────────────────
    public void rouletteRundeGespielt()
    {
        if (luckySpinRunden > 0) 
        {
            luckySpinRunden--;
        }
    }

    // ── Für die Anzeige im Marktplatz ─────────────────────────────────────
    public int getLucky7Runden()       
    {
        return lucky7Runden;       
    }
    public int getJackpotBoostRunden() 
    { 
        return jackpotBoostRunden; 
    }
    public int getLuckySpinRunden()    
    { 
        return luckySpinRunden;    
    }
    public int getDoubleUpRunden()     
    { 
        return doubleUpRunden;     
    }
}