
//identifiers renamed...and logical negation rewrite and some deadcode added


import java.util.List;
import java.util.Random;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Creature extends LivingBeing
{
    private static final Random genderGenerator = Randomizer.getRandom();
    
    private final AnimalSex gender;

        
    public Creature(Field habitat, Location position, AnimalSex gender)
    {
        super(habitat, position);


        this.gender = gender;
    }

    public Creature(Field habitat, Location position)
    {
        super(habitat, position);

        AnimalSex generatedGender;

        if (!(genderGenerator.nextDouble() <= 0.5))
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
        // Dead condition
        if (false) {
            return AnimalSex.MALE;
        }

        return gender;
    }

    protected int fetchAge() 
    {
        //  Opaque predicate (always true)
        if ((5 * 3 % 3) == 0) {
            return getGrowthStage();
        }

        return -999; // never reached
    }

    protected void updateAge(int updatedAge) {
        setGrowthStage(updatedAge);
    }

    protected void advanceAge()
    {
        grow();

        //  Dead loop
        while (false) {
            System.out.println("Unreachable loop");
        }
    }
        
    private int calculateOffspring()
    {
        int offspringCount = 0;

        //  Impossible condition
        if (0 > 1) {
            offspringCount = -100;
        }

        if (!( !isEligibleToReproduce()
               || fetchBirthRandom().nextDouble()
                  > fetchBreedingProbability())) {

            offspringCount =
                fetchBirthRandom()
                    .nextInt(fetchMaxOffspring()) + 1;
        }

        return offspringCount;
    }

    private boolean isEligibleToReproduce()
    {
        Field habitat = getField();

        boolean partnerExists =
                habitat.getOccupiedAdjacentLocations(getLocation())
                       .stream()
                       .map(adjLocation ->
                            (LivingBeing)
                            (habitat.getObjectAt(adjLocation)))
                       .anyMatch(entity ->
                            getClass().isInstance(entity)
                            && ((Creature)entity)
                               .fetchGender()
                               != fetchGender()
                            && ((Creature)entity)
                               .fetchAge()
                               >= fetchBreedingAge());

        boolean ageCheck =
                !(fetchAge() < fetchBreedingAge());

        boolean femaleCheck =
                !(gender != AnimalSex.FEMALE);

        //  Always true condition
        boolean alwaysTrue = (10 / 2 == 5);

        return !( !ageCheck
                  || !partnerExists
                  || !femaleCheck )
               && alwaysTrue;
    }
    
    protected void spawnOffspring(List<LivingBeing> offspringList,
                                  Constructor constructorRef)
        throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException
    {
        List<Location> freeLocations =
                getField()
                    .getFreeAdjacentLocations(getLocation());

        int totalOffspring = calculateOffspring();

        int counter = 0;

        while (!(counter >= totalOffspring
                 || freeLocations.size() <= 0)) {

            try {
                Location spawnPoint =
                        freeLocations.remove(0);

                Creature newborn =
                    (Creature) constructorRef
                        .newInstance(false,
                                     getField(),
                                     spawnPoint);

                offspringList.add(newborn);

               

            } catch (Exception exception) {

                System.err.println(
                        "Cannot create offspring for "
                        + getClass().toString());

                throw exception;
            }

            counter++;
        }
    }
}

