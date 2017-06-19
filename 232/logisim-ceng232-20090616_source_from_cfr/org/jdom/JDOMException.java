/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.sql.SQLException;
import org.xml.sax.SAXException;

public class JDOMException
extends Exception {
    private static final String CVS_ID = "@(#) $RCSfile: JDOMException.java,v $ $Revision: 1.23 $ $Date: 2004/02/27 11:32:57 $ $Name: jdom_1_0 $";
    private Throwable cause;

    public JDOMException() {
        super("Error occurred in JDOM application.");
    }

    public JDOMException(String message) {
        super(message);
    }

    public JDOMException(String message, Throwable cause) {
        super(message);
        this.cause = cause;
    }

    public Throwable getCause() {
        return this.cause;
    }

    public String getMessage() {
        Throwable child;
        String msg = super.getMessage();
        Throwable parent = this;
        while ((child = JDOMException.getNestedException(parent)) != null) {
            Exception grandchild;
            String msg2 = child.getMessage();
            if (child instanceof SAXException && (grandchild = ((SAXException)child).getException()) != null && msg2 != null && msg2.equals(grandchild.getMessage())) {
                msg2 = null;
            }
            if (msg2 != null) {
                msg = msg != null ? String.valueOf(msg) + ": " + msg2 : msg2;
            }
            if (child instanceof JDOMException) break;
            parent = child;
        }
        return msg;
    }

    private static Throwable getNestedException(Throwable parent) {
        if (parent instanceof JDOMException) {
            return ((JDOMException)parent).getCause();
        }
        if (parent instanceof SAXException) {
            return ((SAXException)parent).getException();
        }
        if (parent instanceof SQLException) {
            return ((SQLException)parent).getNextException();
        }
        if (parent instanceof InvocationTargetException) {
            return ((InvocationTargetException)parent).getTargetException();
        }
        if (parent instanceof ExceptionInInitializerError) {
            return ((ExceptionInInitializerError)parent).getException();
        }
        if (parent instanceof RemoteException) {
            return ((RemoteException)parent).detail;
        }
        Throwable nestedException = JDOMException.getNestedException(parent, "javax.naming.NamingException", "getRootCause");
        if (nestedException != null) {
            return nestedException;
        }
        nestedException = JDOMException.getNestedException(parent, "javax.servlet.ServletException", "getRootCause");
        if (nestedException != null) {
            return nestedException;
        }
        return null;
    }

    private static Throwable getNestedException(Throwable parent, String className, String methodName) {
        try {
            Class testClass = Class.forName(className);
            Class objectClass = parent.getClass();
            if (testClass.isAssignableFrom(objectClass)) {
                Class[] argClasses = new Class[]{};
                Method method = testClass.getMethod(methodName, argClasses);
                Object[] args = new Object[]{};
                return (Throwable)method.invoke(parent, args);
            }
        }
        catch (Exception v0) {}
        return null;
    }

    public Throwable initCause(Throwable cause) {
        this.cause = cause;
        return this;
    }

    public void printStackTrace() {
        Throwable child;
        super.printStackTrace();
        Throwable parent = this;
        while ((child = JDOMException.getNestedException(parent)) != null) {
            System.err.print("Caused by: ");
            child.printStackTrace();
            if (child instanceof JDOMException) break;
            parent = child;
        }
    }

    public void printStackTrace(PrintStream s) {
        Throwable child;
        super.printStackTrace(s);
        Throwable parent = this;
        while ((child = JDOMException.getNestedException(parent)) != null) {
            s.print("Caused by: ");
            child.printStackTrace(s);
            if (child instanceof JDOMException) break;
            parent = child;
        }
    }

    public void printStackTrace(PrintWriter w) {
        Throwable child;
        super.printStackTrace(w);
        Throwable parent = this;
        while ((child = JDOMException.getNestedException(parent)) != null) {
            w.print("Caused by: ");
            child.printStackTrace(w);
            if (child instanceof JDOMException) break;
            parent = child;
        }
    }
}

