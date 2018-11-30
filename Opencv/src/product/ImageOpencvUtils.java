package product;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;


import MathUtils.MathUtils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

/**
 * 图像处理工具类1.0.0(只针对几乎没有畸变的图像) 灰度话、二值化、降噪、切割、归一化
 * 
 * @author admin
 */
public class ImageOpencvUtils {
	private static final int BLACK = 0;
	private static final int WHITE = 255;

	// 设置归一化图像的固定大小
	private static final Size dsize = new Size(32, 32);

	// 私有化构造函数
	private ImageOpencvUtils() {
	};

	/**
	 * 作用：输入图像路径，返回mat矩阵
	 * 
	 * @param imgPath
	 *            图像路径
	 * @return
	 */
	public static Mat matFactory(String imgPath) {
		return Imgcodecs.imread(imgPath);
	}

	/**
	 * 作用：输入图像Mat矩阵对象，返回图像的宽度
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @return
	 */
	public static int getImgWidth(Mat src) {
		return src.cols();
	}

	/**
	 * 作用：输入图像Mat矩阵，返回图像的高度
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @return
	 */
	public static int getImgHeight(Mat src) {
		return src.rows();
	}

	/**
	 * 作用：获取图像(y,x)点的像素，我们只针对单通道(灰度图)
	 * 
	 * @param src
	 *            Mat矩阵图像
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
	 * 作用：设置图像(y,x)点的像素，我们只针对单通道(灰度图)
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @param y
	 *            y坐标轴
	 * @param x
	 *            x坐标轴
	 * @param color
	 *            颜色值[0-255]
	 */
	public static void setPixel(Mat src, int y, int x, int color) {
		src.put(y, x, color);
	}

	/**
	 * 作用：保存图像
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @param filePath
	 *            要保存图像的路径及名字
	 * @return
	 */
	public static boolean saveImg(Mat src, String filePath) {
		return Imgcodecs.imwrite(filePath, src);
	}

