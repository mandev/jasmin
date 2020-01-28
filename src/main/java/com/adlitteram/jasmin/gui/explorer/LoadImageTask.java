package com.adlitteram.jasmin.gui.explorer;

import com.adlitteram.jasmin.image.ImageInfo;
import com.adlitteram.jasmin.image.ImageUtils;
import com.adlitteram.jasmin.image.ReadParam;
import com.adlitteram.jasmin.image.XImage;
import com.adlitteram.jasmin.image.ImageTool;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadImageTask extends SwingWorker<XImage, Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoadImageTask.class);

    private final int maxSize;
    private final ExplorerModel explorerModel;
    private final int index;
    private final ImageFile imageFile;

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LoadImageTask other = (LoadImageTask) obj;
        return this.index == other.index;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + index;
        return hash;
    }

    public LoadImageTask(ExplorerModel explorerModel, int index, ImageFile imageFile, int maxSize) {
        this.explorerModel = explorerModel;
        this.index = index;
        this.imageFile = imageFile;
        this.maxSize = maxSize;
    }

    private int getSubsampling(int w, int h) {
        float s = Math.max(((float) w / (float) maxSize), ((float) h / (float) maxSize));
        return Math.max(1, (int) s);
    }

    @Override
    public XImage doInBackground() {

        XImage ximg = null;
        if (!imageFile.checkCompleted()) {
            ImageInfo info = ImageTool.readImageInfo(imageFile.getFile());
            if (info != null) {
                int subSampling = getSubsampling(info.getWidth(), info.getHeight());
                ximg = ImageTool.readXImage(imageFile.getFile(), new ReadParam(subSampling));
                if (ximg != null) {
                    if (ximg.isEmpty()) {
                        ximg = null;
                    }
                    else if (ximg.getSubsampling() > 1 || ximg.getImage().getWidth() > maxSize || ximg.getImage().getHeight() > maxSize) {
                        BufferedImage bi = ImageUtils.getScaledRGBImage(ximg.getImage(), maxSize, maxSize, true);
                        ximg = XImage.createXImage(bi, ximg);
                    }
                }
                if (ximg == null) {
                    ximg = XImage.createErrorXImage(imageFile.getFile().getPath(), maxSize, maxSize);
                }
            }
        }
        return ximg;
    }

    @Override
    protected void done() {
        try {
            XImage ximg = get();
            if (ximg != null) {
                imageFile.setFormat(ximg.getFormat());
                imageFile.setHeight(ximg.getSourceHeight());
                imageFile.setWidth(ximg.getSourceWidth());
                imageFile.setImage(ximg.getImage());
                explorerModel.fireFileUpdated(index, index);
            }
        }
        catch (InterruptedException | ExecutionException ex) {
            LOGGER.warn("", ex);
        }
    }
}
