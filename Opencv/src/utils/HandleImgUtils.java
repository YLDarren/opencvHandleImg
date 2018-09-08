package utils;

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

/**
 * 处理图像的工具类
 * 
 * @author admin
 *
 */
public class HandleImgUtils {
	private static final int BLACK = 0;
	private static final int WHITE = 255;
	//设置归一化图片的固定大小
	private static final Size dsize = new Size(32 , 32);

	// 私有化构造函数
	private HandleImgUtils() {
	};

	/**
	 * 输入图像路径，返回mat矩阵
	 * 
	 * @param imgPath
	 *            图像路径
	 * @return
	 */
	public static Mat matFactory(String imgPath) {
		return Imgcodecs.imread(imgPath);
	}

	/**
	 * 输入图像mat矩阵对象，返回图像的宽度
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static int getImgWidth(Mat src) {
		return src.cols();
	}

	/**
	 * 输入图像mat矩阵对象，返回图像的高度
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static int getImgHeight(Mat src) {
		return src.rows();
	}

	/**
	 * 获取图像(y,x)点的像素，我们处理是单通道的
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param y
	 *            y坐标轴
	 * @param x
	 *            x坐标轴
	 * @return
	 */
	public static int getPixel(Mat src, int y, int x) {
		return (int) src.get(y, x)[0];
	}

	/**
	 * 设置图像(y,x)点的像素，我们处理是单通道的
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param y
	 *            y坐标轴
	 * @param x
	 *            x坐标轴
	 * @param color
	 *            像素[0-255]
	 */
	public static void setPixel(Mat src, int y, int x, int color) {
		src.put(y, x, color);
	}

	/**
	 * 保存图像
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param filePath
	 *            要保存图像的路径及名字
	 * @return
	 */
	public static boolean saveImg(Mat src, String filePath) {
		return Imgcodecs.imwrite(filePath, src);
	}

	/**
	 * 图像灰度话
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static Mat gray(Mat src) {
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
	 * 图像二值化--自适应阀值 1、确定阀值 2、二值化 3、确保白底黑字
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static Mat binaryzation(Mat src) {
		int threshold = 0, threshold_new = 127;
		int nWhite_count, nBlack_count;
		int nWhite_sum, nBlack_sum;
		int nValue;
		int i, j;
		int width = getImgWidth(src), height = getImgHeight(src);
		// 1、确定阀值
		while (threshold != threshold_new) {
			nWhite_sum = nBlack_sum = 0;
			nWhite_count = nBlack_count = 0;

			for (j = 0; j < height; j++) {
				for (i = 0; i < width; i++) {
					nValue = getPixel(src, j, i);
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

		// 2、二值化
		nWhite_sum = nBlack_sum = 0;

		for (j = 0; j < height; j++) {
			for (i = 0; i < width; i++) {
				nValue = getPixel(src, j, i);
				if (nValue > threshold_new) {
					setPixel(src, j, i, WHITE);
					nWhite_sum++;
				} else {
					setPixel(src, j, i, BLACK);
					nBlack_sum++;
				}
			}
		}

		// 3、确保白底黑字
		if (nBlack_sum > nWhite_sum) {
			for (j = 0; j < height; j++) {
				for (i = 0; i < width; i++) {
					nValue = getPixel(src, j, i);
					if (nValue == 0) {
						setPixel(src, j, i, WHITE);
					} else {
						setPixel(src, j, i, BLACK);
					}
				}
			}
		}
		return src;
	}

	/**
	 * 8邻域降噪，又有点像9宫格降噪;即如果9宫格中心被异色包围，则同化
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param pNum
	 *            阀值
	 * @return
	 */
	public static Mat navieRemoveNoise(Mat src, int pNum) {
		int i, j, m, n, nValue, nCount;
		int width = getImgWidth(src), height = getImgHeight(src);

		// 对图像的边缘进行预处理
		src = strokeWhite(src);

		// 如果一个点的周围都是白色的，自己确实黑色的，同化
		for (j = 1; j < height - 1; j++) {
			for (i = 1; i < width - 1; i++) {
				nValue = getPixel(src, j, i);
				if (nValue == 0) {
					nCount = 0;
					// 比较(j , i)周围的9宫格，如果周围都是白色，同化
					for (m = j - 1; m <= j + 1; m++) {
						for (n = i - 1; n <= i + 1; n++) {
							if (getPixel(src, m, n) == 0) {
								nCount++;
							}
						}
					}
					if (nCount <= pNum) {
						// 周围黑色点的个数小于阀值pNum,把自己设置成白色
						setPixel(src, j, i, WHITE);
					}
				} else {
					nCount = 0;
					// 比较(j , i)周围的9宫格，如果周围都是黑色，同化
					for (m = j - 1; m <= j + 1; m++) {
						for (n = i - 1; n <= i + 1; n++) {
							if (getPixel(src, m, n) == 0) {
								nCount++;
							}
						}
					}
					if (nCount >= 8 - pNum) {
						// 周围黑色点的个数大于等于(8 - pNum),把自己设置成黑色
						setPixel(src, j, i, BLACK);
					}
				}
			}
		}
		return src;
	}

