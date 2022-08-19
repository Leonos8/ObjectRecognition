package classifier;

import java.util.ArrayList;

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
		Imgproc.resize(matrix, img, sz); //resizes the matrix
		
		Mat tmp=img.clone(); //Clone the matrix, i presume to prevent messing up the original
		
		double[][] Gx=new double[img.cols()][img.rows()];
		double[][] Gy=new double[img.cols()][img.rows()];
		double[][] magnitude=new double[img.cols()][img.rows()];
		double[][] theta=new double[img.cols()][img.rows()];
		
		int binNum=9; //0-180
		int stepSize=180/binNum;
		
		for(int x=0; x<img.cols(); x++)
		{
			for(int y=0; y<img.rows(); y++)
			{			
				if(x==0 && y==0)
				{
					Gx[x][y]=img.get(y, x+1)[0]-img.get(y, x)[0];
					Gy[x][y]=img.get(y, x)[0]-img.get(y+1, x)[0];
				}
				else if(x==0 && y==img.rows()-1)
				{
					Gx[x][y]=img.get(y, x+1)[0]-img.get(y, x)[0];
					Gy[x][y]=img.get(y-1, x)[0]-img.get(y, x)[0];
				}
				else if(x==img.cols()-1 && y==0)
				{
					Gx[x][y]=img.get(y, x)[0]-img.get(y, x-1)[0];
					Gy[x][y]=img.get(y, x)[0]-img.get(y+1, x)[0];
				}
				else if(x==img.cols()-1 && y==img.rows()-1)
				{
					Gx[x][y]=img.get(y, x)[0]-img.get(y, x-1)[0];
					Gy[x][y]=img.get(y-1, x)[0]-img.get(y, x)[0];
				}
				else if(x==0)
				{
					Gx[x][y]=img.get(y, x+1)[0]-img.get(y, x)[0];
					Gy[x][y]=img.get(y-1, x)[0]-img.get(y+1, x)[0];
				}
				else if(x==img.cols()-1)
				{
					Gx[x][y]=img.get(y, x)[0]-img.get(y, x-1)[0];
					Gy[x][y]=img.get(y-1, x)[0]-img.get(y+1, x)[0];
				}
				else if(y==0)
				{
					Gx[x][y]=img.get(y, x+1)[0]-img.get(y, x-1)[0];
					Gy[x][y]=img.get(y, x)[0]-img.get(y+1, x)[0];
				}
				else if(y==img.rows()-1)
				{
					Gx[x][y]=img.get(y, x+1)[0]-img.get(y, x-1)[0];
					Gy[x][y]=img.get(y-1, x)[0]-img.get(y, x)[0];
				}
				else
				{
					Gx[x][y]=img.get(y, x+1)[0]-img.get(y, x-1)[0];
					Gy[x][y]=img.get(y-1, x)[0]-img.get(y+1, x)[0];
					
					magnitude[x][y]=Math.sqrt(Math.pow(Gx[x][y], 2)+Math.pow(Gy[x][y], 2));
					theta[x][y]=Math.toDegrees(Math.abs(Math.atan(Gy[x][y]/Gx[x][y])));
				}
				
				tmp.put(y, x, theta[x][y]); //puts a new pixel in location x, y
			}
		}
		
		double[][][] ninePointHistogram=calculate9PointHistogram(magnitude, theta, binNum, stepSize);
		
		Imgproc.resize(tmp, tmp, new Size(640, 480));
		Image i=new Image();
		i.displayImage(tmp);
	}
	
	public double[][][] calculate9PointHistogram(double[][] magnitude, double[][] theta, int binNum, int stepSize)
	{
		double[][][] ninePointHistogram=new double[16][8][9];
		
		for(int i=0; i<128; i+=8)
		{
			double[][] temp=new double[8][9];
			
			for(int j=0; j<64; j+=8)
			{
				double[][] magnitudeValues=new double[8][8];
				double[][] thetaValues=new double[8][8];
				
				for(int x=0; x<8; x++)
				{
					for(int y=0; y<8; y++)
					{
						magnitudeValues[x][y]=magnitude[j+y][i+x];
						thetaValues[x][y]=theta[j+y][i+x];
					}
				}
				
				double[] bins=new double[binNum];
				
				for(int t=0; t<binNum; t++)
				{
					bins[t]=0;
				}
				
				for(int k=0; k<magnitudeValues.length; k++)
				{
					for(int l=0; l<magnitudeValues[0].length; l++)
					{
						int jValue = calculateJ(thetaValues[k][l], stepSize);
				        double Vj = calculateJValue(magnitudeValues[k][l], thetaValues[k][l], jValue, stepSize);
				        double Vj1 = magnitudeValues[k][l] - Vj;
				        bins[jValue]+=Vj;
				        bins[jValue+1]+=Vj1;
					}
				}
			    temp[j/8]=bins;
			}
			ninePointHistogram[i/8]=temp;
		}
		
		return ninePointHistogram;
	}
	
	public int calculateJ(double angle, int stepSize)
	{
		int j=(int) ((angle/stepSize)-.5);
		
		return j;
	}
	
	public double calculateCj(int j, int stepSize) //Center of Jth bin
	{
		double Cj=stepSize*(j+.5);
		
		return Cj;
	}
	
	public double calculateJValue(double magnitude, double angle, int j, int stepSize)
	{
		double Cj=calculateCj(j+1, stepSize);
		double Vj=magnitude*((Cj-angle)/stepSize);
		
		return Vj;
	}
}
