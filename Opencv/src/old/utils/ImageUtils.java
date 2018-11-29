package old.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class ImageUtils {
	private static final int BLACK = 0;
	private static final int WHITE = 255;
	// 设置归一化图片的固定大小
	private static final Size dsize = new Size(32, 32);

	private Mat mat;

	/**
	 * 空参构造函数
	 */
	public ImageUtils() {

	}

	/**
	 * 通过图像路径创建一个mat矩阵
	 * 
	 * @param imgFilePath
	 *            图像路径
	 */
	public ImageUtils(String imgFilePath) {
		mat = Imgcodecs.imread(imgFilePath);
	}

	/**
	 * 通过mat矩阵初始化
	 * 
	 * @param mat
	 */
	public void ImageUtils(Mat mat) {
		this.mat = mat;
	}

	/**
	 * 动态加载图片
	 * 
	 * @param imgFilePath
	 */
	public void loadImg(String imgFilePath) {
		mat = Imgcodecs.imread(imgFilePath);
	}

	/**
	 * 获得mat矩阵
	 * 
	 * @return
	 */
	public Mat getMat() {
		return this.mat;
	}

	/**
	 * 获取图片高度的函数
	 * 
	 * @return
	 */
	public int getHeight() {
		return mat.rows();
	}
	
	public static int getHeight(Mat src) {
		return src.rows();
	}
	
	/**
	 * 获取图片宽度的函数
	 * 
	 * @return
	 */
	public int getWidth() {
		return mat.cols();
	}
	
	public static int getWidth(Mat src) {
		return src.cols();
	}
	/**
	 * 获取图片像素点的函数 我们处理的仅仅是单通道的
	 * 
	 * @param y
	 * @param x
	 * @return
	 */
	public int getPixel(int y, int x) {
		// 我们处理的是单通道灰度图
		return (int) mat.get(y, x)[0];
	}

	public static int getPixel(Mat src, int y, int x) {
		// 我们处理的是单通道灰度图
		return (int) src.get(y, x)[0];
	}

	/**
	 * 设置图片像素点的函数 处理的是单通道的
	 * 
	 * @param y
	 * @param x
	 * @param color
	 */
	public void setPixel(int y, int x, int color) {
		// 我们处理的是单通道灰度图
		mat.put(y, x, color);
	}

	public static void setPixel(Mat src, int y, int x, int color) {
		src.put(y, x, color);
	}

	/**
	 * 保存图片的函数
	 * 
	 * @param filename
	 * @return
	 */
	public boolean saveImg(String filename) {
		return Imgcodecs.imwrite(filename, mat);
	}

	public static boolean saveImg(String filename, Mat src) {
		return Imgcodecs.imwrite(filename, src);
	}

	/**
	 * 灰度化
	 */
	public void toGray() {
		Mat gray = new Mat();
		if (mat.channels() == 3) {
			Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
			mat = gray;
		} else {
			System.out.println("the image file is not RGB file!");
		}
	}

	public static Mat toGray(Mat src) {
		Mat gray = new Mat();
		if (src.channels() == 3) {
			Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
			src = gray;
		} else {
			System.out.println("the image file is not RGB file!");
		}
		return src;
	}

	/**
	 * 二值化 自适应阀值 1、先确定阀值 2、二值化 3、确保白底黑字
	 */
	public void binaryzation() {

		int threshold = 0, threshold_new = 127;
		int nWhite_count, nBlack_count;
		int nWhite_sum, nBlack_sum;
		int nValue;
		int i, j;

		int width = getWidth(), height = getHeight();

		while (threshold != threshold_new) {
			nWhite_sum = nBlack_sum = 0;
			nWhite_count = nBlack_count = 0;

			for (j = 0; j < height; ++j) {
				for (i = 0; i < width; i++) {
					nValue = getPixel(j, i);

					if (nValue > threshold_new) {
						nWhite_sum += nValue;
						nWhite_count++;
					} else {
						nBlack_sum += nValue;
						nBlack_count++;
					}
				}
			}

			nWhite_sum = nWhite_sum / nWhite_count;
			nBlack_sum = nBlack_sum / nBlack_count;
			threshold = threshold_new;
			threshold_new = (nWhite_sum + nBlack_sum) / 2;
		}

		int nBlack = 0;
		int nWhite = 0;

		for (j = 0; j < height; ++j) {
			for (i = 0; i < width; ++i) {
				nValue = getPixel(j, i);
				if (nValue > threshold_new) {
					setPixel(j, i, WHITE);
					nWhite++;
				} else {
					setPixel(j, i, BLACK);
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
						setPixel(j, i, WHITE);
					} else {
						setPixel(j, i, BLACK);
					}
				}
			}
		}
	}

	public static Mat binaryzation(Mat src) {
		int threshold = 0, threshold_new = 127;
		int nWhite_count, nBlack_count;
		int nWhite_sum, nBlack_sum;
		int nValue;
		int i, j;

		int width = getWidth(src), height = getHeight(src);

		while (threshold != threshold_new) {
			nWhite_sum = nBlack_sum = 0;
			nWhite_count = nBlack_count = 0;

			for (j = 0; j < height; ++j) {
				for (i = 0; i < width; i++) {
					nValue = getPixel(src , j , i);

					if (nValue > threshold_new) {
						nWhite_sum += nValue;
						nWhite_count++;
					} else {
						nBlack_sum += nValue;
						nBlack_count++;
					}
				}
			}

			nWhite_sum = nWhite_sum / nWhite_count;
			nBlack_sum = nBlack_sum / nBlack_count;
			threshold = threshold_new;
			threshold_new = (nWhite_sum + nBlack_sum) / 2;
		}

		int nBlack = 0;
		int nWhite = 0;

		for (j = 0; j < height; ++j) {
			for (i = 0; i < width; ++i) {
				nValue = getPixel(src , j, i);
				if (nValue > threshold_new) {
					setPixel(src , j, i, WHITE);
					nWhite++;
				} else {
					setPixel(src , j, i, BLACK);
					nBlack++;
				}
			}
		}

		// 确保白底黑字
		if (nBlack > nWhite) {
			for (j = 0; j < height; ++j) {
				for (i = 0; i < width; ++i) {
					nValue = (int) (src.get(j, i)[0]);
					if (nValue == 0) {
						setPixel(src , j, i, WHITE);
					} else {
						setPixel(src , j, i, BLACK);
					}
				}
			}
		}
		
		return src;
	}

	/**
	 * 8邻域降噪,又有点像9宫格降噪;即如果9宫格中心被异色包围，则同化
	 * 
	 * @param pNum
	 *            默认值为1
	 */
	public void navieRemoveNoise(int pNum) {
		int i, j, m, n, nValue, nCount;
		int nWidth = getWidth(), nHeight = getHeight();

		// 对图像的边缘进行预处理
		mat = this.StrokeWhite(mat);

		// 如果一个点的周围都是白色的，而它确实黑色的，删除它
		for (j = 1; j < nHeight - 1; ++j) {
			for (i = 1; i < nWidth - 1; ++i) {
				nValue = getPixel(j, i);
				if (nValue == 0) {
					nCount = 0;
					// 比较(j ,i)周围的9宫格，如果周围都是白色的，同化
					for (m = j - 1; m <= j + 1; ++m) {
						for (n = i - 1; n <= i + 1; ++n) {
							if (getPixel(m, n) == 0) {
								nCount++;
							}
						}
					}
					if (nCount <= pNum) {
						// 周围黑色点的个数小于阀值pNum,把该点设置白色
						setPixel(j, i, WHITE);
					}
				} else {
					nCount = 0;
					// 比较(j ,i)周围的9宫格，如果周围都是黑色的，同化
					for (m = j - 1; m <= j + 1; ++m) {
						for (n = i - 1; n <= i + 1; ++n) {
							if (getPixel(m, n) == 0) {
								nCount++;
							}
						}
					}
					if (nCount >= 7) {
						// 周围黑色点的个数大于等于7,把该点设置黑色;即周围都是黑色
						setPixel(j, i, BLACK);
					}
				}
			}
		}

	}

	public static Mat navieRemoveNoise(Mat src , int pNum) {
		int i, j, m, n, nValue, nCount;
		int nWidth = getWidth(src), nHeight = getHeight(src);

		// 对图像的边缘进行预处理
		src = StrokeWhite(src);

		// 如果一个点的周围都是白色的，而它确实黑色的，删除它
		for (j = 1; j < nHeight - 1; ++j) {
			for (i = 1; i < nWidth - 1; ++i) {
				nValue = getPixel(src , j, i);
				if (nValue == 0) {
					nCount = 0;
					// 比较(j ,i)周围的9宫格，如果周围都是白色的，同化
					for (m = j - 1; m <= j + 1; ++m) {
						for (n = i - 1; n <= i + 1; ++n) {
							if (getPixel(src , m, n) == 0) {
								nCount++;
							}
						}
					}
					if (nCount <= pNum) {
						// 周围黑色点的个数小于阀值pNum,把该点设置白色
						setPixel(src , j, i, WHITE);
					}
				} else {
					nCount = 0;
					// 比较(j ,i)周围的9宫格，如果周围都是黑色的，同化
					for (m = j - 1; m <= j + 1; ++m) {
						for (n = i - 1; n <= i + 1; ++n) {
							if (getPixel(src , m, n) == 0) {
								nCount++;
							}
						}
					}
					if (nCount >= 7) {
						// 周围黑色点的个数大于等于7,把该点设置黑色;即周围都是黑色
						setPixel(src , j, i, BLACK);
					}
				}
			}
		}
		return src;
	}
	
	/**
	 * 连通域降噪
	 * 
	 * @param pArea
	 *            默认值为1
	 */
	public void contoursRemoveNoise(double pArea) {
		int i, j, color = 1;
		int nWidth = getWidth(), nHeight = getHeight();

		for (i = 0; i < nWidth; ++i) {
			for (j = 0; j < nHeight; ++j) {
				if (getPixel(j, i) == BLACK) {
					// 用不同颜色填充连接区域中的每个黑色点
					// floodFill就是把一个点x的所有相邻的点都涂上x点的颜色，一直填充下去，直到这个连通区域内所有的点都被填充完为止
					Imgproc.floodFill(mat, new Mat(), new Point(i, j), new Scalar(color));
					color++;
				}
			}
		}

		// 统计不同颜色点的个数
		int[] ColorCount = new int[255];

		for (i = 0; i < nWidth; ++i) {
			for (j = 0; j < nHeight; ++j) {
				if (getPixel(j, i) != 255) {
					ColorCount[getPixel(j, i) - 1]++;
				}
			}
		}

		// 去除噪点
		for (i = 0; i < nWidth; ++i) {
			for (j = 0; j < nHeight; ++j) {

				if (ColorCount[getPixel(j, i) - 1] <= pArea) {
					setPixel(j, i, WHITE);
				}
			}
		}

		for (i = 0; i < nWidth; ++i) {
			for (j = 0; j < nHeight; ++j) {
				if (getPixel(j, i) < WHITE) {
					setPixel(j, i, BLACK);
				}
			}
		}

	}
	
	public static Mat contoursRemoveNoise(Mat src , double pArea) {
		int i, j, color = 1;
		int nWidth = getWidth(src), nHeight = getHeight(src);

		for (i = 0; i < nWidth; ++i) {
			for (j = 0; j < nHeight; ++j) {
				if (getPixel(src , j, i) == BLACK) {
					// 用不同颜色填充连接区域中的每个黑色点
					// floodFill就是把一个点x的所有相邻的点都涂上x点的颜色，一直填充下去，直到这个连通区域内所有的点都被填充完为止
					Imgproc.floodFill(src, new Mat(), new Point(i, j), new Scalar(color));
					color++;
				}
			}
		}

		// 统计不同颜色点的个数
		int[] ColorCount = new int[255];

		for (i = 0; i < nWidth; ++i) {
			for (j = 0; j < nHeight; ++j) {
				if (getPixel(src , j, i) != 255) {
					ColorCount[getPixel(src , j, i) - 1]++;
				}
			}
		}

		// 去除噪点
		for (i = 0; i < nWidth; ++i) {
			for (j = 0; j < nHeight; ++j) {

				if (ColorCount[getPixel(src , j, i) - 1] <= pArea) {
					setPixel(src , j, i, WHITE);
				}
			}
		}

		for (i = 0; i < nWidth; ++i) {
			for (j = 0; j < nHeight; ++j) {
				if (getPixel(src , j, i) < WHITE) {
					setPixel(src , j, i, BLACK);
				}
			}
		}

		return src;
	}

	/**
	 * 图像腐蚀/膨胀处理 腐蚀和膨胀对处理没有噪声的图像很有利，慎用
	 */
	public void erodeDilateImg() {
		Mat outImage = new Mat();

		// size 越小，腐蚀的单位越小，图片越接近原图
		Mat structImage = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 2));

		/**
		 * 图像腐蚀 腐蚀说明： 图像的一部分区域与指定的核进行卷积， 求核的最`小`值并赋值给指定区域。 腐蚀可以理解为图像中`高亮区域`的'领域缩小'。
		 * 意思是高亮部分会被不是高亮部分的像素侵蚀掉，使高亮部分越来越少。
		 */
		Imgproc.erode(mat, outImage, structImage, new Point(-1, -1), 2);
		mat = outImage;

		/**
		 * 膨胀 膨胀说明： 图像的一部分区域与指定的核进行卷积， 求核的最`大`值并赋值给指定区域。 膨胀可以理解为图像中`高亮区域`的'领域扩大'。
		 * 意思是高亮部分会侵蚀不是高亮的部分，使高亮部分越来越多。
		 */
		Imgproc.dilate(mat, outImage, structImage, new Point(-1, -1), 2);
		mat = outImage;

	}

	/**
	 * 图像切割,水平投影法切割
	 * (仅适用于有表格的图像)
	 * @return
	 */
	public List<Mat> cutImgX() {
		int i, j;
		int nWidth = getWidth(), nHeight = getHeight();
		int[] xNum = new int[nHeight], cNum;
		int average = 0;// 记录像素的平均值
		// 统计出每行黑色像素点的个数
		for (i = 0; i < nHeight; i++) {
			for (j = 0; j < nWidth; j++) {
				if (getPixel(i, j) == BLACK) {
					xNum[i]++;
				}

			}
		}

		// 经过测试这样得到的平均值最优
		cNum = Arrays.copyOf(xNum, xNum.length);
		Arrays.sort(cNum);
		for (i = 31 * nHeight / 32; i < nHeight; i++) {
			average += cNum[i];
		}
		average /= (nHeight / 32);

		// 把需要切割的y点都存到cutY中
		List<Integer> cutY = new ArrayList<Integer>();
		for (i = 0; i < nHeight; i++) {
			if (xNum[i] > average) {
				cutY.add(i);
			}
		}

		// 优化cutY,把距离相差在8以内的都清除掉
		if (cutY.size() != 0) {

			int temp = cutY.get(cutY.size() - 1);
			// 因为线条有粗细,优化cutY
			for (i = cutY.size() - 2; i >= 0; i--) {
				int k = temp - cutY.get(i);
				if (k <= 8) {
					cutY.remove(i);
				} else {
					temp = cutY.get(i);

				}

			}
		}

		// 把切割的图片都保存到YMat中
		List<Mat> YMat = new ArrayList<Mat>();
		for (i = 1; i < cutY.size(); i++) {
			// 设置感兴趣的区域
			int startY = cutY.get(i - 1);
			int height = cutY.get(i) - startY;
			Mat temp = new Mat(mat, new Rect(0, startY, nWidth, height));
			Mat t = new Mat();
			temp.copyTo(t);
			YMat.add(t);
		}

		return YMat;
	}

	/**
	 * 图像切割,垂直投影法切割
	 * (仅适用于有表格的图像)
	 * @return
	 */
	public List<Mat> cutImgY() {

		int i, j;
		int nWidth = getWidth(), nHeight = getHeight();
		int[] yNum = new int[nWidth], cNum;
		int average = 0;// 记录像素的平均值
		// // 统计出每列黑色像素点的个数
		for (i = 0; i < nWidth; i++) {
			for (j = 0; j < nHeight; j++) {
				if (getPixel(j, i) == BLACK) {
					yNum[i]++;
				}

			}
		}

		// 经过测试这样得到的平均值最优 , 平均值的选取很重要
		cNum = Arrays.copyOf(yNum, yNum.length);
		Arrays.sort(cNum);
		for (i = 31 * nWidth / 32; i < nWidth; i++) {
			average += cNum[i];
		}
		average /= (nWidth / 28);

		// 把需要切割的x点都存到cutY中,
		List<Integer> cutX = new ArrayList<Integer>();
		for (i = 0; i < nWidth; i += 2) {
			if (yNum[i] >= average) {
				cutX.add(i);
			}
		}

		if (cutX.size() != 0) {

			int temp = cutX.get(cutX.size() - 1);
			// 因为线条有粗细,优化cutY
			for (i = cutX.size() - 2; i >= 0; i--) {
				int k = temp - cutX.get(i);
				if (k <= 10) {
					cutX.remove(i);
				} else {
					temp = cutX.get(i);

				}

			}
		}

		// 把切割的图片都保存到YMat中
		List<Mat> XMat = new ArrayList<Mat>();
		for (i = 1; i < cutX.size(); i++) {
			// 设置感兴趣的区域
			int startX = cutX.get(i - 1);
			int width = cutX.get(i) - startX;
			Mat temp = new Mat(mat, new Rect(startX, 0, width, nHeight));
			Mat t = new Mat();
			temp.copyTo(t);
			XMat.add(t);
		}

		return XMat;
	}

	/**
	 * 把切割的图片归一化到相同的大小
	 */
	public Mat resize(Mat src) {
		src = this.toGray(src);
		src = this.binaryzation(src);
		// 描边
		src = this.StrokeWhite(src);
		// 降噪
		src = this.navieRemoveNoise(src, 1);
		src = this.contoursRemoveNoise(src, 1.0);
		Mat dst = new Mat();
		// 区域插值(INTER_AREA):图像放大时类似于线性插值，图像缩小时可以避免波纹出现。
		Imgproc.resize(src, dst, this.dsize, 0, 0, Imgproc.INTER_AREA);
		return dst;
	}

	/**
	 * 给单通道的图像边缘描边
	 */
	public static Mat StrokeWhite(Mat src) {
		
		int i, nWidth = getWidth(src), nHeight = getHeight(src);
		// 对图像的边缘进行预处理
		for (i = 0; i < nWidth; ++i) {
			setPixel(src , i, 0, WHITE);
			setPixel(src, i, nHeight - 1, WHITE);
		}
		for (i = 0; i < nHeight; ++i) {
			setPixel(src, 0, i, WHITE);
			setPixel(src, nWidth - 1, i, WHITE);
		}
		
		return src;
	}

}
