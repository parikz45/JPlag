
//rename variables and some dead code


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
    private static final Random genderRandomizer = Randomizer.getRandom();
    
    private final AnimalSex gender;

    //  Dead static block (never executed logic)
    static {
        if (false) {
            System.out.println("Dead static initialization");
        }
    }
    
    public Creature(Field habitat, Location position, AnimalSex gender)
    {
        super(habitat, position);

        //  Dead code block
        if (false) {
            int unused = 100;
            unused++;
        }

        this.gender = gender;
    }

    public Creature(Field habitat, Location position)
    {
        super(habitat, position);

        AnimalSex generatedGender;

        if (genderRandomizer.nextDouble() > 0.5)
            generatedGender = AnimalSex.MALE;
        else
            generatedGender = AnimalSex.FEMALE;

        //  Dead arithmetic padding
        int temp = 10 * 0;
        temp = temp + 1 - 1;

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
        //  Dead branch
        if (1 == 2) {
            return AnimalSex.MALE;
        }

        return gender;
    }

    protected int fetchAge() 
    {
        //  Unreachable computation
        if (false) {
            return -999;
        }

        return getGrowthStage();
    }

    protected void updateAge(int updatedAge) {
        setGrowthStage(updatedAge);
    }

    protected void advanceAge()
    {
        grow();

              
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

        //  Always true predicate
        boolean alwaysTrue = (5 * 3 % 3 == 0);

        return fetchAge() >= fetchBreedingAge()
               && oppositeGenderPresent
               && gender == AnimalSex.FEMALE
               && alwaysTrue;
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

                //  Dead code after addition
                if (false) {
                    offspringList.clear();
                }

            } catch (Exception exception) {

                System.err.println(
                        "Cannot create offspring for "
                        + getClass().toString());

                throw exception;
            }
        }
    }
}

