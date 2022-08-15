package main;

import org.opencv.core.Core;

import classifier.FeatureExtraction;

public class Main 
{
	public static void main(String[] args)
	{
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		//Image image=new Image();
		
		//Preprocess processor=new Preprocess();
		FeatureExtraction fe=new FeatureExtraction();
	}
}
