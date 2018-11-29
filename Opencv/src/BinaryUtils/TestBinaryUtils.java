package BinaryUtils;

import GeneralUtils.GeneralUtils;
import GrayUtils.GrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

public class TestBinaryUtils {

    @Before
    public void init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    /**
     * 测试opencv自带的二值化
     */
    public void testBinaryNative(){
        String imgPath = "H:/ideaCode/opencvHandleImg/Opencv/src/BinaryUtils/img/test1/1.png";
        String destPath = "H:/ideaCode/opencvHandleImg/Opencv/src/BinaryUtils/img/test2/";

        Mat src = GeneralUtils.matFactory(imgPath);

        src = GrayUtils.grayColByAdapThreshold(src);

        src = BinaryUtils.binaryNative(src);

        GeneralUtils.saveImg(src , destPath + "binaryNative.png");
    }

    @Test
    /**
     * 测试自定义二值化
     */
    public void testBinaryzation(){
        String imgPath = "H:/ideaCode/opencvHandleImg/Opencv/src/BinaryUtils/img/test1/1.png";
        String destPath = "H:/ideaCode/opencvHandleImg/Opencv/src/BinaryUtils/img/test2/";

        Mat src = GeneralUtils.matFactory(imgPath);

        src = GrayUtils.grayColByAdapThreshold(src);

        src = BinaryUtils.binaryzation(src);

        GeneralUtils.saveImg(src , destPath + "binaryzation.png");

    }

    @Test
    public void testPartBinaryzation(){
//        String imgPath = "H:/ideaCode/opencvHandleImg/Opencv/src/BinaryUtils/img/test1/1.png";
//        String destPath = "H:/ideaCode/opencvHandleImg/Opencv/src/BinaryUtils/img/test2/";
        for(int i = 1 ; i <= 183 ; i++){
            String imgPath = "C:/Users/X240/Desktop/cut/car1/" + i + ".png";
            String destPath = "C:/Users/X240/Desktop/cut/car1binary/";

            Mat src = GeneralUtils.matFactory(imgPath);

            src = GrayUtils.grayColByAdapThreshold(src);

            //opencv自带的二值化
            src = BinaryUtils.binaryNative(src);

            //局部自适应二值化
//            src = BinaryUtils.partBinaryzation(src);

            //全局自适应二值化
//            src = BinaryUtils.binaryzation(src);

            GeneralUtils.saveImg(src , destPath + "partBinaryzation"+i+".png");
        }


    }


}
