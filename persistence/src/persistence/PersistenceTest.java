package persistence;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;

import com.vogella.emf.webpage.model.webpage.Web;
import com.vogella.emf.webpage.model.webpage.Webpage;
import com.vogella.emf.webpage.model.webpage.WebpageFactory;
import com.vogella.emf.webpage.model.webpage.WebpagePackage;

public class PersistenceTest {

	public static void main(String[] args) {
		List<Resource.Factory> factories = Arrays.<Resource.Factory>asList(XMIResourceImpl::new, XtendXMIResource::new);
		
		
		int i = 0;
		for (Resource.Factory fac : factories) {
			Resource res = createTestResource(fac, i++);
			try {
				res.save(Collections.EMPTY_MAP);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private static Resource createTestResource(Resource.Factory resfac, int count) {
		WebpagePackage.eINSTANCE.eClass();
		WebpageFactory fac = WebpageFactory.eINSTANCE;
		
		Web web = fac.createWeb();
		web.setName("WebName");
		web.setDescription("WebDescription");
		web.setKeywords("WebKeywords");
		web.setTitle("WebTitle");
		
		for (int i = 0; i < 3; i++) {
			Webpage page = fac.createWebpage();
			page.setName("WebPageName " + i);
			page.setDescription("WebPageDescription " + i);
			page.setTitle("WebPageTitle " + i);
			web.getPages().add(page);
		}
		
		Web web2 = fac.createWeb();
		
		Resource.Factory.Registry reg = Resource.Factory.Registry.INSTANCE;
		reg.getExtensionToFactoryMap().put("*", resfac);
		
		Resource res = new ResourceSetImpl().createResource(URI.createFileURI("/tmp/emf_persistence/web" + count + ".xmi"));
		res.getContents().add(web);
		res.getContents().add(web2);
		
		return res;
	}

}
