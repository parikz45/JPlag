//rename variables

import java.util.List;
import java.util.Random;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2016.02.29 (2)
 */
public abstract class Creature extends LivingBeing
{
    // A shared random number generator that determines the animal's sex
    private static final Random genderRandomizer = Randomizer.getRandom();
    
    // The creature's gender
    private final AnimalSex gender;
    
    /**
     * Create a new creature at location in field.
     */
    public Creature(Field habitat, Location position, AnimalSex gender)
    {
        super(habitat, position);
        this.gender = gender;
    }

    /**
     * Creates a new creature with its gender randomly determined.
     */
    public Creature(Field habitat, Location position)
    {
        super(habitat, position);
        AnimalSex generatedGender;

        if (genderRandomizer.nextDouble() > 0.5)
            generatedGender = AnimalSex.MALE;
        else
            generatedGender = AnimalSex.FEMALE;

        this.gender = generatedGender;
    }
    
    abstract public void performAction(List<LivingBeing> offspringList)
            throws InstantiationException, IllegalAccessException,
                   IllegalArgumentException, InvocationTargetException,
                   NoSuchMethodException, SecurityException;

    abstract protected int fetchBreedingAge();
    abstract protected double fetchBreedingProbability();
    abstract protected int fetchMaxOffspring();
    abstract protected int fetchMaximumAge();
    abstract protected Random fetchBirthRandom();
    
    protected AnimalSex fetchGender()
    {
        return gender;
    }

    protected int fetchAge() 
    {
        return getGrowthStage();
    }

    protected void updateAge(int updatedAge) {
        setGrowthStage(updatedAge);
    }

    protected void advanceAge()
    {
        grow();
    }
        
    private int calculateOffspringCount()
    {
        int offspringCount = 0;

        if(isEligibleToBreed() &&
           fetchBirthRandom().nextDouble() <= fetchBreedingProbability()) {

            offspringCount =
                fetchBirthRandom().nextInt(fetchMaxOffspring()) + 1;
        }

        return offspringCount;
    }

    private boolean isEligibleToBreed()
    {
        Field habitat = getField();

        boolean oppositeGenderPresent =
                habitat.getOccupiedAdjacentLocations(getLocation())
                       .stream()
                       .map(adjLoc ->
                            (LivingBeing)(habitat.getObjectAt(adjLoc)))
                       .anyMatch(entity ->
                            getClass().isInstance(entity)
                            && ((Creature)entity).fetchGender()
                               != fetchGender()
                            && ((Creature)entity).fetchAge()
                               >= fetchBreedingAge());

        return fetchAge() >= fetchBreedingAge()
               && oppositeGenderPresent
               && gender == AnimalSex.FEMALE;
    }
    
    protected void createOffspring(List<LivingBeing> offspringList,
                                   Constructor constructorRef)
            throws InstantiationException, IllegalAccessException,
                   IllegalArgumentException, InvocationTargetException
    {
        List<Location> availableSpots =
                getField().getFreeAdjacentLocations(getLocation());

        int offspringTotal = calculateOffspringCount();

        for(int index = 0;
            index < offspringTotal && availableSpots.size() > 0;
            index++) {

            try {
                Location newPosition = availableSpots.remove(0);

                Creature newborn =
                    (Creature) constructorRef
                        .newInstance(false, getField(), newPosition);

                offspringList.add(newborn);

            } catch (Exception exception) {

                System.err.println(
                        "Cannot create offspring for "
                        + getClass().toString());

                throw exception;
            }
        }
    }
}

