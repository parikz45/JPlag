
// Constructor if–else → Ternary and breed() condition → Ternary



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

        AnimalSex randSex =
            (sexRandomizer.nextDouble() > 0.5)
            ? AnimalSex.MALE
            : AnimalSex.FEMALE;

        this.sex = randSex;
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
    
    protected AnimalSex getSex()
    {
        return sex;
    }

    protected int getAge() 
    {
        return getGrowthStage();
    }

    protected void setAge(int newAge) {
        setGrowthStage(newAge);
    }

    protected void incrementAge()
    {
        grow();
    }
        
    private int breed()
    {
        int births = 0;

        births = (canBreed() &&
                  getBirthRng().nextDouble() <= getBreedingProbability())
                 ? getBirthRng().nextInt(getMaxLitterSize()) + 1
                 : 0;

        return births;
    }

    private boolean canBreed()
    {
        Field field = getField();

        boolean hasOppositeSex =
                field.getOccupiedAdjacentLocations(getLocation())
                     .stream()
                     .map(beingLoc ->
                          (LivingBeing)(field.getObjectAt(beingLoc)))
                     .anyMatch(animal ->
                          getClass().isInstance(animal) 
                          && ((Animal)animal).getSex() != getSex()
                          && ((Animal)animal).getAge()
                             >= getBreedingAge());

        return (getAge() >= getBreedingAge()
                && hasOppositeSex
                && sex == AnimalSex.FEMALE);
    }
    
    protected void giveBirth(List<LivingBeing> newAnimals,
                             Constructor newAnimalCtor)
        throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException
    {
        List<Location> free =
                getField().getFreeAdjacentLocations(getLocation());

        int births = breed();

        for(int b = 0;
            b < births && free.size() > 0;
            b++) {

            try {
                Location loc = free.remove(0);

                Animal young =
                    (Animal) newAnimalCtor
                        .newInstance(false, getField(), loc);

                newAnimals.add(young);

            } catch (Exception e) {
                System.err.println(
                    "Cannot give birth to a new "
                    + getClass().toString());
                throw e;
            }
        }
    }
}
