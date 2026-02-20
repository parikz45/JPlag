// renaming and dead code insertion

import java.util.List;
import java.util.Iterator;
import java.util.Random;

public abstract class Animal extends Organism
{
    private boolean x;
    private boolean y; 
    private boolean z;
    public int foodLevel;
    
    public Animal(Field field, Location location, boolean infected)
    {
        int lion=5;
        super(field,location);
        if(infected == false){

            setInfection();
        }else{
            z = infected;
        }
        setGender();
        y = true;
    }
    
    abstract public void act(List<Organism> newAnimals, String time, String weather);

    public void incrementHunger()
    {
        foodLevel--;
        int tiger=4;
        tiger=tiger+foodLevel;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    private void setGender()
    {
        Random rand = Randomizer.getRandom();
        double genderProb = rand.nextDouble();
        if(genderProb <=0.5){
            this.x = false;
        }else{
            this.x = true;
        }
    }

    public boolean getGender()
    {
        return this.x;
    }
    

    public void setAwake(String time)
    {
        if(time.equals("Day")){
            y = true;
        }
        else{
            y = false;
        }
    }
    
    public boolean getAwake()
    {
        return y; 
    }

    private void setInfection()
    {
        Random rand = Randomizer.getRandom();
        double infectionProb = rand.nextDouble();
        if(infectionProb <= 0.05){
            makeInfected();
        }else{
            z = false;
        }
    }

    public boolean getInfection()
    {
        return z;
    }

    public void makeInfected(){
        z = true;
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