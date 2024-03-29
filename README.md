# 写在前面

- 平时只能在下班时间能够抽出点时间来写这个程序，该程序目前还有相当多的Bug，这里面有一些已知的隐患，也有一些还未使用到的地方，也有许多功能还没来得及实现，后续会慢慢补齐。
- github: https://github.com/markyao98/douyinHarvest/
- 欢迎有能力的小伙伴加入我的项目，也欢迎大佬指正~~
- 抖音: @他看到了自己


# 程序介绍

- 本程序目前完成的功能：
  - 展示
  - 分析
  - 采集
  - 数据的监控（目前是监控点赞数量和回复数量）



## 程序的结构&启动

- 本程序分为java端和python端，其中python端担负分析的功能，这两端是分离开的，没有Python程序的启动也不影响程序的运行，只是会缺失分析的功能。
- 其中java项目使用的是springboot（application.java启动），而python使用flask（go.py启动）

- 需要
  - windows系统（因为分析的词云图还有其他图片资源都是存放在文件系统里面）
  - 采集时需要联网
  - java8
  - python3
  - mysql5.7
  - 安装java的pom依赖以及Python程序的相关依赖。

- 配置

- java端：
  - 在application.yml文件里面，有个cookie字段配置，如果遇到采集数据失败，可更换cookie，一般都会成功，如果一直失败就消停两天再进行采集
- python端：
  - plotWordcloud.py文件里面的f**ilepath**变量，需要修改成自己的文件夹路径



## 个人牢骚

- 前端方面，我并没有使用框架（现在有点后悔了），用的是原生的html+js，引入了axios来发送请求。
- 数据的分析（词云生成，分词）使用的是python的jieba库和wordcloud，最后使用flask完成跟java后端的交互。
- 以上两部分都是比较容易实现的，而且也不是本程序的重点。本程序的关键之处在于数据的获取，一切皆数据，没有数据其他都是浮云。

- 下面将讲讲采集数据时受的折磨。注意： **这部分纯属个人牢骚**

- **数据的采集**，程序的核心部分，一开始想得太简单了，在控制台抓个接口不就完事了吗？

- 要是真这么简单我就不需要花费这么多时间了= =

- 首先是从控制台抓取到的接口，放在postman等工具上用的时候，会出现时灵时不灵的情况，然后是里面有一个参数误导了我 —— "**msToken**",这个参数让我以为每一页数据的获取都需要他们系统生成的token才能拿到，所以直接导致了我后面的一个重大失误——使用webDriver采集数据

  - 通过webdriver模拟浏览器的点击，滑动行为，然后通过截取html元素来获取数据，但是我发现前端页面上数据的信息维度太少了。而且面对那种上万条的评论区，电脑根本没办法同时采集多个评论区，效率低不说，维护成本也极高，如果页面保持元素源代码不变还好，由于是根据id （有时根据class） 来获取元素，一旦元素源代码发生改变，我就得进行大量的维护工作。根本不现实。 但是后面发现，webdriver可以通过监听控制台的方式，获取到请求过的接口，所以根据这一思路，我做出了第一个版本的采集器。也就是通过webDriver来监听控制台，获取接口，存放数据库，然后再通过httpClient发起请求获取数据。
  - 一开始这个方式也是挺奏效的，但是后面又不行了，经常拿不到数据，我以为是cookie的问题，于是就换cookie,结果换cookie也不行（偶尔可以） ，于是想到既然我都已经监听控制台了，那我还拿个毛的接口啊！直接拿它响应回来的数据不就行了吗！ 
  - 用这样的方式，又奏效了几天。只是后面又是连续两天拿不到数据。这一回我实在是没有办法了，只能去各大平台上找，看看能不能拿到一个正确的接口，但是结果并不是很好。官方的开发者平台没有，开源平台上只能找到一些比较旧的接口，现在根本是用不了的。
  - 没有办法解决了咋整？那就不想办法！ 大力出奇迹！，在postman上疯狂尝试加减参数，变换参数的组合（从控制台拿到的接口参数有十几个），没想到最后终于还试成功了

  - 目前这个接口还算稳定，现在我只需要通过页码的更换就可以愉快的拿数据了。

- 解决了数据的来源问题，思路一下子就打开了。我也把自己的一个想法再次融合进来，并且实现了——数据的监控。

- 为什么要做这个监控呢？因为之前有个朋友私信我问我能否获取评论的历史点赞数据，我说没办法拿到，因为那属于个人隐私。但是这个想法在我脑子里转了好久..

- 虽然没法拿到历史（过去）的数据，但是可以拿到未来的数据呀！只不过这个功能的前提是要解决数据的采集问题，如果依靠之前的模拟浏览器行为来采集，是绝对无法完成这个功能的，只有能够稳定的采集数据才行。





# 最后

- 本程序目前的不足以及一些展望
  - 管理接口还有很多没有实现，例如群组的删除修改，视频的删除修改。目前只是在数据库可视化界面进行修改。
  - 视频采集的时候拿不到视频的标题数据，需要自己去数据库改标题= = 这个很蛋疼，但是目前没有视频信息的接口，而且标题信息的意义并不大，后面我再想办法搞一搞。
  - 数据量的攀升导致的系统性能下降，现在我自己采集的数据量已有百万级，明显感觉到了性能的瓶颈。后续我会考虑做一次大升级，拆分系统为几个模块，然后数据对接es。
  - 以上痛点并非本程序的硬伤，优先级较低。如果有新功能的设想，我会优先完成新想法的开发。
