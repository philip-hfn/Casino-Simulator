public class Slot 
{
    // instance variables - replace the example below with your own
    private int slot1;
    private int slot2;
    private int slot3;
    public int gewinn;
    private int kontostand;

    public Slot()
    {
        this.slot1=0;
        this.slot2=0;
        this.slot3=0; 
        this.kontostand = 100;
    }

    public int slot1()
    {    
        this.slot1 = (int)(Math.random() * 9 + 1);
        return slot1;
    }

    public int slot2()
    {    
        this.slot2 = (int)(Math.random() * 9 + 1);
        return slot2;
    }

    public int slot3()
    {    
        this.slot3 = (int)(Math.random() * 9 + 1);
        return slot3;
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

    public int getKontostand()
    {
        return kontostand;
    }

    public void setKontostand(int nKontostand)
    {
        kontostand=nKontostand;
    }

    public void drehen()
    {
        this.slot1=slot1();
        this.slot2=slot2();
        this.slot3=slot3();
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
        if(slot1 == slot2 || slot2 == slot3 ||slot1==slot3)
        {
            return true;
        }
        return false;
    }

    public boolean super7IchKaufDasKasino()
    {
        if(slot1 == 7 && slot2 == 7 && slot3==7)
        {
            return true;
        }
        return false;
    }

    public boolean super7()
    {
        if(slot1 == 7 || slot2 == 7 || slot3 ==7)
        {
            return true;
        }
        return false;
    }

    public boolean mega7()
    {
        if(slot1 == 7 && slot2 == 7 ||slot1 == 7 && slot3 == 7||slot2 == 7 && slot3 == 7)
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

    public int gewinnBerechnen(int nEinsatz)
    {
        int einsatz= nEinsatz;
        kontostand=kontostand-einsatz;
        this.gewinn=0;
        drehen();
        if(strasse())
        {
            kontostand=kontostand + 104*einsatz;

        }else if(super7IchKaufDasKasino())
        {
            kontostand=kontostand + 729*einsatz;

        }
        else if(hauptGewinn())
        {
            kontostand=kontostand + 81*einsatz;

        }
        else if(mega7())
        {
            kontostand=kontostand + 81*einsatz;

        }
        else if(kleinerGewinn())
        {

        }
        else if(super7())
        {

        }
        else
        {

        }
        
        return kontostand;
    }
}