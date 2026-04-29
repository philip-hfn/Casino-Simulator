
import java.util.Random;
/**
 * Beschreiben Sie hier die Klasse Roulette.
 * 
 * @author (Ihr Name) 
 * @version (eine Versionsnummer oder ein Datum)
 */
public class Roulette extends Spiel
{
    public int ergebnis;
    public int gewinn;
    private boolean[][] felder;
    boolean kriteriumFarbe;
    boolean kriteriumGerade;
    int kriteriumZahl;
    public int einsatz;

    public Roulette()
    {
        felder=new boolean[37][4];
    }

    public void arrayBefuellen()
    {
        //[n][0] ist Spalte für gerade
        //[n][1] ist Spalte für rot (ist nicht schwarz)
        for(int i=1; i==36; i++)
        {
            felder[0][0] = false;//Auf Null ist nicht setzbar
            if(i%2==0)
            {
                felder[i][0] = true;
            }
            else
            {
                felder[i][0] = false;
            }
            if(i<=18)
            {
                felder[i][1] = true;
            }
            else
            {
                felder[i][1] = false;
            }
        }
    }

    /**
     * farbe:schwarz oder rot
     * gerade: ja oder nein
     * zahl: 
     */
    public void wettmoeglichkeitenAnbieten(String farbe, String gerade, int zahl)
    {

        if(farbe.equals("rot"))
        {
            kriteriumFarbe = true;
        }
        else if (farbe.equals("schwarz"))
        {
            kriteriumFarbe = false;
        }
        else if (farbe.equals("-"))
        {
            Boolean kriteriumFarbe = null;
        }
        if(gerade.equals("ja"))
        {
            kriteriumGerade = true;
        }
        else if (gerade.equals("nein"))
        {
            kriteriumGerade = false;
        }
        else if (gerade.equals("-"))
        {
            Boolean kriteriumGerade = null;
        }
        if(gerade.equals("-")&&farbe.equals("-")&&zahl<37&&zahl>0)
        {
            kriteriumZahl = zahl;
        }
    }

    public int einsatzFestlegen(int nEinsatz)
    {
        einsatz = nEinsatz;
        return einsatz;
    }

    public int rouletteDrehen()
    {
        Random random = new Random();
        ergebnis = random.nextInt(37);
        return ergebnis;
    }

    public int gewinnBerechnen()
    {
        boolean ergebnisGerade = felder[rouletteDrehen()][0];
        boolean ergebnisFarbe = felder[rouletteDrehen()][1];
        int ergebnisZahl = rouletteDrehen();

        if(kriteriumZahl==0)
        {
            if(kriteriumGerade = ergebnisGerade)
            {
                gewinn = einsatz*2;
            }
            if(kriteriumFarbe = ergebnisFarbe)
            {
                gewinn = einsatz*2;
            }
        }
        if(kriteriumZahl == ergebnisZahl&&kriteriumZahl!=0)
        {
            gewinn = einsatz*36;
        }
        // if(kriteriumGerade != ergebnisGerade||kriteriumFarbe != ergebnisFarbe)
        // {
            // gewinn = 0;
        // }
        kriteriumZahl = 0;
        return gewinn;
    }
    
    public int spieldurchfuehren(int nEinsatz, String farbe, String gerade, int zahl)
    {
        arrayBefuellen();
        wettmoeglichkeitenAnbieten(farbe, gerade, zahl);
        einsatzFestlegen(nEinsatz);
        rouletteDrehen();
        gewinnBerechnen();
        System.out.println("Ergebnis: "+ergebnis);
        return gewinn;
    }
}