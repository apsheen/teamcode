package org.firstinspires.ftc.teamcode;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

public class OpenCVCameraTest {
    public static void main(String args[]) {
        Mat frame = new Mat();
        VideoCapture camera = new VideoCapture(0);
        if (camera.read(frame)) {
            System.out.println("Capturing face.");
        }
    }
}
