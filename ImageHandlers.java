import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class ImageHandlers {

    static BufferedImage loadImage(String path) {
        BufferedImage img = null;
        try {
            img = ImageIO.read(new File(path));
        } catch (Exception e) {
            System.out.println("Couldn't read image: " + path);
            e.printStackTrace();
            return null;
        }
        return img;
    }

    static void saveImage(BufferedImage img, String path) {
        path += ".png";
        try {
            ImageIO.write(img, "png", new File(path));
        } catch (Exception e) {
            System.out.println("Couldn't save image: " + path);
            e.printStackTrace();
            return;
        }
    }
}
