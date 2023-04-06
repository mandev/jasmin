package com.adlitteram.jasmin.image.icc;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RasterOp;
import java.awt.image.WritableRaster;

public class IccColorConvertOp implements BufferedImageOp, RasterOp {

    private static final RenderingHints hints;

    static {
        hints = new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    private final ColorConvertOp op;

    public IccColorConvertOp() {
        this(hints);
    }

    public IccColorConvertOp(RenderingHints hints) {
        op = new ColorConvertOp(hints);
    }

    public IccColorConvertOp(ColorSpace cspace, RenderingHints hints) {
        op = new ColorConvertOp(cspace, hints);
    }

    public IccColorConvertOp(ColorSpace srcCspace, ColorSpace dstCspace, RenderingHints hints) {
        op = new ColorConvertOp(srcCspace, dstCspace, hints);
    }

    public IccColorConvertOp(ICC_Profile[] profiles, RenderingHints hints) {
        op = new ColorConvertOp(profiles, hints);
    }

    @Override
    public BufferedImage filter(BufferedImage src, BufferedImage dest) {
        return ((BufferedImageOp) op).filter(src, dest);
    }

    @Override
    public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
        return ((BufferedImageOp) op).createCompatibleDestImage(src, destCM);
    }

    @Override
    public final Rectangle2D getBounds2D(BufferedImage src) {
        return ((BufferedImageOp) op).getBounds2D(src);
    }

    @Override
    public WritableRaster filter(Raster src, WritableRaster dest) {
        return ((RasterOp) op).filter(src, dest);
    }

    @Override
    public WritableRaster createCompatibleDestRaster(Raster src) {
        return ((RasterOp) op).createCompatibleDestRaster(src);
    }

    @Override
    public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
        return ((RasterOp) op).getPoint2D(srcPt, dstPt);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return ((RasterOp) op).getRenderingHints();
    }

    @Override
    public final Rectangle2D getBounds2D(Raster src) {
        return ((RasterOp) op).getBounds2D(src);
    }
}
