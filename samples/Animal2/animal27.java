
import java.util.List;
import java.util.Iterator;
import java.util.Random;

// Data-representation variant: replace boolean state with enums 


public abstract class Animal extends Organism
{
    private enum Gender { FEMALE, MALE }
    private enum WakeState { AWAKE, ASLEEP }
    private enum InfectionState { INFECTED, CLEAN }

    private Gender gender;
    private WakeState wakeState;
    private InfectionState infectionState;

    public int foodLevel;
    
    public Animal(Field field, Location location, boolean infected)
    {
        super(field, location);
        if(!infected){
            setInfection();
        } else {
            infectionState = InfectionState.INFECTED;
        }
        setGender();
        wakeState = WakeState.AWAKE;
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
        if(genderProb <= 0.5){
            this.gender = Gender.MALE;
        } else {
            this.gender = Gender.FEMALE;
        }
    }

    public boolean getGender()
    {
        return this.gender == Gender.FEMALE;
    }

    public void setAwake(String time)
    {
        if("Day".equals(time)){
            wakeState = WakeState.AWAKE;
        }
        else{
            wakeState = WakeState.ASLEEP;
        }
    }
    
    public boolean getAwake()
    {
        return wakeState == WakeState.AWAKE; 
    }

    private void setInfection()
    {
        Random rand = Randomizer.getRandom();
        double infectionProb = rand.nextDouble();
        if(infectionProb <= 0.05){
            makeInfected();
        } else {
            infectionState = InfectionState.CLEAN;
        }
    }

    public boolean getInfection()
    {
        return infectionState == InfectionState.INFECTED;
    }

    public void makeInfected(){
        infectionState = InfectionState.INFECTED;
        age++;
    }

    public void spreadInfection()
    {
        if(getInfection() && getLocation() != null){
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object organism = field.getObjectAt(where);
                double infectionProb = Randomizer.getRandom().nextDouble();
                if(infectionProb <= 0.05 && organism instanceof Animal){
                    Animal nearbyAnimal = (Animal) organism;
                    nearbyAnimal.makeInfected();
                }
            }
        }
    }
}
