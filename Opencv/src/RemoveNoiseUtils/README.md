### 降噪

> 参考博客[https://blog.csdn.net/ysc6688/article/details/50772382](https://blog.csdn.net/ysc6688/article/details/50772382)

##### 8邻域降噪
> 我感觉9宫格降噪更形象一点；即9宫格中心被异色包围，则同化

##### 连通域降噪
`我们先介绍一个函数(floodFill): `
floodFill就是把一个点x的所有相邻的点都涂上x点的颜色，一直填充下去，直到这个区域内所有的点都被填充完为止

> 在计算的过程中，每扫描到一个黑色（灰度值为0）的点，
就将与该点连通的所有点的灰度值都改为1，
因此这一个连通域的点都不会再次重复计算了。
下一个灰度值为0的点所有连通点的颜色都改为2，
这样依次递加，直到所有的点都扫描完。
接下来再次扫描所有的点，
统计每一个灰度值对应的点的个数，
每一个灰度值的点的个数对应该连通域的大小，
并且不同连通域由于灰度值不同，
因此每个点只计算一次，不会重复。
这样一来就统计到了每个连通域的大小，
再根据预设的阀值，
如果该连通域大小小于阀值，则其就为噪点。
这个算法比较适合检查大的噪点。

*原博主代码存在一些漏洞，因为从1-244只能填充244块连通域，
如果连通域大于244块，大于的那些块将被填充为白色，
影响了我们的有效数据，针对这种情况了我结合降噪做了一个递归填充，
即每次只填充244块，给定一个标志[true/false]；true表示填充全部填充完，
false表示无填充完，还需递归。而随之迎来的是如果你的有效数据的连通域如果大于244块，
那将会产生递归循环，具体如何改进还未想出，先用一个计数器挡挡(手动滑稽)*

`8邻域降噪`

![8邻域降噪](https://github.com/YLDarren/opencvHandleImg/blob/master/Opencv/src/RemoveNoiseUtils/test2/connectedRemoveNoise.png)

`连通域降噪`

![连通域降噪](https://github.com/YLDarren/opencvHandleImg/blob/master/Opencv/src/RemoveNoiseUtils/test2/eghitRemoveNoise.png)
