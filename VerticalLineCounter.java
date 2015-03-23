import java.awt.Color;
import java.awt.Point;
import marvin.image.MarvinImage;
import marvin.io.MarvinImageIO;
import marvin.plugin.MarvinImagePlugin;
import marvin.util.MarvinPluginLoader;

public class VerticalLineCounter {

    private MarvinImagePlugin threshold = MarvinPluginLoader.loadImagePlugin("org.marvinproject.image.color.thresholding");

    public VerticalLineCounter(){
        // Binarize
        MarvinImage image = MarvinImageIO.loadImage("./lines.jpg");
        MarvinImage binImage = image.clone();
        //threshold.setAttribute("threshold", 127);
        threshold.process(image, binImage);

        // Find lines and save an output image
        MarvinImage imageOut = findVerticalLines(binImage, image);
        MarvinImageIO.saveImage(imageOut, "./lines_out.png");
    }

    private MarvinImage findVerticalLines(MarvinImage binImage, MarvinImage originalImage){
        MarvinImage imageOut = originalImage.clone();
        boolean[][] processedPixels = new boolean[binImage.getWidth()][binImage.getHeight()];
        int color;
        Point endPoint;
        int totalLines=0;
        for(int y=0; y<binImage.getHeight(); y++){
            for(int x=0; x<binImage.getWidth(); x++){
                if(!processedPixels[x][y]){
                    color = binImage.getIntColor(x, y);

                    // Black?
                    if(color == 0xFF000000){
                        endPoint = getEndOfLine(x,y,binImage,processedPixels);

                        // Line lenght threshold
                        if(endPoint.x - x > 5 || endPoint.y - y > 5){
                            imageOut.fillRect(x-2, y-2, 5, 5, Color.red);
                            imageOut.fillRect(endPoint.x-2, endPoint.y-2, 5, 5, Color.green);
                            totalLines++;
                            System.out.println("Vertical line fount at: ("+x+","+y+","+endPoint.x+","+endPoint.y+")");
                        }
                    }
                }
                processedPixels[x][y] = true;
            }
        }
        System.out.println("Vertical lines total: "+totalLines);
        return imageOut;
    }

    private Point getEndOfLine(int x, int y, MarvinImage image, boolean[][] processedPixels){
        int xC=x;
        int cY=y;
        while(true){
            processedPixels[xC][cY] = true;
            processedPixels[xC-1][cY] = true;
            processedPixels[xC-2][cY] = true;
            processedPixels[xC-3][cY] = true;
            processedPixels[xC+1][cY] = true;
            processedPixels[xC+2][cY] = true;
            processedPixels[xC+3][cY] = true;

            if(getSafeIntColor(xC,cY,image)  < 0xFF000000){
                // nothing
            }
            else if(getSafeIntColor(xC-1,cY,image) == 0xFF000000){
                xC = xC-2;
            }
            else if(getSafeIntColor(xC-2,cY,image) == 0xFF000000){
                xC = xC-3;
            }
            else if(getSafeIntColor(xC+1,cY,image)  == 0xFF000000){ 
                xC = xC+2;
            }
            else if(getSafeIntColor(xC+2,cY,image)  == 0xFF000000){ 
                xC = xC+3;
            }
            else{
                return new Point(xC, cY);
            }
            cY++;
        }
    }
    private int getSafeIntColor(int x, int y, MarvinImage image){
        if(x >= 0 && x < image.getWidth() && y >= 0 && y < image.getHeight()){
            return image.getIntColor(x, y);
        }
        return -1;
    }
    public static void main(String args[]){
        new VerticalLineCounter();
        System.exit(0);
    }
}