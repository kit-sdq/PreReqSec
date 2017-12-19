package de.fzi.decision.security.ui.models.impl;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;

import de.fzi.decision.security.ui.models.ISecurityContainer;
import security.Catalog;
import security.Container;
import security.securityPatterns.PatternCatalog;
import security.securityPrerequisites.PrerequisiteCatalog;
import security.securityThreats.ThreatCatalog;

/**
 * An implementation of the ISecurityContainer interface.
 */
public class SecurityContainer implements ISecurityContainer {
	private Resource resource;
	private AdapterFactoryEditingDomain editingDomain;
	
	/**
	 * Creates a new SecurityContainer with the specified editingDomain.
	 * When model.load(URI uri) is called the resource will be loaded
	 * into the ResourceSet of the editingDomain.
	 * 
	 * @param editingDomain the editingDomain that is to be used for all resources
	 */
	public SecurityContainer(AdapterFactoryEditingDomain editingDomain) {
		this.editingDomain = editingDomain;
	}

	@Override
	public void load(URI uri) {	
		try {
			resource = editingDomain.getResourceSet().getResource(uri, true);
		} catch (Exception e) {
			resource = editingDomain.getResourceSet().getResource(uri, false);
		}
	}
	
	@Override
	public PrerequisiteCatalog getPrerequisiteCatalog() {
		for (Catalog catalog : getSecurityCatalog().getContains()) {
			if (catalog instanceof PrerequisiteCatalog) {
				return (PrerequisiteCatalog) catalog;
			}
		}
		
		return null;
	}
	
	@Override
	public PatternCatalog getPatternCatalog() {
		for (Catalog catalog : getSecurityCatalog().getContains()) {
			if (catalog instanceof PatternCatalog) {
				return (PatternCatalog) catalog;
			}
		}
		
		return null;
	}
	
	@Override
	public ThreatCatalog getAttackCatalog() {
		for (Catalog catalog : getSecurityCatalog().getContains()) {
			if (catalog instanceof ThreatCatalog) {
				return (ThreatCatalog) catalog;
			}
		}
		
		return null;
	}
	
	private Container getSecurityCatalog() {
		return (Container) resource.getContents().get(0);
	}
}
