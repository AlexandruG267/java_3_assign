package leidenuniv.symbolicai;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import leidenuniv.symbolicai.logic.KB;
import leidenuniv.symbolicai.logic.Predicate;
import leidenuniv.symbolicai.logic.Sentence;
import leidenuniv.symbolicai.logic.Term;

public class MyAgent extends Agent {
	
	

	@Override
	public KB forwardChain(KB kb) {
		//This method should perform a forward chaining on the kb given as argument, until no new facts are added to the KB.
		//It starts with an empty list of facts. When ready, it returns a new KB of ground facts (bounded).
		//The resulting KB includes all deduced predicates, actions, additions and deletions, and goals.
		//These are then processed by processFacts() (which is already implemented for you)
		//HINT: You should assume that forwardChain only allows *bound* predicates to be added to the facts list for now.
		
		return null;
	}

	@Override
	public boolean findAllSubstitutions(Collection<HashMap<String, String>> allSubstitutions,
			HashMap<String, String> substitution, Vector<Predicate> conditions, HashMap<String, Predicate> facts) {
		//Recursive method to find *all* valid substitutions for a vector of conditions, given a set of facts
		//The recursion is over Vector<Predicate> conditions (so this vector gets shorter and shorter, the farther you are with finding substitutions)
		//It returns true if at least one substitution is found (can be the empty substitution, if nothing needs to be substituted to unify the conditions with the facts)
		//allSubstitutions is a list of all substitutions that are found, which was passed by reference (so you use it build the list of substitutions)
		//substitution is the one we are currently building recursively.
		//conditions is the list of conditions you  still need to find a subst for (this list shrinks the further you get in the recursion).
		//facts is the list of predicates you need to match against (find substitutions so that a predicate form the conditions unifies with a fact)
		
		// if conditions gets smaller the further we are, then it's a base case (no more conditions to check):
		if (conditions.isEmpty()) {	
			// we found something that matches, so add the current substitution and return (modify and move on)
			allSubstitutions.add(new HashMap<>(substitution));
			return true;
		}

		// this boolean will mark whether we found some substitution or not, after all the checks are done
		boolean found_something = false;
		
		// if this is not the case, then it's time to solve for the conditions
		// this is done by iterating through the facts, to unify the conditions with them
		Predicate first_condition = conditions.get(0);
		// technically, subList makes a List type, but we bypass this by immediately making a Vector out of it 
		Vector<Predicate> remaining_conditions = new Vector<>(conditions.subList(1, conditions.size()));
		
		// we gotta make the checks to the '=' and '!=' predicates before the main substitution branch
		// here we basically check the 'flag' or 'label' (however you wish to call it) of the Predicate itself
		// I WILL ONLY CHECK for when "substitution" is not empty, i.e. when it's not the first predicate that gets
		// analyzed 	
		if (first_condition.eql) {
			Predicate substituted = substitute(first_condition, substitution);

			// it'll look like "=(X,peter)" substituted with {X=peter, Y=joost}
			// and this will work out so
			if (substituted.eql()) {
				// now checking if the elements of the 
				return findAllSubstitutions(allSubstitutions, substitution, remaining_conditions, facts);
			}
			// if it's not, we must clip the branch with the wrong values
			else { return false; }
		}
		
		// analogous for .not
		if (first_condition.not) {
			Predicate substituted = substitute(first_condition, substitution);
			
			if (substituted.not()) {
				return findAllSubstitutions(allSubstitutions, substitution, remaining_conditions, facts);
			}
			else { return false;}
		}
		
		// now we have condition to match with, so it's time to get the facts to match against
		for (Predicate fact: facts.values()) {
			HashMap<String, String> new_substitution = unifiesWith(first_condition, fact);
			
			// now, to check if it's empty (null)
			if (new_substitution != null) {
				// we found a new substitution, now it's time to add it to the existing combination and check its validity
				// this is the "Y=joost in both substitutions" check in conditions
				// [parent(X,Y), parent(Y,Z)] and facts [parent(joost,peter), parent(peter,leon)] 
				boolean consistent = true;
				for (HashMap.Entry<String, String> entry: new_substitution.entrySet()) {
					String variable = entry.getKey();
					String value = entry.getValue();
					
					// this is the "make it or break it" point, where we check for keys in the current HashMap to have the same
					// value for a key that is repeating (Y can't be equal to both "peter" and "leon")
					if (substitution.containsKey(variable)) {
						if (!substitution.get(variable).equals(value)) {
							consistent = false;
							break;
						}
					}
				}
				
				if (consistent) {
					// get got to ... something, either a pass or a fail for the current substitution. Nonetheless, 
					// we must move on with the recursion
					
					HashMap<String, String> combined = new HashMap<>(substitution);
					combined.putAll(new_substitution);  // dump the contents of the new substitution, that passed the test (hopefully)
					// into combined
					
					boolean found = findAllSubstitutions(allSubstitutions, combined, remaining_conditions, facts);
					
					// check our winning condition
					if (found) found_something = true;
				}
			}
		}
		
		return found_something; 
	}

