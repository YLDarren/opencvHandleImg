package RotationUtils;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

/**
 * 旋转矩形工具类
 */
public class RotationUtils {

    /**
     * 旋转矩形
     * 返回旋转后的Mat
     * @param mat
     *            mat矩阵
     * @param rect
     *            矩形
     * @return
     */
    public static Mat rotation(Mat mat, RotatedRect rect) {
        // 获取矩形的四个顶点
        Point[] rectPoint = new Point[4];
        rect.points(rectPoint);

        double angle = rect.angle + 90;

        Point center = rect.center;

        Mat CorrectImg = new Mat(mat.size(), mat.type());

        mat.copyTo(CorrectImg);

        // 得到旋转矩阵算子
        Mat matrix = Imgproc.getRotationMatrix2D(center, angle, 0.8);

        Imgproc.warpAffine(CorrectImg, CorrectImg, matrix, CorrectImg.size(), 1, 0, new Scalar(0, 0, 0));

        return CorrectImg;
    }
}
