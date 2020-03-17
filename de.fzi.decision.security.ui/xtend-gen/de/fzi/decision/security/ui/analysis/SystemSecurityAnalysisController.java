package de.fzi.decision.security.ui.analysis;

import analysis.PreReqSecSecurityAnalyzer;
import de.fzi.decision.security.ui.analysis.SecurityAnalysisController;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.action.IAction;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.palladiosimulator.pcm.core.composition.AssemblyContext;
import org.palladiosimulator.pcm.core.entity.Entity;

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
      final Consumer<EObject> _function = (EObject it) -> {
        if (((it != null) && (it instanceof AssemblyContext))) {
          this.prettyPrintAttacksPossible(analyzer.analyze(it), it);
        }
      };
      rootContainer.eContents().forEach(_function);
    }
  }
}
