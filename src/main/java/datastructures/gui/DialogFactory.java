package datastructures.gui;

import ij.gui.GenericDialog;

public class DialogFactory {

    public static GenericDialog MakeWarningDialog(String message) {
        GenericDialog gd = new GenericDialog("Warning");
        gd.addMessage(message);
        gd.hideCancelButton();

        return gd;
    }

}
