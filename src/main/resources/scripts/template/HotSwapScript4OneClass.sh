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
if [ ! -d "./arthas-hot-swap" ]
then
  mkdir ./arthas-hot-swap
  echo "mkdir ./arthas-hot-swap success"
else
  rm -rf ./arthas-hot-swap
  mkdir ./arthas-hot-swap
  echo "./arthas-hot-swap exists, delete the directory first, and then create a new one"
fi

cd ./arthas-hot-swap

rm -f /tmp/arthas-hot-swap-result

openssl version
if [[ $? -eq 0 ]]; then
    echo " openssl has been installed successfully "
else
    echo " openssl is not installed, and installation were start next "
    sudo yum install openssl openssl-devel
fi

curl  ${currentClassOssUrl} >> encrypt-${className}.txt
openssl enc -aes-128-cbc -a -d -in encrypt-${className}.txt -out ${className}.class -K $1 -iv $2

curl -L http://gjusp.alicdn.com/jucube/arthas-install.sh | sh

rm -f tmp_in
mknod tmp_in p
exec 8<> tmp_in
./as.sh <&8 &

sleep 2s
echo "1" >> tmp_in

sleep 2s
echo "redefine $(pwd)/${className}.class > /tmp/arthas-hot-swap-result" >> tmp_in

echo "q" >> tmp_in
sleep 1s

swapResult=$(cat /tmp/arthas-hot-swap-result | grep "success")
echo $swapResult
if [[ $swapResult != "" ]]
then
echo '
************************* The following files were successfully hot deployed *****************************
***
*** ${className}.class
***
***********************************************************************************************************
'
else
echo '
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Failed to hot deployed the following files %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%%%
%%% ${className}.class
%%%
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
'
fi
