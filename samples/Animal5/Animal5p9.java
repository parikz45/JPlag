//some methods extracted and some deadcode added

import java.util.List;
import java.util.Iterator;
import java.util.Random;

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

    public Animal(boolean randomAge, Field field, Location location,
                  int breedingAge, int maxAge,
                  double breedingProb, int maxLitterSize,
                  int foodValue, boolean randomSickness,
                  boolean isSick)
    {
        super(field, location);

        BREEDING_AGE = breedingAge;
        MAX_AGE = maxAge;
        BREEDING_PROBABILITY = breedingProb;
        MAX_LITTER_SIZE = maxLitterSize;

        isMale = rand.nextBoolean();

        //  Extracted method 1
        initializeAgeAndFood(randomAge, foodValue);

        this.isSick = isSick;
        if (randomSickness)
            this.isSick = rand.nextDouble() <= 0.05;

        //  Dead code
        if (false) {
            System.out.println("Unreachable");
        }
    }

    /* ================== Extracted Method 1 ================== */
    private void initializeAgeAndFood(boolean randomAge, int foodValue)
    {
        if(randomAge) {
            age = rand.nextInt(80);
            foodLevel = rand.nextInt(foodValue);
        }
        else {
            age = 0;
            foodLevel = foodValue;
        }
    }

    protected void incrementAge()
    {
        age++;

        //  Extracted method 2
        checkDeathByAge();

        //  Dead arithmetic
        int dummy = 5 * 0;
        dummy++;
    }

    /* ================== Extracted Method 2 ================== */
    private void checkDeathByAge()
    {
        if (isSick) {
            if (age > MAX_AGE/2)
                setDead();
        }
        else if (age > MAX_AGE) {
            setDead();
        }
    }

    protected boolean isMale()
    {
        return isMale;
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

        //  Dead loop
        while(false) {
            System.out.println("Never runs");
        }
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

    protected boolean getSick()
    {
        return isSick;
    }

    protected int breed()
    {
        int births = 0;

        //  Extracted method 3
        if (isReproductionSuccessful()) {
            births = rand.nextInt(MAX_LITTER_SIZE) + 1;
        }

        return births;
    }

    /* ================== Extracted Method 3 ================== */
    private boolean isReproductionSuccessful()
    {
        return canBreed()
               && rand.nextDouble() <= BREEDING_PROBABILITY;
    }
}
