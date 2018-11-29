package old.test;

import old.utils.ImageUtils;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;

public class TestOpencv {

	public static void main(String[] args) {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/open/test.png");
		String dest = "C:/Users/admin/Desktop/open";
		Mat src = Imgcodecs.imread(imgFile.toString(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);

		Mat dst = new Mat();

		Imgproc.adaptiveThreshold(src, dst, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 13, 5);
		Imgcodecs.imwrite(dest + "/AdaptiveThreshold" + imgFile.getName(), dst);
	}

	@Test
	public void toGray() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/open/test.png");
		String dest = "C:/Users/admin/Desktop/open";

		Mat src = Imgcodecs.imread(imgFile.toString());
		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		src = gray;
		binaryzation(src);
		Imgcodecs.imwrite(dest + "/binaryzation" + imgFile.getName(), src);
	}

	@Test
	public void binaryzation() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/open/test.png");
		String dest = "C:/Users/admin/Desktop/open";
		// 先经过一步灰度化
		Mat src = Imgcodecs.imread(imgFile.toString());
		Mat gray = new Mat();
		Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
		src = gray;
		// 二值化
		binaryzation(src);
		Imgcodecs.imwrite(dest + "/binaryzation" + imgFile.getName(), src);
	}

	public void binaryzation(Mat mat) {
		int BLACK = 0;
		int WHITE = 255;

		int ucThre = 0, ucThre_new = 127;
		int nBack_count, nData_count;
		int nBack_sum, nData_sum;
		int nValue;
		int i, j;

		int width = mat.width(), height = mat.height();

		while (ucThre != ucThre_new) {
			nBack_sum = nData_sum = 0;
			nBack_count = nData_count = 0;

			for (j = 0; j < height; ++j) {
				for (i = 0; i < width; i++) {
					nValue = (int) mat.get(j, i)[0];

					if (nValue > ucThre_new) {
						nBack_sum += nValue;
						nBack_count++;
					} else {
						nData_sum += nValue;
						nData_count++;
					}
				}
			}

			nBack_sum = nBack_sum / nBack_count;
			nData_sum = nData_sum / nData_count;
			ucThre = ucThre_new;
			ucThre_new = (nBack_sum + nData_sum) / 2;
		}

		int nBlack = 0;
		int nWhite = 0;

		for (j = 0; j < height; ++j) {
			for (i = 0; i < width; ++i) {
				nValue = (int) mat.get(j, i)[0];
				if (nValue > ucThre_new) {
					mat.put(j, i, WHITE);
					nWhite++;
				} else {
					mat.put(j, i, BLACK);
					nBlack++;
				}
			}
		}

		// 确保白底黑字
		if (nBlack > nWhite) {
			for (j = 0; j < height; ++j) {
				for (i = 0; i < width; ++i) {
					nValue = (int) (mat.get(j, i)[0]);
					if (nValue == 0) {
						mat.put(j, i, WHITE);
					} else {
						mat.put(j, i, BLACK);
					}
				}
			}
		}
	}

	@Test
	public void testOpencvBinary() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/open/test.png");
		String dest = "C:/Users/admin/Desktop/open";

		Mat src = Imgcodecs.imread(imgFile.toString(), Imgcodecs.CV_LOAD_IMAGE_GRAYSCALE);
		Imgcodecs.imwrite(dest + "/AdaptiveThreshold1" + imgFile.getName(), src);

		Mat dst = new Mat();

		Imgproc.adaptiveThreshold(src, dst, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY, 13, 5);
		Imgcodecs.imwrite(dest + "/AdaptiveThreshold2" + imgFile.getName(), dst);

		Imgproc.adaptiveThreshold(src, dst, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, 13, 5);
		Imgcodecs.imwrite(dest + "/AdaptiveThreshold3" + imgFile.getName(), dst);

		Imgproc.adaptiveThreshold(src, dst, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY, 13, 5);
		Imgcodecs.imwrite(dest + "/AdaptiveThreshold4" + imgFile.getName(), dst);

		Imgproc.adaptiveThreshold(src, dst, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 13, 5);
		Imgcodecs.imwrite(dest + "/AdaptiveThreshold5" + imgFile.getName(), dst);
	}

	@Test
	public void test() {
		// 这个必须要写,不写报java.lang.UnsatisfiedLinkError
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		File imgFile = new File("C:/Users/admin/Desktop/open/test.png");
		String dest = "C:/Users/admin/Desktop/open";
		
		ImageUtils img = new ImageUtils(imgFile.toString());
		
		//灰度化
		img.toGray();
		//二值化
		img.binaryzation();
		
		//8邻域降噪
		img.navieRemoveNoise(1);
		
		img.saveImg(dest + "navieRemoveNoise-" + imgFile.getName() );
		
	}
}
