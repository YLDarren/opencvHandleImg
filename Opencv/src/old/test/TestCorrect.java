package old.test;

import old.utils.HandleImgUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import product.ImageOpencvUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 主要测试图片矫正功能， 并添加主工具类中
 * @author admin
 *
 */
public class TestCorrect {

	/**
	 * 边缘检测
	 * @param src
	 */
	public static Mat canny(Mat src) {
		Mat mat = src.clone();
		Imgproc.Canny(src, mat, 60, 200);
		HandleImgUtils.saveImg(mat, "C:/Users/admin/Desktop/opencv/open/x/canny.jpg");
		return mat;
	}
	
	/**
	 * 找图像轮廓findContours()
	 * @param src
	 */
	public static void findContours(Mat src) {
		//边缘检测
		Mat cannyMat = TestCorrect.canny(src);
		src = cannyMat;
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		/**
		 * mode参数
		 * RETR_EXTERNAL:表示只检测最外层轮廓，对所有轮廓设置hierarchy[i][2]=hierarchy[i][3]=-1 
		 * RETR_LIST:提取所有轮廓，并放置在list中，检测的轮廓不建立等级关系 
		 * RETR_CCOMP:提取所有轮廓，并将轮廓组织成双层结构(two-level hierarchy),顶层为连通域的外围边界，次层位内层边界 
		 * RETR_TREE:提取所有轮廓并重新建立网状轮廓结构 
		 * RETR_FLOODFILL：官网没有介绍，应该是洪水填充法 
		 */
		/**
		 * method参数
		 * CHAIN_APPROX_NONE：获取每个轮廓的每个像素，相邻的两个点的像素位置差不超过1 
		 * CHAIN_APPROX_SIMPLE：压缩水平方向，垂直方向，对角线方向的元素，值保留该方向的重点坐标，如果一个矩形轮廓只需4个点来保存轮廓信息 
		 * CHAIN_APPROX_TC89_L1和CHAIN_APPROX_TC89_KCOS使用Teh-Chinl链逼近算法中的一种 
		 */
		Imgproc.findContours(src, contours, hierarchy, Imgproc.RETR_EXTERNAL , Imgproc.CHAIN_APPROX_NONE , new Point(0 , 0));
		
		//绘制轮廓图
		Mat drawing = new Mat(cannyMat.size() , CvType.CV_8UC3);
		
		for(int i = 0 ; i < contours.size() ; i++) {
//			Scalar color = Scalar( rng.uniform(0, 255), rng.uniform(0,255), rng.uniform(0,255) );
			Scalar color = new Scalar( 255 , 255 , 255);
//		       drawContours( drawing, contours, i, color, 2, 8, hierarchy, 0, Point() );
			Imgproc.drawContours(drawing, contours, i, color , 2 , 8 , hierarchy , 0 , new Point());
		}
		
		HandleImgUtils.saveImg(drawing, "C:/Users/admin/Desktop/opencv/open/x/correct.jpg");
	}
	
	
	/**
	 * 矫正图像
	 * @param src
	 */
	public static void correct(Mat src) {
		Mat srcImg = src;
		//边缘检测
		Mat cannyMat = TestCorrect.canny(src);
		
		src = cannyMat;
		
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();
		
		//寻找轮廓
		Imgproc.findContours(src, contours, hierarchy, Imgproc.RETR_EXTERNAL , Imgproc.CHAIN_APPROX_NONE , new Point(0 , 0));
		
		//找出匹配到的最大轮廓
		double area = Imgproc.boundingRect(contours.get(0)).area();
		int index = 0;
		
		//找出匹配到的最大轮廓
		for(int i = 0 ; i < contours.size() ; i++) {
			double tempArea = Imgproc.boundingRect(contours.get(i)).area();
			if(tempArea > area) {
				area = tempArea;
				index = i;
			}
		}
		
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contours.get(index).toArray());
		
		RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);
		
		//获取矩形的四个顶点
		Point[] rectPoint = new Point[4];
		rect.points(rectPoint);
		
		double angle = rect.angle + 90;
		
		Point center = rect.center;
		
		Mat RoiSrcimg = new Mat(cannyMat.size() , cannyMat.type());
		
		srcImg.copyTo(RoiSrcimg);
		
		//得到旋转矩阵算子
		Mat matrix = Imgproc.getRotationMatrix2D(center, angle, 0.8);
		
		Imgproc.warpAffine(RoiSrcimg,RoiSrcimg,matrix,RoiSrcimg.size(),1,0,new Scalar(0,0,0));
		
		HandleImgUtils.saveImg(srcImg, "C:/Users/admin/Desktop/opencv/open/x/srcImg.jpg");
		
		HandleImgUtils.saveImg(RoiSrcimg, "C:/Users/admin/Desktop/opencv/open/x/correct.jpg");
	}
	
	@Test
	public void test() {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat src = HandleImgUtils.matFactory("C:/Users/admin/Desktop/opencv/open/x/x6.jpg");
		TestCorrect.correct(src);
		
	}


    /**
     * 测试
     *
     * @author admin
     *
     */
    public static class TestProduct {

        @Before
        public void init() {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        }

        @Test
        public void test() {
            String imgPath = "C:/Users/X240/Desktop/opencv/product/correctMat.jpg";
            String destPath = "C:/Users/X240/Desktop/opencv/product/cut/";
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
         * 测试切割后的图像归一化
         */
        public void testResize(){
            String imgPath = "C:/Users/X240/Desktop/opencv/product/cut/cut-0.jpg";
            String destPath = "C:/Users/X240/Desktop/opencv/product/cut/";
            Mat src = ImageOpencvUtils.matFactory(imgPath);
            // 灰度话
            src = ImageOpencvUtils.gray(src);
            // 二值化
            src = ImageOpencvUtils.binaryzation(src);

            //归一化
            src = ImageOpencvUtils.resize(src);

            ImageOpencvUtils.saveImg(src, destPath + "resize.jpg");
        }

        @Test
        /**
         * 测试寻找目标图像
         */
        public void testCorrect() {
            String imgPath = "C:/Users/X240/Desktop/opencv/product/p6.jpg";
            String destPath = "C:/Users/X240/Desktop/opencv/product/";
            Mat src = ImageOpencvUtils.matFactory(imgPath);
            Mat correctMat = ImageOpencvUtils.correct(src);
            ImageOpencvUtils.saveImg(correctMat, destPath + "correctMat.jpg");
        }

        @Test
        /**
         * 测试画出所有轮廓
         */
        public void testfindCon() {
            String imgPath = "C:/Users/X240/Desktop/opencv/product/cut/cut-1.jpg";
            String destPath = "C:/Users/X240/Desktop/opencv/product/";
            Mat src = ImageOpencvUtils.matFactory(imgPath);

            ImageOpencvUtils.findCon(src);
        }

        @Test
        /**
         * 测试画出最大轮廓
         */
        public void testfindMaxCon() {
            String imgPath = "C:/Users/X240/Desktop/opencv/product/cut/cut-8.jpg";
            String destPath = "C:/Users/X240/Desktop/opencv/product/";
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
}
