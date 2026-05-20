
/**
 * Beschreiben Sie hier die Klasse Spieler.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Spieler
{
    private int kontostand;
    
    public Spieler()
    {
        kontostand = 1000;
    }
    
    public int getKontostand()
    {
        return kontostand;
    }
    
    public void setKontostand(int konto)
    {
        kontostand = konto;
    }
}
