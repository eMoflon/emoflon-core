package smartEMF.impl;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.impl.EPackageImpl;
import smartEMF.Book;
import smartEMF.Library;
import smartEMF.Person;
import smartEMF.Shelf;
import smartEMF.SmartEMFFactory;
import smartEMF.SmartEMFPackage;

public class SmartEMFPackageImpl extends EPackageImpl implements SmartEMFPackage {

    private EClass BookEClass = null;
    private EClass ShelfEClass = null;
    private EClass LibraryEClass = null;
    private static boolean isInited = false;
    private boolean isCreated = false;
    private boolean isInitialized = false;
    private EClass PersonEClass = null;

    private SmartEMFPackageImpl() { 
        super(eNS_URI, SmartEMFFactory.eINSTANCE);
    }

    public EClass getBook() { 
        return BookEClass;
    }

    public EAttribute getBook_Name() { 
        return (EAttribute) this.BookEClass.getEStructuralFeatures().get(0);
    }

    public EAttribute getBook_ID() { 
        return (EAttribute) this.BookEClass.getEStructuralFeatures().get(1);
    }

    public EClass getLibrary() { 
        return LibraryEClass;
    }

    public EAttribute getLibrary_Name() { 
        return (EAttribute) this.LibraryEClass.getEStructuralFeatures().get(0);
    }

    public EReference getLibrary_Contained_shelves() { 
        return (EReference) this.LibraryEClass.getEStructuralFeatures().get(1);
    }

    public EReference getLibrary_Library_users() { 
        return (EReference) this.LibraryEClass.getEStructuralFeatures().get(2);
    }

    public EClass getPerson() { 
        return PersonEClass;
    }

    public EAttribute getPerson_Name() { 
        return (EAttribute) this.PersonEClass.getEStructuralFeatures().get(0);
    }

    public EReference getPerson_Borrowed_books() { 
        return (EReference) this.PersonEClass.getEStructuralFeatures().get(1);
    }

    public EClass getShelf() { 
        return ShelfEClass;
    }

    public EAttribute getShelf_Shelf_no() { 
        return (EAttribute) this.ShelfEClass.getEStructuralFeatures().get(0);
    }

    public EReference getShelf_Contained_books() { 
        return (EReference) this.ShelfEClass.getEStructuralFeatures().get(1);
    }

    public static SmartEMFPackage init() { 
        if (isInited) return (SmartEMFPackage) EPackage.Registry.INSTANCE.getEPackage(SmartEMFPackage.eNS_URI);
        Object registered_SmartEMFPackage = EPackage.Registry.INSTANCE.getEPackage(SmartEMFPackage.eNS_URI);
        SmartEMFPackageImpl the_SmartEMFPackage = (SmartEMFPackageImpl) ((registered_SmartEMFPackage instanceof SmartEMFPackageImpl) ? registered_SmartEMFPackage : new SmartEMFPackageImpl());
        isInited = true;
        the_SmartEMFPackage.createPackageContents();
        the_SmartEMFPackage.initializePackageContents();
        EPackage.Registry.INSTANCE.put(SmartEMFPackage.eNS_URI, the_SmartEMFPackage);
        return the_SmartEMFPackage;
    }

    public void createPackageContents() { 
        if (this.isCreated) return;
        this.isCreated = true;
        BookEClass = this.createEClass(BOOK);
        this.createEAttribute(BookEClass, SmartEMFPackage.BOOK__NAME);
        this.createEAttribute(BookEClass, SmartEMFPackage.BOOK__I_D);
        LibraryEClass = this.createEClass(LIBRARY);
        this.createEAttribute(LibraryEClass, SmartEMFPackage.LIBRARY__NAME);
        this.createEReference(LibraryEClass, SmartEMFPackage.LIBRARY__CONTAINED_SHELVES);
        this.createEReference(LibraryEClass, SmartEMFPackage.LIBRARY__LIBRARY_USERS);
        PersonEClass = this.createEClass(PERSON);
        this.createEAttribute(PersonEClass, SmartEMFPackage.PERSON__NAME);
        this.createEReference(PersonEClass, SmartEMFPackage.PERSON__BORROWED_BOOKS);
        ShelfEClass = this.createEClass(SHELF);
        this.createEAttribute(ShelfEClass, SmartEMFPackage.SHELF__SHELF_NO);
        this.createEReference(ShelfEClass, SmartEMFPackage.SHELF__CONTAINED_BOOKS);
    }

    public void initializePackageContents() { 
        if (this.isInitialized) return;
        isInitialized = true;
        setName(SmartEMFPackage.eNAME);
        setNsPrefix(SmartEMFPackage.eNS_PREFIX);
        setNsURI(SmartEMFPackage.eNS_URI);
        
        //Obtain other dependent packages
        SmartEMFPackage theSmartEMFPackage = (SmartEMFPackage) EPackage.Registry.INSTANCE.getEPackage(SmartEMFPackage.eNS_URI);
        
        //add sub-packages
        
        //Create type parameters for all EClasses
        //Set Bounds for the type parameters
        
        //Create type parameters for all EDataTypes
         //Set Bounds for the type parameters
        
        //init the EClasses
        //init the EClass Book
        initEClass(BookEClass, Book.class, "Book", false, false, true);
        //EStructuralFeatures for EClass Book
        initEAttribute(getBook_Name(), ecorePackage.getEString(), "name", null, 0, 1, Book.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEAttribute(getBook_ID(), ecorePackage.getEInt(), "ID", null, 0, 1, Book.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //init the EClass Library
        initEClass(LibraryEClass, Library.class, "Library", false, false, true);
        //EStructuralFeatures for EClass Library
        initEAttribute(getLibrary_Name(), ecorePackage.getEString(), "Name", null, 0, 1, Library.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getLibrary_Contained_shelves(), this.getShelf(), null, "contained_shelves", null, 0, -1, Library.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getLibrary_Library_users(), this.getPerson(), null, "library_users", null, 0, -1, Library.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //init the EClass Person
        initEClass(PersonEClass, Person.class, "Person", false, false, true);
        //EStructuralFeatures for EClass Person
        initEAttribute(getPerson_Name(), ecorePackage.getEString(), "Name", null, 0, 1, Person.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getPerson_Borrowed_books(), this.getBook(), null, "borrowed_books", null, 0, 5, Person.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        //init the EClass Shelf
        initEClass(ShelfEClass, Shelf.class, "Shelf", false, false, true);
        //EStructuralFeatures for EClass Shelf
        initEAttribute(getShelf_Shelf_no(), ecorePackage.getEIntegerObject(), "shelf_no", null, 0, 1, Shelf.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        initEReference(getShelf_Contained_books(), this.getBook(), null, "contained_books", null, 0, -1, Shelf.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED);
        createResource(this.eNS_URI);
    }

}
