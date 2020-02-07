package de.fzi.decision.security.ui.analysis;

import analysis.PreReqSecSecurityAnalyzer;
import de.fzi.decision.security.ui.analysis.SecurityAnalysisController;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.palladiosimulator.pcm.core.entity.Entity;
import security.securityThreats.Attack;

/**
 * Controls the security analysis for a whole system in the editor.
 * 
 * @author Robert Hochweiss
 */
@SuppressWarnings("all")
public class SystemSecurityAnalysisController extends SecurityAnalysisController {
  /**
   * Executed once the context menu item is clicked
   */
  @Override
  public void run(final IAction action) {
    if (((this.selection != null) && (this.selection instanceof EObject))) {
      final EObject rootContainer = EcoreUtil.getRootContainer(this.selection);
      String _switchResult = null;
      boolean _matched = false;
      if (rootContainer instanceof Entity) {
        _matched=true;
        _switchResult = ((Entity)rootContainer).getEntityName();
      }
      if (!_matched) {
        _switchResult = rootContainer.toString();
      }
      final String sysName = _switchResult;
      InputOutput.<String>println(((("\n" + "Analyzing the system ") + sysName) + " for security vulnerabilities:"));
      final PreReqSecSecurityAnalyzer analyzer = new PreReqSecSecurityAnalyzer();
      final ArrayList<String> structAnalysisResults = new ArrayList<String>();
      final HashMap<EObject, List<Attack>> possibleAttacksPerObject = new HashMap<EObject, List<Attack>>();
      final Consumer<EObject> _function = (EObject it) -> {
        if ((it != null)) {
          possibleAttacksPerObject.put(it, analyzer.analyze(it, structAnalysisResults));
        }
      };
      rootContainer.eContents().forEach(_function);
      final Consumer<String> _function_1 = (String it) -> {
        InputOutput.<String>println(it);
      };
      structAnalysisResults.forEach(_function_1);
      final BiConsumer<EObject, List<Attack>> _function_2 = (EObject k, List<Attack> v) -> {
        this.prettyPrintAttacksPossible(v, k);
      };
      possibleAttacksPerObject.forEach(_function_2);
    }
  }
}
