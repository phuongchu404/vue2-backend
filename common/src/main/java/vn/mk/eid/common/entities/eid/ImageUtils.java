package vn.mk.eid.common.entities.eid;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageUtils {
    public static byte[] imageToPng(byte[] input) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(input);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        BufferedImage read = ImageIO.read(byteArrayInputStream);
        ImageIO.write(read, "PNG", byteArrayOutputStream);
        byte[] out = byteArrayOutputStream.toByteArray();
        byteArrayInputStream.close();
        byteArrayOutputStream.close();
        return out;
    }

    public static byte[] imageToPdf(byte[] input) throws IOException {
        ByteArrayOutputStream baos  = new ByteArrayOutputStream(input.length);
        baos.write(input);
        byte[] out = baos.toByteArray();
        baos.close();
        return out;
    }

}
