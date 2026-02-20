
// Statements Reordered


import java.util.List;
import java.util.Iterator;
import java.util.Random;

/**
 * A class representing the shared characteristics of all animals
 *
 * @version 2019.02.20
 */
public abstract class Animal extends Actor
{
    private int BREEDING_AGE;
    private int MAX_AGE;
    private double BREEDING_PROBABILITY;
    private int MAX_LITTER_SIZE;

    protected int foodLevel;
    private int age;
    private boolean isMale;
    private boolean isSick;

    private static final Random rand = Randomizer.getRandom();

    /**
     * Constructor for objects of class Animal
     */
    public Animal(boolean randomAge, Field field, Location location,
                  int breedingAge, int maxAge,
                  double breedingProb, int maxLitterSize,
                  int foodValue, boolean randomSickness, boolean isSick)
    {
        super(field, location);

        //  Assign gender FIRST
        isMale = rand.nextBoolean();

        //  Assign sickness SECOND
        this.isSick = isSick;
        if (randomSickness) {
            this.isSick = rand.nextDouble() <= 0.05;
        }

        //  Assign constants THIRD
        BREEDING_AGE = breedingAge;
        MAX_AGE = maxAge;
        BREEDING_PROBABILITY = breedingProb;
        MAX_LITTER_SIZE = maxLitterSize;

        //  Assign age & food LAST
        if (randomAge) {
            age = rand.nextInt(80);
            foodLevel = rand.nextInt(foodValue);
        } else {
            age = 0;
            foodLevel = foodValue;
        }
    }

    protected void incrementAge()
    {
        age++;
        if (isSick){
            if (age > MAX_AGE/2)
                setDead();
        } else if(age > MAX_AGE) {
            setDead();
        }
    }



    protected void incrementHunger()
    {
        if(isSick){
            foodLevel--;
        }
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }
  protected boolean isMale()
    {
        return isMale;
    }
    protected double getBreedingProb()
    {
        return BREEDING_PROBABILITY;
    }

    protected int getMaxLitter()
    {
        return MAX_LITTER_SIZE;
    }

    protected boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

  

    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }


  protected boolean getSick()
    {
        return isSick;
    }

}
