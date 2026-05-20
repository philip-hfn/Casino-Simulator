
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
    boolean kriteriumGeradeGesetzt;//gibt an ob auf gerade/ungerade gesetzt wurde
    boolean kriteriumFarbeGesetzt;//gibt an ob auf Farbe gesetzt wurde
    boolean farbeGesetz;
    public int einsatz;
    public boolean hauptgewinn;
    //public int kontostand;
    private int[] rouletteOrder = {
            0,32,15,19,4,21,2,25,17,34,6,27,13,36,
            11,30,8,23,10,5,24,16,33,1,20,14,31,
            9,22,18,29,7,28,12,35,3,26
        };
    Spieler spieler;

    public Roulette(Spieler nSpieler)
    {
        felder=new boolean[37][4];
        spieler = nSpieler;
        arrayBefuellen();
        //kontostand = 1000;
    }

    public void arrayBefuellen()
    {
        //[n][0] ist Spalte für gerade
        //[n][1] ist Spalte für rot (ist nicht schwarz)
        felder[0][0] = false;//Auf Null ist nicht setzbar
        for(int i=1; i<=36; i++)
        {

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
            kriteriumFarbeGesetzt = true;
            farbeGesetz = false;
        }
        else if (farbe.equals("schwarz"))
        {
            kriteriumFarbe = false;
            kriteriumFarbeGesetzt = true;
            farbeGesetz = false;
        }
        else if (farbe.equals("-"))
        {
            kriteriumFarbeGesetzt = false;
            farbeGesetz = false;
        }
        if(gerade.equals("gerade"))
        {
            kriteriumGerade = true;
            kriteriumGeradeGesetzt = true;
            farbeGesetz = false;
        }
        else if (gerade.equals("ungerade"))
        {
            kriteriumGerade = false;
            kriteriumGeradeGesetzt = true;
            farbeGesetz = false;
        }
        else if (gerade.equals("-"))
        {
            kriteriumGeradeGesetzt = false;
            farbeGesetz = false;
        }
        if(gerade.equals("-")&&farbe.equals("-")&&zahl<37&&zahl>0)
        {
            kriteriumZahl = zahl;
            farbeGesetz = true;
        }
    }

    public int einsatzFestlegen(int nEinsatz)
    {
        int konto = spieler.getKontostand();
        if(nEinsatz<=konto)
        {
            einsatz = nEinsatz;
            spieler.setKontostand(konto - einsatz);
            return einsatz;
        }
        else
        {
            System.out.println("Nicht genügend Geld!");
            return 0;
        }
    }

    public int rouletteDrehen()
    {
        Random random = new Random();
        ergebnis = random.nextInt(37);
        return ergebnis;
    }

    public void kontoAktualisieren(int gewinn)
    {
        spieler.setKontostand(spieler.getKontostand() + gewinn);
    }

    public int gewinnBerechnen()
    {
        int ergebnisZahl = rouletteDrehen();
        boolean ergebnisGerade = felder[ergebnisZahl][0];
        boolean ergebnisFarbe = felder[ergebnisZahl][1];
        //gewinn = einsatz;

        if(kriteriumZahl==0)
        {
            if(kriteriumGerade == ergebnisGerade && kriteriumFarbe == ergebnisFarbe&&kriteriumGeradeGesetzt == true&&kriteriumFarbeGesetzt == true)
            {
                gewinn = einsatz * 4;
            }

            else if(kriteriumGeradeGesetzt == true)
            {
                if(kriteriumGerade == ergebnisGerade)
                {
                    gewinn = einsatz*2;
                }
            }
            else if(kriteriumFarbeGesetzt == true)
            {
                if(kriteriumFarbe == ergebnisFarbe)
                {
                    gewinn = einsatz*2;
                }
            }
        }
        if(kriteriumZahl == ergebnisZahl&&kriteriumZahl!=0)
        {
            gewinn = einsatz*36;
            hauptgewinn = true;
        }
        if(kriteriumGerade != ergebnisGerade&&kriteriumGeradeGesetzt == true) 
        {
            gewinn = 0;
        }
        if(kriteriumFarbe != ergebnisFarbe&&kriteriumFarbeGesetzt == true) 
        {
            gewinn = 0;
        }
        if(ergebnisZahl != kriteriumZahl&&farbeGesetz == true) 
        {
            gewinn = 0;
        }
        // if(ergebnisZahl == 0)//Muss noch überprüft werden
        // {
        // kontostand = kontostand - einsatz;
        // }
        kriteriumZahl = 0;
        return gewinn;

    }

    // public int gewinnBerechnen()
    // {
    // int ergebnisZahl = rouletteDrehen();
    // boolean ergebnisGerade = felder[ergebnisZahl][0];
    // boolean ergebnisFarbe = felder[ergebnisZahl][1];

    // gewinn = 0;

    // if(kriteriumZahl != 0)
    // {
    // if(kriteriumZahl == ergebnisZahl)
    // {
    // gewinn = einsatz * 36;
    // }
    // }
    // else
    // {
    // if(kriteriumGeradeGesetzt && kriteriumGerade == ergebnisGerade)
    // {
    // gewinn += einsatz * 2;
    // }

    // if(kriteriumFarbeGesetzt && kriteriumFarbe == ergebnisFarbe)
    // {
    // gewinn += einsatz * 2;
    // }
    // }

    // kriteriumZahl = 0;
    // return gewinn;
    // }

    public int spieldurchfuehren(int nEinsatz, String farbe, String gerade, int zahl)
    {
        hauptgewinn = false;
        wettmoeglichkeitenAnbieten(farbe, gerade, zahl);
        einsatzFestlegen(nEinsatz);
        gewinn = gewinnBerechnen();
        System.out.println("Ergebnis: "+ergebnis);
        kontoAktualisieren(gewinn);
        einsatz = 0;
        return gewinn;
    }

    public boolean getHauptgewinn()
    {
        if(hauptgewinn == true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    public int getWinkelIndex()
    {
        int index = 0;

        for(int i=0;i<rouletteOrder.length;i++){
            if(rouletteOrder[i] == ergebnis){
                index = i;
                break;
            }
        }
        return index;
    }

    public int[] getRouletteReihenfolge()
    {
        return rouletteOrder;
    }
}