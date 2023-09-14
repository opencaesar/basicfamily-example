package viewpoint;

import java.util.ArrayList;
import java.util.HashSet;

import io.opencaesar.oml.Entity;
import io.opencaesar.oml.NamedInstance;
import io.opencaesar.oml.Relation;
import io.opencaesar.oml.util.OmlDelete.CascadeDirection;
import io.opencaesar.oml.util.OmlDelete.CascadeRule;
import io.opencaesar.oml.util.OmlDelete;
import io.opencaesar.oml.util.OmlRead;
import io.opencaesar.rosetta.sirius.viewpoint.OmlServices;

/**
 * The services class used by VSM.
 */
public class Services {
    
    public int calculateSiblingsCount(NamedInstance self) {
    	var siblings = new HashSet<>();

    	var parents = OmlServices.findTargetInstances(self, "basicfamily:parents");
    	for (var parent : parents) {
    		siblings.addAll(OmlServices.findTargetInstances(parent, "basicfamily:children"));
    	}
    	
    	siblings.remove(self);
    	
    	return siblings.size();
    }
    
    public void cascadeDelete(NamedInstance instance) {
    	var cascadeRules = new ArrayList<CascadeRule>();
    	
    	Entity man = (Entity) OmlRead.getMemberByAbbreviatedIri(instance.getOntology(), "basicfamily:Man");
    	Entity woman = (Entity) OmlRead.getMemberByAbbreviatedIri(instance.getOntology(), "basicfamily:Woman");
    	Relation parents = (Relation) OmlRead.getMemberByAbbreviatedIri(instance.getOntology(), "basicfamily:parents");
    	Relation mother = (Relation) OmlRead.getMemberByAbbreviatedIri(instance.getOntology(), "basicfamily:mother");
    	
    	cascadeRules.add(new CascadeRule(CascadeDirection.TARGET_TO_SOURCE, null, parents, null, "delete children"));
    	cascadeRules.add(new CascadeRule(CascadeDirection.SOURCE_TO_TARGET, man, mother, woman, "delete mother"));
    	
    	var result = OmlDelete.cascadeDelete(instance, cascadeRules);
    	OmlDelete.recursiveDelete(result);
    }
}
