# ArthasHotSwap
[![Releases](https://raw.githubusercontent.com/xxxtai/ArthasHotSwap/083b940e1182f574c3afb1fba5b728a6410a4510/src/main/resources/icons/readme-cn.svg)](https://github.com/xxxtai/ArthasHotSwap/blob/master/README-CN.md)
[![Releases](https://raw.githubusercontent.com/xxxtai/ArthasHotSwap/69f4f1db9b07d615daa8737761c6dea97a49ccc3/src/main/resources/icons/readme-en.svg)](https://github.com/xxxtai/ArthasHotSwap/blob/master/README.md)
[![Releases](https://raw.githubusercontent.com/xxxtai/ArthasHotSwap/1ee146561f52ff9b00d11372e095baec69d26537/src/main/resources/icons/release.svg)](https://github.com/xxxtai/ArthasHotSwap/releases)
[![Releases](https://raw.githubusercontent.com/xxxtai/ArthasHotSwap/8351ed6660130eddd0a8b1adeee7dd99ac4121fc/src/main/resources/icons/arthas.svg)](https://github.com/alibaba/arthas)
## 引言
热部署是帮助开发人员提高效率的利器，如果你的开发语言是java，开发环境是远程服务器，远程服务器debug端口被限制，那么你可能无法通过debug HotSwap实现热部署，那么本文介绍的IntelliJ IDEA插件可以帮助你在远程服务器实现热部署，而且操作简单快捷。本文热部署插件是基于[Arthas redefine](https://arthas.aliyun.com/doc/redefine.html)命令实现的，使用该插件进行远程热部署无需任何配置，无需使用debug端口，只需几个简单动作就能完成。

## 安装插件
方式一：就像安装普通IDEA插件一样，从IDEA插件仓库搜索安装。

方式二：从[Releases](https://github.com/xxxtai/ArthasHotSwap/releases)下载最新安装包，然后打开IDEA，然后依次点击 “左上角IntelliJ IDEA” -> "Prefrences" -> “plugins” -> “install pulgin from disk” -> "选择下载的安装包"。
## 配置插件
通常你无需配置就可以直接使用，在一些特殊情况下，需要根据应用配置“Java命令绝对路径“或者“指定Arthas连接Java进程的全路径类名”。
打开IDEA，然后依次点击 “IntelliJ IDEA(左上角)” -> "Prefrences" -> “Tools” -> “ArthasHotSwap”。

## 热部署步骤
#### 第一步：编译整个工程
热部署使用的是class文件，所以需要先使用“mvn compile”编译相关工程，后续可以使用IDEA的Recompile编译单个文件，节省编译时间。
#### 第二步：ArthasHotSwap插件执行
选择需要热部署的java源文件或者class文件，在类名或方法名上单击右键，选择“ArthasHotSwap”的“Swap this class”，插件后台执行成功后会把热部署需要的命令复制到粘贴板。

![第二步：选择Arthas Hot Swap插件选项](https://ata2-img.oss-cn-zhangjiakou.aliyuncs.com/b47d34513f1d4c53f4fd309464ef37b7.jpg)

#### 第三步：登录远程服务器粘贴命令并执行
登录远程服务器，粘贴热部署命令并执行，热部署完成，该机器运行着最新的class。机器第一次执行热部署命令，可能失败，再执行一次试下。由于需要阿里云OSS作为媒介传递字节码文件，你的远程服务器需要能够访问阿里云OSS服务器。

![第三步：登录远程服务器粘贴命令并执行](https://user-images.githubusercontent.com/17845368/111869345-f5291f00-89b9-11eb-827b-1b3fd6119979.png)

![第四步：执行完成，热部署成功](https://ata2-img.oss-cn-zhangjiakou.aliyuncs.com/ff42a488e0a3c2c7aee5e0d1874fadea.png)

## 热部署的限制
由于本插件是基于Arthas的[redefine](https://alibaba.github.io/arthas/redefine.html)命令实现热部署，redefine又是基于JDK Instrumentation API实现的热部署，所以和debug方式实现热部署有一样的限制。限制如下：无法修改类名、方法名，无法修改类属性名称，无法新增类属性，无法新增非静态方法。由于本插件暂时无法获取内部类的class文件，所以暂时不支持修改内部类。

## ArthasHotSwap的原理
[Arthas](https://github.com/alibaba/arthas)是阿里巴巴开源的Java代码诊断工具。Arthas的[redefine命令](https://alibaba.github.io/arthas/redefine.html)是基于JDK Instrumentation API实现的热部署。Arthas的redefine命令其实是把Instrumentation API的redefineClasses方法包装后提供给用户，那么我们就可以根据redefine命令发挥想象力，实现热部署。

Arthas官方文档推荐使用jad/mc/redefine等一连串命令实现class远程热替换，流程大概是：jad命令对老class进行反编译->vim编辑源码->mc命令编译源码->redefine热替换class。这种方式存在两个问题：一是太麻烦，二是mc编译大概率会失败。如果能这样就好，本地修改代码编译后上传class文件到远程服务器，再使用redefine命令热替换class。因此官方文档又推荐，首先将class二进制文件转换成base64编码，然后再复制粘贴到远程服务器，再把base64编码转换成class文件，最后，使用redefine进行热替换。这么麻烦，还不如重新部署呢。所以redefine命令比较鸡肋。

Arthas官方推荐的热替换方法最大的问题在于，上传class文件到远程服务器进行热替换的流程太麻烦，那么这些麻烦且固定的流程为何不交给机器来做呢。所以，本文推荐的“ArthasHotSwap”插件就有必要了，该插件就是为了提升使用Arthas进行热替换的效率，几个简单的动作就能热替换一个class文件，可以解决开发过程中80%的热部署需求，剩下的20%用重新部署解决就好了。

### ArthasHotSwap插件执行流程
1. 根据用户选择的源文件找到class文件，默认在/target/classes路径下面查找，用户也可以直接选择class文件；
2. 使用AES-128加密class文件，输出base64编码；
3. 加密的class文件上传至oss，返回oss访问地址；
4. 渲染热部署需要执行的脚本，渲染后的脚本也上传至oss，返回oss访问地址；
5. 组装热部署命令，并自动复制到粘贴板。

### 远程服务端热部署执行流程
1. 登录远程服务器，粘贴热部署命令并执行；
2. 下载热部署脚本，执行热部署脚本；
3. 创建工作空间；
4. 检查是否安装openssl，没有安装则进行安装；
5. 下载AES-128加密的class文件；
6. 使用openssl enc解密得到class二进制文件；
7. 通过脚本方式安装arthas；
8. 创建与arthas通信的管道并启动arthas；
9. arthas选择java进程，默认选择第一个，也可以用户指定；
10. arthas执行redefine命令热替换class文件；
11. 打印热替换结果。
