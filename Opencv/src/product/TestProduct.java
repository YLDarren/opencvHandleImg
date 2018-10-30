package product;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

/**
 * 测试
 * 
 * @author admin
 *
 */
public class TestProduct {

	@Before
	public void init() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}

	@Test
	public void test() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/product/correctMat.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/product/cut/";
		Mat src = ImageOpencvUtils.matFactory(imgPath);
		// 灰度话
		src = ImageOpencvUtils.gray(src);
		// 二值化
		src = ImageOpencvUtils.binaryzation(src);
		// 降噪
		src = ImageOpencvUtils.connectedRemoveNoise(src, 10.0);
		src = ImageOpencvUtils.erodeDilateImg(src);
		ImageOpencvUtils.saveImg(src, destPath + "removeNoise.jpg");
		// 切割
		List<Mat> matArray = ImageOpencvUtils.cut(src);
		System.out.println("matArray.size() " + matArray.size());
		for (int i = 0; i < matArray.size(); i++) {
			ImageOpencvUtils.saveImg(matArray.get(i), destPath + "cut-" + i + ".jpg");
		}
	}

	@Test
	/**
	 * 测试寻找目标图像
	 */
	public void testCorrect() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/product/p3.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/product/";
		Mat src = ImageOpencvUtils.matFactory(imgPath);
		Mat correctMat = ImageOpencvUtils.correct(src);
		ImageOpencvUtils.saveImg(correctMat, destPath + "correctMat.jpg");
	}

	@Test
	/**
	 * 测试画出所有轮廓
	 */
	public void testfindCon() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/product/cut/cut-1.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/product/";
		Mat src = ImageOpencvUtils.matFactory(imgPath);

		ImageOpencvUtils.findCon(src);
	}

	@Test
	/**
	 * 测试画出最大轮廓
	 */
	public void testfindMaxCon() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/product/cut/cut-8.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/product/";
		Mat src = ImageOpencvUtils.matFactory(imgPath);
		// 灰度话
		src = ImageOpencvUtils.gray(src);
		// 二值化
		src = ImageOpencvUtils.binaryzation(src);
		// 降噪
		src = ImageOpencvUtils.connectedRemoveNoise(src, 10.0);
		ImageOpencvUtils.findMaxCon(src);
	}
}
