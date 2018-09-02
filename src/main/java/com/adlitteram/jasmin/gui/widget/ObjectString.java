
package com.adlitteram.jasmin.gui.widget;

public class ObjectString<T> {

    private T object;
    private String label;

    public ObjectString(T obj, String str) {
        object = obj;
        label = str;
    }

    @Override
    public String toString() {
        return label;
    }

    public String getLabel() {
        return label;
    }

    public T getObject() {
        return object;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;

        if (object == null) {
            return (obj == null);
        }

        if (obj instanceof ObjectString) {
            ObjectString objstr = (ObjectString) obj;
            return (object.equals(objstr.getObject()));
        }

        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.object != null ? this.object.hashCode() : 0);
        hash = 79 * hash + (this.label != null ? this.label.hashCode() : 0);
        return hash;
    }
}
