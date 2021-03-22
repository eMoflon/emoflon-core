package smartEMF;
import org.eclipse.emf.ecore.EFactory;

public interface SmartEMFFactory extends EFactory {
    SmartEMFFactory eINSTANCE = smartEMF.impl.SmartEMFFactoryImpl.init();
    SmartEMFPackage getSmartEMFPackage();
     Book createBook();
     Library createLibrary();
     Person createPerson();
     Shelf createShelf();
}
