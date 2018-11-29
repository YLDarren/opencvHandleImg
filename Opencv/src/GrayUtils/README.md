### 灰度化

##### opencv自带的灰度化

##### 均值灰度化降噪
> `细粒度灰度化，只降低噪声，不对有效数据做任何的加强处理`
根据灰度化后的图像每一列的像素值的平均值(默认)或者其他表达式值作为阀值，把大于阀值的像素都改为255
`可以在一定程度上降低噪声，而不对有效数据造成任何影响`

##### k值灰度化降噪
> 根据灰度化后的图像每一列的像素值的第k大值作为阀值，把大于阀值的像素都改为255

##### 局部自适应灰度化降噪
> 阀值选取思路：这里采用了一种类似K均值的方法，就是先选择一个值作为阀值，统计大于这个阀值的所有像素的灰度平均值和小于这个阀值的所有像素的灰度平均值，再求这两个值的平均值作为新的阀值。重复上面的计算，直到每次更新阀值后，大于该阀值和小于该阀值的像素数目不变为止。
取自[https://blog.csdn.net/ysc6688/article/details/50772371](https://blog.csdn.net/ysc6688/article/details/50772371)
只不过我把阀值的选取作用于每一列，不是整张图像，在一定层度上降低了噪声，而不会对有效数据造成影响

##### 全局自适应灰度化降噪
> 同上，只不过这次不再选取每一列而是选取整张图

`效果图`

`opencv自带的灰度化`
![opencv自带的灰度化](grayNative.png)

`均值灰度化降噪`

![均值灰度化降噪](https://github.com/YLDarren/opencvHandleImg/blob/master/Opencv/src/GrayUtils/img/test3/test2/grayRowByMidle.png)

`k值灰度化降噪`

![k值灰度化降噪](https://github.com/YLDarren/opencvHandleImg/blob/master/Opencv/src/GrayUtils/img/test3/test2/grayRowByKLargest.png)

`局部自适应灰度化降噪`

![局部自适应灰度化降噪](https://github.com/YLDarren/opencvHandleImg/blob/master/Opencv/src/GrayUtils/img/test3/test2/grayColByPartAdapThreshold.png)

`全局自适应灰度化降噪`

![全局自适应灰度化降噪](https://github.com/YLDarren/opencvHandleImg/blob/master/Opencv/src/GrayUtils/img/test3/test2/grayColByAdapThreshold.png)
