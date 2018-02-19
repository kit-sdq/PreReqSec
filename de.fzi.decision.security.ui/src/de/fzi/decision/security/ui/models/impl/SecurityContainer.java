package de.fzi.decision.security.ui.models.impl;

import org.eclipse.emf.common.command.Command;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.edit.command.AddCommand;
import org.eclipse.emf.edit.command.DeleteCommand;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.decision.security.ui.models.ISecurityContainer;
import security.Catalog;
import security.Container;
import security.securityPatterns.PatternCatalog;
import security.securityPatterns.SecurityPattern;
import security.securityPatterns.impl.SecurityPatternsFactoryImpl;
import security.securityPrerequisites.Prerequisite;
import security.securityPrerequisites.PrerequisiteCatalog;
import security.securityPrerequisites.impl.SecurityPrerequisitesFactoryImpl;
import security.securityThreats.Attack;
import security.securityThreats.ThreatCatalog;
import security.securityThreats.impl.SecurityThreatsFactoryImpl;

/**
 * An implementation of the ISecurityContainer interface.
 */
public class SecurityContainer implements ISecurityContainer {
	private Resource resource;
	private AdapterFactoryEditingDomain editingDomain;
	
	private final Logger logger = LoggerFactory.getLogger(SecurityContainer.class);
	
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
		if (resource != null) {
			logger.info("Resource found: " + resource.toString());
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

	@Override
	public URI getResourceURI() {
		return resource.getURI();
	}

	@Override
	public void addSecurityPattern(SecurityPattern pattern) {
		PatternCatalog catalog = getPatternCatalog();
		if (catalog == null) {
			catalog = (new SecurityPatternsFactoryImpl()).createPatternCatalog();
			getSecurityCatalog().getContains().add(catalog);
		}
		Command command = AddCommand.create(editingDomain, catalog, null, pattern);
		editingDomain.getCommandStack().execute(command);
	}

	@Override
	public void deleteSecurityPattern(String id) {
		for (SecurityPattern pattern : getPatternCatalog().getSecurityPatterns()) {
			if (pattern.getId().equals(id)) {
				Command command = DeleteCommand.create(editingDomain, pattern);
				editingDomain.getCommandStack().execute(command);
				break;
			}
		}
	}

	@Override
	public void addPrerequisite(Prerequisite prerequisite) {
		PrerequisiteCatalog catalog = getPrerequisiteCatalog();
		if (catalog == null) {
			catalog = (new SecurityPrerequisitesFactoryImpl()).createPrerequisiteCatalog();
			getSecurityCatalog().getContains().add(catalog);
		}
		Command command = AddCommand.create(editingDomain, catalog, null, prerequisite);
		editingDomain.getCommandStack().execute(command);
	}

	@Override
	public void deletePrerequisite(String id) {
		for (Prerequisite pre : getPrerequisiteCatalog().getPrerequisites()) {
			if (pre.getId().equals(id)) {
				Command command = DeleteCommand.create(editingDomain, pre);
				editingDomain.getCommandStack().execute(command);
				break;
			}
		}
	}

	@Override
	public void addAttack(Attack attack) {
		ThreatCatalog catalog = getAttackCatalog();
		if (catalog == null) {
			catalog = (new SecurityThreatsFactoryImpl()).createThreatCatalog();
			getSecurityCatalog().getContains().add(catalog);
		}
		Command command = AddCommand.create(editingDomain, catalog, null, attack);
		editingDomain.getCommandStack().execute(command);
	}

	@Override
	public void deleteAttack(String id) {
		for (Attack attack : getAttackCatalog().getAttacks()) {
			if (attack.getId().equals(id)) {
				Command command = DeleteCommand.create(editingDomain, attack);
				editingDomain.getCommandStack().execute(command);
				break;
			}
		}
	}
}
