package emfcodegenerator.test;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;

import com.vogella.emf.webpage.model.webpage.Web;
import com.vogella.emf.webpage.model.webpage.Webpage;
import com.vogella.emf.webpage.model.webpage.WebpageFactory;

public class NotificationTest {

	public static void main(String[] args) {
		Adapter adapter = new AdapterImpl() {
			public void notifyChanged(Notification n) {
				System.out.println(n);
			}
		};
		
		WebpageFactory fac = WebpageFactory.eINSTANCE;
		Web web = fac.createWeb();
		web.eAdapters().add(adapter);
		web.setDescription("description");
		web.setName("name");
		web.getPages().add(fac.createWebpage());
		List<Webpage> pageList = Stream.generate(fac::createWebpage).limit(10).collect(Collectors.toList());
		web.getPages().addAll(pageList);
	}

}
