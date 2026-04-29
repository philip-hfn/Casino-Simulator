
public class Slot extends Spiel 
{
    // instance variables - replace the example below with your own
    private int slot1;
    private int slot2;
    private int slot3;
    public int gewinn;

    public Slot()
    {
        this.slot1=0;
        this.slot2=0;
        this.slot3=0;                 
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
        this.gewinn=0;
        einsatz = nEinsatz;
        drehen();
        if(strasse())
        {
            return this.gewinn=this.gewinn + 104*einsatz;

        }else if(super7IchKaufDasKasino())
        {
            return this.gewinn=this.gewinn + 729*einsatz;

        }
        else if(hauptGewinn())
        {
            return this.gewinn=this.gewinn + 81*einsatz;

        }
        else if(mega7())
        {
            return this.gewinn=this.gewinn + 81*einsatz;

        }
        else if(kleinerGewinn())
        {
            return 0;

        }
        else if(super7())
        {
            return 0;

        }
        else
        {
            return 0;
        }
    }
}
