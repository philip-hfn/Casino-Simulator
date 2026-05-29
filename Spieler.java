/**
 * Verwaltet die Daten des Spielers, insbesondere den aktuellen Kontostand.
 * * @author (Dein Name) 
 * @version (Datum)
 */
public class Spieler
{
    private int kontostand; // Privates Feld fuer den Kontostand, um Datenkapselung zu gewaehrleisten

    /**
     * Konstruktor fuer die Klasse Spieler.
     * Initialisiert das Konto bei Spielstart mit einem Startguthaben von 1000$.
     */
    public Spieler()
    {
        // Setzt das Startguthaben auf 1000
        this.kontostand = 1000;
    }

    /**
     * Gibt den aktuellen Kontostand des Spielers zurueck.
     * @return Der aktuelle Kontostand als Integer.
     */
    public int getKontostand()
    {
        return kontostand;
    }

    /**
     * Setzt den Kontostand auf einen expliziten neuen Wert.
     * @param nKontostand Der neue Wert fuer den Kontostand.
     */
    public void setKontostand(int nKontostand)
    {
        // Ueberschreibt den alten Kontostand mit dem neuen Wert
        kontostand = nKontostand;    
    }

    /**
     * Veraendert den aktuellen Kontostand um einen bestimmten Betrag.
     * @param nKontostand Der Betrag, um den der Kontostand angepasst werden soll (positiv fuer Gewinn, negativ fuer Einsatz).
     */
    public void changeKontostand(int nKontostand)
    {
        // Addiert den Betrag (kann negativ sein) zum bestehenden Kontostand
        kontostand = kontostand + nKontostand;    
    }
}