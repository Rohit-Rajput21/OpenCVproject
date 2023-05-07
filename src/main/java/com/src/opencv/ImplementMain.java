package com.src.opencv;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
class HoughCirclesRun2 {
    public void run(String[] args) {
    	 Mat dst = new Mat(), cdst = new Mat(), cdstP;
        String default_file = "C:\\Users\\rohit\\Desktop\\gauge1.jpeg";
        String filename = ((args.length > 0) ? args[0] : default_file);
        // Load an image
        Mat src = Imgcodecs.imread(filename, Imgcodecs.IMREAD_COLOR);
        // Check if image is loaded fine
        if( src.empty() ) {
            System.out.println("Error opening image!");
            System.out.println("Program Arguments: [image_name -- default "
                    + default_file +"] \n");
            System.exit(-1);
        }
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)gray.rows()/16, // change this value to detect circles with different distances to each other
                100.0, 30.0,50, 0); // change the last two parameters
                // (min_radius & max_radius) to detect larger circles

        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(src, center, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(src, center, radius, new Scalar(255,0,255), 3, 8, 0 );
        }
        Mat mask = new Mat(src.rows(), src.cols(), CvType.CV_8U, Scalar.all(0));

        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(mask, center, radius, new Scalar(255,255,255), -1, 8, 0 );
        }

        Mat masked = new Mat();
        src.copyTo( masked, mask );

        // Apply Threshold
        Mat thresh = new Mat();
        Imgproc.threshold( mask, thresh, 1, 255, Imgproc.THRESH_BINARY );

        // Find Contour
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Imgproc.findContours(thresh, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Crop
        Rect rect = Imgproc.boundingRect(contours.get(0));
        Mat cropped = masked.submat(rect);
//
//        HighGui.imshow("detected circles", src);
//        HighGui.imshow("Cropped circle", cropped);
//        HighGui.waitKey();
    Mat src1 = cropped;
        
        // Check if image is loaded fine
    //    if( src.empty() ) {
    //        System.out.println("Error opening image!");
    //        System.out.println("Program Arguments: [image_name -- default "
    //                + default_file +"] \n");
    //        System.exit(-1);
    //    }
        // Edge detection
        Imgproc.Canny(src1, dst, 50, 200, 3, false);
        // Copy edges to the images that will display the results in BGR
        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);
        cdstP = cdst.clone();
        // Standard Hough Line Transform
        Mat lines = new Mat(); // will hold the results of the detection
        Imgproc.HoughLines(dst, lines, 1, Math.PI/15, 50); // runs the actual detection
        // Draw the lines
        for (int x = 0; x < lines.rows(); x++) {
            double rho = lines.get(x, 0)[0],
                    theta = lines.get(x, 0)[1];
            double a = Math.cos(theta), b = Math.sin(theta);
            double x0 = a*rho, y0 = b*rho;
            Point pt1 = new Point(Math.round(x0 + 1000*(-b)), Math.round(y0 + 1000*(a)));
            Point pt2 = new Point(Math.round(x0 - 1000*(-b)), Math.round(y0 - 1000*(a)));
            Imgproc.line(cdst, pt1, pt2, new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
        }
      //  System.out.println(lines);
        // Probabilistic Line Transform
        Mat linesP = new Mat(); // will hold the results of the detection
        Imgproc.HoughLinesP(dst, linesP, 1, Math.PI/10, 30, 30, 2); // runs the actual detection
        // Draw the lines
        for (int x = 0; x < linesP.rows(); x++) {
            double[] l = linesP.get(x, 0);
            Imgproc.line(cdstP, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
        }
      // Show results
      // System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
	  // String file ="C:\\Users\\rohit\\Desktop\\gauge1.jpeg";
	      Mat src2 = cropped;
	      //Converting the source image to binary
	      Mat gray1 = new Mat(src.rows(), src2.cols(), src2.type());
	      Imgproc.cvtColor(src2, gray1, Imgproc.COLOR_BGR2GRAY);
	      Mat binary = new Mat(src2.rows(), src2.cols(), src2.type(), new Scalar(0));
	      Imgproc.threshold(gray1, binary, 100, 255, Imgproc.THRESH_BINARY_INV);
	      //Finding Contours
	      List<MatOfPoint> contour = new ArrayList<>();
	      Mat hierarchey = new Mat();
	      Imgproc.findContours(binary, contour, hierarchey, Imgproc.RETR_TREE,
	      Imgproc.CHAIN_APPROX_SIMPLE);
	      Iterator<MatOfPoint> it = contour.iterator();
	      while(it.hasNext()) {
	         System.out.println(it.next());
	      }
	      Mat draw = Mat.zeros(binary.size(), CvType.CV_8UC3);
	      for (int i = 0; i < contour.size(); i++) {
	     //   System.out.println(contour);
	         Scalar color = new Scalar(0, 0, 255);
	         //Drawing Contours
	         Imgproc.drawContours(draw, contour, i, color, 2, Imgproc.LINE_8, hierarchey, 2 ) ;
	      }
	  // double angle=findAngle(lines);
	      
	   HighGui.imshow("Contours operation", draw);
       HighGui.imshow("detected circles", src);
       HighGui.imshow("Cropped circle", cropped);
    // HighGui.imshow("Source", src1);
       HighGui.imshow("Detected Lines (in red) - Standard Hough Line Transform", cdst);
       HighGui.imshow("Detected Lines (in red) - Probabilistic Line Transform", cdstP);
    // Wait and Exit
       HighGui.waitKey();
        System.exit(0);
        
    }
    public  double findAngle(List<Point> points) {
        Point a = points.get(points.size()-2);
        Point b = points.get(points.size()-3);
        Point c = points.get(points.size()-1);
        double m1 = slope(b, a);
        double m2 = slope(b, c);
        double angle = Math.atan((m2-m1)/(1+m1*m2));
        angle = Math.round(Math.toDegrees(angle));
        if (angle<0) {
            angle = 180+angle;
        }
        // Assumes image and cv2 variables are already defined elsewhere
//        cv.putText(img, Double.toString(angle),
//                      new Point(b.x-40, b.y+40),
//                      cv.FONT_HERSHEY_DUPLEX, 2, new Scalar(0,0,255),2);
//        cv.imshow("image", img);
        return angle;
    }

    public  double slope(Point pt1, Point pt2) {
        if(pt1.x == pt2.x) {
            return Double.POSITIVE_INFINITY;
        } else {
            return (double)(pt2.y - pt1.y) / (pt2.x - pt1.x);
        }
    }
}
public class ImplementMain {
    public static void main(String[] args) {
        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new HoughCirclesRun2().run(args);
    }
}
