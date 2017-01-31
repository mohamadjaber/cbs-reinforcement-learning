package aub.edu.lb.benchmarks;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class DiningGoodGeneration {
	private Integer nbOfPhilosophers;

	private PrintStream bipFile; 
	private PrintStream badStateFile; 

	public DiningGoodGeneration(int nbOfPhilosophers, String bipfFileName, String badStateFileName) {
		try {
			this.nbOfPhilosophers = nbOfPhilosophers;
			bipFile = new PrintStream(new File(bipfFileName));
			badStateFile = new PrintStream(new File(badStateFileName));

			bipFile.println("model dining");
			CreateConnectors();
			CreateAtomics();
			CreateCompoundType();
			
	
			bipFile.println("component DiningPhilosopher top");
			bipFile.write("end\n".getBytes());
			bipFile.close();
			
			generateBadState();
			badStateFile.close();
		}
		catch(IOException e) {
			System.out.println(e);
		}
	}
	
	public void generateBadState() {
		for(int i = 0 ; i < nbOfPhilosophers ; i++) {
			if(i == 0) badStateFile.print("p" + i +"_Eating");
			else badStateFile.print(", p" + i + "_Eating");
		}
	}
	


	private void CreateConnectors() throws IOException {
		bipFile.println("  connector type SyncThree(Port p1, Port p2, Port p3)");
		bipFile.println("    define  p1 p2 p3");
		bipFile.println("    on p1 p2 p3 provided true ");
		bipFile.println("      up   {}");
		bipFile.println("      down {}");
		bipFile.println("  end");
	}
	
	private void CreateAtomics() throws IOException {
		bipFile.println("  //Atomic component for Philosopher");
		bipFile.println("  atomic type Philosopher(int id)");
		bipFile.println("    export port Port get()");
		bipFile.println("	 export port Port release()");
		    
		bipFile.println("    place Hungry, Eating");
		bipFile.println("    initial to Hungry do {}");
		   
		bipFile.println("    on get from Hungry to Eating ");
		bipFile.println("      provided true ");
		bipFile.println("      do { printf(\"Philosopher %d starts eating\\n\", id); }");
		 
		bipFile.println("    on release from Eating to Hungry ");
		bipFile.println("      provided true ");
		bipFile.println("      do { printf(\"Philosopher %d finishes eating\\n\", id); }");
		bipFile.println("  end");

		bipFile.println("  // Atomic component for Fork");
		bipFile.println("  atomic type Fork(int id)");
		bipFile.println("    export port Port get()");
		bipFile.println("    export port Port release()");
		    
		bipFile.println("    place Available, Occupied");
		bipFile.println("   initial to Available do {}");
		    
		bipFile.println("    on get from Available to Occupied ");
		bipFile.println("      provided true ");
		bipFile.println("     do { printf(\"Fork %d has been reserved by the philo in the Right\\n\", id); }");
		
	
		bipFile.println("    on release from Occupied to Available ");
		bipFile.println("      provided true ");
		bipFile.println("     do { printf(\"Fork %d has been released from the philo in the Right\\n\", id); }");

		bipFile.println("  end");
	}
	
	

	private void CreateCompoundType() throws IOException {
		bipFile.println("\n\n  compound type DiningPhilosopher");
		for(int i = 0 ; i < nbOfPhilosophers ; i++)
		{
			bipFile.println("    component Philosopher p"+i+"("+i+")");
			bipFile.println("    component Fork f"+i+"("+i+")");
		}
		for(int i = 0 ; i < nbOfPhilosophers ; i++)
		{
			bipFile.println("    connector SyncThree connGet"+ i + "(f"+ i + ".get, p"+ i + ".get, f"+ (i+1)%nbOfPhilosophers + ".get)");
			bipFile.println("    connector SyncThree connRelease"+ i + "(f"+ i + ".release, p"+ i + ".release, f"+ (i+1)%nbOfPhilosophers + ".release)");
		}

		bipFile.println("end\n");
	}



}
