// Exact copy, removed comments and whitespace only

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
        super(field,location);
        if(infected == false){

            setInfection();
        }else{
            isInfected = infected;
        }
        setGender();
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

    private void setGender()
    {
        Random rand = Randomizer.getRandom();
        double genderProb = rand.nextDouble();
        if(genderProb <=0.5){
            this.isFemale = false;
        }else{
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

    private void setInfection()
    {
        Random rand = Randomizer.getRandom();
        double infectionProb = rand.nextDouble();
        if(infectionProb <= 0.05){
            makeInfected();
        }else{
            isInfected = false;
        }
    }

    public boolean getInfection()
    {
        return isInfected;
    }

    public void makeInfected(){
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
                        if( infectionProb <= 0.05){
                            if(organism instanceof Animal){
                                Animal nearbyAnimal = (Animal) organism;
                                nearbyAnimal.makeInfected();
                            }
                        }
                    }
                }
        }
    }
}