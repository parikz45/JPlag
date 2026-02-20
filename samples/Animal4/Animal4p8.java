 //some Method Extraction + Dead Code Version

import java.util.List;
import java.util.Random;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Animal extends LivingBeing
{
    private static final Random sexRandomizer = Randomizer.getRandom();
    private final AnimalSex sex;

   
    public Animal(Field field, Location location, AnimalSex sex)
    {
        super(field, location);
        this.sex = sex;
    }

    public Animal(Field field, Location location)
    {
        super(field, location);
        this.sex = generateRandomSex();

        //  Dead arithmetic padding
        int dummy = 5 * 0;
        dummy++;
        dummy--;
    }

    //  Extracted gender generation
    private AnimalSex generateRandomSex() {
        return (sexRandomizer.nextDouble() > 0.5)
                ? AnimalSex.MALE
                : AnimalSex.FEMALE;
    }

    abstract public void act(List<LivingBeing> newAnimals)
        throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException,
               NoSuchMethodException, SecurityException;

    abstract protected int getBreedingAge();
    abstract protected double getBreedingProbability();
    abstract protected int getMaxLitterSize();
    abstract protected int getMaxAge();
    abstract protected Random getBirthRng();

    protected AnimalSex getSex() {
        return sex;
    }

    protected int getAge() {
        return getGrowthStage();
    }

    protected void setAge(int newAge) {
        setGrowthStage(newAge);
    }

    protected void incrementAge() {
        grow();

        //  Dead loop
        while (false) {
            System.out.println("Unreachable");
        }
    }

    private int breed() {
        if (!isReproductionSuccessful()) {
            return 0;
        }
        return generateBirthCount();
    }

    //  Extracted probability + eligibility logic
    private boolean isReproductionSuccessful() {
        return canBreed()
               && getBirthRng().nextDouble()
                  <= getBreedingProbability();
    }

    //  Extracted birth count generation
    private int generateBirthCount() {
        return getBirthRng()
                .nextInt(getMaxLitterSize()) + 1;
    }

    private boolean canBreed() {
        boolean partnerExists = hasOppositeSexNearby();

        boolean ageCheck = getAge() >= getBreedingAge();
        boolean femaleCheck = sex == AnimalSex.FEMALE;

        // Opaque predicate (always true)
        boolean alwaysTrue = (10 / 2 == 5);

        return partnerExists && ageCheck && femaleCheck && alwaysTrue;
    }

    // Extracted partner detection logic
    private boolean hasOppositeSexNearby() {
        Field field = getField();

        return field.getOccupiedAdjacentLocations(getLocation())
                    .stream()
                    .map(loc ->
                        (LivingBeing)(field.getObjectAt(loc)))
                    .anyMatch(entity ->
                        getClass().isInstance(entity)
                        && ((Animal)entity).getSex() != getSex()
                        && ((Animal)entity).getAge()
                           >= getBreedingAge());
    }

    protected void giveBirth(List<LivingBeing> newAnimals,
                             Constructor newAnimalCtor)
        throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException
    {
        List<Location> free =
                getField()
                .getFreeAdjacentLocations(getLocation());

        int births = breed();

        for(int b = 0;
            b < births && free.size() > 0;
            b++) {

            try {
                createNewAnimal(newAnimals,
                                newAnimalCtor,
                                free);

            } catch (Exception e) {
                System.err.println(
                        "Cannot give birth to a new "
                        + getClass().toString());
                throw e;
            }
        }
    }

    //  Extracted newborn creation
    private void createNewAnimal(List<LivingBeing> list,
                                 Constructor ctor,
                                 List<Location> free)
        throws InstantiationException,
               IllegalAccessException,
               IllegalArgumentException,
               InvocationTargetException
    {
        Location loc = free.remove(0);

        Animal young =
            (Animal) ctor.newInstance(false,
                                      getField(),
                                      loc);

        list.add(young);

        //  Dead branch
        if (false) {
            list.clear();
        }
    }
}


