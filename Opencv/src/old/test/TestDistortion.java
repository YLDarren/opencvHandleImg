package old.test;

import old.utils.HandleImgUtils;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.utils.Converters;

import java.util.List;

/**
 * 测试矫正畸变的图像
 * 
 * @author admin
 *
 */
public class TestDistortion {

	/**
	 * 透视变换
	 * 
	 * @param src
	 */
	public static void warpPerspective(Mat src) {
		// 灰度话
		src = HandleImgUtils.gray(src);
		HandleImgUtils.saveImg(src, "C:/Users/admin/Desktop/opencv/open/q/src-gray.jpg");
		// Canny
		Mat cannyMat = HandleImgUtils.canny(src);
		// 寻找最大矩形
		RotatedRect rect = HandleImgUtils.findMaxRect(cannyMat);
		Point[] rectPoint = new Point[4];
		rect.points(rectPoint);

		// 点的顺序[左上 ，右上 ，右下 ，左下]
		List<Point> listSrcs = java.util.Arrays.asList(rectPoint[2], rectPoint[3], rectPoint[0], rectPoint[1]);
		Mat srcPoints = Converters.vector_Point_to_Mat(listSrcs, CvType.CV_32F);

		Rect r = rect.boundingRect();
		List<Point> listDsts = java.util.Arrays.asList(new Point(r.x, r.y), new Point(r.x + r.width, r.y),
				new Point(r.x + r.width, r.y + r.height), new Point(r.x, r.y + r.height));
		Mat dstPoints = Converters.vector_Point_to_Mat(listDsts, CvType.CV_32F);

		Mat perspectiveMmat = Imgproc.getPerspectiveTransform(srcPoints, dstPoints);

		for (Point p : listSrcs) {
			System.out.println(p.x + " , " + p.y);
		}
		System.out.println("......................................");
		for (Point p : listDsts) {
			System.out.println(p.x + " , " + p.y);
		}

		Mat dst = new Mat();

		Imgproc.warpPerspective(src, dst, perspectiveMmat, src.size(), Imgproc.INTER_LINEAR + Imgproc.WARP_INVERSE_MAP,
				1, new Scalar(0));

		HandleImgUtils.saveImg(src, "C:/Users/admin/Desktop/opencv/open/q/src-src1.jpg");
		HandleImgUtils.saveImg(cannyMat, "C:/Users/admin/Desktop/opencv/open/q/src-canny.jpg");
		HandleImgUtils.saveImg(dst, "C:/Users/admin/Desktop/opencv/open/q/src-dst.jpg");
	}

	/**
	 * 透视变换改进
	 * 
	 * @param src
	 */
	public static void warpPerspectiveImprove(Mat src) {
		// 灰度话
		src = HandleImgUtils.gray(src);
		// 找到四个点
		Point[] points = HandleImgUtils.findFourPoint(src);

		// Canny
		Mat cannyMat = HandleImgUtils.canny(src);
		// 寻找最大矩形
		RotatedRect rect = HandleImgUtils.findMaxRect(cannyMat);

		// 点的顺序[左上 ，右上 ，右下 ，左下]
		List<Point> listSrcs = java.util.Arrays.asList(points[0], points[1], points[2], points[3]);
		Mat srcPoints = Converters.vector_Point_to_Mat(listSrcs, CvType.CV_32F);

		Rect r = rect.boundingRect();
		r.x = Math.abs(r.x);
		r.y = Math.abs(r.y);
		List<Point> listDsts = java.util.Arrays.asList(new Point(r.x, r.y), new Point(r.x + r.width, r.y),
				new Point(r.x + r.width, r.y + r.height), new Point(r.x, r.y + r.height));
		
		System.out.println(r.x + "," + r.y);
		
		Mat dstPoints = Converters.vector_Point_to_Mat(listDsts, CvType.CV_32F);

		Mat perspectiveMmat = Imgproc.getPerspectiveTransform(srcPoints, dstPoints);
		
		Mat dst = new Mat();

		Imgproc.warpPerspective(src, dst, perspectiveMmat, src.size(), Imgproc.INTER_LINEAR + Imgproc.WARP_INVERSE_MAP,
				1, new Scalar(0));

		HandleImgUtils.saveImg(dst, "C:/Users/admin/Desktop/opencv/open/q/src-dst.jpg");

	}

	@Test
	public void test() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = HandleImgUtils.matFactory("C:/Users/admin/Desktop/opencv/open/q/x11.jpg");
		TestDistortion.warpPerspectiveImprove(src);
	}

	@Test
	/**
	 * 利用函数approxPolyDP来对指定的点集进行逼近
	 */
	public void testApproxPolyDP() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = HandleImgUtils.matFactory("C:/Users/admin/Desktop/opencv/open/q/x8.jpg");
		// 灰度话
		src = HandleImgUtils.gray(src);
		Mat cannyMat = HandleImgUtils.canny(src);
		Point[] points = HandleImgUtils.useApproxPolyDPFindPoints(cannyMat);
		for (int i = 0; i < points.length; i++) {
			System.out.println(points[i].y + " , " + points[i].x);
			src.put((int) points[i].y, (int) points[i].x, 255);
		}
		// 把点集划分到四个区域
		// 根据矩形中对角线最长，找到四个角的点坐标
		HandleImgUtils.saveImg(src, "C:/Users/admin/Desktop/opencv/open/q/x8-approxPolyDP.jpg");
	}

	@Test
	public void testFindFourPoint() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = HandleImgUtils.matFactory("C:/Users/admin/Desktop/opencv/open/q/x8.jpg");
		// 灰度话
		src = HandleImgUtils.gray(src);
		Point[] points = HandleImgUtils.findFourPoint(src);

		for (int i = 0; i < points.length; i++) {
			System.out.println(points[i].y + " , " + points[i].x);
			src.put((int) points[i].y, (int) points[i].x, 255);
		}

		HandleImgUtils.saveImg(src, "C:/Users/admin/Desktop/opencv/open/q/x8-FindFourPoint.jpg");
	}
	
	@Test
	/**
	 * 测试透视变换
	 */
	public void testWarpPerspective() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = HandleImgUtils.matFactory("C:/Users/admin/Desktop/opencv/open/q/x10.jpg");
		src = HandleImgUtils.warpPerspective(src);
		HandleImgUtils.saveImg(src, "C:/Users/admin/Desktop/opencv/open/q/x10-testWarpPerspective.jpg");
	}
}
