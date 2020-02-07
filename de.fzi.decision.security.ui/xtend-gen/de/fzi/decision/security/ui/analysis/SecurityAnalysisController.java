package de.fzi.decision.security.ui.analysis;

import analysis.PreReqSecSecurityAnalyzer;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.actions.ActionDelegate;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.InputOutput;
import org.palladiosimulator.pcm.core.entity.Entity;
import security.securityThreats.Attack;

/**
 * Controls the security analysis in the editor
 */
@SuppressWarnings("all")
public class SecurityAnalysisController extends ActionDelegate implements IActionDelegate {
  protected EObject selection;
  
  /**
   * Executed once the context menu item is clicked
   */
  @Override
  public void run(final IAction action) {
    super.run(action);
    if (((this.selection != null) && (this.selection instanceof EObject))) {
      final PreReqSecSecurityAnalyzer analyzer = new PreReqSecSecurityAnalyzer();
      final ArrayList<String> structAnalysisResults = new ArrayList<String>();
      List<Attack> possibleAttacks = analyzer.analyze(this.selection, structAnalysisResults);
      InputOutput.println();
      final Consumer<String> _function = (String it) -> {
        InputOutput.<String>println(it);
      };
      structAnalysisResults.forEach(_function);
      this.prettyPrintAttacksPossible(possibleAttacks, this.selection);
    }
  }
  
  /**
   * Executed on selection change in the instance editor
   */
  @Override
  public void selectionChanged(final IAction action, final ISelection newSelection) {
    if ((newSelection instanceof IStructuredSelection)) {
      final Object object = ((IStructuredSelection) newSelection).getFirstElement();
      if ((object instanceof EObject)) {
        this.selection = ((EObject) object);
        action.setEnabled((this.selection != null));
        return;
      }
    }
    this.selection = null;
    action.setEnabled(false);
  }
  
  protected void prettyPrintAttacksPossible(final List<Attack> attacks, final EObject component) {
    if (((component == null) || (attacks == null))) {
      return;
    }
    String _switchResult = null;
    boolean _matched = false;
    if (component instanceof Entity) {
      _matched=true;
      _switchResult = ((Entity)component).getEntityName();
    }
    if (!_matched) {
      _switchResult = component.toString();
    }
    final String name = _switchResult;
    int _size = attacks.size();
    boolean _equals = (_size == 0);
    if (_equals) {
      StringConcatenation _builder = new StringConcatenation();
      _builder.append("No attacks possible on element ");
      _builder.append(name);
      InputOutput.<String>println(_builder.toString());
    } else {
      StringConcatenation _builder_1 = new StringConcatenation();
      int _size_1 = attacks.size();
      _builder_1.append(_size_1);
      _builder_1.append(" ");
      String _xifexpression = null;
      int _size_2 = attacks.size();
      boolean _greaterThan = (_size_2 > 1);
      if (_greaterThan) {
        _xifexpression = "attacks";
      } else {
        _xifexpression = "attack";
      }
      _builder_1.append(_xifexpression);
      _builder_1.append(" possible:");
      InputOutput.<String>println(_builder_1.toString());
      final Consumer<Attack> _function = (Attack it) -> {
        StringConcatenation _builder_2 = new StringConcatenation();
        _builder_2.append("Attack ");
        String _name = it.getName();
        _builder_2.append(_name);
        _builder_2.append(" possible on Element ");
        _builder_2.append(name);
        _builder_2.append("!");
        InputOutput.<String>println(_builder_2.toString());
      };
      attacks.forEach(_function);
    }
  }
}
