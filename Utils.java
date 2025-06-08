import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

class Utils {

    static BufferedImage[] divideImage(BufferedImage img) {
        int width = img.getWidth();
        int height = img.getHeight();

        WritableRaster inputRaster = img.getRaster();
        int rgb[] = new int[4];

        BufferedImage redImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster redRaster = redImg.getRaster();
        int red[] = new int[1];

        BufferedImage greenImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster greenRaster = greenImg.getRaster();
        int green[] = new int[1];

        BufferedImage blueImg = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster blueRaster = blueImg.getRaster();
        int blue[] = new int[1];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                inputRaster.getPixel(x, y, rgb);

                red[0] = rgb[0];
                green[0] = rgb[1];
                blue[0] = rgb[2];

                redRaster.setPixel(x, y, red);
                greenRaster.setPixel(x, y, green);
                blueRaster.setPixel(x, y, blue);
            }
        }

        if (Sequence.save) {
            ImageHandlers.saveImage(redImg, "red/grey");
            ImageHandlers.saveImage(greenImg, "green/grey");
            ImageHandlers.saveImage(blueImg, "blue/grey");
        }

        BufferedImage channels[] = { redImg, greenImg, blueImg };
        return channels;
    }

    static BufferedImage g2c(BufferedImage grey[], String name) {
        int width = grey[0].getWidth();
        int height = grey[0].getHeight();

        BufferedImage color = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster outputRaster = color.getRaster();

        int rgb[] = new int[3];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rgb[0] = grey[0].getRaster().getSample(x, y, 0);
                rgb[1] = grey[1].getRaster().getSample(x, y, 0);
                rgb[2] = grey[2].getRaster().getSample(x, y, 0);
                outputRaster.setPixel(x, y, rgb);
            }
        }

        ImageHandlers.saveImage(color, "./" + name);
        return color;
    }

    static BufferedImage g2b(BufferedImage greyImg, String path) {
        int w = greyImg.getWidth();
        int h = greyImg.getHeight();

        BufferedImage binImg = new BufferedImage(w * 8, h, BufferedImage.TYPE_BYTE_BINARY);
        WritableRaster inputRaster = greyImg.getRaster();
        WritableRaster outpuRaster = binImg.getRaster();

        int grey[] = new int[1];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                inputRaster.getPixel(x, y, grey);
                for (int i = 0; i < 8; i++) {
                    int bit = grey[0] >> (7 - i) & 1;
                    outpuRaster.setSample(x * 8 + i, y, 0, bit);
                }
            }
        }

        if (Sequence.save)
            ImageHandlers.saveImage(greyImg, path);
        return binImg;
    }

    static BufferedImage b2g(BufferedImage binImg, String path) {
        int w = binImg.getWidth() / 8;
        int h = binImg.getHeight();

        BufferedImage greyImg = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster inputRaster = binImg.getRaster();
        WritableRaster outputRaster = greyImg.getRaster();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int val = 0;
                for (int i = 0; i < 8; i++) {
                    int bit = inputRaster.getSample(x * 8 + i, y, 0);
                    val = (val << 1) | bit;
                }
                outputRaster.setSample(x, y, 0, val);
            }
        }

        if (Sequence.save)
            ImageHandlers.saveImage(greyImg, path);
        return greyImg;
    }

    static boolean areIdentical(BufferedImage img1, BufferedImage img2) {
        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        WritableRaster raster1 = img1.getRaster();
        WritableRaster raster2 = img2.getRaster();

        for (int y = 0; y < img1.getHeight(); y++) {
            for (int x = 0; x < img1.getWidth(); x++) {
                int val1 = raster1.getSample(x, y, 0);
                int val2 = raster2.getSample(x, y, 0);
                if (val1 != val2) {
                    return false;
                }
            }
        }
        return true;
    }
}