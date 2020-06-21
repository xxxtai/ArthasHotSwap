package com.xxxtai.arthas.utils;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardUtils {

    public static String getClipboardString() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = clipboard.getContents(null);

        if (trans != null) {
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    return (String) trans.getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) {
                    //
                }
            }
        }
        return null;
    }

    public static void setClipboardString(String text) {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable trans = new StringSelection(text);
            clipboard.setContents(trans, null);
        } catch (Exception e) {
            //
        }
    }

}
