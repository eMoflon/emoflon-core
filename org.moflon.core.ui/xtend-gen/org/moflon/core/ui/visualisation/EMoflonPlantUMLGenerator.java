package org.moflon.core.ui.visualisation;

import java.util.Collection;
import java.util.HashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.Conversions;

@SuppressWarnings("all")
public class EMoflonPlantUMLGenerator {
  private static HashMap<EObject, String> idMap = new HashMap<EObject, String>();
  
  public static String wrapInTags(final String body) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("@startuml");
    _builder.newLine();
    _builder.append(body);
    _builder.newLineIfNotEmpty();
    _builder.append("@enduml");
    _builder.newLine();
    return _builder.toString();
  }
  
  private static Object identifierForObject(final EObject o, final char separator) {
    CharSequence _xblockexpression = null;
    {
      boolean _containsKey = EMoflonPlantUMLGenerator.idMap.containsKey(o);
      boolean _not = (!_containsKey);
      if (_not) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("o");
        int _size = EMoflonPlantUMLGenerator.idMap.keySet().size();
        int _plus = (_size + 1);
        _builder.append(_plus);
        EMoflonPlantUMLGenerator.idMap.put(o, _builder.toString());
      }
      StringConcatenation _builder_1 = new StringConcatenation();
      String _get = EMoflonPlantUMLGenerator.idMap.get(o);
      _builder_1.append(_get);
      _builder_1.append(separator);
      String _name = o.eClass().getName();
      _builder_1.append(_name);
      _xblockexpression = _builder_1;
    }
    return _xblockexpression;
  }
  
  public static String emptyDiagram() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("title Choose an element that can be visualised");
    _builder.newLine();
    return _builder.toString();
  }
  
  public static String visualiseEcoreElements(final Collection<EClass> eclasses, final Collection<EReference> refs) {
    StringConcatenation _builder = new StringConcatenation();
    {
      for(final EClass c : eclasses) {
        _builder.append("class ");
        String _identifierForClass = EMoflonPlantUMLGenerator.identifierForClass(c);
        _builder.append(_identifierForClass);
        _builder.newLineIfNotEmpty();
        {
          EList<EClass> _eSuperTypes = c.getESuperTypes();
          for(final EClass s : _eSuperTypes) {
            _builder.append("\t");
            String _identifierForClass_1 = EMoflonPlantUMLGenerator.identifierForClass(c);
            _builder.append(_identifierForClass_1, "\t");
            _builder.append("--|>");
            String _identifierForClass_2 = EMoflonPlantUMLGenerator.identifierForClass(s);
            _builder.append(_identifierForClass_2, "\t");
            _builder.newLineIfNotEmpty();
          }
        }
      }
    }
    {
      for(final EReference r : refs) {
        String _identifierForClass_3 = EMoflonPlantUMLGenerator.identifierForClass(r.getEContainingClass());
        _builder.append(_identifierForClass_3);
        {
          boolean _isContainment = r.isContainment();
          if (_isContainment) {
            _builder.append(" *");
          }
        }
        _builder.append("--> ");
        CharSequence _multiplicityFor = EMoflonPlantUMLGenerator.multiplicityFor(r);
        _builder.append(_multiplicityFor);
        _builder.append(" ");
        String _identifierForClass_4 = EMoflonPlantUMLGenerator.identifierForClass(r.getEReferenceType());
        _builder.append(_identifierForClass_4);
        _builder.append(" : \"");
        String _name = r.getName();
        _builder.append(_name);
        _builder.append("\"");
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder.toString();
  }
  
  public static String visualiseModelElements(final Collection<EObject> objects, final Collection<Pair<String, Pair<EObject, EObject>>> links) {
    String _xblockexpression = null;
    {
      EMoflonPlantUMLGenerator.idMap.clear();
      StringConcatenation _builder = new StringConcatenation();
      {
        for(final EObject o : objects) {
          _builder.append("object ");
          Object _identifierForObject = EMoflonPlantUMLGenerator.identifierForObject(o);
          _builder.append(_identifierForObject);
          _builder.append("{");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          CharSequence _visualiseAllAttributes = EMoflonPlantUMLGenerator.visualiseAllAttributes(o);
          _builder.append(_visualiseAllAttributes, "\t");
          _builder.newLineIfNotEmpty();
          _builder.append("}");
          _builder.newLine();
        }
      }
      {
        for(final Pair<String, Pair<EObject, EObject>> l : links) {
          Object _identifierForObject_1 = EMoflonPlantUMLGenerator.identifierForObject(l.getRight().getLeft());
          _builder.append(_identifierForObject_1);
          _builder.append(" --> ");
          Object _identifierForObject_2 = EMoflonPlantUMLGenerator.identifierForObject(l.getRight().getRight());
          _builder.append(_identifierForObject_2);
          _builder.append(" : \"");
          String _left = l.getLeft();
          _builder.append(_left);
          _builder.append("\"");
          _builder.newLineIfNotEmpty();
        }
      }
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  private static CharSequence multiplicityFor(final EReference r) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\"");
    {
      int _lowerBound = r.getLowerBound();
      boolean _equals = (_lowerBound == (-1));
      if (_equals) {
        _builder.append("*");
      } else {
        int _lowerBound_1 = r.getLowerBound();
        _builder.append(_lowerBound_1);
      }
    }
    _builder.append("..");
    {
      int _upperBound = r.getUpperBound();
      boolean _equals_1 = (_upperBound == (-1));
      if (_equals_1) {
        _builder.append("*");
      } else {
        int _upperBound_1 = r.getUpperBound();
        _builder.append(_upperBound_1);
      }
    }
    _builder.append("\"");
    return _builder;
  }
  
  private static String identifierForClass(final EClass c) {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("\"");
    String _name = c.getEPackage().getName();
    _builder.append(_name);
    _builder.append(".");
    String _name_1 = c.getName();
    _builder.append(_name_1);
    _builder.append("\"");
    return _builder.toString();
  }
  
  public static CharSequence visualiseAllAttributes(final EObject o) {
    StringConcatenation _builder = new StringConcatenation();
    {
      EList<EAttribute> _eAllAttributes = o.eClass().getEAllAttributes();
      for(final EAttribute a : _eAllAttributes) {
        String _name = a.getName();
        _builder.append(_name);
        _builder.append(" = ");
        Object _eGet = o.eGet(a);
        _builder.append(_eGet);
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }
  
  private static Object identifierForObject(final EObject o) {
    CharSequence _xblockexpression = null;
    {
      boolean _containsKey = EMoflonPlantUMLGenerator.idMap.containsKey(o);
      boolean _not = (!_containsKey);
      if (_not) {
        StringConcatenation _builder = new StringConcatenation();
        _builder.append("o");
        int _size = EMoflonPlantUMLGenerator.idMap.keySet().size();
        int _plus = (_size + 1);
        _builder.append(_plus);
        EMoflonPlantUMLGenerator.idMap.put(o, _builder.toString());
      }
      StringConcatenation _builder_1 = new StringConcatenation();
      String _get = EMoflonPlantUMLGenerator.idMap.get(o);
      _builder_1.append(_get);
      _builder_1.append(".");
      String _name = o.eClass().getName();
      _builder_1.append(_name);
      _xblockexpression = _builder_1;
    }
    return _xblockexpression;
  }
  
  public static String visualiseCorrModel(final Collection<EObject> corrObjects, final Collection<EObject> sourceObjects, final Collection<EObject> targetObjects, final Collection<Pair<String, Pair<EObject, EObject>>> links) {
    String _xblockexpression = null;
    {
      EMoflonPlantUMLGenerator.idMap.clear();
      StringConcatenation _builder = new StringConcatenation();
      CharSequence _plantUMLPreamble = EMoflonPlantUMLGenerator.plantUMLPreamble();
      _builder.append(_plantUMLPreamble);
      _builder.newLineIfNotEmpty();
      _builder.append("together {");
      _builder.newLine();
      {
        for(final EObject so : sourceObjects) {
          _builder.append("class ");
          Object _identifierForObject = EMoflonPlantUMLGenerator.identifierForObject(so, '_');
          _builder.append(_identifierForObject);
          _builder.append(" <<BLACK>> <<SRC>>{");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          CharSequence _visualiseAllAttributes = EMoflonPlantUMLGenerator.visualiseAllAttributes(so);
          _builder.append(_visualiseAllAttributes, "\t");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.append("}");
      _builder.newLine();
      _builder.newLine();
      _builder.append("together {");
      _builder.newLine();
      {
        for(final EObject to : targetObjects) {
          _builder.append("class ");
          Object _identifierForObject_1 = EMoflonPlantUMLGenerator.identifierForObject(to, '_');
          _builder.append(_identifierForObject_1);
          _builder.append(" <<BLACK>> <<TRG>>{");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          CharSequence _visualiseAllAttributes_1 = EMoflonPlantUMLGenerator.visualiseAllAttributes(to);
          _builder.append(_visualiseAllAttributes_1, "\t");
          _builder.newLineIfNotEmpty();
          _builder.append("\t");
          _builder.append("}");
          _builder.newLine();
        }
      }
      _builder.append("}");
      _builder.newLine();
      _builder.append("\t\t");
      _builder.newLine();
      int i = 0;
      _builder.newLineIfNotEmpty();
      {
        for(final EObject o : corrObjects) {
          Object _identifierForObject_2 = EMoflonPlantUMLGenerator.identifierForObject(((EObject[])Conversions.unwrapArray(sourceObjects, EObject.class))[i], '_');
          _builder.append(_identifierForObject_2);
          _builder.append(" <..> ");
          int _plusPlus = i++;
          Object _identifierForObject_3 = EMoflonPlantUMLGenerator.identifierForObject(((EObject[])Conversions.unwrapArray(targetObjects, EObject.class))[_plusPlus], '_');
          _builder.append(_identifierForObject_3);
          _builder.append(" : \"");
          String _name = o.eClass().getName();
          String _plus = (":" + _name);
          String _abbreviate = StringUtils.abbreviate(_plus, 11);
          _builder.append(_abbreviate);
          _builder.append("\"\t");
          _builder.newLineIfNotEmpty();
        }
      }
      _builder.newLine();
      {
        for(final Pair<String, Pair<EObject, EObject>> l : links) {
          Object _identifierForObject_4 = EMoflonPlantUMLGenerator.identifierForObject(l.getRight().getLeft(), '_');
          _builder.append(_identifierForObject_4);
          _builder.append(" --> ");
          Object _identifierForObject_5 = EMoflonPlantUMLGenerator.identifierForObject(l.getRight().getRight(), '_');
          _builder.append(_identifierForObject_5);
          _builder.append(" : \"");
          String _left = l.getLeft();
          _builder.append(_left);
          _builder.append("\"");
          _builder.newLineIfNotEmpty();
        }
      }
      _xblockexpression = _builder.toString();
    }
    return _xblockexpression;
  }
  
  protected static CharSequence plantUMLPreamble() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("hide empty members");
    _builder.newLine();
    _builder.append("hide circle");
    _builder.newLine();
    _builder.append("hide stereotype");
    _builder.newLine();
    _builder.newLine();
    _builder.append("skinparam shadowing false");
    _builder.newLine();
    _builder.newLine();
    _builder.append("skinparam class {");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("BorderColor<<GREEN>> SpringGreen");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("BorderColor<<BLACK>> Black");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("BorderColor<<KERN>> LightGray");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("BackgroundColor<<TRG>> MistyRose");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("BackgroundColor<<SRC>> LightYellow");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("BackgroundColor<<CORR>> LightCyan ");
    _builder.newLine();
    _builder.append("\t");
    _builder.append("ArrowColor Black");
    _builder.newLine();
    _builder.append("}\t");
    _builder.newLine();
    return _builder;
  }
}
