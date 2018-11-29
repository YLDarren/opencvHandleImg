package old.test;

import old.reconsitution.ImgUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.List;

/**
 * 测试 重构的工具类
 * @author admin
 *
 */
public class TestReconsitution {
	
	@Before
	public void init() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	@Test
	/**
	 * 测试灰度话
	 */
	public void testgray() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/123.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/";
		Mat src = ImgUtils.matFactory(imgPath);
		src = ImgUtils.gray(src);
		ImgUtils.saveImg(src, destPath + "gray.jpg");
	}
	
	@Test
	/**
	 * 测试二值化
	 */
	public void testBinary() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/123.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/";
		Mat src = ImgUtils.matFactory(imgPath);
		src = ImgUtils.gray(src);
		src = ImgUtils.binaryzation(src);
		ImgUtils.saveImg(src, destPath + "binary.jpg");
	}
	
	@Test
	/**
	 * 测试降噪
	 */
	public void testRemoveNoise() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/123.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/";
		Mat src = ImgUtils.matFactory(imgPath);
		src = ImgUtils.gray(src);
		src = ImgUtils.binaryzation(src);
		src = ImgUtils.navieRemoveNoise(src, 1);
		src = ImgUtils.connectedRemoveNoise(src, 1.0);
		ImgUtils.saveImg(src, destPath + "removenoise.jpg");
	}
	
	@Test
	/**
	 * 测试水平切割
	 */
	public void testX() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/123.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/";
		Mat src = ImgUtils.matFactory(imgPath);
		src = ImgUtils.gray(src);
		src = ImgUtils.binaryzation(src);
		src = ImgUtils.navieRemoveNoise(src, 1);
		src = ImgUtils.connectedRemoveNoise(src, 1.0);
		List<Mat> list = ImgUtils.cutImgX(src);
		for(int i = 0 ; i < list.size() ; i++) {
			ImgUtils.saveImg(list.get(i) , destPath + "X-" + i  + ".jpg");
		}
	}
	
	@Test
	/**
	 * 测试垂直切割
	 */
	public void testY() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/X-1.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/";
		Mat src = ImgUtils.matFactory(imgPath);
		src = ImgUtils.gray(src);
		src = ImgUtils.binaryzation(src);
		src = ImgUtils.navieRemoveNoise(src, 1);
		src = ImgUtils.connectedRemoveNoise(src, 1.0);
		List<Mat> list = ImgUtils.cutImgY(src);
		for(int i = 0 ; i < list.size() ; i++) {
			ImgUtils.saveImg(list.get(i) , destPath + "Y-" + i  + ".jpg");
		}
	}
	
	@Test
	/**
	 * 测试归一化
	 */
	public void testResize() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/Y-0.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/";
		Mat src = ImgUtils.matFactory(imgPath);
		src = ImgUtils.resize(src);
		ImgUtils.saveImg(src, destPath + "resize.jpg");
	}
	
	@Test
	/**
	 * 测试矫正图像--旋转图像
	 */
	public void testCorrect() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/x/x8.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/x/";
		Mat src = ImgUtils.matFactory(imgPath);
		src = ImgUtils.gray(src);
		src = ImgUtils.correct(src);
		ImgUtils.saveImg(src, destPath + "correct.jpg");
	}
	
	@Test
	/**
	 * 测试透视变换矫正图像
	 */
	public void testWarpPerspective() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/correct.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/reconsitution/";
		Mat src = ImgUtils.matFactory(imgPath);
		src = ImgUtils.warpPerspective(src);
		ImgUtils.saveImg(src, destPath + "warpPerspective.jpg");
	}
	
}
