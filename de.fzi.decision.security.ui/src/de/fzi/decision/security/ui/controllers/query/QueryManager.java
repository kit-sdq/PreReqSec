package de.fzi.decision.security.ui.controllers.query;

import java.util.Collection;

import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import modelLoader.InitializationException;
import modelLoader.LoadingException;
import modelLoader.ModelLoaderEngine;
import parser.InterpreterException;
import parser.QueryInterpreter;
import security.NamedDescribedEntity;
import security.securityPatterns.SecurityPattern;
import security.securityPrerequisites.Prerequisite;
import security.securityThreats.Attack;

/**
 * Manager that acts as an interface to the query plugins.
 * @author matthias
 *
 */
public class QueryManager {
	
	private ModelLoaderEngine engine;
	private QueryInterpreter interpreter;
	private IQueryCallback queryCallback;
	
	/**
	 * Creates a new QueryManager and initializes the modelLoaderEngine and QueryInterpreter
	 * @param resourceUri The URI of the model resource
	 * @throws InitializationException Thrown if the model could not be loaded
	 */
	public QueryManager(IQueryCallback queryCallback, ResourceSet set) throws InitializationException {
		this.queryCallback = queryCallback;
		engine = new ModelLoaderEngine(set);
		interpreter = new QueryInterpreter(engine);
	}
	
	/**
	 * Starts a query and shows the result in the UI. Possible Exceptions will be catched and shown to the user.
	 * @throws InitializationException 
	 */
	public void startQuery(String query) throws InitializationException {
		Collection<NamedDescribedEntity> result = null;
		try {
			result = runQuery(query);
		} catch (InterpreterException e) {
			Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(activeShell, "Illegal query", e.getMessage());
		} catch (LoadingException e) {
			Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(activeShell, "Problems loading the result", e.getMessage());
		}
		if (result != null && !result.isEmpty()) {
			showQueryResult(result);
		} else {
			queryCallback.noResults();
		}
	}
	
	public Collection<NamedDescribedEntity> runQueryAndReturnResult(String query) throws InterpreterException, LoadingException, InitializationException {
		Collection<NamedDescribedEntity> result = null;
		result = runQuery(query);

		return result;
	}
	
	/**
	 * Runs the query via QueryInterpreter and returns the result.
	 * @param query The query given by the user
	 * @return A Collection of the resulting elements
	 * @throws InitializationException 
	 * @throws InterpreterException
	 * @throws LoadingException
	 */
	@SuppressWarnings("unchecked")
	private Collection<NamedDescribedEntity> runQuery(String query) throws InterpreterException, LoadingException, InitializationException {
		Collection<NamedDescribedEntity> result = (Collection<NamedDescribedEntity>) interpreter.interpretQuery(query);
		return result;
	}
	
	private void showQueryResult(Collection<NamedDescribedEntity> result) {
		if (result.iterator().next() instanceof SecurityPattern) {
			queryCallback.setFilterByResultingSecurityPatterns(result.toArray());
		} else if (result.iterator().next() instanceof Prerequisite) {
			queryCallback.setFilterByResultingPrerequisites(result.toArray());
		} else if (result.iterator().next() instanceof Attack) {
			queryCallback.setFilterByResultingAttacks(result.toArray());
		}
	}

}
