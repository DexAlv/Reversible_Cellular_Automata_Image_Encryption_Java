import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.BiFunction;

public class Sequence {

    private static ExecutorService executor = Executors.newFixedThreadPool(3);
    private final static String[] labels = { "red", "green", "blue" };

    public final static boolean save = false;

    private static BufferedImage[] execute(BufferedImage[] input, String type,
            BiFunction<BufferedImage, String, BufferedImage> function) {
        BufferedImage[] output = new BufferedImage[3];

        try {
            Future<BufferedImage>[] futures = new Future[3];
            for (int i = 0; i < 3; i++) {
                final BufferedImage img = input[i];
                final String label = labels[i];
                futures[i] = executor.submit(() -> function.apply(img, label + "/" + type));
            }
            for (int i = 0; i < 3; i++) {
                output[i] = futures[i].get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return null;
        }

        return output;
    }

    public static void main(String[] args) {
        long start = System.nanoTime();
        if (save) {
            for (String label : labels)
                new File(label).mkdirs();
        }

        BufferedImage img = ImageHandlers.loadImage("samples/mototaxi.png");
        if (img == null)
            return;

        BufferedImage grey[] = Utils.divideImage(img);

        BufferedImage bin[] = execute(grey, "bin", Utils::g2b);

        BufferedImage enc[] = execute(bin, "enc", ImageEncryption::encrypt);

        BufferedImage redu[] = execute(enc, "reduced", Utils::b2g);

        BufferedImage reduMixed = Utils.g2c(redu, "reduced");
        if (reduMixed == null)
            return;

        BufferedImage dec[] = execute(enc, "dec", ImageEncryption::decrypt);

        BufferedImage res[] = execute(dec, "res", Utils::b2g);

        BufferedImage fin = Utils.g2c(res, "final");

        System.out.println(Utils.areIdentical(img, fin));

        executor.shutdown();

        long end = System.nanoTime();
        long durationNano = end - start;
        double durationSeconds = durationNano / 1_000_000_000.0;

        System.out.printf("%d x %d: Elapsed -> %f s\n", img.getWidth(), img.getHeight(), durationSeconds);

    }
}
