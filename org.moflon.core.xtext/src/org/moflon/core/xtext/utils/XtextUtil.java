package org.moflon.core.xtext.utils;

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.util.EcoreUtil;

public class XtextUtil
{
   private static XtextUtil instance;
   
   private final EPackage ecoreEPackage;
   
   private final List<EDataType> ecoreEDataTypes;
   
   private XtextUtil(){
      ecoreEPackage = EcoreFactory.eINSTANCE.getEcorePackage();
      EcoreUtil.resolveAll(ecoreEPackage);
      ecoreEDataTypes = mapToSubtype(ecoreEPackage.getEClassifiers(), EDataType.class);
   }
   
   public static XtextUtil getInstance(){
      if(instance == null)
         instance = new XtextUtil();
      return instance;
   }
   
   public <T> List<T> mapToSupertypeUnsorted(Collection <? extends T> subTypes, Class<T> clazz)
   {
      return subTypes.parallelStream().map(clazz::cast).collect(Collectors.toList());
   }
   
   public <T> List<T> mapToSubtypeUnsorted(Collection<? super T> list, Class<T> clazz)
   {
      return list.parallelStream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
   }
   
   public <T> List<T> mapToSubtype(Collection<? super T> list, Class<T> clazz)
   {
      return list.stream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
   }

   public <T> boolean exist(List<T> list, Predicate<? super T> predicate){
      return list.stream().anyMatch(predicate);
   }
   
   public <T> List<T> mapToSupertype(Collection <? extends T> subTypes, Class<T> clazz)
   {
      return subTypes.stream().map(clazz::cast).collect(Collectors.toList());
   }

   public final EPackage getEcoreEPackage(){
      return ecoreEPackage;
   }

   public final List<EDataType> getEcoreEDataTypes(){
      return ecoreEDataTypes;
   }
   
}
