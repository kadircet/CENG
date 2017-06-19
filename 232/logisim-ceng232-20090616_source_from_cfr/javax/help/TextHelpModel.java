/*
 * Decompiled with CFR 0_114.
 */
package javax.help;

import javax.help.HelpModel;
import javax.help.event.TextHelpModelListener;

public interface TextHelpModel
extends HelpModel {
    public String getDocumentTitle();

    public void setDocumentTitle(String var1);

    public void removeAllHighlights();

    public void addHighlight(int var1, int var2);

    public void setHighlights(Highlight[] var1);

    public Highlight[] getHighlights();

    public void addTextHelpModelListener(TextHelpModelListener var1);

    public void removeTextHelpModelListener(TextHelpModelListener var1);

    public static interface Highlight {
        public int getStartOffset();

        public int getEndOffset();
    }

}

