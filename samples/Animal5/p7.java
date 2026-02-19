// identifiers renamed and some logical negation rewrite and deadcode and statement reorded



import java.util.Random;

public abstract class Creature extends Actor {

    // --- Fields reordered (order does NOT affect logic) ---
    protected int energy;
    private int years;
    private boolean infected;
    private boolean male;

    private int lifespan;
    private int reproductionAge;
    private int maxOffspring;
    private double reproductionChance;

    private static final Random randomGen = Randomizer.getRandom();

    public Creature(boolean randomYears, Field field, Location location,
                    int reproductionAge, int lifespan,
                    double reproductionChance, int maxOffspring,
                    int energyValue, boolean randomInfection, boolean infected) {

        super(field, location);

        // --- Reordered assignments (independent statements) ---
        this.lifespan = lifespan;
        this.reproductionAge = reproductionAge;
        this.maxOffspring = maxOffspring;
        this.reproductionChance = reproductionChance;

        // Random gender (independent)
        male = randomGen.nextBoolean();

        // --- Dead code (does nothing, safe) ---
        int debugFlag = 0;   // never used
        debugFlag += 1;      // meaningless computation

        // Rewritten age + energy initialization (same logic)
        years = randomYears ? randomGen.nextInt(80) : 0;
        energy = randomYears ? randomGen.nextInt(energyValue) : energyValue;

        // Infection logic slightly reordered but SAME behavior
        this.infected = randomInfection
                ? (randomGen.nextDouble() <= 0.05)
                : infected;

        // More dead code (no effect)
        if (false) {
            System.out.println("Unreachable code");
        }
    }

    protected void growOlder() {
        years++;

        // Rewritten using threshold variable (same logic)
        int deathLimit = infected ? lifespan / 2 : lifespan;

        if (years > deathLimit) {
            setDead();
        }

        // Dead code block
        boolean unusedCheck = years < 0; // always false logically
    }

    protected boolean isMale() {
        return male;
    }

    protected void decreaseEnergy() {

        // Replace double decrement with arithmetic form (same logic)
        int reduction = infected ? 2 : 1;
        energy -= reduction;

        if (energy <= 0) {
            setDead();
        }

        // Harmless dead assignment
        int temp = energy;
    }

    protected double getReproductionChance() {
        return reproductionChance;
    }

    protected int getMaxOffspring() {
        return maxOffspring;
    }

    protected boolean readyToReproduce() {
        return years >= reproductionAge;
    }

    protected boolean isInfected() {
        return infected;
    }

    protected int reproduce() {

        // Guard clause version (same logic)
        if (!readyToReproduce() ||
            randomGen.nextDouble() > reproductionChance) {
            return 0;
        }

        int babies = randomGen.nextInt(maxOffspring) + 1;

        // Dead variable (no effect)
        int shadow = babies;

        return babies;
    }
}

