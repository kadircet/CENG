/*
 * Decompiled with CFR 0_114.
 */
package com.sun.java.help.impl;

import com.sun.java.help.impl.DocumentParser;
import com.sun.java.help.impl.ParserEvent;
import com.sun.java.help.impl.ParserListener;
import com.sun.java.help.impl.Tag;
import com.sun.java.help.impl.TagProperties;
import java.io.InputStream;
import java.io.Reader;
import java.util.EventListener;

public class Parser
extends DocumentParser {
    protected ParserListener listenerList;

    public Parser(Reader reader) {
        super(reader);
    }

    public Parser(InputStream inputStream) {
        super(inputStream);
    }

    protected void tag(String string, TagProperties tagProperties, boolean bl, boolean bl2) {
        Tag tag = new Tag(string, tagProperties, bl, bl2);
        this.listenerList.tagFound(new ParserEvent((Object)this, tag));
    }

    protected void pi(String string, String string2) {
        this.listenerList.piFound(new ParserEvent(this, string, string2));
    }

    protected void doctype(String string, String string2, String string3) {
        this.listenerList.doctypeFound(new ParserEvent(this, string, string2, string3));
    }

    protected void flush(char[] arrc, int n, int n2) {
        if (n2 == 1 && (arrc[n] == '\n' || arrc[n] == '\r')) {
            return;
        }
        String string = new String(arrc, n, n2);
        this.listenerList.textFound(new ParserEvent((Object)this, string));
    }

    protected void comment(String string) {
        this.listenerList.commentFound(new ParserEvent((Object)this, string));
    }

    protected void errorString(String string) {
        this.listenerList.errorFound(new ParserEvent((Object)this, string));
    }

    protected String documentAttribute(String string) {
        return null;
    }

    public void addParserListener(ParserListener parserListener) {
        this.listenerList = ParserMulticaster.add(this.listenerList, parserListener);
    }

    public void removeParserListener(ParserListener parserListener) {
        this.listenerList = ParserMulticaster.remove(this.listenerList, parserListener);
    }

    protected static class ParserMulticaster
    implements ParserListener {
        protected final EventListener a;
        protected final EventListener b;

        protected ParserMulticaster(EventListener eventListener, EventListener eventListener2) {
            this.a = eventListener;
            this.b = eventListener2;
        }

        protected EventListener remove(EventListener eventListener) {
            if (eventListener == this.a) {
                return this.b;
            }
            if (eventListener == this.b) {
                return this.a;
            }
            EventListener eventListener2 = ParserMulticaster.removeInternal(this.a, eventListener);
            EventListener eventListener3 = ParserMulticaster.removeInternal(this.b, eventListener);
            if (eventListener2 == this.a && eventListener3 == this.b) {
                return this;
            }
            return ParserMulticaster.addInternal(eventListener2, eventListener3);
        }

        protected static EventListener addInternal(EventListener eventListener, EventListener eventListener2) {
            if (eventListener == null) {
                return eventListener2;
            }
            if (eventListener2 == null) {
                return eventListener;
            }
            return new ParserMulticaster(eventListener, eventListener2);
        }

        protected static EventListener removeInternal(EventListener eventListener, EventListener eventListener2) {
            if (eventListener == eventListener2 || eventListener == null) {
                return null;
            }
            if (eventListener instanceof ParserMulticaster) {
                return ((ParserMulticaster)eventListener).remove(eventListener2);
            }
            return eventListener;
        }

        public void tagFound(ParserEvent parserEvent) {
            ((ParserListener)this.a).tagFound(parserEvent);
            ((ParserListener)this.b).tagFound(parserEvent);
        }

        public void piFound(ParserEvent parserEvent) {
            ((ParserListener)this.a).piFound(parserEvent);
            ((ParserListener)this.b).piFound(parserEvent);
        }

        public void doctypeFound(ParserEvent parserEvent) {
            ((ParserListener)this.a).doctypeFound(parserEvent);
            ((ParserListener)this.b).doctypeFound(parserEvent);
        }

        public void textFound(ParserEvent parserEvent) {
            ((ParserListener)this.a).textFound(parserEvent);
            ((ParserListener)this.b).textFound(parserEvent);
        }

        public void commentFound(ParserEvent parserEvent) {
            ((ParserListener)this.a).commentFound(parserEvent);
            ((ParserListener)this.b).commentFound(parserEvent);
        }

        public void errorFound(ParserEvent parserEvent) {
            ((ParserListener)this.a).errorFound(parserEvent);
            ((ParserListener)this.b).errorFound(parserEvent);
        }

        public static ParserListener add(ParserListener parserListener, ParserListener parserListener2) {
            return (ParserListener)ParserMulticaster.addInternal(parserListener, parserListener2);
        }

        public static ParserListener remove(ParserListener parserListener, ParserListener parserListener2) {
            return (ParserListener)ParserMulticaster.removeInternal(parserListener, parserListener2);
        }
    }

}

