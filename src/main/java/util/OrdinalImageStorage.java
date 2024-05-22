package util;

import javafx.scene.image.Image;
import org.tinylog.Logger;
import util.javafx.ImageStorage;

import java.util.Optional;

/**
 * An implementation of the {@code ImageStorage} interface that associates
 * images with a consecutive sequence of integers starting from 0. The images
 * representing the integers are loaded via the classloader of the class
 * specified.
 */
public class OrdinalImageStorage implements ImageStorage<Integer> {

    private final Image[] images;

    /**
     * Creates a {@code OrdinalImageStorage} instance to associate the image
     * resources specified with the integers 0, 1, 2, &hellip;.
     *
     * @param c the class whose classloader is used to load the images
     * @param resourceNames the resource names of the images
     */
    public OrdinalImageStorage(Class<?> c, String... resourceNames) {
        images = new Image[resourceNames.length];
        for (var i = 0; i < resourceNames.length; i++) {
            var url = String.format("%s/%s", c.getPackageName().replace('.', '/'), resourceNames[i]);
            try {
                images[i] = new Image(url);
                Logger.debug("Loaded image from {}", url);
            } catch (Exception e) {
                // Failed to load image
                Logger.warn("Failed to load image from {}", url);
            }
        }
    }

    @Override
    public Optional<Image> get(Integer index) {
        return 0 <= index && index < images.length ? Optional.of(images[index]) : Optional.empty();
    }

}
