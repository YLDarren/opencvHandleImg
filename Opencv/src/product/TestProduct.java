package product;

import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

public class TestProduct {

    @Before
    public void init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    /**
     * 测试归一化
     */
    public void testResize(){
        String imgPath = "C:/Users/X240/Desktop/opencv/product/cut/cut-7.jpg";
        String destPath = "C:/Users/X240/Desktop/opencv/product/cut/";

        Mat src = ImageOpencvUtils.matFactory(imgPath);

        //灰度化
        src = ImageOpencvUtils.gray(src);

        //二值化
        src = ImageOpencvUtils.binaryzation(src);

        //归一化
        src = ImageOpencvUtils.resize(src);

        ImageOpencvUtils.saveImg(src , destPath + "resize.jpg");

    }
}
