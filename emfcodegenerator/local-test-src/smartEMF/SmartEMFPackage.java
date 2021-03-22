package smartEMF;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

public interface SmartEMFPackage extends EPackage {
    String eNAME = "smartEMF";
    String eNS_URI = "http://www.example.org/smartEMF";
    String eNS_PREFIX = "smartEMF";
    SmartEMFPackage eINSTANCE = smartEMF.impl.SmartEMFPackageImpl.init();
    int BOOK = 0;
    int BOOK__I_D = 0;
    int BOOK__NAME = 1;
    int BOOK_FEATURE_COUNT = 2;
    int BOOK_OPERATION_COUNT = 0;
    int LIBRARY = 1;
    int LIBRARY__NAME = 0;
    int LIBRARY__LIBRARY_USERS = 1;
    int LIBRARY__CONTAINED_SHELVES = 2;
    int LIBRARY_FEATURE_COUNT = 3;
    int LIBRARY_OPERATION_COUNT = 0;
    int PERSON = 2;
    int PERSON__BORROWED_BOOKS = 0;
    int PERSON__NAME = 1;
    int PERSON_FEATURE_COUNT = 2;
    int PERSON_OPERATION_COUNT = 0;
    int SHELF = 3;
    int SHELF__CONTAINED_BOOKS = 0;
    int SHELF__SHELF_NO = 1;
    int SHELF_FEATURE_COUNT = 2;
    int SHELF_OPERATION_COUNT = 0;
    EClass getBook();
    EAttribute getBook_ID();
    EAttribute getBook_Name();
    EClass getLibrary();
    EAttribute getLibrary_Name();
    EReference getLibrary_Library_users();
    EReference getLibrary_Contained_shelves();
    EClass getPerson();
    EReference getPerson_Borrowed_books();
    EAttribute getPerson_Name();
    EClass getShelf();
    EReference getShelf_Contained_books();
    EAttribute getShelf_Shelf_no();
    interface Literals {
        EClass BOOK = eINSTANCE.getBook();
        EAttribute BOOK_I_D = eINSTANCE.getBook_ID();
        EAttribute BOOK_NAME = eINSTANCE.getBook_Name();
        EClass LIBRARY = eINSTANCE.getLibrary();
        EAttribute LIBRARY_NAME = eINSTANCE.getLibrary_Name();
        EReference LIBRARY_LIBRARY_USERS = eINSTANCE.getLibrary_Library_users();
        EReference LIBRARY_CONTAINED_SHELVES = eINSTANCE.getLibrary_Contained_shelves();
        EClass PERSON = eINSTANCE.getPerson();
        EReference PERSON_BORROWED_BOOKS = eINSTANCE.getPerson_Borrowed_books();
        EAttribute PERSON_NAME = eINSTANCE.getPerson_Name();
        EClass SHELF = eINSTANCE.getShelf();
        EReference SHELF_CONTAINED_BOOKS = eINSTANCE.getShelf_Contained_books();
        EAttribute SHELF_SHELF_NO = eINSTANCE.getShelf_Shelf_no();
    }
}

