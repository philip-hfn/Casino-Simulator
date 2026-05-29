import java.util.Random;

/**
 * Logik-Klasse für das Roulette-Spiel
 * Verwaltet das Spielfeld, wertet die Einsaetze (Farbe, Gerade/Ungerade, Einzelzahl) aus, berechnet die Gewinnauszahlungen und gleicht das Konto des Spielers ab
 */
public class Roulette
{
    public int ergebnis;  // Die geworfene Zahl der aktuellen Runde (0-36)
    public int gewinn; // Der berechnete Gewinnbetrag der aktuellen Runde
    
    // 2D-Array zur Speicherung der Eigenschaften jeder Zahl:
    //Index 0: true=gerade/false=ungerade, Index 1: true=rot/false=schwarz
    private boolean[][] felder;
    
    // Variablen für die gesetzten Kriterien des Spielers
    boolean kriteriumFarbe;  // true = rot gesetzt, false = schwarz gesetzt
    boolean kriteriumGerade;  // true = gerade gesetzt, false = ungerade gesetzt
    int kriteriumZahl; // Die gesetzte Einzelzahl (1-36)
    
    
    boolean kriteriumGeradeGesetzt;  // true, wenn auf gerade/ungerade gewettet wurde
    boolean kriteriumFarbeGesetzt; // true, wenn auf eine Farbe gewettet wurde
    boolean farbeGesetz;  // Steht für: Es wurde ausschließlich auf eine Einzelzahl gewettet
    
    public int einsatz;// Der aktuell riskierte Geldbetrag
    public boolean hauptgewinn; // Flag, ob die exakte Einzelzahl getroffen wurde (36-facher Gewinn)

    // echte europäische Roulette-Rad-Layout im Uhrzeigersinn
    private int[] rouletteOrder = {
        0,32,15,19,4,21,2,25,17,34,6,27,13,36,
        11,30,8,23,10,5,24,16,33,1,20,14,31,
        9,22,18,29,7,28,12,35,3,26
    };

    Spieler spieler; // Referenz auf das globale Spieler-Objekt zur Kontoverwaltung

    /**
     * Konstruktor der Roulette-Logik
     * @param nSpieler Das aktuelle Spieler-Objekt
     */
    public Roulette(Spieler nSpieler)
    {
        felder  = new boolean[37][4];// Initialisiert das Eigenschafts-Array für 37 Zahlen (0 bis 36)
        spieler = nSpieler;
        arrayBefuellen();
    }

    /**
     * Befuellt das Eigenschaften-Array, weist jeder Zahl ihre Farbe und den Status (gerade/ungerade) nach echten Roulette-Regeln zu
     */
    public void arrayBefuellen()
    {
        felder[0][0] = false; // Die Zahl 0 ist im Roulette ein Sonderfall (weder gerade noch ungerade, weder rot noch schwarz)
        for (int i = 1; i <= 36; i++)// Schleife durchläuft alle Zahlen von 1 bis 36
        {
            felder[i][0] = (i % 2 == 0);// Setzt Index 0 auf true, wenn die Zahl ohne Rest durch 2 teilbar (gerade) ist

            // NEU: echte rote Zahlen beim Roulette
            int[] rot = {1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36};
            felder[i][1] = false;// Zahl als schwarz (false) deklariert
            for (int r : rot)// Abgleich, ob die aktuelle Zahl i in der Liste der roten Zahlen existiert
            {
                if (i == r) 
                { 
                    felder[i][1] = true;// Zahl als rot markieren
                    break; // Suche abbrechen, da Treffer gefunden wurde
                }
            }
        }
    }

