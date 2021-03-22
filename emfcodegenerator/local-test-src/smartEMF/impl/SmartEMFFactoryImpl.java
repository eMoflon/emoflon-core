package smartEMF.impl;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.impl.EFactoryImpl;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import smartEMF.*;
import smartEMF.SmartEMFFactory;

public class SmartEMFFactoryImpl extends EFactoryImpl implements SmartEMFFactory {

    @Deprecated
    public static SmartEMFPackage getPackage() {
        return SmartEMFPackage.eINSTANCE;
    }

    @Override public EObject create(EClass eClass) {
        switch (eClass.getClassifierID()) {
        case SmartEMFPackage.BOOK:
            return createBook();
        case SmartEMFPackage.LIBRARY:
            return createLibrary();
        case SmartEMFPackage.PERSON:
            return createPerson();
        case SmartEMFPackage.SHELF:
            return createShelf();
        default:
            throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
        }
    }

    public Book createBook() {
        BookImpl Book = new BookImpl();
        return Book;
    }

    public Library createLibrary() {
        LibraryImpl Library = new LibraryImpl();
        return Library;
    }

    public Person createPerson() {
        PersonImpl Person = new PersonImpl();
        return Person;
    }

    public Shelf createShelf() {
        ShelfImpl Shelf = new ShelfImpl();
        return Shelf;
    }

    public SmartEMFFactoryImpl() {
        super();
    }

    public SmartEMFPackage getSmartEMFPackage() {
        return (SmartEMFPackage) getEPackage();
    }

    public static SmartEMFFactory init() {
        try {
            SmartEMFFactory theSmartEMFFactory = (SmartEMFFactory) EPackage.Registry.INSTANCE.getEFactory(SmartEMFPackage.eNS_URI);
            if (theSmartEMFFactory != null) {
                return theSmartEMFFactory;
            }
        } catch (Exception exception) {
            EcorePlugin.INSTANCE.log(exception);
        }
        return new SmartEMFFactoryImpl();
    }

}