	/**
	 * 连通域降噪
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param pArea
	 *            阀值
	 * @return
	 */
	public static Mat connectedRemoveNoise(Mat src, double pArea) {
		int i, j, color = 1;
		int width = getImgWidth(src), height = getImgHeight(src);

		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				if (getPixel(src, j, i) == BLACK) {
					// 用不同的颜色填充连接区域中的每个黑色点
					// floodFill就是把与点(i , j)的所有相连通的区域都涂上color颜色
					Imgproc.floodFill(src, new Mat(), new Point(i, j), new Scalar(color));
					color++;
				}
			}
		}

		// 统计不同颜色点的个数
		int[] colorCount = new int[255];
		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				int nValue = getPixel(src, j, i);
				if (nValue != 255) {
					colorCount[nValue - 1]++;
				}
			}
		}

		// 去除噪点
		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				if (colorCount[getPixel(src, j, i) - 1] <= pArea) {
					setPixel(src, j, i, WHITE);
				}
			}
		}

		// 二值化
		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				if (getPixel(src, j, i) < WHITE) {
					setPixel(src, j, i, BLACK);
				}
			}
		}
		return src;
	}

	/**
	 * 水平投影法切割，仅适用于表格的图像
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static List<Mat> cutImgX(Mat src) {
		int i, j;
		int width = getImgWidth(src), height = getImgHeight(src);
		int[] xNum, cNum;
		int average = 0;// 记录黑色像素和的平均值
		// 统计出每行黑色像素点的个数
		xNum = countPixel(src, height, width, true);

		// 经过测试这样得到的平均值最优
		cNum = Arrays.copyOf(xNum, xNum.length);
		Arrays.sort(cNum);
		for (i = 31 * height / 32; i < height; i++) {
			average += cNum[i];
		}
		average /= (height / 32);

		// 把需要切割的y轴点存到cutY中
		List<Integer> cutY = new ArrayList<Integer>();
		for (i = 0; i < height; i++) {
			if (xNum[i] > average) {
				cutY.add(i);
			}
		}

		// 优化cutY,把距离相差在8以内的都清除掉
		if (cutY.size() != 0) {
			int temp = cutY.get(cutY.size() - 1);
			// 因为线条有粗细，优化cutY
			for (i = cutY.size() - 2; i >= 0; i--) {
				int k = temp - cutY.get(i);
				if (k <= 8) {
					cutY.remove(i);
				} else {
					temp = cutY.get(i);
				}
			}
		}

		// 把切割的图片保存到YMat中
		List<Mat> YMat = new ArrayList<Mat>();
		for (i = 1; i < cutY.size(); i++) {
			// 设置感兴趣区域
			int startY = cutY.get(i - 1);
			int h = cutY.get(i) - startY;
			Mat temp = new Mat(src, new Rect(0, startY, width, h));
			Mat t = new Mat();
			temp.copyTo(t);
			YMat.add(t);
		}
		return YMat;
	}

	/**
	 * 垂直投影法切割，仅适用于表格的图像
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static List<Mat> cutImgY(Mat src) {
		int i, j;
		int width = getImgWidth(src), height = getImgHeight(src);
		int[] yNum, cNum;
		int average = 0;// 记录黑色像素和的平均值
		// 统计出每列黑色像素点的个数
		yNum = countPixel(src, width, height, false);

		// 经过测试这样得到的平均值最优，平均值的选取很重要
		cNum = Arrays.copyOf(yNum, yNum.length);
		Arrays.sort(cNum);
		for (i = 31 * width / 32; i < width; i++) {
			average += cNum[i];
		}
		average /= (width / 28);

		// 把需要切割的x轴的点存到cutX中
		List<Integer> cutX = new ArrayList<Integer>();
		for (i = 0; i < width; i += 2) {
			if (yNum[i] >= average) {
				cutX.add(i);
			}
		}

		// 优化cutX
		if (cutX.size() != 0) {
			int temp = cutX.get(cutX.size() - 1);
			// 因为线条有粗细，优化cutX
			for (i = cutX.size() - 2; i >= 0; i--) {
				int k = temp - cutX.get(i);
				if (k <= 10) {
					cutX.remove(i);
				} else {
					temp = cutX.get(i);
				}
			}
		}

		// 把切割的图片都保存到XMat中
		List<Mat> XMat = new ArrayList<Mat>();
		for (i = 1; i < cutX.size(); i++) {
			// 设置感兴趣的区域
			int startX = cutX.get(i - 1) + 4;
			int w = cutX.get(i) - startX - 4;
			Mat temp = new Mat(src, new Rect(startX, 4, w, height - 4));
			Mat t = new Mat();
			temp.copyTo(t);
			XMat.add(t);
		}
		return XMat;
	}

	/**
	 * 把图片归一化到相同的大小
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static Mat resize(Mat src) {
		src =  trimImg(src);
		src = gray(src);
		src = binaryzation(src);
		src = navieRemoveNoise(src, 1);
		src = connectedRemoveNoise(src, 1.0);
		Mat dst = new Mat();
		// 区域插值(INTER_AREA):图像放大时类似于线性插值，图像缩小时可以避免波纹出现。
		Imgproc.resize(src, dst, dsize, 0, 0, Imgproc.INTER_AREA);
		return dst;
	}
	
	/**
	 * 裁剪图像，主要使切割图像的内容更靠中间(即去除内容周边的空白)
	 * 
	 * @param src  Mat矩阵对象
	 * @return
	 */
	public static Mat trimImg(Mat src) {
		//定义具体内容开始的点
		int startUp = 0 , startDown = 0 , startLeft = 0 , startRight = 0;
		int width = getImgWidth(src) , height = getImgHeight(src);
		startUp = confirmPositionUp(src , width , height);
		startDown = confirmPositionDown(src , width , height);
		startLeft = confirmPositionLeft(src , width , height);
		startRight = confirmPositionRight(src , width , height);
		startUp = startUp == 10 ? 0 : startUp - 10; 
		startDown = height - startDown == 10 ? height : startDown + 10;
		startLeft = startLeft == 10 ? 0 : startLeft - 10;
		startRight = width - startRight == 10 ? width : startRight + 10;
		//设置感兴趣的区域
		Mat temp = new Mat(src, new Rect(startLeft , startUp, startRight - startLeft, startDown - startUp));
		Mat t = new Mat();
		temp.copyTo(t);
		return t;
	}
	
	/**
	 * 确定图像内容startUp的位置
	 * @param src Mat矩阵对象
	 * @param width 图像的宽
	 * @param height 图像的高
	 * @return
	 */
	public static int confirmPositionUp(Mat src , int width , int  height) {
		int i , j;
		for(i = 10 ; i < height - 10 ; i++ ) {
			for(j = 10 ; j < width - 10 ; j++) {
				if(getPixel(src , i , j) != WHITE) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * 确定图像内容startDown的位置
	 * @param src Mat矩阵对象
	 * @param width 图像的宽
	 * @param height 图像 的高
	 * @return
	 */
	public static int confirmPositionDown(Mat src , int width , int  height) {
		int i , j;
		for(i = height - 10 ; i > 10 ; i-- ) {
			for(j = 10 ; j < width - 10 ; j++) {
				if(getPixel(src , i , j) != WHITE) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * 确定图像内容startLeft的位置
	 * @param src Mat矩阵对象
	 * @param width 图像的宽
	 * @param height 图像的高
	 * @return
	 */
	public static int confirmPositionLeft(Mat src , int width , int  height) {
		int i , j;
		for(i = 10 ; i < width - 10 ; i++ ) {
			for(j = 10 ; j < height - 10 ; j++) {
				if(getPixel(src , j , i) != WHITE) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * 确定图像内容startRight的位置
	 * @param src Mat矩阵对象
	 * @param width 图像的宽
	 * @param height 图像的高
	 * @return
	 */
	public static int confirmPositionRight(Mat src , int width , int  height) {
		int i , j;
		for(i = width - 10 ; i > 10 ; i-- ) {
			for(j = height - 10 ; j > 10 ; j--) {
				if(getPixel(src , j , i) != WHITE) {
					return i;
				}
			}
		}
		return -1;
	}
	
	/**
	 * 统计图像每行/每列黑色像素点的个数 (n1,n2)=>(height,width),b=true;统计每行
	 * (n1,n2)=>(width,height),b=false;统计每列
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param n1
	 * @param n2
	 * @param b
	 *            true表示统计每行;false表示统计每列
	 * @return
	 */
	public static int[] countPixel(Mat src, int n1, int n2, boolean b) {
		int[] xNum = new int[n1];
		for (int i = 0; i < n1; i++) {
			for (int j = 0; j < n2; j++) {
				if (b) {
					if (getPixel(src, i, j) == BLACK) {
						xNum[i]++;
					}
				} else {
					if (getPixel(src, j, i) == BLACK) {
						xNum[i]++;
					}
				}
			}
		}
		return xNum;
	}

	/**
	 * 给单通道的图像边缘预处理，降噪
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static Mat strokeWhite(Mat src) {
		int i, width = getImgWidth(src), height = getImgHeight(src);
		for (i = 0; i < width; i++) {
			setPixel(src, i, 0, WHITE);
			setPixel(src, i, height - 1, WHITE);
		}
		for (i = 0; i < height; i++) {
			setPixel(src, 0, i, WHITE);
			setPixel(src, width - 1, i, WHITE);
		}
		return src;
	}
}
