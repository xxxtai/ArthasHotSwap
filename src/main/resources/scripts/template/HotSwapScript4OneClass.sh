#Copyright (c) 2020, 2021, xxxtai. All rights reserved.
#!/usr/bin/env bash

echo "
  /\$\$\$\$\$\$              /\$\$     /\$\$
 /\$\$__  \$\$            | \$\$    | \$\$
| \$\$  \ \$\$  /\$\$\$\$\$\$  /\$\$\$\$\$\$  | \$\$\$\$\$\$\$   /\$\$\$\$\$\$   /\$\$\$\$\$\$\$
| \$\$\$\$\$\$\$\$ /\$\$__  \$\$|_  \$\$_/  | \$\$__  \$\$ |____  \$\$ /\$\$_____/
| \$\$__  \$\$| \$\$  \__/  | \$\$    | \$\$  \ \$\$  /\$\$\$\$\$\$\$|  \$\$\$\$\$\$
| \$\$  | \$\$| \$\$        | \$\$ /\$\$| \$\$  | \$\$ /\$\$__  \$\$ \____  \$\$
| \$\$  | \$\$| \$\$        |  \$\$\$\$/| \$\$  | \$\$|  \$\$\$\$\$\$\$ /\$\$\$\$\$\$\$/
|__/  |__/|__/         \___/  |__/  |__/ \_______/|_______/
 /\$\$   /\$\$             /\$\$            /\$\$\$\$\$\$
| \$\$  | \$\$            | \$\$           /\$\$__  \$\$
| \$\$  | \$\$  /\$\$\$\$\$\$  /\$\$\$\$\$\$        | \$\$  \__/ /\$\$  /\$\$  /\$\$  /\$\$\$\$\$\$   /\$\$\$\$\$\$
| \$\$\$\$\$\$\$\$ /\$\$__  \$\$|_  \$\$_/        |  \$\$\$\$\$\$ | \$\$ | \$\$ | \$\$ |____  \$\$ /\$\$__  \$\$
| \$\$__  \$\$| \$\$  \ \$\$  | \$\$           \____  \$\$| \$\$ | \$\$ | \$\$  /\$\$\$\$\$\$\$| \$\$  \ \$\$
| \$\$  | \$\$| \$\$  | \$\$  | \$\$ /\$\$       /\$\$  \ \$\$| \$\$ | \$\$ | \$\$ /\$\$__  \$\$| \$\$  | \$\$
| \$\$  | \$\$|  \$\$\$\$\$\$/  |  \$\$\$\$/      |  \$\$\$\$\$\$/|  \$\$\$\$\$/\$\$\$\$/|  \$\$\$\$\$\$\$| \$\$\$\$\$\$\$/
|__/  |__/ \______/    \___/         \______/  \_____/\___/  \_______/| \$\$____/
                                                                      | \$\$
                                                                      | \$\$
                                                                      |__/
"
echo "************************************************** 1. Prepare the workspace **********************************************************"
if [ ! -d "./arthas-hot-swap" ]
then
  mkdir ./arthas-hot-swap
  echo "******* mkdir ./arthas-hot-swap success"
else
  rm -rf ./arthas-hot-swap
  mkdir ./arthas-hot-swap
  echo "******* ./arthas-hot-swap exists, delete the directory first, and then create a new one"
fi
cd ./arthas-hot-swap
rm -f $(pwd)/arthas-hot-swap-result

echo "**************************************************** 2. install openssl **************************************************************"
openssl version
if [[ $? -eq 0 ]]; then
    echo "*******  openssl has been installed successfully "
else
    echo "*******  openssl is not installed, and installation were start next "
    sudo yum install openssl openssl-devel
fi

echo "********************************************* 3. Download the encrypted file *********************************************************"
curl  %[currentClassOssUrl] >> encrypt-%[className].txt

echo "************************************************* 4. Encrypt the file ****************************************************************"
openssl enc -aes-128-cbc -a -d -in encrypt-%[className].txt -out %[className].class -K $1 -iv $2

echo "************************************************* 5. Install arthas ******************************************************************"
specifyJavaHome=%[specifyJavaHome]
arthas_start_cmd=''

if [[ ${specifyJavaHome} == '' ]]
then
    curl -L https://arthas.aliyun.com/install.sh | sh
    arthas_start_cmd='./as.sh'
else
    curl -O https://arthas.aliyun.com/arthas-boot.jar
    arthas_start_cmd=${specifyJavaHome}" -jar arthas-boot.jar"
fi

selectJavaProcessName=%[selectJavaProcessName]

if [[ ${selectJavaProcessName} != '' ]]
then
    arthas_start_cmd=${arthas_start_cmd}" --select "${selectJavaProcessName}
fi

echo "************************************************* 6. Create a pipeline ***************************************************************"
rm -f tmp_in
mknod tmp_in p
exec 8<> tmp_in
${arthas_start_cmd} <&8 &

echo "********************************************* 7. Choose the java process *************************************************************"
sleep 1s
echo "
" >> tmp_in

echo "*********************************************** 8. Redefine the class ****************************************************************"
sleep 3s
echo "retransform $(pwd)/%[className].class > $(pwd)/arthas-hot-swap-result" >> tmp_in
sleep 4s
echo "quit" >> tmp_in
sleep 2s

swapResult=$(cat $(pwd)/arthas-hot-swap-result | grep "success")
echo $swapResult
if [[ $swapResult != "" ]]
then
echo '
****************************************** 9. The following files were successfully hot deployed *******************************************
*****
***** %[className].class
*****
********************************************************************************************************************************************
'
else
echo '
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% 9. Failed to hot deployed the following files %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%%%
%%%%% %[className].class
%%%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
'
cat $(pwd)/arthas-hot-swap-result
fi

