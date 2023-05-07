package com.src.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class CircleDetectionExample {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load an image
        String imagePath = "C:\\Users\\rohit\\Desktop\\gauge2.jpg";
        Mat image = Imgcodecs.imread(imagePath);

        // Convert the image to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply Gaussian blur to the image
        Imgproc.GaussianBlur(gray, gray, new org.opencv.core.Size(9, 9), 2, 2);

        // Detect circles using Hough Circle Detection algorithm
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1, gray.rows()/8, 200, 100, 0, 0);

        // Draw the detected circles on the original image
        for (int i = 0; i < circles.cols(); i++) {
            double[] circle = circles.get(0, i);
            Point center = new Point(Math.round(circle[0]), Math.round(circle[1]));
            int radius = (int) Math.round(circle[2]);
            Imgproc.circle(image, center, radius, new Scalar(0, 0, 255), 2);
        
        // Gauge calibration
        double pixelValue = 10.0; // Value to be determined experimentally
        double gaugeRadius = radius;
        double gaugeMaxValue = 100.0; // Maximum value of the gauge
        double gaugeMinValue = 0.0; // Minimum value of the gauge
        double gaugeRange = gaugeMaxValue - gaugeMinValue;
        double pixelRange = gaugeRadius - 30; // Minimum value of the gauge is located at 30 pixels from center
        double pixelOffset = gaugeRadius - pixelRange;
        
        // Draw the gauge scale
        for (int j = 0; j <= 200; j += 3) {
            double angle = Math.PI / 2 + (j / gaugeRange) * Math.PI;
            int x1 = (int) Math.round(center.x + (gaugeRadius - 5) * Math.cos(angle));
            int y1 = (int) Math.round(center.y - (gaugeRadius - 5) * Math.sin(angle));
            int x2 = (int) Math.round(center.x + (gaugeRadius - 20) * Math.cos(angle));
            int y2 = (int) Math.round(center.y - (gaugeRadius - 20) * Math.sin(angle));
            Imgproc.line(image, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 255, 0), 2);
        }
        }

        
        // Display the image with detected circles
        Imgcodecs.imwrite("circles.jpg", image);
        HighGui.imshow("detected circles", image);
        System.out.println("Circles detected and saved to circles.jpg");
        HighGui.waitKey();
        System.exit(0);

    }
   }
