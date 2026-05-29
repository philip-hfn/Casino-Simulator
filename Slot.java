public class Slot
{
    private int slot1;
    private int slot2;
    private int slot3;
    private int gewinn;
    private Spieler spieler;

    /**
     * Konstruktor zur Initialisierung des Slots mit einem Spieler-Objekt.
     * @param nSpieler Der Spieler, der die Slot-Maschine bedient.
     */
    public Slot(Spieler nSpieler)
    {
        this.slot1 = 0;
        this.slot2 = 0;
        this.slot3 = 0;
        this.gewinn = 0;
        this.spieler = nSpieler;
    }

    /**
     * Liefert den aktuellen Wert des ersten Slots.
     * @return Den Integer-Wert des ersten Slots.
     */
    public int getSlot1()  
    { 
        return slot1;  
    }

    /**
     * Liefert den aktuellen Wert des zweiten Slots.
     * @return Den Integer-Wert des zweiten Slots.
     */
    public int getSlot2()  
    { 
        return slot2;
    }

    /**
     * Liefert den aktuellen Wert des dritten Slots.
     * @return Den Integer-Wert des dritten Slots.
     */
    public int getSlot3()  
    { 
        return slot3;
    }

    /**
     * Liefert den zuletzt erzielten Gewinn zurueck.
     * @return Den Gewinn als Integer.
     */
    public int getGewinn() 
    { 
        return gewinn; 
    }

    /**
     * Generiert fuer alle drei Slots neue Zufallswerte zwischen 1 und 9.
     */
    public void drehen()
    {
        // Math.random liefert einen Wert von 0.0 bis 1.0, daher die Multiplikation und der Cast
        slot1 = (int)(Math.random() * 9 + 1);
        slot2 = (int)(Math.random() * 9 + 1);
        slot3 = (int)(Math.random() * 9 + 1);
    }

    /**
     * Prueft, ob alle drei Slots identisch sind.
     * @return true, falls alle drei Werte uebereinstimmen.
     */
    public boolean hauptGewinn()
    {
        return slot1 == slot2 && slot2 == slot3;
    }

    /**
     * Prueft auf einen kleinen Gewinn, bei dem mindestens zwei Slots gleich sind.
     * @return true, wenn mindestens ein Paar existiert.
     */
    public boolean kleinerGewinn()
    {
        return slot1 == slot2 || slot2 == slot3 || slot1 == slot3;
    }

    /**
     * Spezial-Jackpot: Prueft, ob alle drei Slots den Wert 7 zeigen.
     * @return true, wenn Jackpot erreicht ist.
     */
    public boolean super7IchKaufDasKasino()
    {
        return slot1 == 7 && slot2 == 7 && slot3 == 7;
    }

    /**
     * Prueft, ob mindestens eine 7 vorhanden ist.
     * @return true, falls eine 7 vorkommt.
     */
    public boolean super7()
    {
        return slot1 == 7 || slot2 == 7 || slot3 == 7;
    }

    /**
     * Prueft, ob genau zwei 7er vorhanden sind.
     * @return true, falls zwei 7er vorkommen.
     */
    public boolean mega7()
    {
        // Kombinationspruefung der drei moeglichen 7er-Paare
        return (slot1 == 7 && slot2 == 7)
        || (slot1 == 7 && slot3 == 7)
        || (slot2 == 7 && slot3 == 7);
    }

    /**
     * Prueft auf Bonus: Eine 7 in Kombination mit zwei gleichen Zahlen.
     * @return true, falls diese spezifische Konstellation vorliegt.
     */
    public boolean bonus7()
    {
        return (slot1 == 7 && slot2 == slot3)
        || (slot2 == 7 && slot1 == slot3)
        || (slot3 == 7 && slot1 == slot2);
    }

    /**
     * Fuehrt den Spielvorgang aus.
     * @param einsatz Der vom Spieler gesetzte Betrag.
     * @return true, wenn das Spiel gueltig war, sonst false.
     */
    public boolean spielen(int einsatz)
    {
        // Ueberpruefung, ob der Einsatz zulaessig ist
        if(einsatz <= 0 || einsatz > spieler.getKontostand())
        {
            gewinn = 0;
            return false;
        }

        // Kontostand aktualisieren und Walzen rotieren
        spieler.changeKontostand(-einsatz);
        drehen();

        // Gewinnlogik mit Hierarchie-Pruefung
        if(super7IchKaufDasKasino())
        {
            gewinn = 800 * einsatz;
        }
        else if(hauptGewinn())
        {
            gewinn = 50 * einsatz;
        }
        else if(mega7())
        {
            gewinn = 20 * einsatz;
        }
        else if(bonus7())
        {
            gewinn = 8 * einsatz;
        }
        else if(kleinerGewinn())
        {
            gewinn = 3 * einsatz;
        }
        else
        {
            gewinn = 0;
        }

        // Wenn ein Gewinn vorliegt, diesen dem Spieler gutschreiben
        if(gewinn > 0)
        {
            spieler.changeKontostand(gewinn);
        }

        return true;
    }
}