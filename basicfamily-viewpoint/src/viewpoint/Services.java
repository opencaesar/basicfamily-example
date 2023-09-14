package viewpoint;

import java.util.ArrayList;
import java.util.HashSet;

import io.opencaesar.oml.NamedInstance;
import io.opencaesar.oml.Relation;
import io.opencaesar.oml.util.OmlDelete;
import io.opencaesar.oml.util.OmlDelete.CascadeDirection;
import io.opencaesar.oml.util.OmlDelete.CascadeRule;
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
    	
    	Relation parents = (Relation) OmlRead.getMemberByAbbreviatedIri(instance.getOntology(), "basicfamily:parents");
    	cascadeRules.add(new CascadeRule(CascadeDirection.TARGET_TO_SOURCE, null, parents, null, "delete children"));
    	
    	var result = OmlDelete.cascadeDelete(instance, cascadeRules);
    	OmlDelete.recursiveDelete(result);
    }
}
