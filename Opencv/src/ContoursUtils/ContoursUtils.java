package ContoursUtils;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 轮廓工具类
 */
public class ContoursUtils {

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
     * 利用函数approxPolyDP来对指定的点集进行逼近 精确度设置好，效果还是比较好的
     *
     * @param cannyMat
     */
    public static Point[] useApproxPolyDPFindPoints(Mat cannyMat) {
        return useApproxPolyDPFindPoints(cannyMat, 0.01);
    }

    /**
     * 利用函数approxPolyDP来对指定的点集进行逼近 精确度设置好，效果还是比较好的
     *
     * @param cannyMat
     * @param threshold
     *            阀值(精确度)
     * @return
     */
    public static Point[] useApproxPolyDPFindPoints(Mat cannyMat, double threshold) {

        MatOfPoint maxContour = findMaxContour(cannyMat);

        MatOfPoint2f approxCurve = new MatOfPoint2f();
        MatOfPoint2f matOfPoint2f = new MatOfPoint2f(maxContour.toArray());

        // 原始曲线与近似曲线之间的最大距离设置为0.01，true表示是闭合的曲线
        Imgproc.approxPolyDP(matOfPoint2f, approxCurve, threshold, true);

        Point[] points = approxCurve.toArray();

        return points;
    }
}
