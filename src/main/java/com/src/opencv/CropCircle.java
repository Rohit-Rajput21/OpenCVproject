package com.src.opencv;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

class HoughCirclesRun1 {
    public void run(String[] args) {
        String filename = "C:\\Users\\rohit\\Desktop\\hough5.png";
        // Load an image
        Mat src = Imgcodecs.imread(filename, Imgcodecs.IMREAD_COLOR);
        // Check if image is loaded fine
        if( src.empty() ) {
            System.out.println("Error opening image!");
        }

        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
        Imgproc.medianBlur(gray, gray, 5);
        Mat circles = new Mat();
        Imgproc.HoughCircles(gray, circles, Imgproc.HOUGH_GRADIENT, 1.0,
                (double)gray.rows()/16, // change this value to detect circles with different distances to each other
                100.0, 30.0, 1, 30); // change the last two parameters (min_radius & max_radius) to detect larger circles

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

        HighGui.imshow("Cropped circle", cropped);
        HighGui.waitKey();
        System.exit(0);
    }
}
public class CropCircle {
    public static void main(String[] args) {
        // Load the native library.
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        new HoughCirclesRun1().run(args);
    }
}
