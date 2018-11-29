package old.test;

import old.utils.HandleImgUtils;
import old.utils.ImageUtils;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.File;
import java.util.List;

public class TestImageUtils {

	@Test
	public void test() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/X-1123.jpg");
		String dest = "C:/Users/admin/Desktop/opencv/open/";

		ImageUtils img = new ImageUtils(imgFile.toString());

		// 灰度化
		img.toGray();
		// 二值化
		img.binaryzation();

		// 8邻域降噪
		img.navieRemoveNoise(1);

		// 连通域降噪
		img.contoursRemoveNoise(1.0);

		// 图像腐蚀、膨胀处理;对几乎没噪点的图像
		// img.erodeDilateImg();

		// img.saveImg(dest + "erodeDilateImg-" + imgFile.getName());

		// 水平切割
		// List<Mat> list = img.cutImgX();
		// for(int i = 0 ; i < list.size() ; i++) {
		// Imgcodecs.imwrite(dest + "X-"+ i + imgFile.getName() , list.get(i) );
		// }

		// 垂直切割
		List<Mat> list = img.cutImgY();
		for (int i = 0; i < list.size(); i++) {
			Imgcodecs.imwrite(dest + "Y" + i + imgFile.getName(), list.get(i));
		}

	}

	// @Test
	/**
	 * 测试像素点 三通道的
	 */
	public void testMat() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/abc.png");
		String dest = "C:/Users/admin/Desktop/opencv/open/";

		ImageUtils img = new ImageUtils(imgFile.toString());

		for (int i = 0; i < img.getWidth(); i++) {
			for (int j = 0; j < img.getHeight(); j++) {
				img.getPixel(i, j);
				System.out.println();
			}
		}
	}

	@Test
	/**
	 * 测试归一化到相同的大小
	 */
	public void testResize() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/Y6X-1123.jpg");
		String dest = "C:/Users/admin/Desktop/opencv/open/";

		ImageUtils img = new ImageUtils(imgFile.toString());

		Imgcodecs.imwrite(dest + "resize-" + imgFile.getName(), img.resize(img.getMat()));

	}

	@Test
	/**
	 * 测试二值化
	 */
	public void testBinaryzation() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/123.jpg");
		String dest = "C:/Users/admin/Desktop/opencv/open/";

		ImageUtils img = new ImageUtils(imgFile.toString());

		img.toGray();

		img.binaryzation();

		Imgcodecs.imwrite(dest + "binaryzation-" + imgFile.getName(), img.getMat());
	}

	@Test
	/**
	 * 测试降噪
	 */
	public void testRemoveNoise() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/xie.jpg");
		String dest = "C:/Users/admin/Desktop/opencv/open/";

		ImageUtils img = new ImageUtils(imgFile.toString());

		img.toGray();

		img.binaryzation();

		img.navieRemoveNoise(1);

		img.contoursRemoveNoise(1.0);

		Imgcodecs.imwrite(dest + "removeNoise-" + imgFile.getName(), img.getMat());
	}

	@Test
	public void testHandleImgUtils() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/test.jpg");
		String dest = "C:/Users/admin/Desktop/opencv/open/";

		Mat mat = HandleImgUtils.matFactory(imgFile.toString());

		// 灰度化
		mat = HandleImgUtils.gray(mat);
		// 二值化
		mat = HandleImgUtils.binaryzation(mat);

		mat = HandleImgUtils.strokeWhite(mat);

		// 8邻域降噪
		mat = HandleImgUtils.navieRemoveNoise(mat, 1);

		// 连通域降噪
		mat = HandleImgUtils.connectedRemoveNoise(mat, 10.0);

		HandleImgUtils.saveImg(mat, dest + "noise-" + imgFile.getName());

		// 水平切割
		 List<Mat> list = HandleImgUtils.cutImgX(mat);
		 System.out.println(list.size());
		 for(int i = 0 ; i < list.size() ; i++) {
			 Imgcodecs.imwrite(dest + "X-"+ i + imgFile.getName() , list.get(i) );
		 }

		// 垂直切割
		// List<Mat> list = HandleImgUtils.cutImgY(mat);
		// for (int i = 0; i < list.size(); i++) {
		// Imgcodecs.imwrite(dest + "Y" + i + imgFile.getName(), list.get(i));
		// }

	}

	@Test
	public void testHandleResize() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/123/123.jpg");
		String dest = "C:/Users/admin/Desktop/opencv/open/";

		Mat mat = HandleImgUtils.matFactory(imgFile.toString());
		
		System.out.println(mat.channels());
		
