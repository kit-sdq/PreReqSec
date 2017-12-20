package de.fzi.decision.security.cdo.client.model;

import org.eclipse.emf.cdo.eresource.CDOResource;

import security.Catalog;
import security.Container;
import security.securityPatterns.PatternCatalog;
import security.securityPrerequisites.PrerequisiteCatalog;
import security.securityThreats.ThreatCatalog;

public class SecurityContainer {
	
	private CDOResource rootResource;
	
	private PatternCatalog patternCatalog;
	private PrerequisiteCatalog prerequisiteCatalog;
	private ThreatCatalog threatCatalog;
	
	public SecurityContainer(CDOResource resource) {
		Container securityContainer = (Container) resource.getContents().get(0);
		initCatalogs(securityContainer);
		this.rootResource = resource;
	}

	private void initCatalogs(Container container) {
		for (Catalog catalog : container.getContains()) {
			if (catalog instanceof PatternCatalog) {
				patternCatalog = (PatternCatalog) catalog;
			}
			else if (catalog instanceof PrerequisiteCatalog) {
				prerequisiteCatalog = (PrerequisiteCatalog) catalog;
			}
			else if (catalog instanceof ThreatCatalog) {
				threatCatalog = (ThreatCatalog) catalog;
			}
		}
	}
	
	public CDOResource getRootResource() {
		return rootResource;
	}
	
	public Container getSecurityContainer() {
		return (Container) rootResource.getContents().get(0);
	}

	public PatternCatalog getPatternCatalog() {
		return patternCatalog;
	}

	public PrerequisiteCatalog getPrerequisiteCatalog() {
		return prerequisiteCatalog;
	}

	public ThreatCatalog getThreatCatalog() {
		return threatCatalog;
	}
	

}
