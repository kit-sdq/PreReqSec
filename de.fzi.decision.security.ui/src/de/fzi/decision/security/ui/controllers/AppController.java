package de.fzi.decision.security.ui.controllers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.databinding.EMFProperties;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.net4j.util.collection.Pair;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.fzi.decision.security.ui.controllers.query.IAnalysisClickListener;
import de.fzi.decision.security.ui.controllers.query.IQueryCallback;
import de.fzi.decision.security.ui.controllers.query.QueryManager;
import de.fzi.decision.security.ui.controllers.viewerfilters.AttackFilter;
import de.fzi.decision.security.ui.controllers.viewerfilters.PatternFilter;
import de.fzi.decision.security.ui.controllers.viewerfilters.PrerequisiteFilter;
import de.fzi.decision.security.ui.main.DelegateSelectionProvider;
import de.fzi.decision.security.ui.models.ISecurityContainer;
import de.fzi.decision.security.ui.views.ISecurityPatternView;
import modelLoader.InitializationException;
import modelLoader.LoadingException;
import parser.InterpreterException;
import security.NamedDescribedEntity;
import security.SecurityPackage;
import security.securityPatterns.SecurityPatternsPackage;
import security.securityPrerequisites.SecurityPrerequisitesPackage;
import security.securityThreats.SecurityThreatsPackage;
import validation.SecurityPatternAnalysis;

/**
 * Main Controller of the UI. Used to handle interaction with the user. 
 */
public class AppController implements IQueryCallback, IAnalysisClickListener {
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
		view.init(parent, this, delegateSelectionProvider, createPatternAttributeList(), 
				createPrerequisiteAttributeList(), createThreatAttributeList(), model);
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
	
	private void initQueryManager() {
		try {
			queryManager = new QueryManager(this, model.getResourceSet());
		} catch (InitializationException e) {
			Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			MessageDialog.openError(activeShell, "No model found", e.getMessage());
		}
	}
	
	private void load(URI uri) {
		model.load(uri);
		initDatabinding();
	}
	
	private List<Pair<EAttribute, String>> createPatternAttributeList() {
		List<Pair<EAttribute, String>> list = new ArrayList<>();
		Pair<EAttribute, String> patternPair = new Pair<EAttribute, String>(SecurityPackage.Literals.NAMED_DESCRIBED_ENTITY__NAME,
				"Security Pattern");
		list.add(patternPair);
		list.add(createDescPair());
		return list;
	}
	
	private List<Pair<EAttribute, String>> createPrerequisiteAttributeList() {
		List<Pair<EAttribute, String>> list = new ArrayList<>();
		Pair<EAttribute, String> prePair = new Pair<EAttribute, String>(SecurityPackage.Literals.NAMED_DESCRIBED_ENTITY__NAME, 
				"Prerequisite");
		list.add(prePair);
		list.add(createDescPair());
		return list;
	}
	
	private List<Pair<EAttribute, String>> createThreatAttributeList() {
		List<Pair<EAttribute, String>> list = new ArrayList<>();
		Pair<EAttribute, String> attackPair = new Pair<EAttribute, String>(SecurityPackage.Literals.NAMED_DESCRIBED_ENTITY__NAME, 
				"Attack");
		list.add(attackPair);
		list.add(createDescPair());
		return list;
	}
	
	private Pair<EAttribute, String> createDescPair() {
		return new Pair<EAttribute, String>(SecurityPackage.Literals.NAMED_DESCRIBED_ENTITY__DESCRIPTION,
				"Description");
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
					
					try {
						queryManager.startQuery(view.getFilterText().trim());
					} catch (InitializationException e1) {
						Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
						MessageDialog.openError(activeShell, "Problems loading the resource", e1.getMessage());
					}
				}
			}
		});		
	}

	@Override
	public void setFilterByResultingSecurityPatterns(Object[] patterns) {
		view.clearSelection();
		patternFilter.setFilterByPattern(patterns);
		prerequisiteFilter.setFilterByPattern(patterns);
		attackFilter.setFilterByPattern(patterns);
	}

	@Override
	public void setFilterByResultingPrerequisites(Object[] prerequisites) {
		view.clearSelection();
		patternFilter.setFilterByPrerequisite(prerequisites);
		prerequisiteFilter.setFilterByPrerequisite(prerequisites);
		attackFilter.setFilterByPrerequisite(prerequisites);
	}

	@Override
	public void setFilterByResultingAttacks(Object[] attacks) {
		view.clearSelection();
		patternFilter.setFilterByAttacks(attacks);
		prerequisiteFilter.setFilterByAttacks(attacks);
		attackFilter.setFilterByAttacks(attacks);
	}
	
	@Override
	public void noResults() {
		view.clearSelection();
		setFilterByResultingSecurityPatterns(model.getPatternCatalog().getSecurityPatterns().toArray());
		Shell activeShell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		MessageDialog.openInformation(activeShell, "No Query Results", "The resulting query was empty.");
	}

	@Override
	public void startAnalysis(String attackQuery, String patternQuery) throws InterpreterException, LoadingException, InitializationException {
		Collection<NamedDescribedEntity> attackResults = queryManager.runQueryAndReturnResult(attackQuery);
		Collection<NamedDescribedEntity> patternResults = queryManager.runQueryAndReturnResult(patternQuery);
		if (attackResults != null && patternResults != null) {
			SecurityPatternAnalysis spa = new SecurityPatternAnalysis();
			boolean analysisResult = spa.runThreatAnalysis(attackResults, patternResults);
			view.setAnalysisResult(Boolean.toString(analysisResult));
		}
	}
}
