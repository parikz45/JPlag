import java.util.List;
import java.util.Iterator;
import java.util.Random;

// Expression-level rewrites(foodLevel-- → foodLevel = foodLevel - 1, genderProb <= 0.5 → !(genderProb > 0.5) etc.)


public abstract class Animal extends Organism
{
    private boolean isFemale;
    private boolean isAwake; 
    private boolean isInfected;
    public int foodLevel;
    
    public Animal(Field field, Location location, boolean infected)
    {
        super(field, location);
        if(!infected){
            setInfection();
        } else {
            isInfected = infected;
        }
        setGender();
        isAwake = true;
    }
    
    abstract public void act(List<Organism> newAnimals, String time, String weather);

    public void incrementHunger()
    {
        foodLevel = foodLevel - 1;
        if(!(foodLevel > 0)) {
            setDead();
        }
    }

    private void setGender()
    {
        Random rand = Randomizer.getRandom();
        double genderProb = rand.nextDouble();
        if(!(genderProb > 0.5)){
            this.isFemale = false;
        } else {
            this.isFemale = true;
        }
    }

    public boolean getGender()
    {
        return this.isFemale == true;
    }

    public void setAwake(String time)
    {
        if("Day".equals(time)){
            isAwake = true;
        }
        else{
            isAwake = false;
        }
    }
    
    public boolean getAwake()
    {
        return isAwake == true; 
    }

    private void setInfection()
    {
        double infectionProb = Randomizer.getRandom().nextDouble();
        if(!(infectionProb > 0.05)){
            makeInfected();
        } else {
            isInfected = false;
        }
    }

    public boolean getInfection()
    {
        return isInfected == true;
    }

    public void makeInfected(){
        isInfected = true;
        age = age + 1;
    }

    public void spreadInfection()
    {
        if(getInfection() == true){
            if(!(getLocation() == null)){
                Field field = getField();
                List<Location> adjacent = field.adjacentLocations(getLocation());
                Iterator<Location> it = adjacent.iterator();
                while(it.hasNext()) {
                    Location where = it.next();
                    Object organism = field.getObjectAt(where);
                    double infectionProb = Randomizer.getRandom().nextDouble();
                    if(!(infectionProb > 0.05)){
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
