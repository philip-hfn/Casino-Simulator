import java.util.Random;

public class Roulette
{
    public int ergebnis;
    public int gewinn;
    private boolean[][] felder;
    boolean kriteriumFarbe;
    boolean kriteriumGerade;
    int kriteriumZahl;
    boolean kriteriumGeradeGesetzt;
    boolean kriteriumFarbeGesetzt;
    boolean farbeGesetz;
    public int einsatz;
    public boolean hauptgewinn;

    // NEU: echte Roulette-Reihenfolge auf dem Rad
    private int[] rouletteOrder = {
        0,32,15,19,4,21,2,25,17,34,6,27,13,36,
        11,30,8,23,10,5,24,16,33,1,20,14,31,
        9,22,18,29,7,28,12,35,3,26
    };

    Spieler spieler;

    public Roulette(Spieler nSpieler)
    {
        felder  = new boolean[37][4];
        spieler = nSpieler;
        arrayBefuellen();
    }

    public void arrayBefuellen()
    {
        felder[0][0] = false;
        for (int i = 1; i <= 36; i++)
        {
            felder[i][0] = (i % 2 == 0);

            // NEU: echte rote Zahlen beim Roulette
            int[] rot = {1,3,5,7,9,12,14,16,18,19,21,23,25,27,30,32,34,36};
            felder[i][1] = false;
            for (int r : rot)
            {
                if (i == r) { felder[i][1] = true; break; }
            }
        }
    }

    public void wettmoeglichkeitenAnbieten(String farbe, String gerade, int zahl)
    {
        if (farbe.equals("rot"))
        {
            kriteriumFarbe        = true;
            kriteriumFarbeGesetzt = true;
            farbeGesetz           = false;
        }
        else if (farbe.equals("schwarz"))
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
        if (gerade.equals("gerade"))
        {
            kriteriumGerade        = true;
            kriteriumGeradeGesetzt = true;
            farbeGesetz            = false;
        }
        else if (gerade.equals("ungerade"))
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

    public int einsatzFestlegen(int nEinsatz)
    {
        int konto = spieler.getKontostand();
        if (nEinsatz <= konto)
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
        int     ergebnisZahl   = rouletteDrehen();
        boolean ergebnisGerade = felder[ergebnisZahl][0];
        boolean ergebnisFarbe  = felder[ergebnisZahl][1];

        if (kriteriumZahl == 0)
        {
            if (kriteriumGerade == ergebnisGerade && kriteriumFarbe == ergebnisFarbe
                && kriteriumGeradeGesetzt && kriteriumFarbeGesetzt)
            {
                gewinn = einsatz * 4;
            }
            else if (kriteriumGeradeGesetzt)
            {
                if (kriteriumGerade == ergebnisGerade) gewinn = einsatz * 2;
            }
            else if (kriteriumFarbeGesetzt)
            {
                if (kriteriumFarbe == ergebnisFarbe) gewinn = einsatz * 2;
            }
        }

        if (kriteriumZahl == ergebnisZahl && kriteriumZahl != 0)
        {
            gewinn      = einsatz * 36;
            hauptgewinn = true;
        }

        if (kriteriumGerade != ergebnisGerade && kriteriumGeradeGesetzt) gewinn = 0;
        if (kriteriumFarbe  != ergebnisFarbe  && kriteriumFarbeGesetzt)  gewinn = 0;
        if (ergebnisZahl    != kriteriumZahl  && farbeGesetz)            gewinn = 0;

        kriteriumZahl = 0;
        return gewinn;
    }

    public int spieldurchfuehren(int nEinsatz, String farbe, String gerade, int zahl)
    {
        hauptgewinn = false;
        wettmoeglichkeitenAnbieten(farbe, gerade, zahl);
        einsatzFestlegen(nEinsatz);
        gewinn = gewinnBerechnen();
        System.out.println("Ergebnis: " + ergebnis);
        kontoAktualisieren(gewinn);
        einsatz = 0;
        return gewinn;
    }

    public boolean getHauptgewinn()
    {
        return hauptgewinn;
    }

    // NEU: liefert die Position der Ergebnis-Zahl auf dem Rad
    public int getWinkelIndex()
    {
        for (int i = 0; i < rouletteOrder.length; i++)
            if (rouletteOrder[i] == ergebnis) return i;
        return 0;
    }

    public int[] getRouletteReihenfolge()
    {
        return rouletteOrder;
    }
}