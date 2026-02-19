
//variables  renamed and for loop converted to while


import java.util.List;
import java.util.Random;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Animal extends LivingBeing
{
    private static final Random genderGenerator = Randomizer.getRandom();
    
    private final AnimalSex gender;
    
    public Animal(Field habitat, Location position, AnimalSex gender)
    {
        super(habitat, position);
        this.gender = gender;
    }

    public Animal(Field habitat, Location position)
    {
        super(habitat, position);

        AnimalSex generatedGender;

        if (genderGenerator.nextDouble() > 0.5)
            generatedGender = AnimalSex.MALE;
        else
            generatedGender = AnimalSex.FEMALE;

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

        if(canBreed() &&
           getBirthRng().nextDouble() <= getBreedingProbability()) {

            offspringCount =
                getBirthRng().nextInt(getMaxLitterSize()) + 1;
        }

        return offspringCount;
    }

    private boolean canBreed()
    {
        Field habitat = getField();

        boolean partnerPresent =
                habitat.getOccupiedAdjacentLocations(getLocation())
                       .stream()
                       .map(adjLocation ->
                            (LivingBeing)(habitat.getObjectAt(adjLocation)))
                       .anyMatch(entity ->
                            getClass().isInstance(entity) 
                            && ((Animal)entity).getSex() != getSex()
                            && ((Animal)entity).getAge()
                               >= getBreedingAge());

        return getAge() >= getBreedingAge()
               && partnerPresent
               && gender == AnimalSex.FEMALE;
    }
    
    protected void giveBirth(List<LivingBeing> offspringList,
                             Constructor constructorRef)
        throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException
    {
        List<Location> freeLocations =
                getField().getFreeAdjacentLocations(getLocation());

        int totalOffspring = breed();

        int counter = 0;

        while (counter < totalOffspring
               && freeLocations.size() > 0) {

            try {
                Location spawnLocation =
                        freeLocations.remove(0);

                Animal newborn =
                    (Animal) constructorRef
                        .newInstance(false, getField(),
                                     spawnLocation);

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

