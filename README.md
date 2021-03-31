# ArthasHotSwap
[![Releases](https://raw.githubusercontent.com/xxxtai/ArthasHotSwap/083b940e1182f574c3afb1fba5b728a6410a4510/src/main/resources/icons/readme-cn.svg)](https://github.com/xxxtai/ArthasHotSwap/blob/master/README-CN.md)
[![Releases](https://raw.githubusercontent.com/xxxtai/ArthasHotSwap/69f4f1db9b07d615daa8737761c6dea97a49ccc3/src/main/resources/icons/readme-en.svg)](https://github.com/xxxtai/ArthasHotSwap/blob/master/README.md)
[![Releases](https://raw.githubusercontent.com/xxxtai/ArthasHotSwap/1ee146561f52ff9b00d11372e095baec69d26537/src/main/resources/icons/release.svg)](https://github.com/xxxtai/ArthasHotSwap/releases)
[![Releases](https://raw.githubusercontent.com/xxxtai/ArthasHotSwap/8351ed6660130eddd0a8b1adeee7dd99ac4121fc/src/main/resources/icons/arthas.svg)](https://github.com/alibaba/arthas)

## Abstract
Sometimes, when you're making minor changes to your code, you want to immediately see how they will behave in a working application without shutting down the process. As we all know, hot swap can be achieved through debugging. But in some cases, the debugging port is restricted due to permission control, then you will not be able to achieve HotSwap through debugging. 

The IntelliJ IDEA plugin introduced in this article can help implement hot swap on a remote server, and the operation is simple and fast. The plugin in this article is based on [Arths redefine command](https://arthas.aliyun.com/doc/en/redefine.html).

## Install plugin
1. The first way, “IntelliJ IDEA(Top Left Corner)” -> "Prefrences" -> “plugins” -> "Browase Repositories" -> search for ArthasHotSwap
2. The second way, Download the latest version of the installation package from [Releases](https://github.com/xxxtai/ArthasHotSwap/releases), then open IDEA and click, “IntelliJ IDEA(Top Left Corner)” -> "Prefrences" -> “plugins” -> “install pulgin from disk” -> "choose the installation package".

## Configure plugin
Usually you can use it directly without configuration. In some special cases, you need to config "the absolute path of java command" or "specify the full path calss name of Java process" according to the application configuration.
Configuration: “IntelliJ IDEA(Top Left Corner)” -> "Prefrences" -> “Tools” -> “ArthasHotSwap”。

## HotSwap steps

### Firstly, Compile the entire project

HotSwap uses bytecode files, so first, we need to compile related projects, in the future, we can only compile the modified files to save time.

### Secondly, Implementation of ArthasHotSwap

Find the files(.java or .class) that need to be modified by HotSwap, right click in the IDEA, choose “ArthasHotSwap” and click “Swap this class”. When the plugin is successfully executed in the background, the plugin will copy the command required for HowSwap to the pasteboard.

![Implementation of ArthasHotSwap](https://ata2-img.oss-cn-zhangjiakou.aliyuncs.com/b47d34513f1d4c53f4fd309464ef37b7.jpg)

### Thirdly, Log in and execute command

Next you need to log in to your remote server, then paste the command form the clipboard and execute it with administrator privileges. If the first execution fails, please execute again. Due to the need for Alibaba Cloud OSS as a medium to transfer bytecode files, your remote server needs to be able to access to the Alibaba Cloud Server.

![Log in and Execute Command](https://user-images.githubusercontent.com/17845368/111869345-f5291f00-89b9-11eb-827b-1b3fd6119979.png)

![success](https://ata2-img.oss-cn-zhangjiakou.aliyuncs.com/ff42a488e0a3c2c7aee5e0d1874fadea.png)

## HotSwap limitations

Due to VM design, HotSwap has the following limitations:

1. it is only available if a method body is modified. Changing signatures is not supported.

2. adding and removing class members is not supported.

3. if the modified method is already in the call stack, the changes will take effect only after the program exits the modified method. Until that moment, the method body remains unchanged, and the frame is marked as obsolete.

## Principle of ArthasHotSwap

[Arthas](https://github.com/alibaba/arthas) is a Java diagnostic tool open-sourced by Alibaba middleware team. [Arthas redefine](https://arthas.aliyun.com/doc/en/redefine.html) is realized on the basis of [Instrumentation API](https://docs.oracle.com/javase/8/docs/api/java/lang/instrument/Instrumentation.html#redefineClasses-java.lang.instrument.ClassDefinition...-). The plugin uploads bytecode files to the remote server with the help of OSS, and reload classes changed with Arthas redefine.

### Implementation process of ArthasHotSwap
1. Find the corresponding class file according to the source file selected by users;
2. Use AES-128 to encrypt class file，output the encoding of base64;
3. Upload encrypted class file to OSS, and return the access address of OSS;
4. To render the script of HotSwap, and upload the script to OSS, return the access address of OSS;
5. Assemble the command of HotSwap and automatically copy it to the pasteboard.

### Implementation process of remote server
1. Log in to the remote server, paste the command and execute it, next you just need to wait for the command execution to complete;
2. Download the script of HotSwap from OSS, and execute it automatically;
3. Create a workspace;
4. Check whether OpenSSL is installed. if not, install it;
5. Download class file encrypted by ES-128;
6. Use OpenSSL ENC to decrypt the encrypted file to get class binary file;
7. Install Arthas；
8. Create a pipe to communicate with Arthas and start Arthas;
9. Arthas selects Java process for attaching, and the first one is selected by default, which can also specified by the use;
10. Arthas executes the command of redefine to replace the class;
11. Prints the result of HotSwap.
