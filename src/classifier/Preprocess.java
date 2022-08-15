package classifier;

import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class Preprocess 
{
	static Image img=new Image();
	Mat matrix=img.createImage();
	
	public Preprocess()
	{
		setMatrix(gammaCorrection(matrix));
	}
	
	public void setMatrix(Mat matrix)
	{
		this.matrix=matrix;
	}
	
	public Mat getMatrix()
	{
		return matrix;
	}
	
	public static Mat gammaCorrection(Mat matrix) //A*Math.pow(matrix.get(x, y)[0]/255, 1/gamma)
	{
		//Turns image into grayscale
		Imgproc.cvtColor(matrix, matrix, Imgproc.COLOR_BGR2GRAY); //TODO Will eventually allow for RGB
		
		img.displayImage(matrix);
		
		double gamma=.5; //TODO Will be changed later to improve accuracy
		double A=255;
		try 
		{
			for(int x=0; x<matrix.rows(); x++)
			{
				for(int y=0; y<matrix.cols(); y++)
				{
					matrix.put(x, y, new double[] {A*Math.pow(matrix.get(x, y)[0]/255, 1/gamma)});
				}
			}
		}catch(NullPointerException ex)
		{
			
		}
		img.displayImage(matrix);
		
		return matrix;
	}
}