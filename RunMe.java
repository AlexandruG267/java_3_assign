package leidenuniv.symbolicai;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;

//REMEMBER TO DELETE OR COMMENT THIS LINE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
import java.util.Vector;  
import java.util.Collection;
import java.util.ArrayList;


import leidenuniv.symbolicai.environment.Maze;
import leidenuniv.symbolicai.logic.Predicate;

public class RunMe {
	//This is our main program class
	// It loads a world, makes an agent and then keeps the agent alive by allowing it to complete it's sense think act cycle 
	public static void main(String[] args) {
		//Load a world
		Maze w=new Maze(new File("data/prison.txt"));
		//Create an agent
		Agent a=new MyAgent();
		a.HUMAN_DECISION=false;
		a.VERBOSE=true;
		//Load the rules and static knowledge for the different steps in the agent cycle
//		a.loadKnowledgeBase("percepts", new File("data/percepts.txt"));
//		a.loadKnowledgeBase("program", new File("data/program.txt"));
//		a.loadKnowledgeBase("actions", new File("data/actions.txt"));
		//a.loadKnowledgeBase("percepts", new File("data/percepts_joost.txt"));
		//a.loadKnowledgeBase("program", new File("data/program_joost.txt"));
		//a.loadKnowledgeBase("actions", new File("data/actions_joost.txt"));
		Predicate p = new Predicate("parent(X,leon)");
		HashMap<String, String> s = new HashMap<>();
		s.put("X", "joost");
		s.put("Y", "leon");
		
//		System.out.println(s);
//		System.out.println(s.entrySet());
		
		// each entry in a HashMap is of type Entry<type1, type2>, and this is just a strung together set/list of Entry types
		// and each Entry is also made up of key-value pairs
//		for (HashMap.Entry<String, String> entry: s.entrySet()){
//			// here, the cat is out of the box, I have access to the raw values in the HashMap
//			System.out.println(entry);
//			String variable = entry.getKey();
//			String value = entry.getValue();
//			System.out.println(variable);
//			System.out.println(value+"\n");
//		}
//		if (s.containsKey("X"))
//			System.out.println("Hurrah"+"\n");
//		System.out.println("Done with HashMaps analysis\n");
		MyAgent agent = new MyAgent();
		
		// points 4 and 5
		System.out.println(agent.substitute(p, s));
		// point 6: testing the main substitution
		// facts
		HashMap<String, Predicate> facts = new HashMap<>();
	    facts.put("f1", new Predicate("parent(peter,joost)"));
	    facts.put("f2", new Predicate("parent(joost,leon)"));
	    facts.put("f3", new Predicate("parent(joost,sacha)"));
	    // conditions
	    Vector<Predicate> conditions = new Vector<>();
	    conditions.add(new Predicate("parent(X,Y)"));
	    conditions.add(new Predicate("parent(Y,Z)"));
	    // result collections
	    Collection<HashMap<String, String>> results = new ArrayList<>();
	    HashMap<String, String> initialSubstitution = new HashMap<>();
	    
	    boolean found = agent.findAllSubstitutions(results, initialSubstitution, conditions, facts);
	    
	    System.out.println("Found: " + found);
	    System.out.println("Number of substitutions: " + results.size());
	    System.out.println("\nAll substitutions:");
	    int i = 1;
	    for (HashMap<String, String> sub : results) {
	        System.out.println("  " + i + ": " + sub);
	        i++;
	    }
	    
	    System.out.println("\n");
	    
	    // point 6: testing '=' and '!=' 
	    facts.clear();
	    facts.put("f1", new Predicate("parent(peter,joost)"));
	    facts.put("f2", new Predicate("parent(joost,leon)"));
	    facts.put("f3", new Predicate("parent(anna,sara)"));
	    
	    conditions.clear();
	    conditions.add(new Predicate("parent(X,Y)"));
	    conditions.add(new Predicate("=(X,joost)"));  // X must be equal to "peter"
	    
	    results.clear();
	    initialSubstitution.clear();

	    found = agent.findAllSubstitutions(results, initialSubstitution, conditions, facts);

	    System.out.println("Found: " + found);
	    System.out.println("Number of substitutions: " + results.size());
	    System.out.println("All substitutions:");
	    i = 1;
	    for (HashMap<String, String> sub : results) {
	        System.out.println("  " + i + ": " + sub);
	        i++;
	    }
	    System.out.println("\n");

		//If you need to test on a simpler file, you may use this one and comment out all the other KBs:
		//a.loadKnowledgeBase("program", new File("data/family.txt"));
		
		
		Scanner io= new Scanner(System.in);
		
		while (true) {
			//have the agent run the sense-think-act loop.
			a.cycle(w);
			
			//wait for an enter 
			System.out.println("Press <enter> in the java console to continue next cycle");
			String input = io.nextLine();
			
		}
	}

}
