package com.src.opencv;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class MainJava {
    
    static{ 
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
    
    public static void main(String[] args) {
        
    	String default_file = "C:\\Users\\rohit\\Desktop\\gauge1.jpeg";
        String filename = ((args.length > 0) ? args[0] : default_file);
        // Load an image
        Mat img = Imgcodecs.imread(filename, Imgcodecs.IMREAD_COLOR);
        
        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(img, gray, Imgproc.COLOR_BGR2GRAY);
        
        // Find circles using Hough transform
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1, 20, 100, 50, (int)(img.height() * 0.35), (int)(img.height() * 0.48));
        
        // Get the circle with the largest radius
        int maxIndex = 0;
        double maxRadius = 0;
        for (int i = 0; i < circles.cols(); i++) {
            double[] circle = circles.get(0, i);
            if (circle[2] > maxRadius) {
                maxIndex = i;
                maxRadius = circle[2];
            }
        }
        
        // Draw the circle on the image
        Point center = new Point(circles.get(0, maxIndex)[0], circles.get(0, maxIndex)[1]);
        Mat gray1 = new Mat();
        Imgproc.cvtColor(img, gray1, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles1 = new Mat();
        Imgproc.HoughCircles(gray1, circles1, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)gray1.rows()/16, // change this value to detect circles with different distances to each other
                100.0, 30.0, 50, 0); // change the last two parameters
                // (min_radius & max_radius) to detect larger circles
        for (int x = 0; x < circles1.cols(); x++) {
            double[] c = circles1.get(0, x);
            Point center1 = new Point(Math.round(c[0]), Math.round(c[1]));
            // circle center
            Imgproc.circle(img, center1, 1, new Scalar(0,100,100), 3, 8, 0 );
            // circle outline
            int radius = (int) Math.round(c[2]);
            Imgproc.circle(img, center1, radius, new Scalar(255,0,255), 3, 8, 0 );
        }
        HighGui.imshow("detected circles", img);
        HighGui.waitKey();
        System.exit(0);
        
        // Draw lines and labels for gauge readings
        int numDivisions = 10;
        int separation = 360 / numDivisions;
        int radiusOffset = 20;
        for (int i = 0; i < numDivisions; i++) {
            double angle = Math.toRadians(i * separation - 90);
            Point p1 = new Point(center.x + (maxRadius - radiusOffset) * Math.cos(angle), center.y + (maxRadius - radiusOffset) * Math.sin(angle));
            Point p2 = new Point(center.x + maxRadius * Math.cos(angle), center.y + maxRadius * Math.sin(angle));
            Imgproc.line(img, p1, p2, new Scalar(0, 255, 0), 2);
            Imgproc.putText(img, Integer.toString(i * (180 / numDivisions)), new Point(center.x + (maxRadius + 20) * Math.cos(angle), center.y + (maxRadius + 20) * Math.sin(angle)), Core.DECOMP_CHOLESKY, 0.5, new Scalar(0, 0, 0), 1, Imgproc.LINE_AA);
        }
        
        // Display the final image
        Imgcodecs.imwrite("gauge_output.png", img);
    }

}