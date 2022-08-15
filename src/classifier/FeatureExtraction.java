package classifier;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class FeatureExtraction 
{
	Preprocess processor=new Preprocess();
	Mat matrix=processor.getMatrix();
	
	public FeatureExtraction()
	{
		HistogramOfOrientedGradients(matrix);
	}
	
	public void HistogramOfOrientedGradients(Mat matrix)
	{
		Mat img=matrix.clone(); //Clones, Mat is matrix of colors
		
		Size sz = new Size(64, 128);
		Imgproc.resize(img, matrix, sz); //resizes the matrix
		
		Mat tmp=img.clone(); //Clone the matrix, i presume to prevent messing up the original
		
		double[][] Gx=new double[img.cols()-2][img.rows()-2];
		double[][] Gy=new double[img.cols()-2][img.rows()-2];
		double[][] magnitude=new double[img.cols()-2][img.rows()-2];
		double[][] theta=new double[img.cols()-2][img.rows()-2];
		
		int binNum=9; //0-180
		int stepSize=180/binNum;
		
		for(int x=1; x<img.cols()-2; x++)
		{
			for(int y=1; y<img.rows()-2; y++)
			{
				//System.out.println(y);
				Gx[x][y]=img.get(y, x+1)[0]-img.get(y, x-1)[0];
				Gy[x][y]=img.get(y-1, x)[0]-img.get(y+1, x)[0];
				
				magnitude[x][y]=Math.sqrt(Math.pow(Gx[x][y], 2)+Math.pow(Gy[x][y], 2));
				theta[x][y]=Math.atan(Gy[x][y]/Gx[x][y]);
				
				tmp.put(x, y, Gx[x][y]); //puts a new pixel in location x, y
				System.out.println(Gx[x].length);
			}
		}
		
		Imgproc.resize(tmp, tmp, new Size(640, 480));
		Image i=new Image();
		i.displayImage(tmp);
	}
}
