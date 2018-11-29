package old.test;


import old.reconsitution.ImgUtils2;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;


public class TestImgUtils2 {

	@Before
	public void init() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	@Test
	/**
	 * 测试旋转图像
	 */
	public void testCorrect() {

		String num = "2";
		String imgPath = "C:/Users/admin/Desktop/opencv/open/test/11/11.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/test/11/";

		Mat src = ImgUtils2.matFactory(imgPath);
		src = ImgUtils2.correct(src);
		ImgUtils2.saveImg(src, destPath + "correct-11.jpg");

	}

	@Test
	/**
	 * 测试透视变换矫正图像
	 */
	public void testWarpPerspective() {
		String num = "2";
		String imgPath = "C:/Users/admin/Desktop/opencv/open/test/" + num + "/correct-" + num + ".jpg";
		 String destPath = "C:/Users/admin/Desktop/opencv/open/test/" + num + "/";
		Mat src = ImgUtils2.matFactory(imgPath);
		src = ImgUtils2.warpPerspective(src);
		 ImgUtils2.saveImg(src, destPath + "warpPerspective.jpg");
	}

	
}
