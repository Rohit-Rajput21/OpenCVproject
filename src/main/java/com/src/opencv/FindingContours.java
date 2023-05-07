package com.src.opencv;

	
import java.util.ArrayList;
	import java.util.Iterator;
	import java.util.List;
	import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
	import org.opencv.core.MatOfPoint;
	import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
	import org.opencv.imgproc.Imgproc;
	public class FindingContours {
	   public static void main(String args[]) throws Exception {
	      //Loading the OpenCV core library
	      System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	      String file ="C:\\Users\\rohit\\Desktop\\gauge1.jpeg";
	      Mat src = Imgcodecs.imread(file);
	      //Converting the source image to binary
	      Mat gray = new Mat(src.rows(), src.cols(), src.type());
	      Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
	      Mat binary = new Mat(src.rows(), src.cols(), src.type(), new Scalar(0));
	      Imgproc.threshold(gray, binary, 100, 255, Imgproc.THRESH_BINARY_INV);
	      //Finding Contours
	      List<MatOfPoint> contours = new ArrayList<>();
	      Mat hierarchey = new Mat();
	      Imgproc.findContours(binary, contours, hierarchey, Imgproc.RETR_TREE,
	      Imgproc.CHAIN_APPROX_SIMPLE);
	      Iterator<MatOfPoint> it = contours.iterator();
	      while(it.hasNext()) {
	         System.out.println(it.next());
	      }
	      Mat draw = Mat.zeros(binary.size(), CvType.CV_8UC3);
	      for (int i = 0; i < contours.size(); i++) {
	         System.out.println(contours);
	         Scalar color = new Scalar(0, 0, 255);
	         //Drawing Contours
	         Imgproc.drawContours(draw, contours, i, color, 2, Imgproc.LINE_8, hierarchey, 2 ) ;
	      }
	      HighGui.imshow("Contours operation", draw);
	      HighGui.waitKey();
	      
	   }
	}
