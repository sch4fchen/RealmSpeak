package com.robin.general.io;

import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.*;
import javax.imageio.stream.ImageOutputStream;

public class ImageFile {
	/**
	 * Saves a JPG file.
	 * 
	 * @param bi					The image to save.
	 * @param outfile				The file path to save to.
	 * @param compressionQuality	ranges between 0 and 1, where 1 specifies minimum compression and maximum quality.
	 * 
	 * @return						true on success.
	 */
    public static boolean saveJpeg(BufferedImage bi, File outfile, float compressionQuality) {
        try {
            // Retrieve jpg image to be compressed
            RenderedImage rendImage = bi;
    
            // Find a jpeg writer
            ImageWriter writer = null;
            Iterator iter = ImageIO.getImageWritersByFormatName("jpg");
            if (iter.hasNext()) {
                writer = (ImageWriter)iter.next();
            }
    
			// instantiate an ImageWriteParam object with default compression options
			ImageWriteParam iwp = writer.getDefaultWriteParam();

			//Now, we can set the compression quality:
			iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			iwp.setCompressionQuality(compressionQuality);

            // Prepare output file
            ImageOutputStream ios = ImageIO.createImageOutputStream(outfile);
            writer.setOutput(ios);
    
            // Write the image
            writer.write(null, new IIOImage(rendImage, null, null), iwp);
    
            // Cleanup
            ios.flush();
            writer.dispose();
            ios.close();
            
            return true;
        }
        catch (IOException e) {
        	return false;
        }
    }
}