//		Imgcodecs.imwrite(dest + "resize-" + imgFile.getName(), HandleImgUtils.resize(mat));

	}

	@Test
	/**
	 * 测试confirmPosition系列函数
	 */
	public void testConfirmPosition() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/Y0X-1123.jpg");
		String dest = "C:/Users/admin/Desktop/opencv/open/";

		Mat mat = HandleImgUtils.matFactory(imgFile.toString());

		HandleImgUtils.trimImg(mat);
	}

	@Test
	/**
	 * 测试判断图像是表格图像还是一般图像
	 */
	public void testJudgeImg() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/a1.png");

		Mat mat = HandleImgUtils.matFactory(imgFile.toString());

		// 灰度化
		mat = HandleImgUtils.gray(mat);
		// 二值化
		mat = HandleImgUtils.binaryzation(mat);

		mat = HandleImgUtils.strokeWhite(mat);

		// 8邻域降噪
		mat = HandleImgUtils.navieRemoveNoise(mat, 1);

		// 连通域降噪
		mat = HandleImgUtils.connectedRemoveNoise(mat, 10.0);

		System.out.println(HandleImgUtils.judgeImg(mat));
	}

	@Test
	/**
	 * 测试切割普通的图像
	 */
	public void testCutNormalImg() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/abc.png");
		String dest = "C:/Users/admin/Desktop/opencv/open/";

		Mat mat = HandleImgUtils.matFactory(imgFile.toString());

		// 灰度化
		mat = HandleImgUtils.gray(mat);
		// 二值化
		mat = HandleImgUtils.binaryzation(mat);

		mat = HandleImgUtils.strokeWhite(mat);

		// 8邻域降噪
		mat = HandleImgUtils.navieRemoveNoise(mat, 1);

		// 连通域降噪
		mat = HandleImgUtils.connectedRemoveNoise(mat, 10.0);

		// 垂直切割
		List<Mat> list = HandleImgUtils.cutNormalImgY(mat);
		for (int i = 0; i < list.size(); i++) {
			Imgcodecs.imwrite(dest + "NormalY" + i + imgFile.getName(), list.get(i));
		}
	}

	@Test
	/**
	 * 测试判断图像是否为空
	 */
	public void testJudgeEmpty() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/a3.png");
		String dest = "C:/Users/admin/Desktop/opencv/open/";

		Mat mat = HandleImgUtils.matFactory(imgFile.toString());
		// 灰度化
		mat = HandleImgUtils.gray(mat);
		// 二值化
		mat = HandleImgUtils.binaryzation(mat);

		System.out.println(HandleImgUtils.judgeEmpty(mat));
	}

	@Test
	/**
	 * 测试整体流程
	 */
	public void testProcess() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/opencv/open/123/123.jpg");
		String dest = "C:/Users/admin/Desktop/opencv/open/123/";
		
		Mat mat = HandleImgUtils.matFactory(imgFile.toString());
		HandleImgUtils.handleImg(mat, dest, imgFile.getName());
	}
	
	@Test
	/**
	 * 测试矫正图像
	 */
	public void testCorrect() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = HandleImgUtils.matFactory("C:/Users/admin/Desktop/opencv/open/x/x8.jpg");
		HandleImgUtils.correct(src);
	}

}
