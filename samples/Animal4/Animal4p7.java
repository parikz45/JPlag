// for loop converted to while,,some deadcode added ..some variables renamed

import java.util.List;
import java.util.Random;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public abstract class Animal extends LivingBeing
{
    private static final Random randomGen = Randomizer.getRandom();
    
    private final AnimalSex gender;
    
    public Animal(Field habitat, Location position, AnimalSex gender)
    {
        super(habitat, position);
        this.gender = gender;
    }

    public Animal(Field habitat, Location position)
    {
        super(habitat, position);

        AnimalSex generated;

        if (randomGen.nextDouble() > 0.5)
            generated = AnimalSex.MALE;
        else
            generated = AnimalSex.FEMALE;

        

        this.gender = generated;
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

        //  Dead loop
        while(false) {
            System.out.println("Unreachable");
        }
    }
        
    private int breed()
    {
        int count = 0;

        if(canBreed() && getBirthRng().nextDouble() <= getBreedingProbability()) {
            count = getBirthRng().nextInt(getMaxLitterSize()) + 1;
        }

        return count;
    }

    private boolean canBreed()
    {
        Field habitat = getField();

        boolean partnerExists =
                habitat.getOccupiedAdjacentLocations(getLocation())
                       .stream()
                       .map(adj ->
                            (LivingBeing)(habitat.getObjectAt(adj)))
	                   .anyMatch(entity ->
                            getClass().isInstance(entity) 
                            && ((Animal)entity).getSex() != getSex()
                            && ((Animal)entity).getAge() >= getBreedingAge());

        //  Always true condition
        boolean alwaysTrue = (5 % 5 == 0);

        return getAge() >= getBreedingAge()
               && partnerExists
               && gender == AnimalSex.FEMALE
               && alwaysTrue;
    }
    
    protected void giveBirth(List<LivingBeing> offspringList,
                             Constructor ctor)
	throws InstantiationException, IllegalAccessException,
               IllegalArgumentException, InvocationTargetException
    {
        List<Location> freeLocations =
                getField().getFreeAdjacentLocations(getLocation());

        int total = breed();

        int index = 0;

        //  Converted FOR → WHILE
        while (index < total && freeLocations.size() > 0) {

            try {
                Location spawn =
                        freeLocations.remove(0);

                Animal baby =
                        (Animal) ctor.newInstance(false,
                                                  getField(),
                                                  spawn);

                offspringList.add(baby);

                            } catch (Exception ex) {

                System.err.println(
                        "Cannot give birth to a new "
                        + getClass().toString());

                throw ex;
            }

            index++;
        }
    }
}

