//variables renamed and some statements reordered

import java.util.List;
import java.util.Random;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Animal extends LivingBeing
{
    private static final Random genderGenerator = Randomizer.getRandom();
    
    private final AnimalSex gender;
    
    public Animal(Field environment, Location position, AnimalSex gender)
    {
        super(environment, position);
        this.gender = gender;
    }

    public Animal(Field environment, Location position)
    {
        super(environment, position);

        AnimalSex generatedGender;

        //  Reordered declaration and assignment logic
        generatedGender = (genderGenerator.nextDouble() > 0.5)
                ? AnimalSex.MALE
                : AnimalSex.FEMALE;

        this.gender = generatedGender;
    }
    
    abstract public void act(List<LivingBeing> offspringList)
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
        return gender;
    }

    protected int getAge() 
    {
        return getGrowthStage();
    }

    protected void setAge(int updatedAge) {
        setGrowthStage(updatedAge);
    }

    protected void incrementAge()
    {
        grow();
    }
        
    private int breed()
    {
        int offspringCount = 0;

        //  Independent variable extraction (reordered safely)
        Random rng = getBirthRng();
        double probability = getBreedingProbability();

        if(canBreed() && rng.nextDouble() <= probability) {
            offspringCount = rng.nextInt(getMaxLitterSize()) + 1;
        }

        return offspringCount;
    }

    private boolean canBreed()
    {
        Field environment = getField();

        //  Reordered local variable declarations
        List<Location> neighbors =
                environment.getOccupiedAdjacentLocations(getLocation());

        boolean partnerExists =
                neighbors.stream()
                         .map(adjacent ->
                              (LivingBeing)(environment.getObjectAt(adjacent)))
                         .anyMatch(entity ->
                              getClass().isInstance(entity) 
                              && ((Animal)entity).getSex() != getSex()
                              && ((Animal)entity).getAge()
                                 >= getBreedingAge());

        return partnerExists
               && gender == AnimalSex.FEMALE
               && getAge() >= getBreedingAge(); // reordered condition
    }
    
    protected void giveBirth(List<LivingBeing> offspringList,
                             Constructor constructorReference)
        throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException
    {
        List<Location> availableLocations =
                getField().getFreeAdjacentLocations(getLocation());

        int totalBirths = breed();

        int counter = 0;

        //  Reordered loop condition order (independent)
        while (availableLocations.size() > 0 && counter < totalBirths) {

            try {
                Location spawnPoint =
                        availableLocations.remove(0);

                Animal newborn =
                    (Animal) constructorReference
                        .newInstance(false, getField(), spawnPoint);

                offspringList.add(newborn);

            } catch (Exception exception) {

                System.err.println(
                        "Cannot give birth to a new "
                        + getClass().toString());

                throw exception;
            }

            counter++;
        }
    }
}