    /**
     * Verarbeitet die Eingaben aus der GUI und bereitet die Wett-Kriterien vor
     * @param farbe Eingabe "rot", "schwarz" oder "-" für keine Farbwette
     * @param gerade Eingabe "gerade", "ungerade" oder "-" für keine Paritätswette
     * @param zahl Die gesetzte Zahl (1-36) oder ungültige Werte/0 für keine Zahlenwette
     */
    public void wettmoeglichkeitenAnbieten(String farbe, String gerade, int zahl)
    {
        if (farbe.equalsIgnoreCase("rot"))
        {
            kriteriumFarbe        = true;
            kriteriumFarbeGesetzt = true;
            farbeGesetz           = false;
        }
        else if (farbe.equalsIgnoreCase("schwarz"))
        {
            kriteriumFarbe        = false;
            kriteriumFarbeGesetzt = true;
            farbeGesetz           = false;
        }
        else if (farbe.equals("-"))
        {
            kriteriumFarbeGesetzt = false;
            farbeGesetz           = false;
        }

        // NEU: akzeptiert "gerade"/"ungerade" statt "ja"/"nein"
        if (gerade.equalsIgnoreCase("gerade"))
        {
            kriteriumGerade        = true;
            kriteriumGeradeGesetzt = true;
            farbeGesetz            = false;
        }
        else if (gerade.equalsIgnoreCase("ungerade"))
        {
            kriteriumGerade        = false;
            kriteriumGeradeGesetzt = true;
            farbeGesetz            = false;
        }
        else if (gerade.equals("-"))
        {
            kriteriumGeradeGesetzt = false;
            farbeGesetz            = false;
        }

        if (gerade.equals("-") && farbe.equals("-") && zahl < 37 && zahl > 0)
        {
            kriteriumZahl = zahl;
            farbeGesetz   = true;
        }
    }

    /**
     * Prueft, ob der Spieler genug Guthaben besitzt, und zieht den Einsatz vorab vom Konto ab
     * @param nEinsatz Der gewuenschte Geldeinsatz
     * @return Den akzeptierten Einsatz     ^
     */
    public int einsatzFestlegen(int nEinsatz)
    {
        int konto = spieler.getKontostand();
        if (nEinsatz <= konto)
        {
            einsatz = nEinsatz; // Setzt den Rundeneinsatz fest
            spieler.setKontostand(konto - einsatz);// Zieht das Geld direkt vom Spielerkonto ab
            return einsatz;
        }
        else
        {
            System.out.println("Nicht genügend Geld!");
            return 0;// Wette wird abgelehnt
        }
    }

    /**
     * Simuliert das Werfen der Kugel in den Roulettekessel
     * @return Eine Zufallszahl zwischen 0 und 36
     */
    public int rouletteDrehen()
    {
        Random random = new Random();  // Erstellt einen neuen Zufallsgenerator
        ergebnis = random.nextInt(37); // Generiert eine Zahl 
        return ergebnis; //Liefert die Gewinnzahl zurück
    }

     /**
     * Fuegt dem Spielerkonto den erziehlten Gewinn hinzu
     * @param gewinn Der auszuzahlende Betrag
     */
    public void kontoAktualisieren(int gewinn)
    {
        spieler.setKontostand(spieler.getKontostand() + gewinn);
    }

