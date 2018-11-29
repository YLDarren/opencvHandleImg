package GeneralUtils;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * opencv的一些通用方法工具类
 */
public class GeneralUtils {

    private static final int BLACK = 0;
    private static final int WHITE = 255;

    // 设置归一化图像的固定大小
    private static final Size dsize = new Size(32, 32);

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
     * 确保白底黑字或者黑底白字
     * @param src
     * @param b true：表示白底黑字 ， false相反
     * @return
     */
    public static Mat turnPixel(Mat src , boolean b){
        if(src != null){
            int width = GeneralUtils.getImgWidth(src);
            int height = GeneralUtils.getImgHeight(src);
            int value;
            int black_num = 0;
            int white_num = 0;
            int i, j;
            for(i = 0 ; i < width ; i++){
                for(j = 0 ; j < height ; j++){
                    value = GeneralUtils.getPixel(src , j , i);
                    if(value == GeneralUtils.getWHITE()){
                        white_num++;
                    }else if(value == GeneralUtils.getBLACK()){
                        black_num++;
                    }
                }
            }

            if(b && black_num > white_num){
                //反转
                src = turnPixel(src);
            }else if(!b && white_num > black_num){
                //反转
                src = turnPixel(src);
            }
        }
        return src;
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

    public static int getBLACK() {
        return BLACK;
    }

    public static int getWHITE() {
        return WHITE;
    }

    public static Size getDsize() {
        return dsize;
    }
}
