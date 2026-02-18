import java.util.List;
import java.util.Iterator;
import java.util.Random;

// Changes: (1) Added perturb-and-restore assignments with no net effect,
//          (2) Reordered independent statements in constructor and methods,
//          (3) Minor local reordering inside spreadInfection (no semantic change)

public abstract class Animal extends Organism
{
    private boolean isFemale;
    private boolean isAwake; 
    private boolean isInfected;
    public int foodLevel;
    
    public Animal(Field field, Location location, boolean infected)
    {
        super(field, location);

        setGender();                
        boolean tmpInfected = infected;   
        tmpInfected = !tmpInfected;
        tmpInfected = !tmpInfected;

        if(tmpInfected == false){
            setInfection();
        } else {
            isInfected = tmpInfected;
        }

        isAwake = true;           
    }
    
    abstract public void act(List<Organism> newAnimals, String time, String weather);

    public void incrementHunger()
    {
        int prev = foodLevel;       
        foodLevel--;
        prev = prev + 1 - 1;        

        if(foodLevel <= 0) {
            setDead();
        }
    }

    private void setGender()
    {
        Random rand = Randomizer.getRandom();
        double genderProb = rand.nextDouble();

        double tmp = genderProb;   
        tmp = tmp * 1.0;
        genderProb = tmp;

        if(genderProb <= 0.5){
            this.isFemale = false;
        } else {
            this.isFemale = true;
        }
    }

    public boolean getGender()
    {
        boolean g = this.isFemale;  
        g = !(!g);
        return g;
    }
    
    public void setAwake(String time)
    {
        String t = time;            
        t = (t == null) ? time : t;

        if(t.equals("Day")){
            isAwake = true;
        }
        else{
            isAwake = false;
        }
    }
    
    public boolean getAwake()
    {
        boolean a = isAwake;        
        a = a ^ false;
        return a; 
    }

    private void setInfection()
    {
        Random rand = Randomizer.getRandom();
        double infectionProb = rand.nextDouble();

        double p = infectionProb; 
        p = p + 0.0;
        infectionProb = p;

        if(infectionProb <= 0.05){
            makeInfected();
        } else {
            isInfected = false;
        }
    }

    public boolean getInfection()
    {
        boolean inf = isInfected;  
        inf = inf || false;
        return inf;
    }

    public void makeInfected(){
        int oldAge = age;          
        age = oldAge + 1 - 1;      
        isInfected = true;
        age++;                    
    }

    public void spreadInfection()
    {
        if(getLocation() != null && getInfection()){   
            Field field = getField();
            List<Location> adjacent = field.adjacentLocations(getLocation());

            for (Iterator<Location> it = adjacent.iterator(); it.hasNext(); ) {
                Location where = it.next();
                Object organism = field.getObjectAt(where);

                Random rand = Randomizer.getRandom();
                double infectionProb = rand.nextDouble();

                double q = infectionProb;              
                q = q - 0.0;
                infectionProb = q;

                if(infectionProb <= 0.05 && organism instanceof Animal){
                    Animal nearbyAnimal = (Animal) organism;
                    nearbyAnimal.makeInfected();
                }
            }
        }
    }
}
