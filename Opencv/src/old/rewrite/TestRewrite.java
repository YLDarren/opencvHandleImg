package old.rewrite;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import java.util.List;

public class TestRewrite {

	@Before
	public void init() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	@Test
	/**
	 * 测试水平切割算法
	 */
	public void testCut() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/test/12/4.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/test/12/";
		Mat src = ImgUtils.matFactory(imgPath);
		src = ImgUtils.gray(src);
		src = ImgUtils.binaryzation(src);
//		ImgUtils.saveImg(src, destPath + "binary.jpg");

		src = ImgUtils.navieRemoveNoise(src, 1);
		src = ImgUtils.connectedRemoveNoise(src, 10.0);

//		ImgUtils.saveImg(src, destPath + "removeNoise.jpg");

		List<Mat> x = ImgUtils._cutImgX(src);
		System.out.println(x.size());
		for(int i = 0 ; i < x.size() ; i++) {
			ImgUtils.saveImg(x.get(i), destPath + "x-"+i+".jpg");
		}


	}


	@Test
	/**
	 * 测试垂直切割
	 */
	public void testY() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/test/12/x-1.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/test/12/";
		Mat src = ImgUtils.matFactory(imgPath);
		List<Mat> y = ImgUtils._cutImgY(src);
		System.out.println(y.size());
		for(int i = 0 ; i < y.size() ; i++) {
			ImgUtils.saveImg(y.get(i), destPath + "y-"+i+".jpg");
		}

	}

	@Test
	/**
	 * 测试寻找轮廓
	 */
	public void testFindContours() {
		String imgPath = "C:/Users/admin/Desktop/opencv/open/test/12/x-1.jpg";
		String destPath = "C:/Users/admin/Desktop/opencv/open/test/12/";
		Mat src = ImgUtils.matFactory(imgPath);
		
		ImgUtils.cut(src);
	}
	
	
}
