package de.fzi.decision.security.ui.controllers;

import java.util.HashMap;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.decision.security.ui.controllers.query.QueryManager;
import de.fzi.decision.security.ui.controllers.viewerfilters.AttackFilter;
import de.fzi.decision.security.ui.controllers.viewerfilters.IQueryFilter;
import de.fzi.decision.security.ui.controllers.viewerfilters.PatternFilter;
import de.fzi.decision.security.ui.controllers.viewerfilters.PrerequisiteFilter;
import de.fzi.decision.security.ui.main.DelegateSelectionProvider;
import de.fzi.decision.security.ui.models.ISecurityContainer;
import de.fzi.decision.security.ui.views.ISecurityPatternView;
import modelLoader.InitializationException;
import security.SecurityPackage;
import security.securityPatterns.SecurityPatternsPackage;
import security.securityPrerequisites.SecurityPrerequisitesPackage;
import security.securityThreats.SecurityThreatsPackage;

/**
 * Main Controller of the UI. Used to handle interaction with the user. 
 */
public class AppController implements IQueryFilter {
	private final ISecurityPatternView view;
	private final ISecurityContainer model;
	private final Logger logger = LoggerFactory.getLogger(AppController.class);
	private PrerequisiteFilter prerequisiteFilter;
	private AttackFilter attackFilter;
	private PatternFilter patternFilter;
	private QueryManager queryManager;
	
	/**
	 * Creates a new AppController.
	 * 
	 * @param view the corresponding view
	 * @param model the corresponding model
	 */
	public AppController(ISecurityPatternView view, ISecurityContainer model) {
		this.view = view;
		this.model = model;
	}
	
	/**
	 * Initializes the application: Calls the initialize method on the view,
	 * registers selection listeners and viewer filter, and calls the load method on the model.
	 * 
	 * @param parent the parent composite for the view
	 * @param uri the uri to the resource that is to be loaded by the model
	 * @param delegateSelectionProvider the delegate selection provider that unifies the selection provider of all
	 * viewers of this user interface
	 */
	public void init(Composite parent, URI uri, DelegateSelectionProvider delegateSelectionProvider) {
		logger.info("init() was called with URI: " + uri.toString() + ".");
		view.init(parent, createPatternAttributeMap(), createPrerequisiteAttributeMap(), createThreatAttributeMap());
		registerSelectionListeners(delegateSelectionProvider);
		registerViewerFilters();
		registerListeners();
		load(uri);
		initQueryManager();
	}

	/**
	 * Reloads the specified resource. Currently only used when changes to the loaded resource were made from
	 * other eclipse editors.
	 * 
	 * @param uri uri to the resource to reload
	 */
	public void reload(URI uri) {
		logger.info("reload() was triggered because of external changes to: " + uri.toString() + ".");
		load(uri);
	}
	
	/**
	 * Calls the analysis and sets the result in the user interface.
	 */
	public void runAnalysis() {
		logger.info("runAnalysis() was called");
		//TODO runn Analysis
		//runThreadAnalysisAndShowResult();
	}
	
	private void initQueryManager() {
		try {
			queryManager = new QueryManager(this, model.getResourceURI());
		} catch (InitializationException e) {
			Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(activeShell, "No model found", e.getMessage());
		}
	}
	
	private void load(URI uri) {
		model.load(uri);
		initDatabinding();
		runAnalysis();
	}
	
	private HashMap<EAttribute, String> createPatternAttributeMap() {
		HashMap<EAttribute, String> map = new HashMap<EAttribute, String>();
		map.put(SecurityPackage.Literals.NAMED_DESCRIBED_ENTITY__NAME, "Security Pattern");
		return map;
	}
	
	private HashMap<EAttribute, String> createPrerequisiteAttributeMap() {
		HashMap<EAttribute, String> map = new HashMap<EAttribute, String>();
		map.put(SecurityPackage.Literals.NAMED_DESCRIBED_ENTITY__NAME, "Prerequisite");
		return map;
	}
	
	private HashMap<EAttribute, String> createThreatAttributeMap() {
		HashMap<EAttribute, String> map = new HashMap<EAttribute, String>();
		map.put(SecurityPackage.Literals.NAMED_DESCRIBED_ENTITY__NAME, "Attack");
		return map;
	}
	
