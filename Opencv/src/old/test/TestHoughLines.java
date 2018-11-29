package old.test;

import old.utils.HandleImgUtils;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * 测试hough直线
 * 
 * @author admin
 *
 */
public class TestHoughLines {

	// 累计hough直线
	public static void testHoughLinesP(Mat src) {
		Mat cannyMat = HandleImgUtils.canny(src);

		Mat lines = new Mat();
		Imgproc.HoughLinesP(cannyMat, lines, 1, 1, 80);

		Mat grayMat = src.clone();

		for (int i = 0; i < lines.rows(); i++) {
			double[] vec = lines.get(i, 0);
			double x1 = vec[0], y1 = vec[1], x2 = vec[2], y2 = vec[3];
			Point start = new Point(x1, y1);
			Point end = new Point(x2, y2);
			Imgproc.line(grayMat, start, end, new Scalar(0, 0, 255), 1, Imgproc.LINE_4, 0);
		}

		HandleImgUtils.saveImg(grayMat, "C:/Users/admin/Desktop/opencv/open/test/2/houghP.jpg");
	}

	// 标准hough直线
	public static void testHoughLines(Mat src) {
		Mat cannyMat = HandleImgUtils.canny(src);

		Mat lines = new Mat();
		Imgproc.HoughLines(cannyMat , lines , 2 , 1 , 100);

		Mat grayMat = src.clone();

		for (int x = 0; x < lines.rows(); x++) {
			double[] vec = lines.get(x, 0);

			double rho = vec[0];
			double theta = vec[1];

			Point pt1 = new Point();
			Point pt2 = new Point();

			double a = Math.cos(theta);
			double b = Math.sin(theta);

			double x0 = a * rho;
			double y0 = b * rho;

			pt1.x = Math.round(x0 + 1000 * (-b));
			pt1.y = Math.round(y0 + 1000 * (a));
			pt2.x = Math.round(x0 - 1000 * (-b));
			pt2.y = Math.round(y0 - 1000 * (a));

			if (theta >= 0) {
				Imgproc.line(grayMat, pt1, pt2, new Scalar( 0 , 0 , 255), 1 , Imgproc.LINE_4, 0);
			}
		}

		HandleImgUtils.saveImg(grayMat,"C:/Users/admin/Desktop/opencv/open/test/2/hough.jpg");

	}

	@Test
	public void test() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = HandleImgUtils.matFactory("C:/Users/admin/Desktop/opencv/open/test/2/2.jpg");
		testHoughLinesP(src);

	}
}
