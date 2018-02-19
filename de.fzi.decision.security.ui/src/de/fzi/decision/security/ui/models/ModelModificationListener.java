package de.fzi.decision.security.ui.models;

/**
 * Listens to events causing addition or deletion of model elements.
 * @author matthias endlichhofer
 *
 */
public abstract class ModelModificationListener {

	protected ISecurityContainer securityContainer;

	public ModelModificationListener(ISecurityContainer container) {
		this.securityContainer = container;
	}
	
	public abstract void addEntity();
	
	public abstract void deleteEntity(String id);
}
