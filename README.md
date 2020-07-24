# Arthas Hot Swap English Abstract
This repo is a plugin of Intellij IDEA that can help you achieve hot deployment without any configuration and debugging. The use process is as follows. The first step, you need use idea to compile your source file; The second step, right-click on the class name of the source file, select 'Arthas Hot Swap' first, and then select 'Swap this class', the command of hot swap will be automatically copied to clipboard; The third step, log in to the remote server, paste the command and execute, the class file you selected will be hot swaped.

# Arthas Hot Swap插件介绍
## 引言
如果你的开发环境是java远程服务器，远程服务器debug端口被限制，那么你可能无法通过debug HotSwap实现热部署，那么本文idea插件可以帮助你远程服务器实现热部署。热部署是帮助开发提高效率的利器，本文介绍一种基于[Arthas](https://github.com/alibaba/arthas)简单快捷的热部署插件([Arthas Hot Swap](https://github.com/xxxtai/arthas-hotswap))。使用该插件进行远程热部署无需任何配置，无需申请debug端口，只需几个简单动作就能完成。
## 使用方法
1. idea安装插件“Arthas Hot Swap”，编译安装或者下载release包安装。
2. 热部署使用的是class文件，所以需要先使用“mvn compile”编译相关工程，后续可以使用idea的Recompile编译单个文件，节省编译时间。
3. 选择需要热部署的java源文件或者class文件，在类名上单击右键，选择“Arthas Hot Swap”的“Swap this class”，插件后台执行成功后会把热部署需要的命令复制到粘贴板。
4. 登录远程服务器，粘贴热部署命令并执行，热部署完成，该机器运行着最新的class。机器第一次执行热部署命令，可能失败，再执行一次试下。

![第一步：选择Arthas Hot Swap插件选项](https://ata2-img.oss-cn-zhangjiakou.aliyuncs.com/b47d34513f1d4c53f4fd309464ef37b7.jpg)

![第二步：登录远程服务器粘贴命令并执行](https://ata2-img.oss-cn-zhangjiakou.aliyuncs.com/cb7c29f73c18e591a8f41b5e96604aa2.jpg)

![第三步：执行完成，热部署成功](https://ata2-img.oss-cn-zhangjiakou.aliyuncs.com/ff42a488e0a3c2c7aee5e0d1874fadea.png)


## Q&A
Q: 热部署有什么限制吗？
A: 由于是基于arthas的redefine命令实现的热部署，redefine又是基于Instrumentation API实现的热部署，所以和idea 的 debug Hot Swap是有一样的限制。限制如下：无法修改类名、方法名，无法修改类属性名称，无法新增类属性，无法新增非静态方法。由于本插件暂时无法获取内部类的class文件，所以不支持修改内部类。

# Arthas Hot Swap插件原理
[Arthas](https://github.com/alibaba/arthas)是阿里巴巴开源的Java代码诊断工具。Arthas的[redefine命令](https://alibaba.github.io/arthas/redefine.html)是基于Instrumentation API实现的热部署，因此和HotSwap热部署方式一样，有同样的限制。Arthas的redefine命令其实是把Instrumentation API的redefineClasses方法包装后提供给用户，那么我们就可以根据redefine命令发挥想象力，实现热部署。

Arthas官方文档推荐使用jad/mc/redefine等一连串命令实现class远程热替换，流程大概是：jad命令对老class进行反编译->vim编辑源码->mc命令编译源码->redefine热替换class。这种方式存在两个问题：一是太麻烦，二是mc编译大概率会失败。如果能这样就好，本地修改代码编译后上传class文件到远程服务器，再使用redefine命令热替换class。因此官方文档又推荐，首先将class二进制文件转换成base64编码，然后再复制粘贴到远程服务器，再把base64编码转换成class文件，最后，使用redefine进行热替换。这么麻烦，还不如重新部署呢。

Arthas官方推荐的热替换方法最大的问题在于，上传class文件到远程服务器进行热替换的流程太麻烦，那么这些麻烦且固定的流程为何不交给机器来做呢。所以，本文推荐的“Arthas Hot Swap”插件就有必要了，该插件就是为了提升使用Arthas进行热替换的效率，几个简单的动作就能热替换一个class文件，可以解决开发过程中80%的热部署需求，剩下的20%用重新部署解决就好了。

## Arthas Hot Swap插件执行流程
1. 根据用户选择的源文件找到class文件，默认在/target/classes路径下面查找，用户也可以直接选择class文件。
2. 使用AES-128加密class文件，输出base64编码。
3. 加密的class文件上传至oss，返回oss访问地址。
4. 渲染热部署需要执行的脚本，渲染后的脚本也上传至oss，返回oss访问地址。
5. 组装热部署命令，并自动复制到粘贴板。

## 远程服务端热部署执行流程
1. 登录远程服务器，粘贴热部署命令并执行。
2. 下载热部署脚本，执行热部署脚本。
3. 创建工作空间。
4. 检查是否安装openssl，没有安装则进行安装。
5. 下载AES-128加密的class文件。
6. 使用openssl enc解密得到class二进制文件。
7. 通过脚本方式安装arthas。
8. 创建与arthas通信的管道并启动arthas。
9. arthas选择java进程，默认选择第一个。
10. arthas执行redefine命令热替换class文件。
11. 打印热替换结果。
