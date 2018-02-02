package de.fzi.decision.security.ui.controllers.query;

public interface IQueryCallback {
	
	public void setFilterByResultingSecurityPatterns(Object[] patterns);
	public void setFilterByResultingPrerequisites(Object[] prerequisites);
	public void setFilterByResultingAttacks(Object[] attacks);
	public void noResults();

}