	@Override
	public HashMap<String, String> unifiesWith(Predicate p, Predicate f) {
		//Returns the valid substitution for which p predicate unifies with f
		//You may assume that Predicate f is fully bound (i.e., it has no variables anymore)
		//The result can be an empty substitution, if no subst is needed to unify p with f (e.g., if p an f contain the same constants or do not have any terms)
		//Please note because f is bound and p potentially contains the variables, unifiesWith is NOT symmetrical
		//So: unifiesWith("human(X)","human(joost)") returns X=joost, while unifiesWith("human(joost)","human(X)") returns null 
		//If no subst is found it returns null
		
		HashMap<String, String> result = new HashMap<>();
		
		// check if the name of the predicates match
		if (!p.getName().equals(f.getName())) {
			System.out.println("The predicates do not match");
	        return null;
	    }
		
		// check the arity of both predicates
		int length_f = f.getTerms().size();
		int length_p = p.getTerms().size();
		
		// give an error if the arity doesn't match
		if (length_f != length_p) {
			System.out.println("Arity does not match");
			return null;
		}
		
		// iterate through the terms
		for (int i = 0; i < p.getTerms().size(); i++) {
	        Term tP = p.getTerm(i);
	        Term tF = f.getTerm(i);

	        if (tP.var) {
	            // variable in p can be bound to constant in f
	            result.put(tP.toString(), tF.toString());
	        } else {
	            // both are constants → must match exactly
	            if (!tP.toString().equals(tF.toString())) {
	                return null;   // conflict, cannot unify
	            }
	        }
	    }

	    return result;
	}

	@Override
	public Predicate substitute(Predicate old, HashMap<String, String> s) {
		// Substitutes all variable terms in predicate <old> for values in substitution <s>
		//(only if a key is present in s matching the variable name of course)
		//Use Term.substitute(s)
		
		Predicate output_predicate = new Predicate(old.toString());
		
		for (Term t: output_predicate.getTerms()) {
			t.substitute(s);
		}
		
		return output_predicate;
	}

	@Override
	public Plan idSearch(int maxDepth, KB kb, Predicate goal) {
		//The main iterative deepening loop
		//Returns a plan, when the depthFirst call returns a plan for depth d.
		//Ends at maxDepth
		//Predicate goal is the goal predicate to find a plan for.
		//Return null if no plan is found.
		return null;
	}

	@Override
	public Plan depthFirst(int maxDepth, int depth, KB state, Predicate goal, Plan partialPlan) {
		//Performs a depthFirst search for a plan to get to Predicate goal
		//Is a recursive function, with each call a deeper action in the plan, building up the partialPlan
		//Caps when maxDepth=depth
		//Returns (bubbles back through recursion) the plan when the state entails the goal predicate
		//Returns null if capped or if there are no (more) actions to perform in one node (state)
		//HINT: make use of think() and act() using the local state for the node in the search you are in.
		return null;
	}
}
