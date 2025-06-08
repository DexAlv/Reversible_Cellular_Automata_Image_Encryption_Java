import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class ImageEncryption {
    private static final int T = 20;

    private static int[][] i2m(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();

        int bits[][] = new int[h][w];
        WritableRaster inputRaster = img.getRaster();

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                bits[y][x] = inputRaster.getSample(x, y, 0);
            }
        }

        return bits;
    }

    private static BufferedImage m2i(int[][] imgBIts) {
        int height = imgBIts.length;
        int width = imgBIts[0].length;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        WritableRaster outpRaster = image.getRaster();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                outpRaster.setSample(x, y, 0, imgBIts[y][x]);
            }
        }

        return image;
    }

    static BufferedImage encrypt(BufferedImage binImg, String path) {
        int[][] bits = addPadding(i2m(binImg));

        int h = bits.length;
        int w = bits[0].length;

        BufferedImage encrypted = new BufferedImage(w, h, BufferedImage.TYPE_BYTE_BINARY);

        for (int y = 0; y < h - 1; y += 2) {
            for (int i = 0; i < T; i++) {
                int temp[] = new int[w - 1];
                for (int x = 1; x < w - 1; x++) {
                    temp[x - 1] = (bits[y + 1][x - 1] + bits[y + 1][x + 1] + bits[y][x]) % 2;
                }

                for (int x = 0; x < w; x++) {
                    bits[y][x] = bits[y + 1][x];
                }

                for (int x = 1; x < w - 1; x++) {
                    bits[y + 1][x] = temp[x - 1];
                }
            }
        }

        encrypted = m2i(bits);
        if (Sequence.save)
            ImageHandlers.saveImage(encrypted, path);
        return encrypted;
    }

    static BufferedImage decrypt(BufferedImage encrypted, String path) {

        int[][] bits = i2m(encrypted);

        int h = bits.length;
        int w = bits[0].length;
        int dBits[][] = new int[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                dBits[y][x] = bits[y][x];
            }
        }

        for (int y = 0; y < h - 1; y += 2) {
            for (int i = 0; i < T; i++) {
                int temp[] = new int[w - 1];
                for (int x = 1; x < w - 1; x++) {
                    temp[x - 1] = (dBits[y][x - 1] + dBits[y][x + 1] + dBits[y + 1][x]) % 2;
                }

                for (int x = 0; x < w; x++) {
                    dBits[y + 1][x] = dBits[y][x];
                }

                for (int x = 1; x < w - 1; x++) {
                    dBits[y][x] = temp[x - 1];
                }
            }
        }

        int[][] unpadded = removePadding(dBits);
        BufferedImage decryptedImage = m2i(unpadded);
        if (Sequence.save)
            ImageHandlers.saveImage(decryptedImage, path);
        return decryptedImage;
    }

    private static int[][] addPadding(int[][] bits) {
        int h = bits.length;
        int w = bits[0].length;
        int[][] padded = new int[h][w + 2];

        for (int y = 0; y < h; y++) {
            padded[y][0] = 0;
            for (int x = 0; x < w; x++) {
                padded[y][x + 1] = bits[y][x];
            }
            padded[y][w + 1] = 0;
        }

        return padded;
    }

    private static int[][] removePadding(int[][] paddedBits) {
        int h = paddedBits.length;
        int w = paddedBits[0].length;
        int[][] bits = new int[h][w - 2];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w - 2; x++) {
                bits[y][x] = paddedBits[y][x + 1];
            }
        }
        return bits;
    }
}
