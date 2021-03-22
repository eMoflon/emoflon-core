package smartEMF.impl;

import emfcodegenerator.notification.SmartEMFNotification;
import emfcodegenerator.util.SmartObject;
import emfcodegenerator.util.collections.LinkedESet;
import java.lang.String;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import smartEMF.Book;
import smartEMF.Person;
import smartEMF.SmartEMFPackage;

public class PersonImpl extends SmartObject implements Person {

    protected LinkedESet<Book> borrowed_books = new LinkedESet<Book>();
    protected String Name = null;

    protected PersonImpl(){
        super(SmartEMFPackage.Literals.PERSON);//regular constructor
    }

    protected PersonImpl(EClass type){
        super(type);//constructor for inheritance
    }

    public EList<Book> getBorrowed_books(){
        return (EList<Book>) this.borrowed_books;
    }

    public void setBorrowed_books(EList<Book> value){
        Object oldValue = this.borrowed_books;
        if(value instanceof LinkedESet){
            this.borrowed_books = (LinkedESet<Book>) value;
        } else {
            throw new IllegalArgumentException();
        }

        if (eNotificationRequired()) eNotify(SmartEMFNotification.set(this, smartEMF.SmartEMFPackage.Literals.PERSON_BORROWED_BOOKS, oldValue, value, -1));
    }

    public String getName(){
        return this.Name;
    }

    public void setName(String value){
        Object oldValue = this.Name;
        this.Name = value;

        if (eNotificationRequired()) eNotify(SmartEMFNotification.set(this, smartEMF.SmartEMFPackage.Literals.PERSON_NAME, oldValue, value, -1));
    }

    @Override
    public void eSet(int feautureID, Object newValue){
        switch(feautureID) {
            case SmartEMFPackage.PERSON__BORROWED_BOOKS:
                setBorrowed_books((LinkedESet<Book>) newValue);
            case SmartEMFPackage.PERSON__NAME:
                setName((String) newValue);
        }
        super.eSet(feautureID, newValue);
    }

    @Override
    public void eUnset(int feautureID){
        switch(feautureID) {
        }
        super.eUnset(feautureID);
    }

    @Override
    public boolean eIsSet(int feautureID){
        switch(feautureID) {
        }
        return super.eIsSet(feautureID);
    }

    @Override
    public String toString(){
        StringBuilder result = new StringBuilder(super.toString() + "(name: Person) ");
        result.append(" (");
        result.append("borrowed_books:");
        result.append(SmartObject.toStringIfNotNull(borrowed_books));
        result.append(", ");
        result.append("Name:");
        result.append(SmartObject.toStringIfNotNull(Name));
        result.append(")");
        return result.toString();
    }

    @Override
    public Object eGet(int feautureID, boolean resolve, boolean coreType){
        switch(feautureID) {
            case SmartEMFPackage.PERSON__BORROWED_BOOKS:
                 return getBorrowed_books();
            case SmartEMFPackage.PERSON__NAME:
                 return getName();
        }
        return super.eGet(feautureID, resolve, coreType);
    }

    @Override
    public Object eGet(EStructuralFeature eFeature){
        if (smartEMF.SmartEMFPackage.Literals.PERSON_BORROWED_BOOKS.equals(eFeature))
            return getBorrowed_books();
        if (smartEMF.SmartEMFPackage.Literals.PERSON_NAME.equals(eFeature))
            return getName();
        return super.eGet(eFeature);
    }

    @Override
    public void eSet(EStructuralFeature eFeature, Object newValue){
        if (smartEMF.SmartEMFPackage.Literals.PERSON_BORROWED_BOOKS.equals(eFeature))
            {setBorrowed_books((LinkedESet<Book>) newValue); return;}
        if (smartEMF.SmartEMFPackage.Literals.PERSON_NAME.equals(eFeature))
            {setName((String) newValue); return;}
        super.eSet(eFeature, newValue);
    }

}

