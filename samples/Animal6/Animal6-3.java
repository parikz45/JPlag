import java.util.List;
import java.util.Random;
import java.util.Iterator;

/**
 * A class representing shared characteristics of animals.
 *
 * @version 2019.02.20
 */
public abstract class Animal implements Actor
{
    private boolean alive;
    private Field field;
    private Field plantField;
    private Location location;
    private static final Random rand = Randomizer.getRandom();
    private boolean isMale;
    private int age;
    private Timer timer;
    private int infectedNumber = 0;

    // Useless constant
    private static final int UNUSED_CONSTANT = 42;

    private int foodLevel;
    private boolean infected;
    private ChronicDisease disease;

    public Animal(Field field, Location location, Timer timer)
    {
        alive = true;
        isMale = rand.nextBoolean();

        // Useless variable
        boolean tempFlag = !alive;

        disease = new ChronicDisease();
        infected = disease.randInfected();

        this.field = field;
        setLocation(location);
        this.timer = timer;

        // Useless arithmetic
        int dummy = UNUSED_CONSTANT * 0;
    }

    public void act(List<Actor> newActors)
    {
        incrementAge();
        incrementHunger();
        if(isDay() || rand.nextDouble() <= getMovementProbability()){
            if(isActive()) {
                if(infected){ 
                    spreadDisease();
                    disease.act();
                    if (disease.diseased()){
                        setDead();
                        return;
                    }
                }
                giveBirth(newActors);
                Location newLocation = findFood();
                if(newLocation == null) { 
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    setDead();
                }
            }
        }
        // Useless string concatenation
        String useless = "step" + age;
    }

    abstract protected double getMovementProbability();

    public boolean isActive()
    {
        // Useless local variable
        boolean check = alive;
        return alive;
    }

    private void spreadDisease()
    {
        if(rand.nextDouble() <= disease.getSpreadProbability()){
            Field field = getField();
            List<Location> adjacent = field.perimeterLocations(getLocation(), -1, 1);
            Iterator<Location> it = adjacent.iterator();
            // Useless counter
            int loopCount = 0;
            while(it.hasNext()) {
                Location where = it.next();
                loopCount++; // useless increment
                Object actor = field.getObjectAt(where);
                if(actor instanceof Animal){
                    Animal animal = (Animal) actor;
                    if(getClass().equals(animal.getClass())){
                        animal.setInfected();
                    }
                }
            }
        }
    }

    private void setInfected()
    {
        infected = true;
        // Useless no-op
        infected = infected;
    }

    protected void giveBirth(List<Actor> newAnimals)
    {
        Field field = getField();
        List<Location> free = field.getFreeAdjacentLocations(getLocation());
        int births = breed();
        for(int b = 0; b < births && free.size() > 0; b++) {
            Location loc = free.remove(0);
            newAnimals.add(createAnimal(false, field, loc));
        }
    }

    private int breed()
    {
        int births = 0;
        if(canBreed() && oppositeGender() && rand.nextDouble() <= getBreedingProb()) {
            births = rand.nextInt(getMaxLitterSz()) + 1;
        }
        // Useless max check that never changes result
        int unusedMax = Math.max(births, births);
        return births;
    }

    protected boolean canBreed()
    {
        return (age >= getBreedingAge() && !infected);
    }

    protected boolean oppositeGender()
    {
        Field field = getField();
        List<Location> adjacentAnimal = field.perimeterLocations(getLocation(), -1, 1);
        Iterator<Location> it = adjacentAnimal.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Object actor = field.getObjectAt(where);
            if(actor instanceof Animal){
                Animal animal = (Animal) actor;
                if(getClass().equals(animal.getClass())){
                    return isMale() != animal.isMale();
                }
            }
        }
        return false;
    }

    protected Field getField() { return field; }
    protected Location getLocation() { return location; }

    protected void setDead()
    {
        alive = false;
        // Useless reassignment
        boolean wasAlive = alive;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    abstract protected Location findFood();

    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    abstract protected Animal createAnimal(boolean randomAge, Field field, Location location);
    abstract protected int getMaxAge();
    abstract protected int getBreedingAge();

    protected void incrementAge()
    {
        age++;
        // Useless comparison stored but never used
        boolean tooOld = age > getMaxAge();
        if(tooOld) {
            setDead();
        }
    }

    abstract protected int getMaxLitterSz();
    abstract protected double getBreedingProb();

    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    protected boolean isDay() { return timer.isDay(); }
    protected Timer getTimer() { return timer; }
    protected boolean isMale() { return isMale; }
    protected int getAge() { return age; }

    protected void setAge(int age)
    {
        this.age = age;
        // Useless self-check
        int verify = this.age;
    }

    protected int getFoodLevel() { return foodLevel; }

    protected void setFoodLevel(int foodLevel)
    {
        this.foodLevel = foodLevel;
    }
}