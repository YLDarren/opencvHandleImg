package GrayUtils;

import GeneralUtils.GeneralUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

/**
 * 测试灰度化
 */
public class TestGrayUtils {
    @Before
    public void init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    /**
     * 测试opencv自带的灰度化方法
     */
    public void testGrayNative(){
        String imgPath = "H:/ideaCode/opencvHandleImg/Opencv/src/GrayUtils/img/test3/test1/1.png";
        String destPath = "H:/ideaCode/opencvHandleImg/Opencv/src/GrayUtils/img/test3/test2/";

        Mat src = GeneralUtils.matFactory(imgPath);

        src = GrayUtils.grayNative(src);

        GeneralUtils.saveImg(src , destPath + "grayNative.png");
    }

    @Test
    /**
     * 测试细粒度灰度化方法
     * 均值灰度化减噪
     */
    public void testGrayColByMidle() {
        String imgPath = "H:/ideaCode/opencvHandleImg/Opencv/src/GrayUtils/img/test3/test1/1.png";
        String destPath = "H:/ideaCode/opencvHandleImg/Opencv/src/GrayUtils/img/test3/test2/";

        Mat src = GeneralUtils.matFactory(imgPath);

        src = GrayUtils.grayColByMidle(src);

        GeneralUtils.saveImg(src , destPath + "grayRowByMidle.png");
    }


    @Test
    /**
     * 测试细粒度灰度化方法
     * k值灰度化减噪
     */
    public void testgrayColByKLargest() {
        String imgPath = "H:/ideaCode/opencvHandleImg/Opencv/src/GrayUtils/img/test3/test1/1.png";
        String destPath = "H:/ideaCode/opencvHandleImg/Opencv/src/GrayUtils/img/test3/test2/";

        Mat src = GeneralUtils.matFactory(imgPath);

        src = GrayUtils.grayColByKLargest(src);

        GeneralUtils.saveImg(src , destPath + "grayRowByKLargest.png");
    }

    @Test
    /**
     * 测试细粒度灰度化方法
     * 局部自适应阀值灰度化减噪
     */
    public void testgrayColByPartAdapThreshold() {
        String imgPath = "H:/ideaCode/opencvHandleImg/Opencv/src/GrayUtils/img/test3/test1/1.png";
        String destPath = "H:/ideaCode/opencvHandleImg/Opencv/src/GrayUtils/img/test3/test2/";

        Mat src = GeneralUtils.matFactory(imgPath);

        src = GrayUtils.grayColByPartAdapThreshold(src);

        GeneralUtils.saveImg(src , destPath + "grayColByPartAdapThreshold.png");
    }


    @Test
    /**
     * 测试细粒度灰度化方法
     * 全局自适应阀值灰度化减噪
     */
    public void testgrayColByAdapThreshold() {
        String imgPath = "H:/ideaCode/opencvHandleImg/Opencv/src/GrayUtils/img/test3/test1/1.png";
        String destPath = "H:/ideaCode/opencvHandleImg/Opencv/src/GrayUtils/img/test3/test2/";

        Mat src = GeneralUtils.matFactory(imgPath);

        src = GrayUtils.grayColByAdapThreshold(src);

        GeneralUtils.saveImg(src , destPath + "grayColByAdapThreshold.png");
    }


}
