# downloader

使用Retrofit进行http\https的下载请求，使用Rxjava、eventbus进行下载进度的监控以及消息传递，使用GreenDao进行下载任务的持久化

**目前仅支持http\https的下载链接，而且仅支持单线程断点续传**

下一步待补充的功能：
* 增加文件打开功能
* 完善okhttp请求响应，进一步优化下载任务的创建
* 完善监控文件是否已删除功能
* 增加多线程下载
* 增加扫一扫下载文件功能
* 增加解析磁力链接以及种子文件
* 增加磁力链接以及种子文件下载

## APP截图：
![](https://github.com/guriytan/downloader/raw/master/Screenshot.png?raw=true)
