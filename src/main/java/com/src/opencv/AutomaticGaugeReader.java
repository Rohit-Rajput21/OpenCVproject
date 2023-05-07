package com.src.opencv;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class AutomaticGaugeReader {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load an image
        String imagePath = "C:\\Users\\rohit\\Desktop\\gauge3.jpg";
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
            
            // Rotate the image by the angle of the circle
//            Mat rotatedImage = new Mat();
//            Point rotationCenter = new Point(center.x, center.y);
//            Mat rotationMatrix = Imgproc.getRotationMatrix2D(rotationCenter, findAngleOfCircle(circles), 1);
//            Imgproc.warpAffine(image, rotatedImage, rotationMatrix, image.size());

            // Find the scale of the gauge using a known value and the radius of the gauge
            double knownValue = 100;
            double radiusInPixels = radius;
            double scale = knownValue / radiusInPixels;

            // Draw the scale of the gauge
            for (int j = 0; j <= 10; j++) {
                double angle = ((j * 30) - 150) * Math.PI / 180;
                double x1 = center.x + (radius - 10) * Math.cos(angle);
                double y1 = center.y + (radius - 10) * Math.sin(angle);
                double x2 = center.x + (radius + 10) * Math.cos(angle);
                double y2 = center.y + (radius + 10) * Math.sin(angle);
                Imgproc.line(image, new Point(x1, y1), new Point(x2, y2), new Scalar(0, 0, 255), 2);
            }

         

            // Calculate the angle of the needle
         // Find the needle using Hough line detection algorithm
            Mat lines = new Mat();
            Imgproc.HoughLines(gray, lines, 1, Math.PI / 180, 150);

            double maxLineLength = 0;
            double lineAngle = 0;
            for (int k = 0; k < lines.rows(); k++) {
                double[] line = lines.get(k, 0);
                double rho = line[0];
                double theta = line[1];
                double a = Math.cos(theta);
                double b = Math.sin(theta);
                double x0 = a * rho;
                double y0 = b * rho;
                double x1 = Math.round(x0 + 1000 * (-b));
                double y1 = Math.round(y0 + 1000 * (a));
                double x2 = Math.round(x0 - 1000 * (-b));
                double y2 = Math.round(y0 - 1000 * (a));
                double lineLength = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

                if (lineLength > maxLineLength && (theta < Math.PI / 4 || theta > 3 * Math.PI / 4)) {
                    maxLineLength = lineLength;
                    lineAngle = theta;
                }
            }

            // Calculate the angle between the needle and the 0 degree mark
            double needleAngle = (lineAngle - Math.PI / 2) * 180 / Math.PI;

            // Map the needle angle to the gauge range
            double needleValue = (needleAngle + 240) / 270 * 120 -35;

            // Display the needle value on the image
            String valueString = String.format("%.0f", needleValue);
            Point valueTextPos = new Point(center.x - 20, center.y + 20);
            Imgproc.putText(image, valueString, valueTextPos, Imgproc.FONT_HERSHEY_SIMPLEX, 1, new Scalar(255, 0, 0), 2);
            HighGui.imshow("detected circles", image);
            HighGui.waitKey();
            System.exit(0);
        }
    }}


