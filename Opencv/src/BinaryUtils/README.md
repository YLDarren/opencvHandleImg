### 二值化

##### opencv自带的二值化

##### csdn二值化 [https://blog.csdn.net/ysc6688/article/details/50772371](https://blog.csdn.net/ysc6688/article/details/50772371)
> 思路：这里采用了一种类似K均值的方法，就是先选择一个值作为阀值，统计大于这个阀值的所有像素的灰度平均值和小于这个阀值的所有像素的灰度平均值，再求这两个值的平均值作为新的阀值。重复上面的计算，直到每次更新阀值后，大于该阀值和小于该阀值的像素数目不变为止。

`效果图`

 `opencv自带的二值化`

![opencv自带的二值化](https://github.com/YLDarren/opencvHandleImg/blob/master/Opencv/src/BinaryUtils/img/test2/binaryNative.png)

`全局二值化`

![全局二值化](https://github.com/YLDarren/opencvHandleImg/blob/master/Opencv/src/BinaryUtils/img/test2/binaryzation.png)

`局部二值化`

![局部二值化](https://github.com/YLDarren/opencvHandleImg/blob/master/Opencv/src/BinaryUtils/img/test2/partBinaryzation.png)
