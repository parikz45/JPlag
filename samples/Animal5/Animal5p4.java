// if else converted into ternary version and dead code inserted

import java.util.List;
import java.util.Iterator;
import java.util.Random;

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
    
    public Animal(boolean  randomAge, Field field, Location location, int breedingAge, int maxAge, double breedingProb, int maxLitterSize, int foodValue, boolean randomSickness, boolean isSick)
    {
        super(field, location);

        BREEDING_AGE = breedingAge;
        MAX_AGE = maxAge;
        BREEDING_PROBABILITY = breedingProb;
        MAX_LITTER_SIZE = maxLitterSize;

        isMale = rand.nextBoolean();


         //DEAD CODE INSERTION

       if(age<0){
       System.out.println("impossible state");

     }


     boolean debugFlag= False;
     if(debugFlag){ 
          System.out.println(foodLevel);
          
    }




       age = randomAge ? rand.nextInt(80) : 0;
       foodLevel = randomAge ? rand.nextInt(foodValue) : foodValue;

       this.isSick = isSick;
       isSick = randomSickness ? (rand.nextDouble() <= 0.05) : isSick;

    }

   
     protected void incrementAge()
     {
         age++;

    if ((isSick && age > MAX_AGE / 2) ||
    (!isSick && age > MAX_AGE)) {
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
        return !(age < BREEDING_AGE);
    }

    
    protected boolean getSick()
    {
      return isSick;
    }
    
    
   protected int breed() {
    return (canBreed() && rand.nextDouble() <= BREEDING_PROBABILITY)
            ? rand.nextInt(MAX_LITTER_SIZE) + 1
            : 0;
}


}