     /**
     * Fuehrt den Wurf aus und vergleicht das Ergebnis mit den gesetzten Gewinnbedingungen
     * Berücksichtigt Kombinationswetten sowie Einzelzahlen
     * @return Der finale Gesamtgewinn dieser Runde
     */
    public int gewinnBerechnen()
    {
        int     ergebnisZahl   = rouletteDrehen();
        boolean ergebnisGerade = felder[ergebnisZahl][0];// Ermittelt die Eigenschaft (Gerade/Ungerade) der geworfenen Zahl
        boolean ergebnisFarbe  = felder[ergebnisZahl][1];// Ermittelt die Eigenschaft (Farbe) der geworfenen Zahl

        if (kriteriumZahl == 0) // Fall 1: Keine Einzelzahl-Wette aktiv 
        {
            if (kriteriumGerade == ergebnisGerade && kriteriumFarbe == ergebnisFarbe
                && kriteriumGeradeGesetzt && kriteriumFarbeGesetzt) // Kombinations-Wette: Sowohl Farbe als auch Gerade/Ungerade wurden gleichzeitig richtig getippt
            {
                gewinn = einsatz * 4;
            }
            else if (kriteriumGeradeGesetzt) // Einfache Wette: Nur auf Gerade/Ungerade gesetzt und gewonnen
            {
                if (kriteriumGerade == ergebnisGerade) gewinn = einsatz * 2;
            }
            else if (kriteriumFarbeGesetzt)// Einfache Wette: Nur auf Farbe gesetzt und gewonnen
            {
                if (kriteriumFarbe == ergebnisFarbe) gewinn = einsatz * 2;
            }
        }

        if (kriteriumZahl == ergebnisZahl && kriteriumZahl != 0) // Fall 2: Der Spieler hat die exakte Zahl erraten
        {
            gewinn      = einsatz * 36;
            hauptgewinn = true;// Loest den Hauptgewinn-Status aus
        }

        // Falls eine der gesetzten Bedingungen nicht mit dem Ergebnis uebereinstimmt, wird der Gewinn auf null gesetzt
        if (kriteriumGerade != ergebnisGerade && kriteriumGeradeGesetzt) 
        {
            gewinn = 0;
        }
        if (kriteriumFarbe  != ergebnisFarbe  && kriteriumFarbeGesetzt) 
        {
            gewinn = 0;
        }
        if (ergebnisZahl    != kriteriumZahl  && farbeGesetz)
        {
            gewinn = 0;
        }

        // Setzt das Zahlenkriterium fuer die naechste Runde zurueck
        kriteriumZahl = 0;
        return gewinn; // Gibt den errechneten Endgewinn zurueck
    }

    /**
     * Steuert den kompletten Ablauf einer einzelnen Wettrunde
     * @param nEinsatz Geldbetrag
     * @param farbe "rot", "schwarz" oder "-"
     * @param gerade "gerade", "ungerade" oder "-"
     * @param zahl Gewuenschte Einzelzahl
     * @return Der finale Gewinnbetrag, der dem Konto gutgeschrieben wurde
     */
    public int spieldurchfuehren(int nEinsatz, String farbe, String gerade, int zahl)
    {
        hauptgewinn = false; // Setzt den Jackpot-Status zu Rundenbeginn zurueck
        wettmoeglichkeitenAnbieten(farbe, gerade, zahl);
        einsatzFestlegen(nEinsatz);// Bucht das Geld vom Konto ab (falls vorhanden)
        gewinn = gewinnBerechnen();
        System.out.println("Ergebnis: " + ergebnis);
        kontoAktualisieren(gewinn); // Zahlt den Gewinn aufs Spielerkonto aus
        einsatz = 0;
        return gewinn;
    }

      /**
     * Gibt an, ob in der aktuellen Runde der Hauptgewinn (Einzelzahl getroffen) erzielt wurde
     * @return true, wenn die Einzelzahl exakt getroffen wurde
     */
    public boolean getHauptgewinn()
    {
        return hauptgewinn;
    }

    // NEU: liefert die Position der Ergebnis-Zahl auf dem Rad
    /**
     * Ermittelt den Index der Gewinnzahl auf dem Roulette-Rad
     * Wichtig für die GUI, um das Rad exakt an der richtigen Stelle zu stoppen
     * @return Der Index (0 bis 36) im `rouletteOrder`-Array
     */
    public int getWinkelIndex()
    {
        for (int i = 0; i < rouletteOrder.length; i++)// Durchläuft die Anordnung des Kessels, um die Position des aktuellen Ergebnisses zu finden
        {
            if (rouletteOrder[i] == ergebnis)
            {
                 return i;// Gibt die Array-Position zurueck
            }
        }
        return 0;
    }

    /**
     * Gibt das Array mit der originalen Zahlenreihenfolge des Roulette-Rades zurueck
     * @return Das int-Array der Rad-Anordnung
     */
    public int[] getRouletteReihenfolge()
    {
        return rouletteOrder;
    }
}