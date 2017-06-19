/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.jdom.DataConversionException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.IllegalDataException;
import org.jdom.IllegalNameException;
import org.jdom.Namespace;
import org.jdom.Verifier;

public class Attribute
implements Serializable,
Cloneable {
    private static final String CVS_ID = "@(#) $RCSfile: Attribute.java,v $ $Revision: 1.52 $ $Date: 2004/03/01 23:58:28 $ $Name: jdom_1_0 $";
    public static final int UNDECLARED_TYPE = 0;
    public static final int CDATA_TYPE = 1;
    public static final int ID_TYPE = 2;
    public static final int IDREF_TYPE = 3;
    public static final int IDREFS_TYPE = 4;
    public static final int ENTITY_TYPE = 5;
    public static final int ENTITIES_TYPE = 6;
    public static final int NMTOKEN_TYPE = 7;
    public static final int NMTOKENS_TYPE = 8;
    public static final int NOTATION_TYPE = 9;
    public static final int ENUMERATED_TYPE = 10;
    protected String name;
    protected transient Namespace namespace;
    protected String value;
    protected int type = 0;
    protected Object parent;

    protected Attribute() {
    }

    public Attribute(String name, String value) {
        this(name, value, 0, Namespace.NO_NAMESPACE);
    }

    public Attribute(String name, String value, int type) {
        this(name, value, type, Namespace.NO_NAMESPACE);
    }

    public Attribute(String name, String value, int type, Namespace namespace) {
        this.setName(name);
        this.setValue(value);
        this.setAttributeType(type);
        this.setNamespace(namespace);
    }

    public Attribute(String name, String value, Namespace namespace) {
        this.setName(name);
        this.setValue(value);
        this.setNamespace(namespace);
    }

    public Object clone() {
        Attribute attribute;
        attribute = null;
        try {
            attribute = (Attribute)super.clone();
        }
        catch (CloneNotSupportedException v0) {}
        attribute.parent = null;
        return attribute;
    }

    public Attribute detach() {
        Element p = this.getParent();
        if (p != null) {
            p.removeAttribute(this.getName(), this.getNamespace());
        }
        return this;
    }

    public final boolean equals(Object ob) {
        return ob == this;
    }

    public int getAttributeType() {
        return this.type;
    }

    public boolean getBooleanValue() throws DataConversionException {
        String valueTrim = this.value.trim();
        if (valueTrim.equalsIgnoreCase("true") || valueTrim.equalsIgnoreCase("on") || valueTrim.equalsIgnoreCase("1") || valueTrim.equalsIgnoreCase("yes")) {
            return true;
        }
        if (valueTrim.equalsIgnoreCase("false") || valueTrim.equalsIgnoreCase("off") || valueTrim.equalsIgnoreCase("0") || valueTrim.equalsIgnoreCase("no")) {
            return false;
        }
        throw new DataConversionException(this.name, "boolean");
    }

    public Document getDocument() {
        if (this.parent != null) {
            return ((Element)this.parent).getDocument();
        }
        return null;
    }

    public double getDoubleValue() throws DataConversionException {
        try {
            return Double.valueOf(this.value.trim());
        }
        catch (NumberFormatException v0) {
            throw new DataConversionException(this.name, "double");
        }
    }

    public float getFloatValue() throws DataConversionException {
        try {
            return Float.valueOf(this.value.trim()).floatValue();
        }
        catch (NumberFormatException v0) {
            throw new DataConversionException(this.name, "float");
        }
    }

    public int getIntValue() throws DataConversionException {
        try {
            return Integer.parseInt(this.value.trim());
        }
        catch (NumberFormatException v0) {
            throw new DataConversionException(this.name, "int");
        }
    }

    public long getLongValue() throws DataConversionException {
        try {
            return Long.parseLong(this.value.trim());
        }
        catch (NumberFormatException v0) {
            throw new DataConversionException(this.name, "long");
        }
    }

    public String getName() {
        return this.name;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public String getNamespacePrefix() {
        return this.namespace.getPrefix();
    }

    public String getNamespaceURI() {
        return this.namespace.getURI();
    }

    public Element getParent() {
        return (Element)this.parent;
    }

    public String getQualifiedName() {
        String prefix = this.namespace.getPrefix();
        if (prefix != null && !prefix.equals("")) {
            return prefix + ':' + this.getName();
        }
        return this.getName();
    }

    public String getValue() {
        return this.value;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.namespace = Namespace.getNamespace((String)in.readObject(), (String)in.readObject());
    }

    public Attribute setAttributeType(int type) {
        if (type < 0 || type > 10) {
            throw new IllegalDataException(String.valueOf(type), "attribute", "Illegal attribute type");
        }
        this.type = type;
        return this;
    }

    public Attribute setName(String name) {
        String reason = Verifier.checkAttributeName(name);
        if (reason != null) {
            throw new IllegalNameException(name, "attribute", reason);
        }
        this.name = name;
        return this;
    }

    public Attribute setNamespace(Namespace namespace) {
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        if (namespace != Namespace.NO_NAMESPACE && namespace.getPrefix().equals("")) {
            throw new IllegalNameException("", "attribute namespace", "An attribute namespace without a prefix can only be the NO_NAMESPACE namespace");
        }
        this.namespace = namespace;
        return this;
    }

    protected Attribute setParent(Element parent) {
        this.parent = parent;
        return this;
    }

    public Attribute setValue(String value) {
        String reason = null;
        reason = Verifier.checkCharacterData(value);
        if (reason != null) {
            throw new IllegalDataException(value, "attribute", reason);
        }
        this.value = value;
        return this;
    }

    public String toString() {
        return "[Attribute: " + this.getQualifiedName() + "=\"" + this.value + "\"" + "]";
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.namespace.getPrefix());
        out.writeObject(this.namespace.getURI());
    }
}

