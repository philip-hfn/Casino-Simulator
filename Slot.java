

public class Slot
{
    // instance variables - replace the example below with your own
    private int slot1;
    private int slot2;
    private int slot3;
    public int gewinn;
    
    public Slot()
    {
        int slot1=0;
        int slot2=0;
        int slot3=0;                 
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
    
    public boolean hauptGewinn()
    {
        if(slot1() == slot2() && slot2() == slot3)
        {
            return true;
        }
        return false;
    }
    public boolean kleinerGewinn()
    {
        if(slot1() == slot2() || slot2() == slot3 ||slot1()==slot3())
        {
            return true;
        }
        return false;
    }
    public boolean super7IchKaufDasKasino()
    {
        if(slot1() == 7 && slot2() == 7 && slot3()==7)
        {
            return true;
        }
        return false;
    }
    public int spielen(int einsatz)
    {
        this.gewinn=this.gewinn - einsatz;
        if(hauptGewinn())
        {
            return this.gewinn=this.gewinn + 1000*einsatz;
            
        }
        else if(kleinerGewinn())
        {
            return this.gewinn=this.gewinn + 5*einsatz;
            
        }
        else if(super7IchKaufDasKasino())
        {
            return this.gewinn=this.gewinn + 1000000*einsatz;
            
        }
        else
        {
            return 0;
        }
    }
}
