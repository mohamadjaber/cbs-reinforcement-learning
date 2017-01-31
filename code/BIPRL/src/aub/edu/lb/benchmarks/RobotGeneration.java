package aub.edu.lb.benchmarks;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

public class RobotGeneration {
	private Integer gridSize;
	private String fileName;
	private Integer robots; 
	private PrintStream BIPFile;
	private PrintStream badStateFile; 


	public RobotGeneration(int gridSize, int robots, String fileName, String badStateFileName) {
		try {
			this.gridSize = gridSize;
			this.fileName = fileName;
			this.robots = robots; 
			BIPFile = new PrintStream(new File(fileName));
			badStateFile = new PrintStream(new File(badStateFileName));

			BIPFile.println("model robot");
			CreateConnectors();
			CreateAtomics();
			CreateCompoundType();

			BIPFile.println("component Robots top");
			BIPFile.write("   end\n".getBytes());
			BIPFile.close();
			
			generateBadState();
			badStateFile.close();
			
		} catch (IOException e) {
			System.out.println(e);
		}
	}
	
	
	public void generateBadState() {
		for(int i = 0 ; i < robots ; i++) {
			for(int j = 0; j < gridSize; j++) {
				badStateFile.println("r" + i + ".l_" + j + "_" + (gridSize - 1));
			}
			
			for(int j = gridSize - 2; j > gridSize / 2; j--) {
				badStateFile.println("r" + i + ".l_" + (gridSize - 1) + "_" + (j));
			}
		}
	}
	


	public String getFileName() {
		return fileName;
	}

	private void CreateConnectors() throws IOException {
		BIPFile.println("  connector type SyncTwo(Port p1, Port p2)");
		BIPFile.println("    define  p1 p2");
		BIPFile.println("    on p1 p2 provided true ");
		BIPFile.println("      up   {}");
		BIPFile.println("      down {}");
		BIPFile.println("  end");
	}

	private void CreateAtomics() throws IOException {
		BIPFile.println("  //Atomic component for Robot");
		BIPFile.println("  atomic type Robot(int id)");
		BIPFile.println("    export port Port rightAction()");
		BIPFile.println("    export port Port upAction()");

		BIPFile.print("    place ");
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				if (i == 0 && j == 0)
					BIPFile.print("l_" + i + "_" + j);
				else
					BIPFile.print(", l_" + i + "_" + j);
			}
		}
		BIPFile.println();

		BIPFile.println("    initial to l_0_0 do {}");
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				// move right or if not last border
				if (i < gridSize - 1 && j < gridSize - 1) {
					BIPFile.println("    on rightAction from l_" + i + "_" + j + " to l_" + (i + 1) + "_" + j + " provided true do {}");
					BIPFile.println("    on upAction from l_" + i + "_" + j + " to l_" + i + "_" + (j + 1)+ " provided true do {}");
				}

			}
		}
		BIPFile.println("  end");
	}

	private void CreateCompoundType() throws IOException {
		BIPFile.println("\n\n  compound type Robots");
		for (int i = 0; i < robots; i++) {
			BIPFile.println("    component Robot r" + i + "(" + i + ")");
		}
		for (int i = 0; i < robots; i++) {
			BIPFile.println("    connector SyncTwo connRight_" + i + "(r" + i + ".rightAction, r" + ((i + 1) % robots)+ ".rightAction)");
			BIPFile.println("    connector SyncTwo connUp_" + i + "(r" + i + ".upAction, r" + ((i + 1) % robots)+ ".upAction)");
		}

		BIPFile.println("  end\n");
	}

}
