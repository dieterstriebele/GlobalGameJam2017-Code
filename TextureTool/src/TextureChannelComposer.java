import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import java.util.*;

public class TextureChannelComposer {

    public static BufferedImage createARGBBufferedImage(int inWidth, int inHeight) {
        System.out.println("Creating new BufferedImage ... "+inWidth+"x"+inHeight);
        BufferedImage tARGBImageIntermediate = new BufferedImage(inWidth,inHeight, BufferedImage.TYPE_INT_ARGB);
        TextureChannelComposer.fillImageWithTransparentColor(tARGBImageIntermediate);
        return tARGBImageIntermediate;
    }

    public static void fillImageWithTransparentColor(Image inImage) {
        Color TRANSPARENT = new Color(0,0,0,0);
        TextureChannelComposer.fillImageWithColor(inImage,TRANSPARENT);
    }

    public static void fillImageWithColor(Image inImage,Color inColor) {
        Graphics2D tGraphics2D = (Graphics2D)inImage.getGraphics(); 
        tGraphics2D.setColor(inColor);
        tGraphics2D.setComposite(AlphaComposite.Src);
        tGraphics2D.fillRect(0,0,inImage.getWidth(null),inImage.getHeight(null));
        tGraphics2D.dispose();
    }

    public static BufferedImage loadARGBImage(InputStream inInputStream) {
        try {
            BufferedImage tARGBImage = ImageIO.read(inInputStream);
            System.out.println("Loaded BufferedImage from InputStream: "+tARGBImage.getWidth()+"x"+tARGBImage.getHeight());
            BufferedImage tARGBImageIntermediate = TextureChannelComposer.createARGBBufferedImage(tARGBImage.getWidth(),tARGBImage.getHeight());
            ((Graphics2D)tARGBImageIntermediate.getGraphics()).drawImage(tARGBImage, 0,0, null);    
            return tARGBImageIntermediate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void saveARGBImage(String inARGBImageFileName,BufferedImage inBufferedImage) {
        try {
            System.out.println("Saving BufferedImage as PNG ... "+inARGBImageFileName);
            ImageIO.write(inBufferedImage, "png", new File(inARGBImageFileName));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int[] getARGBDataBufferFromBufferedImage(BufferedImage inBufferedImage) {
        return ((DataBufferInt)inBufferedImage.getRaster().getDataBuffer()).getData();
    }

    public static BufferedImage selectBufferedImageFromChannelMapping(BufferedImage inImage1, BufferedImage inImage2, BufferedImage inImage3, BufferedImage inImage4, String inChannelMapping) {
    	if (inChannelMapping.contains("1")) {
    		return inImage1;
    	} else if (inChannelMapping.contains("2")) {
    		return inImage2;
    	} else if (inChannelMapping.contains("3")) {
    		return inImage3;
    	} else if (inChannelMapping.contains("4")) {
    		return inImage4;
    	} else {
    		System.out.println("Error! Channel Mqpping does not contain valid image number: " + inChannelMapping);
    		System.out.println("Mapping must contain 1, 2, 3 or 4!");
    	}
    	return null;
    }
    
    public static int selectPixelFromChannelMapping(int[] inImage_RGBA, int inXPos, int inYPos, int inWidth, String inChannelMapping) {
    	int pixel = 0xFFFF00FF;
    	if (inChannelMapping.contains("R")) {
    		pixel = (inImage_RGBA[(inYPos * inWidth) + inXPos] & 0x00FF0000) >> 16;
    	} else if (inChannelMapping.contains("G")) {
    		pixel = (inImage_RGBA[(inYPos * inWidth) + inXPos] & 0x0000FF00) >>  8;
    	} else if (inChannelMapping.contains("B")) {
    		pixel = (inImage_RGBA[(inYPos * inWidth) + inXPos] & 0x000000FF) >>  0;
    	} else if (inChannelMapping.contains("A")) {
    		pixel = (inImage_RGBA[(inYPos * inWidth) + inXPos] & 0xFF000000) >> 24;
        }
    	return pixel;
    }
    
    public static BufferedImage getCombinedRGBAImage(
    		BufferedImage inImage1, BufferedImage inImage2, BufferedImage inImage3, BufferedImage inImage4, 
    		String inChannelMappingRed, String inChannelMappingGreen, String inChannelMappingBlue, String inChannelMappingAlpha   		
    ) {
        try {
        	BufferedImage tImageRed = selectBufferedImageFromChannelMapping(inImage1, inImage2, inImage3, inImage4, inChannelMappingRed);
        	BufferedImage tImageGreen = selectBufferedImageFromChannelMapping(inImage1, inImage2, inImage3, inImage4, inChannelMappingGreen);
        	BufferedImage tImageBlue = selectBufferedImageFromChannelMapping(inImage1, inImage2, inImage3, inImage4, inChannelMappingBlue);
        	BufferedImage tImageAlpha = selectBufferedImageFromChannelMapping(inImage1, inImage2, inImage3, inImage4, inChannelMappingAlpha);
        	
        	int[] tImageRed_RGBA = TextureChannelComposer.getARGBDataBufferFromBufferedImage(tImageRed);
        	int[] tImageGreen_RGBA = TextureChannelComposer.getARGBDataBufferFromBufferedImage(tImageGreen);
        	int[] tImageBlue_RGBA = TextureChannelComposer.getARGBDataBufferFromBufferedImage(tImageBlue);
        	int[] tImageAlpha_RGBA = TextureChannelComposer.getARGBDataBufferFromBufferedImage(tImageAlpha);
        	
        	System.out.print("Combining image channels:");
            BufferedImage tComposedImage = new BufferedImage(tImageRed.getWidth(),tImageRed.getHeight(), BufferedImage.TYPE_INT_ARGB);
            int[] tImageComposed_RGBA = ((DataBufferInt)tComposedImage.getRaster().getDataBuffer()).getData();
            for (int y = 0; y < tComposedImage.getHeight(); y++) {
                for (int x = 0; x < tComposedImage.getWidth(); x++) {
                	
                	int r = TextureChannelComposer.selectPixelFromChannelMapping(tImageRed_RGBA, x, y, tComposedImage.getWidth(), inChannelMappingRed) << 16;
                	int g = TextureChannelComposer.selectPixelFromChannelMapping(tImageGreen_RGBA, x, y, tComposedImage.getWidth(), inChannelMappingGreen) << 8;
                	int b = TextureChannelComposer.selectPixelFromChannelMapping(tImageBlue_RGBA, x, y, tComposedImage.getWidth(), inChannelMappingBlue) << 0;
                	int a = TextureChannelComposer.selectPixelFromChannelMapping(tImageAlpha_RGBA, x, y, tComposedImage.getWidth(), inChannelMappingAlpha) << 24;
                	
                    tImageComposed_RGBA[(y * tComposedImage.getWidth()) + x] = a+r+g+b;                
                }
                if (y%10==0) {
                	System.out.print("X");
                }
            }
            System.out.println();
            System.out.println("Combining image channels successful!");
        	
        	
        	/*
            System.out.println("LOADING ALPHA CHANNEL IMAGE ... "+inAlphaFileName);
            BufferedImage tAlphaImage = AlphaChannelMerger.loadARGBImage(inAlphaFileName,new BufferedInputStream(new FileInputStream(inAlphaFileName)));
            System.out.println("LOADING RGB CHANNEL IMAGE ... "+inRGBFileName);
            BufferedImage tRGBImage = AlphaChannelMerger.loadARGBImage(inRGBFileName,new BufferedInputStream(new FileInputStream(inRGBFileName)));
            int[] tRGBA_Alpha = AlphaChannelMerger.getARGBDataBufferFromBufferedImage(tAlphaImage);
            int[] tRGBA_RGB = AlphaChannelMerger.getARGBDataBufferFromBufferedImage(tRGBImage);
            */

            return tComposedImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static BufferedImage flipBufferedImageVertically(BufferedImage inBufferedImage) {
    	return null;
    }
    
    public static BufferedImage loadImageFromPath(String inMessageToUser) throws Exception {
		System.out.print(inMessageToUser);
		InputStreamReader tInputStreamReader = new InputStreamReader(System.in);
		BufferedReader tBufferedReader = new BufferedReader(tInputStreamReader);
		String tPathToFile = (String)tBufferedReader.readLine();
		BufferedImage tBufferedImage = TextureChannelComposer.loadARGBImage(new FileInputStream(tPathToFile));
		return tBufferedImage;
    }

    public static String getOutputChannelMappingFromUser() throws Exception {
		System.out.print("Enter desired channel mapping for the output image:");
		InputStreamReader tInputStreamReader = new InputStreamReader(System.in);
		BufferedReader tBufferedReader = new BufferedReader(tInputStreamReader);
		String tOutputChannelMapping = tBufferedReader.readLine();
		return tOutputChannelMapping;
    }

    public static boolean getWantsFlippingFromUser() throws Exception {
    	System.out.print("Shoud the image be flipped on the vertical axis (y/n):");
		InputStreamReader tInputStreamReader = new InputStreamReader(System.in);
		BufferedReader tBufferedReader = new BufferedReader(tInputStreamReader);
		String tWantsFlipping = tBufferedReader.readLine();
		if (tWantsFlipping.equalsIgnoreCase("y")) {
			return true;
		} else {
			return false;
		}
    }
    
	public static void main(String[] args) {
		try {
			System.out.println("Starting Texture Channel Composer ...");
			
			BufferedImage tInputImage1 = TextureChannelComposer.loadImageFromPath("Please enter path to input image 1 file:");
			BufferedImage tInputImage2 = TextureChannelComposer.loadImageFromPath("Please enter path to input image 2 file:");
			BufferedImage tInputImage3 = TextureChannelComposer.loadImageFromPath("Please enter path to input image 3 file:");
			BufferedImage tInputImage4 = TextureChannelComposer.loadImageFromPath("Please enter path to input image 4 file:");
			
			/*
			BufferedImage tInputImage1 = TextureChannelComposer.loadARGBImage(new FileInputStream("c:\\GlobalGameJam2015\\Spikes\\TextureTool\\bin\\brainminediffusemap.png"));
			BufferedImage tInputImage2 = TextureChannelComposer.loadARGBImage(new FileInputStream("c:\\GlobalGameJam2015\\Spikes\\TextureTool\\bin\\brainminediffusemap.png"));
			BufferedImage tInputImage3 = TextureChannelComposer.loadARGBImage(new FileInputStream("c:\\GlobalGameJam2015\\Spikes\\TextureTool\\bin\\brainminediffusemap.png"));
			BufferedImage tInputImage4 = TextureChannelComposer.loadARGBImage(new FileInputStream("c:\\GlobalGameJam2015\\Spikes\\TextureTool\\bin\\brainmineambientocclusionmap.png"));
			*/

			/*
			BufferedImage tInputImage1 = TextureChannelComposer.loadARGBImage(new FileInputStream("c:\\GlobalGameJam2015\\Spikes\\TextureTool\\bin\\brainminespecularcolormap.png"));
			BufferedImage tInputImage2 = TextureChannelComposer.loadARGBImage(new FileInputStream("c:\\GlobalGameJam2015\\Spikes\\TextureTool\\bin\\brainminespecularcolormap.png"));
			BufferedImage tInputImage3 = TextureChannelComposer.loadARGBImage(new FileInputStream("c:\\GlobalGameJam2015\\Spikes\\TextureTool\\bin\\brainminespecularcolormap.png"));
			BufferedImage tInputImage4 = TextureChannelComposer.loadARGBImage(new FileInputStream("c:\\GlobalGameJam2015\\Spikes\\TextureTool\\bin\\brainminespecularintensitymap.png"));
			*/
			
			String tOutputChannelMapping = TextureChannelComposer.getOutputChannelMappingFromUser();
			//String tOutputChannelMapping = "R=R1 G=G1 B=B1 A=R4";
			
			StringTokenizer tChannelMappingTokenizer = new StringTokenizer(tOutputChannelMapping,"= ");
			tChannelMappingTokenizer.nextToken();
			String tChannelMappingRed = tChannelMappingTokenizer.nextToken();
			tChannelMappingTokenizer.nextToken();
			String tChannelMappingGreen = tChannelMappingTokenizer.nextToken();
			tChannelMappingTokenizer.nextToken();
			String tChannelMappingBlue = tChannelMappingTokenizer.nextToken();
			tChannelMappingTokenizer.nextToken();
			String tChannelMappingAlpha = tChannelMappingTokenizer.nextToken();
			
			//R=R1 G=G1 B=B1 A=R2
			//R=Grey1
			//FLIP
			BufferedImage tResultImage = TextureChannelComposer.getCombinedRGBAImage(
					tInputImage1, tInputImage2, tInputImage3, tInputImage4, 
					tChannelMappingRed, tChannelMappingGreen, tChannelMappingBlue, tChannelMappingAlpha
			);
			
			boolean tWantsFlipping = TextureChannelComposer.getWantsFlippingFromUser();
			//boolean tWantsFlipping = true;
			if (tWantsFlipping) {
				tResultImage = TextureChannelComposer.flipBufferedImageVertically(tResultImage);
			}
			
			
			System.out.print("Please enter path to output image file:");
			InputStreamReader tInputStreamReader = new InputStreamReader(System.in);
			BufferedReader tBufferedReader = new BufferedReader(tInputStreamReader);
			String tPathToFile = (String)tBufferedReader.readLine();		
			TextureChannelComposer.saveARGBImage(tPathToFile, tResultImage);
			
			//TextureChannelComposer.saveARGBImage("c:\\GlobalGameJam2015\\Spikes\\TextureTool\\bin\\combineddiffuseambientocclusion.png", tResultImage);
			//TextureChannelComposer.saveARGBImage("c:\\GlobalGameJam2015\\Spikes\\TextureTool\\bin\\combinedspecularcolorspecularintensity.png", tResultImage);
			
			System.out.println("Finished Texture Channel Composer ...");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
