// reordered methods and added unreachable method and loop

import java.util.List;
import java.util.Iterator;
import java.util.Random;

public abstract class Animal extends Organism
{
    private boolean isFemale;
    private boolean isAwake; 
    private boolean isInfected;
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
        if(time.equals("Day")){
            isAwake = true;
        }
        else{
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
        if(getInfection()){
            if(getLocation() != null){
                Field field = getField();
                List<Location> adjacent = field.adjacentLocations(getLocation());
                Iterator<Location> it = adjacent.iterator();
                while(it.hasNext()) {
                    Location where = it.next();
                    Object organism = field.getObjectAt(where);
                    Random rand = Randomizer.getRandom();
                    double infectionProb = rand.nextDouble();
                    if(infectionProb <= 0.05){
                        if(organism instanceof Animal){
                            Animal nearbyAnimal = (Animal) organism;
                            nearbyAnimal.makeInfected();
                        }
                    }
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

    private void setGender()
    {
        Random rand = Randomizer.getRandom();
        double genderProb = rand.nextDouble();
        if(genderProb <= 0.5){
            this.isFemale = false;
        } else {
            this.isFemale = true;
        }
    }

    private void setInfection()
    {
        Random rand = Randomizer.getRandom();
        double infectionProb = rand.nextDouble();
        if(infectionProb <= 0.05){
            makeInfected();
        } else {
            isInfected = false;
        }
    }

    /* ---------- Dead Code Added Below ---------- */

    private void neverUsedHelper()
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
