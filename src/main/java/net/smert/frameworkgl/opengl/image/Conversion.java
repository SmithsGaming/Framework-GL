/**
 * Copyright 2012 Jason Sorensen (sorensenj@smert.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package net.smert.frameworkgl.opengl.image;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 *
 * @author Jason Sorensen <sorensenj@smert.net>
 */
public class Conversion {

    public static int[] ConvertByteArrayRGBAToARGBIntArray(byte[] RGBA, int width, int height) {
        int[] intARGB = new int[width * height];

        for (int i = 0; i < intARGB.length; i++) {
            int stride = i * 4;

            intARGB[i]
                    = (RGBA[stride + 3] << 24) & 0xff000000
                    | (RGBA[stride + 0] << 16) & 0x00ff0000
                    | (RGBA[stride + 1] << 8) & 0x0000ff00
                    | (RGBA[stride + 2]) & 0x000000ff;
        }

        return intARGB;
    }

    public static int[] ConvertByteArrayRGBToARGBIntArray(byte[] RGB, int width, int height) {
        int[] intARGB = new int[width * height];

        for (int i = 0; i < intARGB.length; i++) {
            int stride = i * 3;

            intARGB[i]
                    = (0x000000ff << 24) & 0xff000000
                    | (RGB[stride + 0] << 16) & 0x00ff0000
                    | (RGB[stride + 1] << 8) & 0x0000ff00
                    | (RGB[stride + 2]) & 0x000000ff;
        }

        return intARGB;
    }

    public static byte[] ConvertImageARGBToBGRAByteArray(BufferedImage image) {
        int ARGB[] = new int[image.getHeight() * image.getWidth()];
        byte BGRA[] = new byte[image.getHeight() * image.getWidth() * 4];

        image.getRGB(0, 0, image.getWidth(), image.getHeight(), ARGB, 0, image.getWidth());

        for (int i = 0; i < ARGB.length; i++) {
            int cur = ARGB[i];
            int alpha = cur >> 24 & 0xff;
            int red = cur >> 16 & 0xff;
            int green = cur >> 8 & 0xff;
            int blue = cur & 0xff;
            int stride = i * 4;

            BGRA[stride + 0] = (byte) blue;
            BGRA[stride + 1] = (byte) green;
            BGRA[stride + 2] = (byte) red;
            BGRA[stride + 3] = (byte) alpha;
        }

        return BGRA;
    }

    public static byte[] ConvertImageARGBToRGBAByteArray(BufferedImage image) {
        int ARGB[] = new int[image.getHeight() * image.getWidth()];
        byte RGBA[] = new byte[image.getHeight() * image.getWidth() * 4];

        image.getRGB(0, 0, image.getWidth(), image.getHeight(), ARGB, 0, image.getWidth());

        for (int i = 0; i < ARGB.length; i++) {
            int cur = ARGB[i];
            int alpha = cur >> 24 & 0xff;
            int red = cur >> 16 & 0xff;
            int green = cur >> 8 & 0xff;
            int blue = cur & 0xff;
            int stride = i * 4;

            RGBA[stride + 0] = (byte) red;
            RGBA[stride + 1] = (byte) green;
            RGBA[stride + 2] = (byte) blue;
            RGBA[stride + 3] = (byte) alpha;
        }

        return RGBA;
    }

    public static byte[] FlipHorizontally(byte[] pixelData, int width, int height) {
        byte[] pixelDataFlipped = new byte[pixelData.length];
        for (int i = 0; i < height; i++) {
            for (int j = 0, jj = width - 1; j < width; j++, jj--) {
                int offset = (j + (i * width)) * 4;
                int offsetNew = (jj + (i * width)) * 4;
                pixelDataFlipped[offsetNew + 0] = pixelData[offset + 0];
                pixelDataFlipped[offsetNew + 1] = pixelData[offset + 1];
                pixelDataFlipped[offsetNew + 2] = pixelData[offset + 2];
                pixelDataFlipped[offsetNew + 3] = pixelData[offset + 3];
            }
        }
        return pixelDataFlipped;
    }

    public static BufferedImage FlipHorizontally(BufferedImage image) {
        int h = image.getHeight();
        int imageType = (image.getType() == 0) ? BufferedImage.TYPE_INT_ARGB : image.getType();
        int w = image.getWidth();

        BufferedImage flippedimage = new BufferedImage(w, h, imageType);
        Graphics2D g = flippedimage.createGraphics();

        g.drawImage(image, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();

        return flippedimage;
    }

    public static byte[] FlipVertically(byte[] pixelData, int width, int height) {
        byte[] pixelDataFlipped = new byte[pixelData.length];
        for (int i = height - 1, ii = 0; i >= 0; i--, ii++) {
            for (int j = 0; j < width; j++) {
                int offset = (j + (i * width)) * 4;
                int offsetNew = (j + (ii * width)) * 4;
                pixelDataFlipped[offsetNew + 0] = pixelData[offset + 0];
                pixelDataFlipped[offsetNew + 1] = pixelData[offset + 1];
                pixelDataFlipped[offsetNew + 2] = pixelData[offset + 2];
                pixelDataFlipped[offsetNew + 3] = pixelData[offset + 3];
            }
        }
        return pixelDataFlipped;
    }

    public static BufferedImage FlipVertically(BufferedImage image) {
        int h = image.getHeight();
        int imageType = (image.getType() == 0) ? BufferedImage.TYPE_INT_ARGB : image.getType();
        int w = image.getWidth();

        BufferedImage flippedimage = new BufferedImage(w, h, imageType);
        Graphics2D g = flippedimage.createGraphics();

        g.drawImage(image, 0, 0, w, h, 0, h, w, 0, null);
        g.dispose();

        return flippedimage;
    }

}
