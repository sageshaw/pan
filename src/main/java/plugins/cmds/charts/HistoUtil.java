package plugins.cmds.charts;

import java.awt.*;

public class HistoUtil {

    private static final Dimension screenDims;
    public static final Dimension PREVIEW_DIMENSIONS;

    static {
        screenDims = Toolkit.getDefaultToolkit().getScreenSize();
        PREVIEW_DIMENSIONS = new Dimension((int) screenDims.getWidth() / 2, (int) screenDims.getHeight() / 2);
    }



    public static final Font HEADER_FONT = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    public static final Font PARAGRAPH_FONT = new Font(Font.SANS_SERIF, Font.PLAIN, 20);
}
