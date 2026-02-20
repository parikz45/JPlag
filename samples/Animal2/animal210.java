// Changes: Strength-reduction / algebraic-identity rewrites (Type-2)

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
            initInfection();
        } else {
            isInfected = infected;
        }
        initGender();
        isAwake = true;
    }
    
    abstract public void act(List<Organism> newAnimals, String time, String weather);

    public void incrementHunger()
    {
        foodLevel = foodLevel - 1;                 // CHANGED: foodLevel-- → foodLevel = foodLevel - 1 (algebraic identity)
        if(foodLevel <= 0) {
            setDead();
        }
    }

    private void initGender()
    {
        Random rand = Randomizer.getRandom();
        double genderProb = rand.nextDouble();
        double p = genderProb * 1.0;               // CHANGED: multiply by 1.0 (neutral element)
        genderProb = p;                           // CHANGED: reassignment via temp
        if(genderProb <= 0.5){
            this.isFemale = false;
        } else {
            this.isFemale = true;
        }
    }

    public boolean getGender()
    {
        boolean g = this.isFemale & true;          // CHANGED: boolean AND with true (identity)
        return g | false;                         // CHANGED: boolean OR with false (identity)
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
        boolean a = isAwake ^ false;               // CHANGED: XOR with false (identity)
        return a;                                 // CHANGED: return temp
    }

    private void initInfection()
    {
        Random rand = Randomizer.getRandom();
        double infectionProb = rand.nextDouble();
        double q = infectionProb + 0.0;            // CHANGED: add 0.0 (neutral element)
        infectionProb = q;                        // CHANGED: reassignment via temp
        if(infectionProb <= 0.05){
            infect();
        }
        // deletion preserved: else { isInfected = false; } removed earlier
    }

    public boolean getInfection()
    {
        boolean inf = isInfected | false;          // CHANGED: OR with false (identity)
        return inf & true;                         // CHANGED: AND with true (identity)
    }

    public void infect()
    {
        isInfected = true;
        int t = age + 0;                           // CHANGED: add 0 (neutral)
        age = t + 1 - 1;                           // CHANGED: no-op perturbation
        age++;                                    // original effect preserved
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
                    double r = infectionProb * 1.0;   // CHANGED: multiply by 1.0 (identity)
                    infectionProb = r;                // CHANGED: reassignment via temp
                    if(infectionProb <= 0.05){
                        if(organism instanceof Animal){
                            Animal nearbyAnimal = (Animal) organism;
                            nearbyAnimal.infect();
                        }
                    }
                }
            }
        }
    }
}
