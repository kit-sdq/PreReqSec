package de.fzi.decision.security.ui.controllers.viewerfilters;

public interface IQueryFilter {
	
	public void setFilterBySecurityPatterns(Object[] patterns);
	public void setFilterByPrerequisites(Object[] prerequisites);
	public void setFilterByAttacks(Object[] attacks);

}
