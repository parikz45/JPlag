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
    // The age at which a Animal can start to breed.
    private int BREEDING_AGE;
    // The age to which a Animal can live.
    private int MAX_AGE;
    // The likelihood of a Animal breeding.
    private double BREEDING_PROBABILITY;
    // The maximum number of births.
    private int MAX_LITTER_SIZE;
    // The Animals's food level, which is increased by eating Shrew or Snakes.
    protected int foodLevel;
    // The age of the animal
    private int age;
    // Whether the animal is male or female
    private boolean isMale;
    // Whether the animal is sick or not
    private boolean isSick;


    private static final Random rand = Randomizer.getRandom();
    /**
     * Constructor for objects of class Animal
     */
    public Animal(boolean  randomAge, Field field, Location location, int breedingAge, int maxAge, double breedingProb, int maxLitterSize, int foodValue, boolean randomSickness, boolean isSick)
    {
        super(field, location);

        BREEDING_AGE = breedingAge;
        MAX_AGE = maxAge;
        BREEDING_PROBABILITY = breedingProb;
        MAX_LITTER_SIZE = maxLitterSize;

        isMale = rand.nextBoolean();

        if(randomAge) {
            age = rand.nextInt(80);
            foodLevel = rand.nextInt(foodValue);

        }
        else {
            age = 0;
            foodLevel = foodValue;
        }

                this.isSick = isSick;
                if (randomSickness)
                  isSick = rand.nextDouble() <= 0.05;
    }

    /**
     * Increase the age. This could result in the Animal's death.
     */
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

    /**
     * Returns whether or not the animal is male or female.
     */
    protected boolean isMale()
    {
        return isMale;
    }

    /**
     * Make this Animal more hungry. This could result in the Animal's death.
     */
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

    /**
     * Return the breeding probability for this animal.
     */
    protected double getBreedingProb()
    {
        return BREEDING_PROBABILITY;
    }

    /**
     * Return the maximum litter size for this animal.
     */
    protected int getMaxLitter()
    {
        return MAX_LITTER_SIZE;
    }

    /**
     * A Animal can breed if it has reached the breeding age.
     */
    protected boolean canBreed()
    {
        return age >= BREEDING_AGE;
    }

    /**
     * Returns true if the animal is sick.
     */
    protected boolean getSick()
    {
      return isSick;
    }
    
     /* if it can breed.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }
        return births;
    }

}