	private void initDatabinding() {
		view.getLeftViewer().setInput(getPatternObservables());
		view.getRightViewer().setInput(getPrerequisiteObservables());
		view.getOutputViewer().setInput(getThreatObservables());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IObservableList getPatternObservables() {
		EReference patterns = SecurityPatternsPackage.Literals.PATTERN_CATALOG__SECURITY_PATTERNS;
		return EMFProperties.list(patterns).observe(model.getPatternCatalog());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IObservableList getPrerequisiteObservables() {
		EReference prerequisites = SecurityPrerequisitesPackage.Literals.PREREQUISITE_CATALOG__PREREQUISITES;
		return EMFProperties.list(prerequisites).observe(model.getPrerequisiteCatalog());
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private IObservableList getThreatObservables() {
		EReference threats = SecurityThreatsPackage.Literals.THREAT_CATALOG__ATTACKS;
		return EMFProperties.list(threats).observe(model.getAttackCatalog());
	}
	
	private void registerSelectionListeners(DelegateSelectionProvider delegateSelectionProvider) {		
		view.getLeftViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = view.getLeftViewer().getStructuredSelection();
				logger.info("inputSection::leftViewer selection changed to: " + selection);
	
				if (!selection.isEmpty()) {
					delegateSelectionProvider.setSelectionProviderDelegate(view.getLeftViewer());
					logger.info("DelegateSelectionProvider delegating to inputSection::leftViewer");
				}
				
				prerequisiteFilter.setFilterByPattern(selection.toArray());
				attackFilter.setFilterByPattern(selection.toArray());
			}
		});
		
		view.getRightViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = view.getRightViewer().getStructuredSelection();
				logger.info("inputSection::rightViewer selection changed to: " + selection);
				
				if (!selection.isEmpty()) {
					delegateSelectionProvider.setSelectionProviderDelegate(view.getRightViewer());
					logger.info("DelegateSelectionProvider delegating to inputSection::rightViewer");
				}
				
				attackFilter.setFilterByPrerequisite(selection.toArray());
			}
		});
		
		view.getOutputViewer().addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = view.getOutputViewer().getStructuredSelection();
				logger.info("outputSection::outputViewer selection changed to: " + selection);
				
				if (!selection.isEmpty()) {
					delegateSelectionProvider.setSelectionProviderDelegate(view.getOutputViewer());
					logger.info("DelegateSelectionProvider delegating to outputSection::outputViewer");
				}
			}
		});		
	}
	
	private void registerViewerFilters() {
		patternFilter = new PatternFilter(view.getLeftViewer());
		view.getLeftViewer().addFilter(patternFilter);
		prerequisiteFilter = new PrerequisiteFilter(view.getRightViewer());
		view.getRightViewer().addFilter(prerequisiteFilter);
		attackFilter = new AttackFilter(view.getOutputViewer());
		view.getOutputViewer().addFilter(attackFilter);
	}
	
	private void registerListeners() {
		view.setFilterKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == SWT.ESC) {
					logger.info("toolbar::filter registered Escape Key.");
					view.clearFilterText();
					logger.info("toolbar::filter cleared.");
				} else if (e.character == SWT.LF || e.character == SWT.CR || e.character == SWT.KEYPAD_CR) {
					logger.info("toolbar::filter registered Enter Key.");
					logger.info("toolbar::filter query: " + view.getFilterText().trim());
					
					queryManager.startQuery(view.getFilterText().trim());
				}
			}
		});		
	}

	@Override
	public void setFilterBySecurityPatterns(Object[] patterns) {
		patternFilter.setFilterByPattern(patterns);
		prerequisiteFilter.setFilterByPattern(patterns);
		attackFilter.setFilterByPattern(patterns);
	}

	@Override
	public void setFilterByPrerequisites(Object[] prerequisites) {
		patternFilter.setFilterByPrerequisite(prerequisites);
		prerequisiteFilter.setFilterByPrerequisite(prerequisites);
		attackFilter.setFilterByPrerequisite(prerequisites);
	}

	@Override
	public void setFilterByAttacks(Object[] attacks) {
		patternFilter.setFilterByAttacks(attacks);
		prerequisiteFilter.setFilterByAttacks(attacks);
		attackFilter.setFilterByAttacks(attacks);
	}
}
