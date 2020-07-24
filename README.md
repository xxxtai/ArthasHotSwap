# arthas-hotswap
This repo is a plugin of Intellij IDEA that can help you achieve hot deployment without any configuration and debugging. The use process is as follows. The first step, you need use idea to compile your source file; The second step, right-click on the class name of the source file, select 'Arthas Hot Swap' first, and then select 'Swap this class', the command of hot swap will be automatically copied to clipboard; The third step, log in to the remote server, paste the command and execute, the class file you selected will be hot swaped.


##引言
热部署是帮助开发提高效率的利器，本文介绍一种基于[Arthas](https://github.com/alibaba/arthas)简单快捷的热部署插件([Arthas Hot Swap](https://github.com/xxxtai/arthas-hotswap))。使用该插件进行远程热部署无需任何配置，无需申请debug端口，只需几个简单动作就能完成。
##使用方法
1.idea安装插件“Arthas Hot Swap”，下载文章后面的安装包进行本地安装。
2.热部署使用的是class文件，所以需要先使用“mvn compile”编译相关工程，后续可以使用idea的Recompile编译单个文件，节省编译时间。
3.选择需要热部署的java源文件或者class文件，在类名上单击右键，选择“Arthas Hot Swap”的“Swap this class”，插件后台执行成功后会把热部署需要的命令复制到粘贴板。
4.登录远程服务器，粘贴热部署命令并执行，热部署完成，该机器运行着最新的class。机器第一次执行热部署命令，可能失败，再执行一次试下。

![第一步：选择Arthas Hot Swap插件选项](https://ata2-img.oss-cn-zhangjiakou.aliyuncs.com/bc6b14b1a24226f83ca019b43d1c986b.png)

![第二步：登录远程服务器粘贴命令并执行](https://ata2-img.oss-cn-zhangjiakou.aliyuncs.com/8cf9c577d28104b75df6804183d8d0c9.png)

![第三步：执行完成，热部署成功](https://ata2-img.oss-cn-zhangjiakou.aliyuncs.com/ff42a488e0a3c2c7aee5e0d1874fadea.png)

##Q&A
Q：Arthas Hot Swap插件安装哪个版本？
A：arthas-hotswap-2018.1.7-ultimate适用于大多数版本的idea，但是不适用于2020.1 community版本的idea，所以单独打包了一个arthas-hotswap-2020.1.3-community版本。

Q:热部署有什么限制吗？
A:由于是基于arthas的redefine命令实现的热部署，redefine又是基于Instrumentation API实现的热部署，所以和idea 的 debug Hot Swap是有一样的限制。限制如下：无法修改类名、方法名，无法修改类属性名称，无法新增类属性，无法新增非静态方法。由于本插件暂时无法获取内部类的class文件，所以不支持修改内部类。

##几种热部署方式对比
###HotSwap
在2002年的时候，Sun在Java 1.4的JVM中引入了一种新的被称作HotSwap的实验性技术，这一技术被合成到了Debugger API内部，其允许调试者使用同一个类标识来更新类的字节码。这意味着所有对象都可以引用一个更新后的类，并在它们的方法被调用的时候执行新的代码，这就避免了无论何时只要有类的字节码被修改就要重载容器的这种要求。所有新式的IDE（包括Eclipse、IDEA和NetBeans）都支持这一技术，从Java 5开始，这一功能还通过Instrumentation API直接提供给Java应用使用。
这种方式限制了只能修改方法体，其他什么都不能修改，我觉得这些功能也是足够了，集团日常环境使用也挺便利。预发环境一定需要申请debug端口，大部分开发其实都是在预发环境的，预发debug都使用同一台跳板机，网络拥堵，很多时候热部署都会失败，反正我是没成功过。另外，申请预发debug的后门被关闭了～。
###JRebel
JRebel工作在与HotSwap不同的一个抽象层面上。鉴于HotSwap是工作在虚拟机层面上，且依赖于JVM的内部运作，JRebel用到了JVM的两个显著的功能特征——抽象的字节码和类加载器。类加载器允许JRebel辨别出类被加载的时刻，然后实时地翻译字节码，用以在虚拟机和可执行代码之间创建另一个抽象层。
JRebel需要收费，我也没有使用过，但是看网上文章，JRebel适合本地项目开发使用，如果需要在远程服务器上使用，应该需要安装SDK。在我们集团，使用人应该很少，我没有发现有人使用JRebel。
###HotCode2
HotCode2是集团内部的一个热部署解决方案，具体可以参考[Java 全站式热部署解决方案 HotCode2 正式发布
](https://www.atatech.org/articles/30561?spm=ata.13269325.0.0.346d49faGfTpH7)。HotCode2功能挺强大，就是不好用，需要各种配置以及安装SDK，近些年也没有更新，内网相关的最新帖子都是3、4年前的，可见使用人也不多，至少在我身边没人使用它。以前也尝试过使用它，最后以失败告终。
###Arthas
Arthas是阿里巴巴开源的Java代码诊断工具，具体可以看下这篇文章（[精益求精 | 诊断利器Arthas Github Star破2万的总结和规划](https://www.atatech.org/articles/171527?spm=ata.13269325.0.0.4e6049faTYvnbu)）。Arthas的[redefine命令](https://alibaba.github.io/arthas/redefine.html)是基于Instrumentation API实现的热部署，因此和HotSwap热部署方式一样，有同样的限制。Arthas的redefine命令其实是把Instrumentation API的redefineClasses方法包装后提供给用户，那么我们就可以根据redefine命令发挥想象力，实现热部署。
Arthas官方文档推荐使用jad/mc/redefine等一连串命令实现class远程热替换，流程大概是：jad命令对老class进行反编译->vim编辑源码->mc命令编译源码->redefine热替换class。这种方式存在两个问题：一是太麻烦，二是mc编译大概率会失败。如果能这样就好，本地修改代码编译后上传class文件到远程服务器，再使用redefine命令热替换class。因此官方文档又推荐，首先将class二进制文件转换成base64编码，然后再复制粘贴到远程服务器，再把base64编码转换成class文件，最后，使用redefine进行热替换。这么麻烦，还不如重新部署呢。
所以，本文推荐的“Arthas Hot Swap”插件就有必要了，该插件就是为了提升使用Arthas进行热替换的效率，几个简单的动作就能热替换一个class文件，可以解决开发过程中80%的热部署需求，剩下的20%用重新部署解决就好了。
###小结
HotSwap、JRebel、HotCode2都拥有比较完善的功能，但是各种原因导致他们不好用。其实，我们经常使用的是简单热部署功能，比如在联调的时候发现bug，需要修改几行代码，或者希望加一行日志查看哪里出问题了，但是却要重新部署等待10多分钟。Arthas Hot Swap只拥有很简单功能（热替换一个class，class只能是修改方法体里面的代码），但是它操作十分简单快捷，无需任何配置，无需申请debug端口。

#Arthas Hot Swap插件原理
上文说到Arthas官方推荐的热替换方法最大的问题在于，上传class文件到远程服务器进行热替换的流程太麻烦，那么这些麻烦且固定的流程为何不交给机器来做呢。

##Arthas Hot Swap插件执行流程
1.根据用户选择的源文件找到class文件，默认在/target/classes路径下面查找，用户也可以直接选择class文件。
2.使用AES-128加密class文件，输出base64编码。
3.加密的class文件上传至oss，返回oss访问地址。
4.渲染热部署需要执行的脚本，渲染后的脚本也上传至oss，返回oss访问地址。
5.组装热部署命令，并自动复制到粘贴板。

##远程服务端热部署执行流程
1.登录远程服务器，粘贴热部署命令并执行。
2.下载热部署脚本，执行热部署脚本。
3.创建工作空间。
4.检查是否安装openssl，没有安装则进行安装。
5.下载AES-128加密的class文件。
6.使用openssl enc解密得到class二进制文件。
7.通过脚本方式安装arthas。
8.创建与arthas通信的管道并启动arthas。
9.arthas选择java进程，默认选择第一个。
10.arthas执行redefine命令热替换class文件。
11.打印热替换结果。
