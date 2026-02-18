// changed access modifier of methods and converted if-else to switch statement

import java.util.List;
import java.util.Iterator;
import java.util.Random;

public abstract class Animal extends Organism
{
    protected boolean isFemale;     
    private boolean isAwake; 
    protected boolean isInfected;   
    public int foodLevel;

    abstract public void act(List<Organism> newAnimals, String time, String weather);

    public boolean getAwake()
    {
        return isAwake; 
    }

    public boolean getGender()
    {
        return this.isFemale;
    }

    public boolean getInfection()
    {
        return isInfected;
    }

    public void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    public void setAwake(String time)
    {
        switch(time) {
            case "Day":
                isAwake = true;
                break;
            default:
                isAwake = false;
        }
    }

    public void makeInfected()
    {
        isInfected = true;
        age++;
    }

    public void spreadInfection()
    {
        if(getInfection() && getLocation() != null){
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());

            for (Iterator<Location> it = adjacent.iterator(); it.hasNext(); ) {
                Location where = it.next();
                Object organism = field.getObjectAt(where);
                Random rand = Randomizer.getRandom();
                double infectionProb = rand.nextDouble();

                if(infectionProb <= 0.05 && organism instanceof Animal){
                    Animal nearbyAnimal = (Animal) organism;
                    nearbyAnimal.makeInfected();
                }
            }
        }
    }

    public Animal(Field field, Location location, boolean infected)
    {
        super(field, location);
        if(infected == false){
            setInfection();
        } else {
            isInfected = infected;
        }
        setGender();
        isAwake = true;
    }

    protected void setGender()   
    {
        Random rand = Randomizer.getRandom();
        double genderProb = rand.nextDouble();
        if(genderProb <= 0.5){
            this.isFemale = false;
        } else {
            this.isFemale = true;
        }
    }

    protected void setInfection()   
    {
        Random rand = Randomizer.getRandom();
        double infectionProb = rand.nextDouble();
        if(infectionProb <= 0.05){
            makeInfected();
        } else {
            isInfected = false;
        }
    }

    /* ---------- Dead Code ---------- */

    protected void neverUsedHelper()   
    {
        int x = 10;
        int y = 20;
        int z = x + y;
    }

    private void deadLoop()
    {
        for(int i = 0; i < 0; i++) {
            System.out.println("This will never execute");
        }
    }
}
