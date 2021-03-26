package persistence;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import smartEMF.Book;
import smartEMF.Library;
import smartEMF.Person;
import smartEMF.Shelf;
import smartEMF.SmartEMFFactory;
import smartEMF.SmartEMFPackage;

@SuppressWarnings("unused")
public class PersistenceTest {

	public static void main(String[] args) {
		SmartEMFPackage.eINSTANCE.eClass();
		
		List<Resource.Factory> factories = Arrays.<Resource.Factory>asList(
//				XMIResourceImpl::new,
				XtendXMIResource::new
				);
		
		
		int i = 0;
		for (Resource.Factory fac : factories) {
			Resource res = createTestResource(fac, i++);
			try {
				res.save(Collections.EMPTY_MAP);
			} catch (Throwable t) {
				t.printStackTrace();
			}
//			i++;
		}
		
		for (Resource.Factory fac : factories) {
			for (int k = 0; k < i; k++) {
				Resource res = fac.createResource(URI.createFileURI("/tmp/emf_persistence/web" + k + ".xmi"));
				try {
					Resource.Factory.Registry.INSTANCE.getExtensionToFactoryMap().put("*", fac);
					res.load(Collections.emptyMap());
					System.out.println(res.getContents());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	private static Resource createTestResource(Resource.Factory resfac, int count) {SmartEMFPackage.eINSTANCE.eClass();
		SmartEMFFactory fac = SmartEMFFactory.eINSTANCE;
		
		Library lib = fac.createLibrary();
		lib.setName("Library 1");
		Person[] people = new Person[3];
		for (int i = 0; i < 3; i++) {
				Shelf sh = fac.createShelf();
				sh.setShelf_no(i);
				lib.getContained_shelves().add(sh);
				ArrayList<Book> books = new ArrayList<>();
				for (int j = 0; j < 5; j++) {
					Book bk = fac.createBook();
					bk.setID(i*10 + j + 1);
					bk.setName("Book " + i + "." + j);
					sh.getContained_books().add(bk);
					books.add(bk);
				}
				people[i] = fac.createPerson();
				people[i].setName("Library user " + i);
				people[i].getBorrowed_books().add(books.get((int)(Math.random() * 5)));
		}
		for (Person p : people) lib.getLibrary_users().add(p);
		Library lib2 = fac.createLibrary();
		lib2.setName("Library 2");
		Shelf sh = fac.createShelf();
		sh.setShelf_no(0);
		lib2.getContained_shelves().add(sh);
		Book bk = fac.createBook();
		bk.setID(31);
		bk.setName("Book 3.0");
		sh.getContained_books().add(bk);
		people[0].getBorrowed_books().add(bk);
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("*", resfac);
		
		Resource res = new ResourceSetImpl().createResource(URI.createFileURI("/tmp/emf_persistence/web" + count + ".xmi"));
		res.getContents().add(lib);
		res.getContents().add(lib2);
		
		return res;
	}

}