	/**
	 * 作用：灰度话
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @return
	 */
	public static Mat gray(Mat src) {
		Mat gray = new Mat();
		if (src.channels() == 3) {
			Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);
			src = gray;
		} else {
			System.out.println("The Image File Is Not The RGB File!");
		}
		return src;
	}

	/**
	 * 作用：自适应选取阀值
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @return
	 */
	public static int getAdapThreshold(Mat src) {
		int threshold = 0, threshold_new = 127;
		int nWhite_count, nBlack_count;
		int nWhite_sum, nBlack_sum;
		int value, i, j;
		int width = getImgWidth(src), height = getImgHeight(src);

		while (threshold != threshold_new) {
			nWhite_sum = nBlack_sum = 0;
			nWhite_count = nBlack_count = 0;
			for (j = 0; j < height; j++) {
				for (i = 0; i < width; i++) {
					value = getPixel(src, j, i);
					if (value > threshold_new) {
						nWhite_count++;
						nWhite_sum += value;
					} else {
						nBlack_count++;
						nBlack_sum += value;
					}
				}
			}
			threshold = threshold_new;
			threshold_new = (nWhite_sum / nWhite_count + nBlack_sum / nBlack_count) / 2;
		}

		return threshold;
	}

	/**
	 * 图像二值化 阀值自适应确定
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @return
	 */
	public static Mat binaryzation(Mat src) {
		if (src.channels() != 1) {
			throw new RuntimeException("不是单通道图，需要先灰度话！！！");
		}
		int threshold = getAdapThreshold(src);
		return binaryzation(src, threshold);
	}

	/**
	 * 作用：翻转图像像素
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @return
	 */
	public static Mat turnPixel(Mat src) {
		if (src.channels() != 1) {
			throw new RuntimeException("不是单通道图，需要先灰度话！！！");
		}
		int j, i, value;
		int width = getImgWidth(src), height = getImgHeight(src);
		for (j = 0; j < height; j++) {
			for (i = 0; i < width; i++) {
				value = getPixel(src, j, i);
				if (value == 0) {
					setPixel(src, j, i, WHITE);
				} else {
					setPixel(src, j, i, BLACK);
				}
			}
		}
		return src;
	}

	/**
	 * 图像二值化
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @param b
	 *            [true/false] true：表示白底黑字，false表示黑底白字
	 * @return
	 */
	public static Mat binaryzation(Mat src, boolean b) {
		if (src.channels() != 1) {
			throw new RuntimeException("不是单通道图，需要先灰度话！！！");
		}
		int threshold = getAdapThreshold(src);
		return binaryzation(src, threshold, b);
	}

	/**
	 * 图像二值化
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @param threshold
	 *            阀值
	 * @return
	 */
	public static Mat binaryzation(Mat src, int threshold) {
		if (src.channels() != 1) {
			throw new RuntimeException("不是单通道图，需要先灰度话！！！");
		}
		return binaryzation(src, threshold, true);
	}

	/**
	 * 图像二值化
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @param threshold
	 *            阀值
	 * @param b
	 *            [true/false] true：表示白底黑字，false表示黑底白字
	 * @return
	 */
	public static Mat binaryzation(Mat src, int threshold, boolean b) {
		if (src.channels() != 1) {
			throw new RuntimeException("不是单通道图，需要先灰度话！！！");
		}
		int nWhite_sum = 0, nBlack_sum = 0;
		int i, j;
		int width = getImgWidth(src), height = getImgHeight(src);
		int value;
		for (j = 0; j < height; j++) {
			for (i = 0; i < width; i++) {
				value = getPixel(src, j, i);
				if (value > threshold) {
					setPixel(src, j, i, WHITE);
					nWhite_sum++;
				} else {
					setPixel(src, j, i, BLACK);
					nBlack_sum++;
				}
			}
		}

		if (b) {
			// 白底黑字
			if (nBlack_sum > nWhite_sum) {
				src = turnPixel(src);
			}
		} else {
			// 黑底白字
			if (nWhite_sum > nBlack_sum) {
				src = turnPixel(src);
			}
		}
		return src;
	}

	/**
	 * 作用：给单通道的图像边缘预处理，降噪(默认白底黑字)
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static Mat strokeWhite(Mat src) {
		if (src.channels() != 1) {
			throw new RuntimeException("不是单通道图，需要先灰度话！！！");
		}
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

	/**
	 * 8邻域降噪，又有点像9宫格降噪;即如果9宫格中心被异色包围，则同化 作用：降噪(默认白底黑字)
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param pNum
	 *            阀值 默认取1即可
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
	 * 连通域降噪 作用：降噪(默认白底黑字)
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param pArea
	 *            阀值 默认取1即可
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
	 * 压缩像素值数量；即统计zipLine行像素值的数量为一行
	 * 
	 * @param num
	 * @param zipLine
	 */
	public static int[] zipLinePixel(int[] num, int zipLine) {
		int len = num.length / zipLine;
		int[] result = new int[len];
		int sum;
		for (int i = 0, j = 0; i < num.length && i + zipLine < num.length; i += zipLine) {
			sum = 0;
			for (int k = 0; k < zipLine; k++) {
				sum += num[i + k];
			}
			result[j++] = sum;
		}
		return result;
	}

	/**
	 * 水平投影法切割，适用于类似表格的图像(默认白底黑字) 改进
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static List<Mat> _cutImgX(Mat src) {
		int i, j;
		int width = getImgWidth(src), height = getImgHeight(src);
		int[] xNum, cNum;
		int average = 0;// 记录黑色像素和的平均值

		int zipLine = 3;
		// 压缩像素值数量；即统计三行像素值的数量为一行// 统计出每行黑色像素点的个数
		xNum = zipLinePixel(countPixel(src, height, width, true), zipLine);

		// 排序
		cNum = Arrays.copyOf(xNum, xNum.length);
		Arrays.sort(cNum);

		for (i = 31 * cNum.length / 32; i < cNum.length; i++) {
			average += cNum[i];
		}
		average /= (height / 32);

		// System.out.println(average);

		// 把需要切割的y轴点存到cutY中
		List<Integer> cutY = new ArrayList<Integer>();
		for (i = 0; i < xNum.length; i++) {
			if (xNum[i] > average) {
				cutY.add(i * zipLine + 1);
			}
		}

		// 优化cutY,把距离相差在30以内的都清除掉
		if (cutY.size() != 0) {
			int temp = cutY.get(cutY.size() - 1);
			// 因为线条有粗细，优化cutY
			for (i = cutY.size() - 2; i >= 0; i--) {
				int k = temp - cutY.get(i);
				if (k <= 30) {
					cutY.remove(i + 1);
				} else {
					temp = cutY.get(i);
				}
			}
			temp = cutY.get(cutY.size() - 1);
			// 因为线条有粗细，优化cutY
			for (i = cutY.size() - 2; i >= 0; i--) {
				int k = temp - cutY.get(i);
				if (k <= 30) {
					cutY.remove(i + 1);
				} else {
					temp = cutY.get(i);
				}
			}
		}

		// // 把切割的图片保存到YMat中
		List<Mat> YMat = new ArrayList<Mat>();
		for (i = 1; i < cutY.size(); i++) {
			// // 设置感兴趣区域
			int startY = cutY.get(i - 1);
			int h = cutY.get(i) - startY;
			// System.out.println(startY);
			// System.out.println(h);
			Mat temp = new Mat(src, new Rect(0, startY, width, h));
			Mat t = new Mat();
			temp.copyTo(t);
			YMat.add(t);
		}
		return YMat;
	}

	/**
	 * 垂直投影法切割，适用于类似表格的图像(默认白底黑字) 改进
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static List<Mat> _cutImgY(Mat src) {
		int i, j;
		int width = getImgWidth(src), height = getImgHeight(src);
		int[] yNum, cNum;
		int average = 0;// 记录黑色像素和的平均值

		int zipLine = 2;
		// 压缩像素值数量；即统计三行像素值的数量为一行// 统计出每列黑色像素点的个数
		yNum = zipLinePixel(countPixel(src, width, height, false), zipLine);

		// 经过测试这样得到的平均值最优，平均值的选取很重要
		cNum = Arrays.copyOf(yNum, yNum.length);
		Arrays.sort(cNum);
		for (i = 31 * cNum.length / 32; i < cNum.length; i++) {
			average += cNum[i];
		}
		average /= (cNum.length / 32);

		// 把需要切割的x轴的点存到cutX中
		List<Integer> cutX = new ArrayList<Integer>();
		for (i = 0; i < yNum.length; i++) {
			if (yNum[i] >= average) {
				cutX.add(i * zipLine + 2);
			}
		}

		// 优化cutX
		if (cutX.size() != 0) {
			int temp = cutX.get(cutX.size() - 1);
			// 因为线条有粗细，优化cutX
			for (i = cutX.size() - 2; i >= 0; i--) {
				int k = temp - cutX.get(i);
				if (k <= 100) {
					cutX.remove(i);
				} else {
					temp = cutX.get(i);
				}
			}
			temp = cutX.get(cutX.size() - 1);
			// 因为线条有粗细，优化cutX
			for (i = cutX.size() - 2; i >= 0; i--) {
				int k = temp - cutX.get(i);
				if (k <= 100) {
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
			int startX = cutX.get(i - 1);
			int w = cutX.get(i) - startX;
			Mat temp = new Mat(src, new Rect(startX, 0, w, height));
			Mat t = new Mat();
			temp.copyTo(t);
			XMat.add(t);
		}
		return XMat;
	}

	/**
	 * 切割 因为是表格图像，采用新的切割思路，中和水平切割和垂直切割一次性切割出所有的小格子
	 * 
	 * @param src
	 * @return
	 */
	public static List<Mat> cut(Mat src) {
		if (src.channels() == 3) {
			// TODO
		}
		int i, j, k;
		int width = getImgWidth(src), height = getImgHeight(src);
		int[] xNum = new int[height], copy_xNum;
		int x_average = 0;
		int value = -1;
		// 统计每行每列的黑色像素值
		for (i = 0; i < width; i++) {
			for (j = 0; j < height; j++) {
				value = getPixel(src, j, i);
				if (value == BLACK) {
					xNum[j]++;
				}
			}
		}

		int zipXLine = 3;
		xNum = zipLinePixel(xNum, zipXLine);

		// 排序 ............求水平切割点
		copy_xNum = Arrays.copyOf(xNum, xNum.length);
		Arrays.sort(copy_xNum);

		for (i = 31 * copy_xNum.length / 32; i < copy_xNum.length; i++) {
			x_average += copy_xNum[i];
		}
		x_average /= (height / 32);

		// System.out.println("x_average: " + x_average);

		// 把需要切割的y轴点存到cutY中
		List<Integer> cutY = new ArrayList<Integer>();
		for (i = 0; i < xNum.length; i++) {
			if (xNum[i] > x_average) {
				cutY.add(i * zipXLine + zipXLine / 2);
			}
		}

		// 优化cutY,把距离相差在30以内的都清除掉
		if (cutY.size() != 0) {
			int temp = cutY.get(cutY.size() - 1);
			// 因为线条有粗细，优化cutY
			for (i = cutY.size() - 2; i >= 0; i--) {
				k = temp - cutY.get(i);
				if (k <= 10 * zipXLine) {
					cutY.remove(i + 1);
				} else {
					temp = cutY.get(i);
				}
			}
			temp = cutY.get(cutY.size() - 1);
			// 因为线条有粗细，优化cutY
			for (i = cutY.size() - 2; i >= 0; i--) {
				k = temp - cutY.get(i);
				if (k <= 10 * zipXLine) {
					cutY.remove(i + 1);
				} else {
					temp = cutY.get(i);
				}
			}
		}

		// 把需要切割的x轴的点存到cutX中
		/**
		 * 新思路，因为不是很畸变的图像，y轴的割点还是比较好确定的 随机的挑选一个y轴割点，用一个滑动窗口去遍历选中点所在直线，确定x轴割点
		 */
		List<Integer> cutX = new ArrayList<Integer>();
		int choiceY = cutY.size() > 1 ? cutY.get(1) : (cutY.size() > 0 ? cutY.get(0) : -1);
		if (choiceY == -1) {
			throw new RuntimeException("切割失败，没有找到水平切割点");
		}

		int winH = 5;
		List<Integer> LH1 = new ArrayList<Integer>();
		List<Integer> LH2 = new ArrayList<Integer>();
		if (choiceY - winH >= 0 && choiceY + winH <= height) {
			// 上下
			for (i = 0; i < width; i++) {
				value = getPixel(src, choiceY - winH, i);
				if (value == BLACK) {
					LH1.add(i);
				}
				value = getPixel(src, choiceY + winH, i);
				if (value == BLACK) {
					LH2.add(i);
				}
			}
		} else if (choiceY + winH <= height && choiceY + 2 * winH <= height) {
			// 下
			for (i = 0; i < width; i++) {
				value = getPixel(src, choiceY + 2 * winH, i);
				if (value == BLACK) {
					LH1.add(i);
				}
				value = getPixel(src, choiceY + winH, i);
				if (value == BLACK) {
					LH2.add(i);
				}
			}
		} else if (choiceY - winH >= 0 && choiceY - 2 * winH >= 0) {
			// 上
			for (i = 0; i < width; i++) {
				value = getPixel(src, choiceY - winH, i);
				if (value == BLACK) {
					LH1.add(i);
				}
				value = getPixel(src, choiceY - 2 * winH, i);
				if (value == BLACK) {
					LH2.add(i);
				}
			}
		} else {
			throw new RuntimeException("切割失败，图像异常");
		}

		// 优化LH1、LH2,把距离相差在30以内的都清除掉
		if (LH1.size() != 0) {
			int temp = LH1.get(LH1.size() - 1);
			// 因为线条有粗细，优化cutY
			for (i = LH1.size() - 2; i >= 0; i--) {
				k = temp - LH1.get(i);
				if (k <= 50) {
					LH1.remove(i + 1);
				} else {
					temp = LH1.get(i);
				}
			}
			temp = LH1.get(LH1.size() - 1);
			// 因为线条有粗细，优化cutY
			for (i = LH1.size() - 2; i >= 0; i--) {
				k = temp - LH1.get(i);
				if (k <= 50) {
					LH1.remove(i + 1);
				} else {
					temp = LH1.get(i);
				}
			}
		}
		if (LH2.size() != 0) {
			int temp = LH2.get(LH2.size() - 1);
			// 因为线条有粗细，优化cutY
			for (i = LH2.size() - 2; i >= 0; i--) {
				k = temp - LH2.get(i);
				if (k <= 50) {
					LH2.remove(i + 1);
				} else {
					temp = LH2.get(i);
				}
			}
			temp = LH2.get(LH2.size() - 1);
			// 因为线条有粗细，优化cutY
			for (i = LH2.size() - 2; i >= 0; i--) {
				k = temp - LH2.get(i);
				if (k <= 50) {
					LH2.remove(i + 1);
				} else {
					temp = LH2.get(i);
				}
			}
		}

		if (LH1.size() < LH2.size()) {
			// 进一步优化LH1
			int avg = 0;
			for (k = 1; k < LH1.size() - 2; k++) {
				avg += LH1.get(k + 1) - LH1.get(k);
			}
			avg /= (LH1.size() - 2);

			int temp = LH1.get(LH1.size() - 1);
			for (i = LH1.size() - 2; i >= 0; i--) {
				k = temp - LH1.get(i);
				if (k <= avg) {
					LH1.remove(i + 1);
				} else {
					temp = LH1.get(i);
				}
			}
			cutX = LH1;
		} else {
			// 进一步优化LH2
			int avg = 0;
			for (k = 1; k < LH2.size() - 2; k++) {
				avg += LH2.get(k + 1) - LH2.get(k);
			}
			avg /= (LH2.size() - 2);

			int temp = LH2.get(LH2.size() - 1);
			for (i = LH2.size() - 2; i >= 0; i--) {
				k = temp - LH2.get(i);
				if (k <= avg) {
					LH2.remove(i + 1);
				} else {
					temp = LH2.get(i);
				}
			}
			cutX = LH2;
		}

		List<Mat> destMat = new ArrayList<Mat>();
		for (i = 1; i < cutY.size(); i++) {
			for (j = 1; j < cutX.size(); j++) {
				// 设置感兴趣的区域
				int startX = cutX.get(j - 1);
				int w = cutX.get(j) - startX;
				int startY = cutY.get(i - 1);
				int h = cutY.get(i) - startY;
				Mat temp = new Mat(src, new Rect(startX + 2, startY + 2, w - 2, h - 2));
				Mat t = new Mat();
				temp.copyTo(t);
				destMat.add(t);
			}
		}

		return destMat;
	}

	/**
	 * 旋转矩形
	 * 
	 * @param cannyMat
	 *            mat矩阵
	 * @param rect
	 *            矩形
	 * @return
	 */

	public static Mat rotation(Mat cannyMat, RotatedRect rect) {
		// 获取矩形的四个顶点
		Point[] rectPoint = new Point[4];
		rect.points(rectPoint);

		int a = 0;

		if (cannyMat.height() > cannyMat.width()) {
			a = 90;
		}

		double angle = rect.angle + 90 + a;

		Point center = rect.center;

		Mat CorrectImg = new Mat(cannyMat.size(), cannyMat.type());

		cannyMat.copyTo(CorrectImg);

		// 得到旋转矩阵算子
		Mat matrix = Imgproc.getRotationMatrix2D(center, angle, 0.8);

		Imgproc.warpAffine(CorrectImg, CorrectImg, matrix, CorrectImg.size(), 1, 0, new Scalar(0, 0, 0));

		return CorrectImg;
	}

	/**
	 * 把矫正后的图像切割出来
	 * 
	 * @param correctMat
	 *            图像矫正后的Mat矩阵
	 */
	public static Mat cutRect(Mat correctMat, Mat nativeCorrectMat) {
		// 获取最大矩形
		RotatedRect rect = findMaxRect(correctMat);

		Point[] rectPoint = new Point[4];
		rect.points(rectPoint);

		int[] roi = cutRectHelp(rectPoint);

		Mat temp = new Mat(nativeCorrectMat, new Rect(roi[0], roi[1], roi[2], roi[3]));
		Mat t = new Mat();
		temp.copyTo(t);

		// saveImg(t, "C:/Users/admin/Desktop/opencv/open/x/cutRect.jpg");
		return t;
	}

	/**
	 * canny算法，边缘检测
	 * 
	 * @param src
	 * @return
	 */
	public static Mat canny(Mat src) {
		Mat mat = src.clone();
		Imgproc.Canny(src, mat, 60, 200);
		// saveImg(mat, "C:/Users/admin/Desktop/opencv/open/x/canny.jpg");
		return mat;
	}

	/**
	 * 寻找轮廓，并按照递增排序
	 * 
	 * @param cannyMat
	 * @return
	 */
	public static List<MatOfPoint> findContours(Mat cannyMat) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();

		// 寻找轮廓
		Imgproc.findContours(cannyMat, contours, hierarchy, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE,
				new Point(0, 0));

		if (contours.size() <= 0) {
			throw new RuntimeException("未找到图像轮廓");
		} else {
			// 对contours进行了排序，按递增顺序
			contours.sort(new Comparator<MatOfPoint>() {
				@Override
				public int compare(MatOfPoint o1, MatOfPoint o2) {
					MatOfPoint2f mat1 = new MatOfPoint2f(o1.toArray());
					RotatedRect rect1 = Imgproc.minAreaRect(mat1);
					Rect r1 = rect1.boundingRect();

					MatOfPoint2f mat2 = new MatOfPoint2f(o2.toArray());
					RotatedRect rect2 = Imgproc.minAreaRect(mat2);
					Rect r2 = rect2.boundingRect();

					return (int) (r1.area() - r2.area());
				}
			});
			return contours;
		}
	}

	/**
	 * 作用：返回边缘检测之后的最大轮廓
	 * 
	 * @param cannyMat
	 *            Canny之后的Mat矩阵
	 * @return
	 */
	public static MatOfPoint findMaxContour(Mat cannyMat) {
		List<MatOfPoint> contours = findContours(cannyMat);
		return contours.get(contours.size() - 1);
	}

	/**
	 * 返回边缘检测之后的最大矩形
	 * 
	 * @param cannyMat
	 *            Canny之后的mat矩阵
	 * @return
	 */
	public static RotatedRect findMaxRect(Mat cannyMat) {
		MatOfPoint maxContour = findMaxContour(cannyMat);

		MatOfPoint2f matOfPoint2f = new MatOfPoint2f(maxContour.toArray());

		RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);

		return rect;
	}

	/**
	 * 把矫正后的图像切割出来--辅助函数(修复)
	 * 
	 * @param rectPoint
	 *            矩形的四个点
	 * @return int[startLeft , startUp , width , height]
	 */
	public static int[] cutRectHelp(Point[] rectPoint) {
		double minX = rectPoint[0].x;
		double maxX = rectPoint[0].x;
		double minY = rectPoint[0].y;
		double maxY = rectPoint[0].y;
		for (int i = 1; i < rectPoint.length; i++) {
			minX = rectPoint[i].x < minX ? rectPoint[i].x : minX;
			maxX = rectPoint[i].x > maxX ? rectPoint[i].x : maxX;
			minY = rectPoint[i].y < minY ? rectPoint[i].y : minY;
			maxY = rectPoint[i].y > maxY ? rectPoint[i].y : maxY;
		}
		int[] roi = { (int) Math.abs(minX), (int) Math.abs(minY), (int) Math.abs(maxX - minX),
				(int) Math.abs(maxY - minY) };
		return roi;
	}

	/**
	 * 矫正图像
	 * 
	 * @param src
	 * @return
	 */
	public static Mat correct(Mat src) {
		// Canny
		Mat cannyMat = canny(src);

		// 获取最大矩形
		RotatedRect rect = findMaxRect(cannyMat);

		// 旋转矩形
		Mat CorrectImg = rotation(cannyMat, rect);
		Mat NativeCorrectImg = rotation(src, rect);

		// 裁剪矩形
		Mat dst = cutRect(CorrectImg, NativeCorrectImg);

		// saveImg(src, "C:/Users/admin/Desktop/opencv/open/x/srcImg.jpg");

		// saveImg(CorrectImg, "C:/Users/admin/Desktop/opencv/open/x/correct.jpg");
		return dst;
	}

	/**
	 * 图像腐蚀/膨胀处理 腐蚀和膨胀对处理没有噪声的图像很有利，慎用
	 */
	public static Mat erodeDilateImg(Mat src) {
		Mat outImage = new Mat();

		// size 越小，腐蚀的单位越小，图片越接近原图
		Mat structImage = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(2, 2));

		/**
		 * 图像腐蚀 腐蚀说明： 图像的一部分区域与指定的核进行卷积， 求核的最`小`值并赋值给指定区域。 腐蚀可以理解为图像中`高亮区域`的'领域缩小'。
		 * 意思是高亮部分会被不是高亮部分的像素侵蚀掉，使高亮部分越来越少。
		 */
		Imgproc.erode(src, outImage, structImage, new Point(-1, -1), 2);
		src = outImage;

		/**
		 * 膨胀 膨胀说明： 图像的一部分区域与指定的核进行卷积， 求核的最`大`值并赋值给指定区域。 膨胀可以理解为图像中`高亮区域`的'领域扩大'。
		 * 意思是高亮部分会侵蚀不是高亮的部分，使高亮部分越来越多。
		 */
		Imgproc.dilate(src, outImage, structImage, new Point(-1, -1), 2);
		src = outImage;

		return src;
	}

	/**
	 * 
	 * 画出所有的矩形
	 * @param src
	 * @return
	 */
	public static void findCon(Mat src) {
		Mat cannyMat = canny(src);
		List<MatOfPoint> contours = findContours(cannyMat);

		Mat rectMat = src.clone();
		Scalar scalar = new Scalar(0, 0, 255);
		for (int i = contours.size() - 1; i >= 0; i--) {
			MatOfPoint matOfPoint = contours.get(i);
			MatOfPoint2f matOfPoint2f = new MatOfPoint2f(matOfPoint.toArray());

			RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);

			Rect r = rect.boundingRect();

			System.out.println(r.area() + " --- " + i);

			rectMat = paintRect(rectMat, r, scalar);

		}

		saveImg(rectMat, "C:/Users/X240/Desktop/opencv/product/cut/paintRect.jpg");

	}

	/**
	 * 
	 * 画出最大的矩形
	 * @param src
	 * @return
	 */
	public static void findMaxCon(Mat src) {
		Mat cannyMat = canny(src);
		
		RotatedRect rect = findMaxRect(cannyMat);
		
		Rect r = rect.boundingRect();
		
		Mat rectMat = src.clone();
		Scalar scalar = new Scalar(0, 0, 255);
		
		rectMat = paintRect(rectMat, r, scalar);

		saveImg(rectMat, "C:/Users/X240/Desktop/opencv/product/cut/paintRect.jpg");

	}

	
	/**
	 * 画矩形
	 * 
	 * @param src
	 * @param r
	 * @param scalar
	 * @return
	 */
	public static Mat paintRect(Mat src, Rect r, Scalar scalar) {
		Point pt1 = new Point(r.x, r.y);
		Point pt2 = new Point(r.x + r.width, r.y);
		Point pt3 = new Point(r.x + r.width, r.y + r.height);
		Point pt4 = new Point(r.x, r.y + r.height);

		Imgproc.line(src, pt1, pt2, scalar, 5);
		Imgproc.line(src, pt2, pt3, scalar, 5);
		Imgproc.line(src, pt3, pt4, scalar, 5);
		Imgproc.line(src, pt4, pt1, scalar, 5);

		return src;
	}

	/**
	 * 把图片归一化到相同的大小
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static Mat resize(Mat src) {
	    src = trimImg(src);
		Mat dst = new Mat();
		// 区域插值(INTER_AREA):图像放大时类似于线性插值，图像缩小时可以避免波纹出现。
		Imgproc.resize(src, dst, dsize, 0, 0, Imgproc.INTER_AREA);
		return dst;
	}

    /**
     * 去除图像中的空白
     * @param src
     * @return
     */
	public static Mat trimImg(Mat src){
        List<Double> colList = MathUtils.avgColMat(src);//每一列的平均值
        List<Double> rowList = MathUtils.avgRowMat(src);//每一行的平均值

        double colAvg = MathUtils.getSumInList(colList) / colList.size();
        double rowAvg = MathUtils.getSumInList(rowList) / rowList.size();

        int blankCol1 = -1;//空白列的关键分割点(左)
        int blankCol2 = -1;//空白列的关键分割点(右)
        int blankRow1 = -1;//空白行的关键分割点(上)
        int blankRow2 = -1;//空白行的关键分割点(下)

        int preValue = -1;
        int curValue = -1;
        int count = 0;
        boolean b = true;
        for(int i = 0 ; i < colList.size() ; i++){
            if(b == false){
                break;
            }
            if(colList.get(i) > colAvg){
                //求空白列的关键分割点(左)
                curValue = i;
                if(preValue != -1){
                    if(curValue - preValue == 1){
                        //连续
                        count ++;
                    }else{
                        //不连续
                        if(count > 10){
                            blankCol1 = 2 * i / 3;
                            b = false;
                        }
                    }

                }
                preValue = i;
            }
        }

        preValue = -1;
        curValue = -1;
        count = 0;
        b = true;
        for(int i = colList.size() - 1 ; i >= 0 ; i--){
            if(b == false){
                break;
            }
            if(colList.get(i) > colAvg){
                //求空白列的关键分割点(右)
                curValue = i;
                if(preValue != -1){
                    if(curValue - preValue == -1){
                        //连续
                        count ++;
                    }else{
                        //不连续
                        if(count > 10){
                            blankCol2 = i + (colList.size() - i) / 3;
                            b = false;
                        }
                    }

                }
                preValue = i;
            }
        }


        preValue = -1;
        curValue = -1;
        count = 0;
        b = true;
        for(int i = 0 ; i < rowList.size() ; i++){
            if(rowList.get(i) > rowAvg){
                //空白行的关键分割点(上)
                if(b == false){
                    break;
                }
                if(rowList.get(i) > rowAvg){
                    curValue = i;
                    if(preValue != -1){
                        if(curValue - preValue == 1){
                            //连续
                            count ++;
                        }else{
                            //不连续
                            if(count > 10){
                                blankRow1 = i / 2;
                                b = false;
                            }
                        }

                    }
                    preValue = i;
                }
            }
        }

        preValue = -1;
        curValue = -1;
        count = 0;
        b = true;
        for(int i = rowList.size() - 1 ; i >= 0 ; i--){
            if(rowList.get(i) > rowAvg){
                //空白行的关键分割点(下)
                if(b == false){
                    break;
                }
                if(rowList.get(i) > rowAvg){
                    curValue = i;
                    if(preValue != -1){
                        if(curValue - preValue == -1){
                            //连续
                            count ++;
                        }else{
                            //不连续
                            if(count > 10){
                                blankRow2 = i + 2 * (rowList.size() - i) / 3;
                                b = false;
                            }
                        }

                    }
                    preValue = i;
                }
            }
        }


        /**
         * int blankCol1 = -1;//空白列的关键分割点(左)
         *         int blankCol2 = -1;//空白列的关键分割点(右)
         *         int blankRow1 = -1;//空白行的关键分割点(上)
         *         int blankRow2 = -1;//空白行的关键分割点(下)
         */
        //选择感兴趣区域
        blankCol1 = blankCol1 == -1 ? 0 : blankCol1;
        blankCol2 = blankCol2 == -1 ? colList.size() : blankCol2;
        blankRow1 = blankRow1 == -1 ? 0 : blankRow1;
        blankRow2 = blankRow2 == -1 ? rowList.size() : blankRow2;


        Mat temp = new Mat(src, new Rect(blankCol1 , blankRow1, blankCol2 - blankCol1, blankRow2 - blankRow1));
        Mat t = new Mat();
        temp.copyTo(t);

	    return t;
    }



	/**
	 * 画实心圆
	 * 
	 * @param src
	 * @param point
	 *            点
	 * @param size
	 *            点的尺寸
	 * @param scalar
	 *            颜色
	 * @param path
	 *            保存路径
	 */
	public static boolean paintCircle(Mat src, Point[] point, int size, Scalar scalar, String path) {
		if (src == null || point == null) {
			throw new RuntimeException("Mat 或者 Point 数组不能为NULL");
		}
		for (Point p : point) {
			Imgproc.circle(src, p, size, scalar, -1);
		}

		if (path != null && !"".equals(path)) {
			return saveImg(src, path);
		}

		return false;
	}

}
