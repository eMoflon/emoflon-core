package org.moflon.core.xtext.scoping.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class MOSLScopeUtil
{
   private static MOSLScopeUtil instance;

   private MOSLScopeUtil()
   {
   }

   public static MOSLScopeUtil getInstance()
   {
      if (instance == null)
         instance = new MOSLScopeUtil();
      return instance;
   }

   public <R extends EObject> R getRootObject(EObject context, Class<R> clazz)
   {
      Stack<EObject> stack = new Stack<EObject>();
      stack.push(context);
      while (!stack.isEmpty())
      {
         EObject element = stack.pop();
         if (element == null)
         {
            return null;
         } else if (clazz.isInstance(element))
            return clazz.cast(element);
         stack.push(element.eContainer());
      }
      return null;
   }

   public <E extends EObject> List<E> getObjectsFromResource(Resource resource, Class<E> clazz){
      List<EObject> allContent = new ArrayList<>();
      resource.getAllContents().forEachRemaining(allContent::add);
      return allContent.parallelStream().filter(clazz::isInstance).map(clazz::cast).collect(Collectors.toList());
   }
   
   public <E extends EObject> E getObjectFromResourceSet(URI uri, ResourceSet resourceSet, Class<E> clazz)
   {
      Resource res = getResource(uri, resourceSet, true);
      E scopingRoot = clazz.cast(res.getContents().get(0));
      return scopingRoot;
   }

   public ResourceSet getResourceSet()
   {
      return getResourceSet("xmi");
   }

   private Resource getResource(URI uri, ResourceSet resourceSet, boolean load)
   {
      try
      {
         Resource res = resourceSet.getResource(uri, false);
         if (res == null)
         {
            res = resourceSet.createResource(uri);
         }
         if (load)
            res.load(Collections.EMPTY_MAP);
         return res;
      } catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public Resource addToResource(URI uri, ResourceSet resourceSet, EObject obj)
   {
      Resource resource = getResource(uri, resourceSet, false);
      resource.getContents().clear();
      resource.getContents().add(obj);
      return resource;
   }

   public void saveToResource(URI uri, ResourceSet resourceSet, EObject obj)
   {
      try
      {
         Resource resource = addToResource(uri, resourceSet, obj);
         resource.save(Collections.EMPTY_MAP);
      } catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   public ResourceSet getResourceSet(String ext)
   {
      ResourceSet resourceSet = new ResourceSetImpl();
      Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
      Map<String, Object> m = reg.getExtensionToFactoryMap();
      Object factory = m.getOrDefault(ext, new XMIResourceFactoryImpl());
      m.put(ext, factory);
      return resourceSet;
   }
}
