public class Slot
{
    private int slot1;
    private int slot2;
    private int slot3;
    private int gewinn;
    public int kontostand;
    Spieler spieler;
    public Slot(Spieler nSpieler)
    {
        this.slot1 = 0;
        this.slot2 = 0;
        this.slot3 = 0;
        this.kontostand = 100;
        this.gewinn = 0;
        spieler = nSpieler;
    }

    public int getSlot1()
    {
        return slot1;
    }

    public int getSlot2()
    {
        return slot2;
    }

    public int getSlot3()
    {
        return slot3;
    }
    
    public int getGewinn()
    {
        return gewinn;
    }
    
    public void drehen()
    {
        slot1 = (int)(Math.random() * 9 + 1);
        slot2 = (int)(Math.random() * 9 + 1);
        slot3 = (int)(Math.random() * 9 + 1);
    }

    public boolean hauptGewinn()
    {
        if(slot1 == slot2 && slot2 == slot3)
        {
            return true;
        }
        return false;
    }

    public boolean kleinerGewinn()
    {
        if(slot1 == slot2 || slot2 == slot3 ||   slot1 == slot3)
        {
            return true;
        }
        return false;
    }

    public boolean super7IchKaufDasKasino()
    {
        if(slot1 == 7 && slot2 == 7 && slot3 == 7)
        {
            return true;
        }
        return false;
    }

    public boolean super7()
    {
        if(slot1 == 7 || slot2 == 7 || slot3 == 7)
        {
            return true;
        }
        return false;
    }

    public boolean mega7()
    {
        if((slot1 == 7 && slot2 == 7) || (slot1 == 7 && slot3 == 7) ||  (slot2 == 7 && slot3 == 7))
        {
            return true;
        }
        return false;
    }

    public boolean strasse()
    {
        if(slot1 + 1 == slot2 && slot2 + 1 == slot3)
        {
            return true;
        }
        return false;
    }

    public void spielen(int nEinsatz)
    {
        kontoAktualisieren(gewinnBerechnen(nEinsatz));
    }
    
    public int gewinnBerechnen(int nEinsatz)
    {
        int einsatz = nEinsatz;
        if(einsatz <= 0)
        {
            gewinn = -1;
            return kontostand;
        }
        if(einsatz > kontostand)
        {
            gewinn = -2;
            return kontostand;
        }
        kontostand = kontostand - einsatz;
        drehen();
        gewinn = 0;
        if(super7IchKaufDasKasino())
        {
            gewinn = 729 * einsatz;
        }
        else if(strasse())
        {
            gewinn = 104 * einsatz;
        }
        else if(hauptGewinn())
        {
            gewinn = 81 * einsatz;
        }
        else if(mega7())
        {
            gewinn = 40 * einsatz;
        }
        else if(kleinerGewinn())
        {
            gewinn = 10 * einsatz;
        }
        else if(super7())
        {
            gewinn = 5 * einsatz;
        }
        return gewinn;
    }
    
    public void kontoAktualisieren(int gewinn)
    {
        spieler.changeKontostand(gewinn);
    }
}