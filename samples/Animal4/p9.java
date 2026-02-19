//STATEMENTS REORDERED AND FOR LOOP CONVERTED TO WHILE


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

        AnimalSex randSex;

        // Reordered independent assignment logic
        randSex = (sexRandomizer.nextDouble() > 0.5)
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

        //  Reordered independent variable extraction
        Random rng = getBirthRng();
        double probability = getBreedingProbability();

        if(canBreed() && rng.nextDouble() <= probability) {
            births = rng.nextInt(getMaxLitterSize()) + 1;
        }

        return births;
    }

    private boolean canBreed()
    {
        Field field = getField();

        //  Reordered local variable declaration
        List<Location> neighbors =
                field.getOccupiedAdjacentLocations(getLocation());

        boolean hasOppositeSex =
                neighbors.stream()
                         .map(beingLoc ->
                              (LivingBeing)(field.getObjectAt(beingLoc)))
                         .anyMatch(animal ->
                              getClass().isInstance(animal) 
                              && ((Animal)animal).getSex() != getSex()
                              && ((Animal)animal).getAge()
                                 >= getBreedingAge());

        //  Reordered condition order (independent)
        return hasOppositeSex
               && sex == AnimalSex.FEMALE
               && getAge() >= getBreedingAge();
    }
    
    protected void giveBirth(List<LivingBeing> newAnimals,
                             Constructor newAnimalCtor)
        throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException
    {
        List<Location> free =
                getField().getFreeAdjacentLocations(getLocation());

        int births = breed();

        int b = 0;

        //  Converted FOR → WHILE
        while (b < births && free.size() > 0) {

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

            b++;
        }
    }
}
