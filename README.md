# downloader

使用Retrofit进行http\https的下载请求，使用Rxjava、eventbus进行下载进度的监控以及消息传递，使用GreenDao进行下载任务的持久化

**目前仅支持http\https的下载链接，而且仅支持单线程断点续传**

下一步待补充的功能：
* 文件打开、新建任务重命名
* 优化下载任务的创建
* 监控文件是否已删除功能
* 多线程下载
* 扫一扫下载文件功能
* 解析磁力链接以及种子文件并下载

## APP截图：
<img src="https://github.com/guriytan/downloader/raw/master/Screenshot.png" width = "300"/>
