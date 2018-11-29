package RemoveNoiseUtils;

import BinaryUtils.BinaryUtils;
import GeneralUtils.GeneralUtils;
import GrayUtils.GrayUtils;
import org.junit.Before;
import org.junit.Test;
import org.opencv.core.Core;
import org.opencv.core.Mat;

/**
 * 测试降噪
 */
public class TestRemoveNoiseUtils {
    @Before
    public void init() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    @Test
    /**
     * 测试8邻域降噪
     */
    public void testEghitRemoveNoise(){
        String imgPath = "H:/ideaCode/opencvHandleImg/Opencv/src/RemoveNoiseUtils/test1/1.png";
        String destPath = "H:/ideaCode/opencvHandleImg/Opencv/src/RemoveNoiseUtils/test2/";

        Mat src = GeneralUtils.matFactory(imgPath);

        src = GrayUtils.grayColByPartAdapThreshold(src);

        src = BinaryUtils.binaryzation(src);

        // 8邻域降噪
        src = RemoveNoiseUtils.eghitRemoveNoise(src , 1);

        GeneralUtils.saveImg(src , destPath + "eghitRemoveNoise.png");

    }

    @Test
    /**
     * 连通域降噪
     */
    public void testConnectedRemoveNoise(){
        String imgPath = "H:/ideaCode/opencvHandleImg/Opencv/src/RemoveNoiseUtils/test1/1.png";
        String destPath = "H:/ideaCode/opencvHandleImg/Opencv/src/RemoveNoiseUtils/test2/";

        Mat src = GeneralUtils.matFactory(imgPath);

        src = GrayUtils.grayColByPartAdapThreshold(src);

        src = BinaryUtils.binaryzation(src);

        // 连通域降噪
        src = RemoveNoiseUtils.connectedRemoveNoise(src , 1);

        GeneralUtils.saveImg(src , destPath + "connectedRemoveNoise.png");

    }
}
