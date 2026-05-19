public class Slot
{
    private int slot1;
    private int slot2;
    private int slot3;
    private int gewinn;
    private Spieler spieler;

    public Slot(Spieler nSpieler)
    {
        this.slot1   = 0;
        this.slot2   = 0;
        this.slot3   = 0;
        this.gewinn  = 0;
        this.spieler = nSpieler;
    }

    public int getSlot1()  { return slot1;  }
    public int getSlot2()  { return slot2;  }
    public int getSlot3()  { return slot3;  }
    public int getGewinn() { return gewinn; }

    public void drehen()
    {
        slot1 = (int)(Math.random() * 9 + 1);
        slot2 = (int)(Math.random() * 9 + 1);
        slot3 = (int)(Math.random() * 9 + 1);
    }

    public boolean hauptGewinn()
    {
        return slot1 == slot2 && slot2 == slot3;
    }

    public boolean kleinerGewinn()
    {
        return slot1 == slot2 || slot2 == slot3 || slot1 == slot3;
    }

    public boolean super7IchKaufDasKasino()
    {
        return slot1 == 7 && slot2 == 7 && slot3 == 7;
    }

    public boolean super7()
    {
        return slot1 == 7 || slot2 == 7 || slot3 == 7;
    }

    public boolean mega7()
    {
        return (slot1 == 7 && slot2 == 7)
            || (slot1 == 7 && slot3 == 7)
            || (slot2 == 7 && slot3 == 7);
    }

    public boolean bonus7()
    {
        return (slot1 == 7 && slot2 == slot3)
            || (slot2 == 7 && slot1 == slot3)
            || (slot3 == 7 && slot1 == slot2);
    }

    public boolean spielen(int einsatz)
    {
        if(einsatz <= 0 || einsatz > spieler.getKontostand())
        {
            gewinn = 0;
            return false;
        }

        spieler.changeKontostand(-einsatz);
        drehen();

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

        if(gewinn > 0)
        {
            spieler.changeKontostand(gewinn);
        }

        return true;
    }
}