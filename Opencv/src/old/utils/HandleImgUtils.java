package old.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opencv.core.CvType;
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
import org.opencv.utils.Converters;

/**
 * 处理图像的工具类
 * 
 * @author admin
 *
 */
public class HandleImgUtils {
	private static final int BLACK = 0;
	private static final int WHITE = 255;
	// 设置归一化图片的固定大小
	private static final Size dsize = new Size(32, 32);

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
	 * 垂直投影法切割，仅适用于不是表格的图像
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static List<Mat> cutNormalImgY(Mat src) {
		int i, j;
		int width = getImgWidth(src), height = getImgHeight(src);
		int[] yNum, cNum;
		int average = 0;// 记录黑色像素和的平均值
		// 统计出每列黑色像素点的个数
		yNum = countPixel(src, width, height, false);

		// 经过测试这样得到的平均值最优，平均值的选取很重要
		cNum = Arrays.copyOf(yNum, yNum.length);
		Arrays.sort(cNum);
		for (i = 0; i < width / 8; i++) {
			average += cNum[i];
		}
		average /= width;

		// 把需要切割的x轴的点存到cutX中
		List<Integer> cutX = new ArrayList<Integer>();
		for (i = 0; i < width; i += 2) {
			if (yNum[i] <= average) {
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
	 * canny算法，边缘检测
	 * 
	 * @param src
	 * @return
	 */
	public static Mat canny(Mat src) {
		Mat mat = src.clone();
		Imgproc.Canny(src, mat, 60, 200);
		HandleImgUtils.saveImg(mat, "C:/Users/admin/Desktop/opencv/open/x/canny.jpg");
		return mat;
	}

	/**
	 * 返回边缘检测之后的最大矩形,并返回
	 * 
	 * @param cannyMat
	 *            Canny之后的mat矩阵
	 * @return
	 */
	public static RotatedRect findMaxRect(Mat cannyMat) {

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();

		// 寻找轮廓
		Imgproc.findContours(cannyMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE,
				new Point(0, 0));

		// 找出匹配到的最大轮廓
		double area = Imgproc.boundingRect(contours.get(0)).area();
		int index = 0;

		// 找出匹配到的最大轮廓
		for (int i = 0; i < contours.size(); i++) {
			double tempArea = Imgproc.boundingRect(contours.get(i)).area();
			if (tempArea > area) {
				area = tempArea;
				index = i;
			}
		}

		MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contours.get(index).toArray());

		RotatedRect rect = Imgproc.minAreaRect(matOfPoint2f);

		return rect;
	}

	/**
	 * 利用函数approxPolyDP来对指定的点集进行逼近 精确度设置好，效果还是比较好的
	 * 
	 * @param cannyMat
	 */
	public static Point[] useApproxPolyDPFindPoints(Mat cannyMat) {

		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		Mat hierarchy = new Mat();

		// 寻找轮廓
		Imgproc.findContours(cannyMat, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE,
				new Point(0, 0));

		// 找出匹配到的最大轮廓
		double area = Imgproc.boundingRect(contours.get(0)).area();
		int index = 0;

		// 找出匹配到的最大轮廓
		for (int i = 0; i < contours.size(); i++) {
			double tempArea = Imgproc.boundingRect(contours.get(i)).area();
			if (tempArea > area) {
				area = tempArea;
				index = i;
			}
		}

		MatOfPoint2f approxCurve = new MatOfPoint2f();
		MatOfPoint2f matOfPoint2f = new MatOfPoint2f(contours.get(index).toArray());

		// 原始曲线与近似曲线之间的最大距离设置为0.01，true表示是闭合的曲线
		Imgproc.approxPolyDP(matOfPoint2f, approxCurve, 0.01, true);

		Point[] points = approxCurve.toArray();

		return points;
	}

	/**
	 * 把点击划分到四个区域中，即左上，右上，右下，左下
	 * 
	 * @param points
	 *            逼近的点集
	 * @param referencePoints
	 *            四个参照点集(通过寻找最大轮廓，进行minAreaRect得到四个点[左上，右上，右下，左下])
	 */
	public static Map<String, List> pointsDivideArea(Point[] points, Point[] referencePoints) {
		// px1 左上，px2左下，py1右上，py2右下
		List<Point> px1 = new ArrayList<Point>(), px2 = new ArrayList<Point>(), py1 = new ArrayList<Point>(),
				py2 = new ArrayList<Point>();
		int thresold = 50;// 设置距离阀值
		double distance = 0;
		for (int i = 0; i < referencePoints.length; i++) {
			for (int j = 0; j < points.length; j++) {
				distance = Math.pow(referencePoints[i].x - points[j].x, 2)
						+ Math.pow(referencePoints[i].y - points[j].y, 2);
				if (distance < Math.pow(thresold, 2)) {
					if (i == 0) {
						px1.add(points[j]);
					} else if (i == 1) {
						py1.add(points[j]);
					} else if (i == 2) {
						py2.add(points[j]);
					} else if (i == 3) {
						px2.add(points[j]);
					}
				} else {
					continue;
				}
			}
		}
		Map<String, List> map = new HashMap<String, List>();
		map.put("px1", px1);
		map.put("px2", px2);
		map.put("py1", py1);
		map.put("py2", py2);

		return map;
	}

	/**
	 * 获取四个顶点的参照点，返回Point数组[左上，右上，右下，左下] 思路： 我们可以把四个点分成两部分，左部分，右部分
	 * 左部分：高的为左上，低的为左下(高低是以人的视觉) 右部分同理 首先我们找到最左和最右的位置，以它们的两个中间为分界点，
	 * 靠左的划分到左部分，靠右的划分到右部分 如果一个区域有三个或更多，哪个比较靠近分界线，划分到少的那个区域
	 * 
	 * @param cannyMat
	 * @return
	 */
	public static Point[] findReferencePoint(Mat cannyMat) {
		RotatedRect rect = findMaxRect(cannyMat);
		Point[] referencePoints = new Point[4];
		rect.points(referencePoints);
		double minX = Double.MAX_VALUE;
		double maxX = Double.MIN_VALUE;
		for (int i = 0; i < referencePoints.length; i++) {
			referencePoints[i].x = Math.abs(referencePoints[i].x);
			referencePoints[i].y = Math.abs(referencePoints[i].y);
			minX = referencePoints[i].x < minX ? referencePoints[i].x : minX;
			maxX = referencePoints[i].x > maxX ? referencePoints[i].x : maxX;
		}

		double center = (minX + maxX) / 2;
		List<Point> leftPart = new ArrayList<Point>();
		List<Point> rightPart = new ArrayList<Point>();
		// 划分左右两个部分
		for (int i = 0; i < referencePoints.length; i++) {
			if (referencePoints[i].x < center) {
				leftPart.add(referencePoints[i]);
			} else if (referencePoints[i].x > center) {
				rightPart.add(referencePoints[i]);
			} else {
				if (leftPart.size() < rightPart.size()) {
					leftPart.add(referencePoints[i]);
				} else {
					rightPart.add(referencePoints[i]);
				}
			}
		}
		double minDistance = 0;
		int minIndex = 0;
		if (leftPart.size() < rightPart.size()) {
			// 左部分少
			minDistance = rightPart.get(0).x - center;
			minIndex = 0;
			for (int i = 1; i < rightPart.size(); i++) {
				if (rightPart.get(i).x - center < minDistance) {
					minDistance = rightPart.get(i).x - center;
					minIndex = i;
				}
			}
			leftPart.add(rightPart.remove(minIndex));

		} else if (leftPart.size() > rightPart.size()) {
			// 右部分少
			minDistance = center - leftPart.get(0).x;
			minIndex = 0;
			for (int i = 1; i < leftPart.size(); i++) {
				if (center - leftPart.get(0).x < minDistance) {
					minDistance = center - leftPart.get(0).x;
					minIndex = i;
				}
			}
			rightPart.add(leftPart.remove(minIndex));
		}

		if (leftPart.get(0).y < leftPart.get(1).y) {
			referencePoints[0] = leftPart.get(0);
			referencePoints[3] = leftPart.get(1);
		}

		if (rightPart.get(0).y < rightPart.get(1).y) {
			referencePoints[1] = rightPart.get(0);
			referencePoints[2] = rightPart.get(1);
		}

		return referencePoints;
	}

	/**
	 * 具体的寻找四个顶点的坐标
	 * 
	 * @param map
	 *            四个点集域 即左上，右上，右下，左下
	 * @return
	 */
	public static Point[] specificFindFourPoint(Map<String, List> map) {
		Point[] result = new Point[4];// [左上，右上，右下，左下]
		List<Point> px1 = map.get("px1");// 左上
		List<Point> px2 = map.get("px2");// 左下
		List<Point> py1 = map.get("py1");// 右上
		List<Point> py2 = map.get("py2");// 右下

		System.out.println("px1.size() " + px1.size());
		System.out.println("px2.size() " + px2.size());
		System.out.println("py1.size() " + py1.size());
		System.out.println("py2.size() " + py2.size());

		double maxDistance = 0;
		double tempDistance;
		int i, j;
		int p1 = 0, p2 = 0;// 记录点的下标
		// 寻找左上，右下
		for (i = 0; i < px1.size(); i++) {
			for (j = 0; j < py2.size(); j++) {
				tempDistance = Math.pow(px1.get(i).x - py2.get(j).x, 2) + Math.pow(px1.get(i).y - py2.get(j).y, 2);
				if (tempDistance > maxDistance) {
					maxDistance = tempDistance;
					p1 = i;
					p2 = j;
				}
			}
		}
		result[0] = px1.get(p1);
		result[2] = py2.get(p2);

		// 寻找左下，右上
		maxDistance = 0;
		for (i = 0; i < px2.size(); i++) {
			for (j = 0; j < py1.size(); j++) {
				tempDistance = Math.pow(px2.get(i).x - py1.get(j).x, 2) + Math.pow(px2.get(i).y - py1.get(j).y, 2);
				if (tempDistance > maxDistance) {
					maxDistance = tempDistance;
					p1 = i;
					p2 = j;
				}
			}
		}
		result[1] = py1.get(p2);
		result[3] = px2.get(p1);
		return result;
	}

	/**
	 * 寻找四个顶点的坐标 思路： 1、canny描边 2、寻找最大轮廓 3、对最大轮廓点集合逼近，得到轮廓的大致点集合
	 * 4、把点击划分到四个区域中，即左上，右上，左下，右下 5、根据矩形中，对角线最长，找到矩形的四个顶点坐标
	 * 
	 * @param src
	 */
	public static Point[] findFourPoint(Mat src) {
		// 1、canny描边
		Mat cannyMat = canny(src);
		// 2、寻找最大轮廓;3、对最大轮廓点集合逼近，得到轮廓的大致点集合
		Point[] points = useApproxPolyDPFindPoints(cannyMat);
		
		//在图像上画出逼近的点
		Mat approxPolyMat = src.clone();
		for( int i = 0; i < points.length ; i++) {
			setPixel(approxPolyMat, (int)points[i].y, (int) points[i].x, 255);
		}
		
		saveImg(approxPolyMat, "C:/Users/admin/Desktop/opencv/open/q/x11-approxPolyMat.jpg");
		
		// 获取参照点集
		Point[] referencePoints = findReferencePoint(cannyMat);

		// 4、把点击划分到四个区域中，即左上，右上，左下，右下(效果还可以)
		Map<String, List> map = pointsDivideArea(points, referencePoints);

		// 画出标记四个区域中的点集
		Mat areaMat = src.clone();
		List<Point> px1 = map.get("px1");// 左上
		List<Point> px2 = map.get("px2");// 左下
		List<Point> py1 = map.get("py1");// 右上
		List<Point> py2 = map.get("py2");// 右下

		for (int i = 0; i < px1.size(); i++) {
			setPixel(areaMat, (int) px1.get(i).y, (int) px1.get(i).x, 255);
		}

		for (int i = 0; i < px2.size(); i++) {
			setPixel(areaMat, (int) px2.get(i).y, (int) px2.get(i).x, 255);
		}

		for (int i = 0; i < py1.size(); i++) {
			setPixel(areaMat, (int) py1.get(i).y, (int) py1.get(i).x, 255);
		}

		for (int i = 0; i < py2.size(); i++) {
			setPixel(areaMat, (int) py2.get(i).y, (int) py2.get(i).x, 255);
		}

		saveImg(areaMat, "C:/Users/admin/Desktop/opencv/open/q/x11-pointsDivideArea.jpg");

		// 5、根据矩形中，对角线最长，找到矩形的四个顶点坐标(效果不好)
		Point[] result = specificFindFourPoint(map);

		return result;
	}

	/**
	 * 透视变换，矫正图像 思路： 1、寻找图像的四个顶点的坐标(重要) 思路： 1、canny描边 2、寻找最大轮廓
	 * 3、对最大轮廓点集合逼近，得到轮廓的大致点集合 4、把点击划分到四个区域中，即左上，右上，左下，右下 5、根据矩形中，对角线最长，找到矩形的四个顶点坐标
	 * 2、根据输入和输出点获得图像透视变换的矩阵 3、透视变换
	 * 
	 * @param src
	 */
	public static Mat warpPerspective(Mat src) {
		// 灰度话
		src = HandleImgUtils.gray(src);
		// 找到四个点
		Point[] points = HandleImgUtils.findFourPoint(src);

		// Canny
		Mat cannyMat = HandleImgUtils.canny(src);
		// 寻找最大矩形
		RotatedRect rect = HandleImgUtils.findMaxRect(cannyMat);

		// 点的顺序[左上 ，右上 ，右下 ，左下]
		List<Point> listSrcs = java.util.Arrays.asList(points[0], points[1], points[2], points[3]);
		Mat srcPoints = Converters.vector_Point_to_Mat(listSrcs, CvType.CV_32F);

		Rect r = rect.boundingRect();
		r.x = Math.abs(r.x);
		r.y = Math.abs(r.y);
		List<Point> listDsts = java.util.Arrays.asList(new Point(r.x, r.y), new Point(r.x + r.width, r.y),
				new Point(r.x + r.width, r.y + r.height), new Point(r.x, r.y + r.height));

		System.out.println(r.x + "," + r.y);

		Mat dstPoints = Converters.vector_Point_to_Mat(listDsts, CvType.CV_32F);

		Mat perspectiveMmat = Imgproc.getPerspectiveTransform(srcPoints, dstPoints);

		Mat dst = new Mat();

		Imgproc.warpPerspective(src, dst, perspectiveMmat, src.size(), Imgproc.INTER_LINEAR + Imgproc.WARP_INVERSE_MAP,
				1, new Scalar(0));
		
		return dst;

	}

	/**
	 * 旋转矩形
	 * 
	 * @param src
	 *            mat矩阵
	 * @param rect
	 *            矩形
	 * @return
	 */
	public static Mat rotation(Mat cannyMat, RotatedRect rect) {
		// 获取矩形的四个顶点
		Point[] rectPoint = new Point[4];
		rect.points(rectPoint);

		double angle = rect.angle + 90;

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
	public static void cutRect(Mat correctMat, Mat nativeCorrectMat) {
		// 获取最大矩形
		RotatedRect rect = findMaxRect(correctMat);

		Point[] rectPoint = new Point[4];
		rect.points(rectPoint);

		int[] roi = cutRectHelp(rectPoint);

		Mat temp = new Mat(nativeCorrectMat, new Rect(roi[0], roi[1], roi[2], roi[3]));
		Mat t = new Mat();
		temp.copyTo(t);

		HandleImgUtils.saveImg(t, "C:/Users/admin/Desktop/opencv/open/x/cutRect.jpg");
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
		int[] roi = { (int) (minX), (int) minY, (int) (maxX - minX), (int) (maxY - minY) };
		return roi;
	}

	/**
	 * 矫正图像
	 * 
	 * @param src
	 * @return
	 */
	public static void correct(Mat src) {
		// Canny
		Mat cannyMat = canny(src);

		// 获取最大矩形
		RotatedRect rect = findMaxRect(cannyMat);

		// 旋转矩形
		Mat CorrectImg = rotation(cannyMat, rect);
		Mat NativeCorrectImg = rotation(src, rect);

		// 裁剪矩形
		cutRect(CorrectImg, NativeCorrectImg);

		HandleImgUtils.saveImg(src, "C:/Users/admin/Desktop/opencv/open/x/srcImg.jpg");

		HandleImgUtils.saveImg(CorrectImg, "C:/Users/admin/Desktop/opencv/open/x/correct.jpg");
	}

	/**
	 * 裁剪图像，主要使切割图像的内容更靠中间(即去除内容周边的空白)
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static Mat trimImg(Mat src) {
		// 定义具体内容开始的点
		int startUp = 0, startDown = 0, startLeft = 0, startRight = 0;
		int thresold = 30;
		int width = getImgWidth(src), height = getImgHeight(src);
		startUp = confirmPositionUp(src, width, height);
		startDown = confirmPositionDown(src, width, height);
		startLeft = confirmPositionLeft(src, width, height);
		startRight = confirmPositionRight(src, width, height);
		startUp = startUp <= thresold || startUp == -1 ? 0 : startUp - thresold;
		startDown = height - startDown <= thresold || startDown == -1 ? height : startDown + thresold;
		startLeft = startLeft <= thresold || startLeft == -1 ? 0 : startLeft - thresold;
		startRight = width - startRight <= thresold || startRight == -1 ? width : startRight + thresold;
		// 设置感兴趣的区域
		Mat temp = new Mat(src, new Rect(startLeft, startUp, startRight - startLeft, startDown - startUp));
		Mat t = new Mat();
		temp.copyTo(t);
		return t;
	}

	/**
	 * 确定图像内容startUp的位置
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param width
	 *            图像的宽
	 * @param height
	 *            图像的高
	 * @return
	 */
	public static int confirmPositionUp(Mat src, int width, int height) {
		int i, j;
		int thresold = 10;
		for (i = thresold; i < height - thresold; i++) {
			for (j = thresold; j < width - thresold; j++) {
				if (getPixel(src, i, j) != WHITE) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 确定图像内容startDown的位置
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param width
	 *            图像的宽
	 * @param height
	 *            图像 的高
	 * @return
	 */
	public static int confirmPositionDown(Mat src, int width, int height) {
		int i, j;
		int thresold = 10;
		for (i = height - thresold; i > thresold; i--) {
			for (j = thresold; j < width - thresold; j++) {
				if (getPixel(src, i, j) != WHITE) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 确定图像内容startLeft的位置
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param width
	 *            图像的宽
	 * @param height
	 *            图像的高
	 * @return
	 */
	public static int confirmPositionLeft(Mat src, int width, int height) {
		int i, j;
		int thresold = 10;
		for (i = thresold; i < width - thresold; i++) {
			for (j = thresold; j < height - thresold; j++) {
				if (getPixel(src, j, i) != WHITE) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * 确定图像内容startRight的位置
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @param width
	 *            图像的宽
	 * @param height
	 *            图像的高
	 * @return
	 */
	public static int confirmPositionRight(Mat src, int width, int height) {
		int i, j;
		int thresold = 10;
		for (i = width - thresold; i > thresold; i--) {
			for (j = height - thresold; j > thresold; j--) {
				if (getPixel(src, j, i) != WHITE) {
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

	/**
	 * 判单输入的图像是表格类型的图，还是一般的图 根据表格图像的特征，即表格横线大致一样长的特征 true表示是表格图像 false表示是一般图像
	 * 
	 * @param src
	 *            Mat矩阵对象
	 * @return
	 */
	public static boolean judgeImg(Mat src) {
		int width = getImgWidth(src), height = getImgHeight(src);
		int[] xNum;
		int count = 0;// 计数器
		// 统计出每行黑色像素点的个数
		xNum = countPixel(src, height, width, true);
		Arrays.sort(xNum);
		for (int i = xNum.length - 2, max = xNum[xNum.length - 1]; i > 0; i--) {
			if (max - xNum[i] <= 100) {
				count++;
			} else {
				break;
			}
		}
		if (count > 8 || count == 0) {
			return false;
		}
		return true;
	}

	/**
	 * 输入图像矩阵，判断图像是否有内容 true表示没有内容，false表示有内容
	 * 
	 * @param src
	 * @return
	 */
	public static boolean judgeEmpty(Mat src) {
		int width = getImgWidth(src), height = getImgHeight(src);
		// 统计出每行黑色像素点的个数
		int[] xNum = countPixel(src, height, width, true);
		int thresold = 50;// 把像素点和的阀值设置位50，如果不大于则认为无内容，即空图
		int sum = 0;// 记录图像黑色像素的和
		for (int i = 0; i < xNum.length; i++) {
			sum += xNum[i];
		}
		if (sum > thresold) {
			return false;
		}
		return true;
	}

	/**
	 * 灰度话、二值化、降噪、判图类型、切割、判空、归一化
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @param filePath
	 *            图像保存路径
	 * @param basicName
	 *            图像保存的基本名字
	 */
	public static void handleImg(Mat src, String filePath, String basicName) {
		src = grayRemoveNoise(src);
		boolean b = judgeImg(src);
		int count = 1;// 记录生成图像的个数
		if (b) {
			System.out.println("表格图像");
			// 表格图像
			List<Mat> XMatList = cutImgX(src);
			for (int i = 0; i < XMatList.size(); i++) {
				saveImg(XMatList.get(i), filePath + i + "-X-" + basicName);
			}
			System.out.println("XMatList = " + XMatList.size());
			List<Mat> YMatList = new ArrayList<Mat>();
			for (Mat mat : XMatList) {
				List<Mat> tempYMatLsit = cutImgY(mat);
				System.out.println("tempYMatLsit = " + tempYMatLsit.size());
				for (Mat mat1 : tempYMatLsit) {
					YMatList.add(mat1);
				}
				tempYMatLsit = null;
			}
			XMatList = null;

			// 遍历YMatList
			for (Mat matY : YMatList) {
				// 判空
				boolean b1 = judgeEmpty(matY);
				if (!b1) {
					// 有内容，归一化
					matY = resize(matY);
					String name = filePath + count + "-resize-" + basicName;
					saveImg(matY, name);
					System.out.println("生成图像的名字 " + name);
					count++;
				}
			}
			YMatList = null;
		} else {
			System.out.println("不是表格图像");
			// 不是表格图像，只实现了垂直投影切割
			List<Mat> YMatList = cutNormalImgY(src);
			System.out.println("YMatList = " + YMatList.size());
			// 遍历YMatList
			for (Mat matY : YMatList) {
				// 判空
				boolean b1 = judgeEmpty(matY);
				if (!b1) {
					// 有内容，归一化
					matY = resize(matY);
					String name = filePath + count + "-" + basicName;
					saveImg(matY, name);
					System.out.println("生成图像的名字 " + name);
					count++;
				}
			}
			YMatList = null;
		}
	}

	/**
	 * 灰度话、二值化、降噪
	 * 
	 * @param src
	 *            Mat矩阵图像
	 * @return
	 */
	public static Mat grayRemoveNoise(Mat src) {
		src = gray(src);
		src = binaryzation(src);
		src = navieRemoveNoise(src, 1);
		src = connectedRemoveNoise(src, 1.0);
		return src;
	}

}
