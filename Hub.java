
/**
 * Beschreiben Sie hier die Klasse Hub.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Hub
{
    private int kontostand;
    private Slot slotmaschine;
    private Roulette roulette;
    private Spieler spieler;
    /**
     * Konstruktor für Objekte der Klasse Hub
     */
    public Hub()
    {
        kontostand = 0;
        slotmaschine = new Slot();
        roulette = new Roulette();
        spieler = new Spieler();    
    }
    
    public int gibKontostand()
    {
        return kontostand;    
    }
    
    public void changeKontostand(int change)
    {
        kontostand = kontostand - change;
    }
    
    private void setKontostand(int newKontostand)
    {
        kontostand = newKontostand;
    }

    public void kontostandAnzigen()
    {
    
    }
    
    public void verfuegbareSpieleAnzeigen()
    {
    
    }
    
    public void spielerAnzeigen()
    {
    
    }
    
    public void weiterleitenRoulette()
    {
    
    }
    
    public void weiterleitenSLot()
    {
    
    }

}