import java.util.List;
import java.util.Random;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Animal extends LivingBeing
{
    // A shared random number generator that determines the animal's sex
    private static final Random sexRandomizer = Randomizer.getRandom();
    
    // Individual characteristics (instance fields).
    
    // The animal's sex
    private final AnimalSex sex;
    
    /**
     * Create a new animal at location in field.
     * 
     * @param field The field currently occupied.
     * @param location The location within the field.
     * @param sex the animal's sex
     */
    public Animal(Field field, Location location, AnimalSex sex)
    {
        super(field, location);
        this.sex = sex;
    }

    /**
     * Creates a new animal with its sex randomly determined.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location)
    {
	super(field, location);
        AnimalSex randSex;
        if (sexRandomizer.nextDouble() > 0.5)
            randSex = AnimalSex.MALE;
        else
            randSex = AnimalSex.FEMALE;
        this.sex = randSex;
    }
    
    /**
     * Make this animal act - that is: make it do
     * whatever it wants/needs to do.
     * @param newAnimals A list to receive newly born animals.
     */
    abstract public void act(List<LivingBeing> newAnimals) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException;

    /*
     * these methods will, in concrete subclasses, return constant values,
     * and are here so that each implementing class would have its own values.
     */
    abstract protected int getBreedingAge();
    abstract protected double getBreedingProbability();
    abstract protected int getMaxLitterSize();
    abstract protected int getMaxAge();
    abstract protected Random getBirthRng();
    
    /**
     * Returns the sex of the animal. Obviously, only a female animal
     * can give birth to youngs.
     * @return the animal's sex
     */
    protected AnimalSex getSex()
    {
        return sex;
    }

    /**
     * Returns the animal's age.
     * @preturn the animal's age
     */
    protected int getAge() 
    {
        return getGrowthStage();
    }

    /**
     * Sets the age of the animal.
     * @param newAge the new age of the animal.
     */
    protected void setAge(int newAge) {
	setGrowthStage(newAge);
    }

    /**
     * Increase the age.
     * This could result in the animal's death.
     */
    protected void incrementAge()
    {
        grow();
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    private int breed()
    {
        int births = 0;
        if(canBreed() && getBirthRng().nextDouble() <= getBreedingProbability()) {
            births = getBirthRng().nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * An animal can breed if it has reached the breeding age, 
     * has other members of the opposite sex and of its species nearby, 
     * and is female.
     * @return true if the animal can breed, false otherwise.
     */
    private boolean canBreed()
    {
        Field field = getField();
        boolean hasOppositeSex = field.getOccupiedAdjacentLocations(getLocation())
                                      .stream()
                                      .map(beingLoc -> (LivingBeing)(field.getObjectAt(beingLoc)))
	                              .anyMatch(animal -> getClass().isInstance(animal) 
                                                && ((Animal)animal).getSex() != getSex()
                                                && ((Animal)animal).getAge() >= getBreedingAge());
        return getAge() >= getBreedingAge() && hasOppositeSex && sex == AnimalSex.FEMALE;
    }
    
    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * The new animal is created through reflection.
     * @param newAnimals A list to return newly born animals.
     * @param newAnimalCtor The constructor to create the new animal with.
     */
    protected void giveBirth(List<LivingBeing> newAnimals, Constructor newAnimalCtor)
	throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        // New animals are born into adjacent locations.
        // Get a list of adjacent free locations.
        List<Location> free = getField().getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
	    try {
		Location loc = free.remove(0);
		Animal young = (Animal) newAnimalCtor.newInstance(false, getField(), loc);
		newAnimals.add(young);
	    } catch (Exception e) {
		System.err.println("Cannot give birth to a new " + getClass().toString());
	        throw e;
	    }
        }
    }
}
