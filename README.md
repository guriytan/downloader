# downloader

使用Retrofit进行http\https的下载请求，通过多线程执行下载，支持断点续传功能，使用 ~~Rxjava、~~ eventbus进行新建任务指令的发送，使用GreenDao进行下载任务的持久化，下载框架中使用handler机制实现进度的监听回调，使用临时文件记录线程下载起点，总下载进度保存数据库用于显示，但以临时文件为准。

**目前仅支持http\https的下载链接 ~~，而且仅支持单线程断点续传~~**

下一步待补充的功能：
* 打开已下载文件
* 新建任务重命名
* 控制任务下载的多线程数量
* ~~优化下载任务的创建~~
* 监控文件是否已删除功能
* ~~多线程下载~~
* 扫一扫下载文件功能
* 解析磁力链接以及种子文件并下载

## APP截图：
<img src="https://github.com/guriytan/downloader/raw/master/Screenshot.png" width = "300"/>
