### java利用opencv处理图片(灰度化、二值化、降噪、切割、归一化)

##### 本项目只是作为一个工具类，对图像(主要是表格中的手写数字)进行一系列的预处理，交由TensorFlow进行识别

##### 环境搭建[https://www.jianshu.com/p/cb1f44fa1522](https://www.jianshu.com/p/cb1f44fa1522)

##### java基于opencv实现图像数字识别(二)—基本流程[https://www.jianshu.com/p/5359924b05ee](https://www.jianshu.com/p/5359924b05ee)

##### Java基于opencv实现图像数字识别(三)—灰度化和二值化[https://www.jianshu.com/p/8ec78f531d2c](https://www.jianshu.com/p/8ec78f531d2c)

##### Java基于opencv实现图像数字识别(四)—图像降噪[https://www.jianshu.com/p/943e451cbb8a](https://www.jianshu.com/p/943e451cbb8a)

##### Java基于opencv实现图像数字识别(五)—腐蚀、膨胀处理[https://www.jianshu.com/p/e28a1e938c28](https://www.jianshu.com/p/e28a1e938c28)

##### Java基于opencv实现图像数字识别(五)—投影法分割字符[https://www.jianshu.com/p/25ac6dac1408](https://www.jianshu.com/p/25ac6dac1408)

#### 还需要做的事情
```
1. 图像归一化后，具体内容太小，需要在归一化之前把具体内容边上的空白前切掉(思路：找到图像中内容最上、下、左、右的四个点)
2. 图像校正(霍夫直线)
3. 不是表格的图像的切割
4. 程序优化
```

