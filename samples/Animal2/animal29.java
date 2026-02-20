// Changes: method renaming (Type-2) + internal call updates + statement deletion (Type-3, removed redundant else in initInfection())

import java.util.List;
import java.util.Iterator;
import java.util.Random;

public abstract class Animal extends Organism
{
    private boolean isFemale;
    private boolean isAwake; 
    private boolean isInfected;
    public int foodLevel;
    
    public Animal(Field field, Location location, boolean infected)
    {
        super(field, location);
        if(infected == false){
            initInfection();              // RENAMED: setInfection() -> initInfection()
        } else {
            isInfected = infected;
        }
        initGender();                    // RENAMED: setGender() -> initGender()
        isAwake = true;
    }
    
    abstract public void act(List<Organism> newAnimals, String time, String weather);

    public void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    private void initGender()            // RENAMED
    {
        Random rand = Randomizer.getRandom();
        double genderProb = rand.nextDouble();
        if(genderProb <= 0.5){
            this.isFemale = false;
        } else {
            this.isFemale = true;
        }
    }

    public boolean getGender()
    {
        return this.isFemale;
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
    
    public boolean getAwake()
    {
        return isAwake; 
    }

    private void initInfection()         // RENAMED (NOTE: redundant else { isInfected = false; } was deleted here)
    {
        Random rand = Randomizer.getRandom();
        double infectionProb = rand.nextDouble();
        if(infectionProb <= 0.05){
            infect();                   // RENAMED: makeInfected() -> infect()
        }
        // DELETED: else { isInfected = false; }  (redundant due to default false)
    }

    public boolean getInfection()
    {
        return isInfected;
    }

    public void infect()                 // RENAMED
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
                            nearbyAnimal.infect();   // UPDATED CALL
                        }
                    }
                }
            }
        }
    }
}
