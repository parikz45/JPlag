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
    // Whether the animal is alive or not.
    // Changed from private to protected so subclasses can access directly if needed
    protected boolean alive;
    // The animal's field.
    // Changed from private to protected (used heavily by subclasses via getField())
    protected Field field;
    // The plant's field.
    // Changed from private to protected (subclasses may need direct access)
    protected Field plantField;
    // The animal's position in the field.
    // Changed from private to protected
    protected Location location;
    // A shared random generator.
    // Changed from private to protected so subclasses can use the same random instance
    protected static final Random rand = Randomizer.getRandom();
    // The animal's gender.
    // Changed from private to protected
    protected boolean isMale;
    // The animal's age.
    // Changed from private to protected
    protected int age;
    // A timer to keep track of day and night.
    // Changed from private to protected
    protected Timer timer;
    // Number of animals infected.
    // Changed from private to protected
    protected int infectedNumber = 0;

    // The animal's food level, increased when eating prey or plants.
    // Changed from private to protected
    protected int foodLevel;
    // Whether the animal is infected.
    // Changed from private to protected
    protected boolean infected;
    // An instance of the ChronicDisease class
    // Changed from private to protected
    protected ChronicDisease disease;

    /**
     * Create a new animal at location in field.
     *
     * @param field The field currently occupied.
     * @param location The location within the field.
     */
    public Animal(Field field, Location location, Timer timer)
    {
        alive = true;
        isMale = rand.nextBoolean();

        disease = new ChronicDisease();
        infected = disease.randInfected();

        this.field = field;
        setLocation(location);
        this.timer = timer;
    }

    /**
     * This is what animals do most of the time - they move around
     * and eat.
     * Sometimes they will spread disease, breed, or die of old age.
     * @param newActors A list to return newly born actors.
     */
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
                        // Killed by disease.
                        setDead();
                        return;
                    }
                }
                giveBirth(newActors);
                // Move towards a source of food if found.
                Location newLocation = findFood();
                if(newLocation == null) {
                    // No food found - try to move to a free location.
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }
                // See if it was possible to move.
                if(newLocation != null) {
                    setLocation(newLocation);
                }
                else {
                    // Overcrowding.
                    setDead();
                }
            }
        }
    }

    /**
     * @return Movement probability at night for each animal.
     */
    abstract protected double getMovementProbability();

    /**
     * Check whether the animal is alive or not.
     * @return true if the animal is still alive.
     */
    public boolean isActive()
    {
        return alive;
    }

    /**
     * Spread the disease to the animals of the same species in the
     * neighboring locations.
     * Changed from private to protected so subclasses can override or call if needed.
     */
    protected void spreadDisease()
    {
        if(rand.nextDouble() <= disease.getSpreadProbability()){
            Field field = getField();
            List<Location> adjacent = field.perimeterLocations
                (getLocation(), -1, 1);
            Iterator<Location> it = adjacent.iterator();
            while(it.hasNext()) {
                Location where = it.next();
                Object actor = field.getObjectAt(where);
                // Check that the object is an animal.
                if(actor instanceof Animal){
                    Animal animal = (Animal) actor;
                    // Check that the animal is of the same species.
                    if(getClass().equals(animal.getClass())){
                        // Spread the disease.
                        animal.setInfected();
                    }
                }
            }
        }
    }

    /**
     * Set variable infected to true.
     * Changed from private to protected so subclasses can call setInfected() directly.
     */
    protected void setInfected()
    {
        infected = true;
    }

    /**
     * Check whether or not this animal is to give birth at this step.
     * New births will be made into free adjacent locations.
     * @param newAnimals A list to return newly born animals.
     */
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

    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * Changed from private to protected so subclasses can override breeding behavior.
     * @return The number of births (may be zero).
     */
    protected int breed()
    {
        int births = 0;
        if(canBreed() && oppositeGender() &&
        rand.nextDouble() <= getBreedingProb()) {
            births = rand.nextInt(getMaxLitterSz()) + 1;
        }
        return births;
    }

    /**
     * An animal can breed if it has reached the breeding age and it is
     * not infected by the disease.
     *
     * @return true if the animal can breed, false otherwise.
     */
    protected boolean canBreed()
    {
        return (age >= getBreedingAge() && !infected);
    }

    /**
     * An animal can breed only if it has an animal of opposite gender
     * nearby.
     */
    protected boolean oppositeGender()
    {
        Field field = getField();
        List<Location> adjacentAnimal = field.perimeterLocations
            (getLocation(), -1, 1);
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

    /**
     * @return The animal's field.
     */
    protected Field getField()
    {
        return field;
    }

    /**
     * @return The animal's location.
     */
    protected Location getLocation()
    {
        return location;
    }

    /**
     * Indicate that the animal is no longer alive.
     * It is removed from the field.
     */
    protected void setDead()
    {
        alive = false;
        if(location != null) {
            field.clear(location);
            location = null;
            field = null;
        }
    }

    /**
     * Look for either plants or prey adjacent to the current location.
     * @return Where food was found, or null if it wasn't.
     */
    abstract protected Location findFood();

    /**
     * Place the animal at the new location in the given field.
     * @param newLocation The animal's new location.
     */
    public void setLocation(Location newLocation)
    {
        if(location != null) {
            field.clear(location);
        }
        location = newLocation;
        field.place(this, newLocation);
    }

    /**
     * Create new animal.
     * @param randomAge If true, the animal will have a random age,
     * age 0 otherwise.
     * @param field The field.
     * @param location The location for the new animal.
     */
    abstract protected Animal createAnimal(boolean randomAge,
    Field field, Location location);

    /**
     * @return The maximum age of the animal.
     */
    abstract protected int getMaxAge();

    /**
     * @return The breeding age of the animal.
     */
    abstract protected int getBreedingAge();

    /**
     * Increase the age. This could result in the animal's death.
     */
    protected void incrementAge()
    {
        age++;
        if(age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * return Maximum number of offspring from this animal.
     */
    abstract protected int getMaxLitterSz();

    /**
     * return The breeding probability of this animal.
     */
    abstract protected double getBreedingProb();

    /**
     * Make this animal more hungry. This could result in the animal's
     * death.
     */
    protected void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            setDead();
        }
    }

    protected boolean isDay()
    {
        return timer.isDay();
    }

    /**
     * @return Common timer.
     */
    protected Timer getTimer()
    {
        return timer;
    }

    /**
     * Check whether the animal is male or female.
     * @return true if the animal is male, false if it is female.
     */
    protected boolean isMale()
    {
        return isMale;
    }

    /**
     * @return The animal's age.
     */
    protected int getAge()
    {
        return age;
    }

    /**
     * Set the age of the animal.
     * @param age The age to be set to.
     */
    protected void setAge(int age)
    {
        this.age = age;
    }

    /**
     * @return The animal's food level.
     */
    protected int getFoodLevel()
    {
        return foodLevel;
    }

    /**
     * Set the food level of the animal.
     * @param foodLevel The food level to be set to.
     */
    protected void setFoodLevel(int foodLevel)
    {
        this.foodLevel = foodLevel;
    }
}