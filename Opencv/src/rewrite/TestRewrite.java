package rewrite;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

public class TestRewrite {

	@Before
	public void init() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	@Test
	/**
	 * 测试切割算法
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
		
		for(int i = 0 ; i < x.size() ; i++) {
			ImgUtils.saveImg(x.get(i), destPath + "x-"+i+".jpg");
		}
		
		
	}
	
	
}
