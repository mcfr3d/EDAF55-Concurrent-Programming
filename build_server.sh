#!/bin/bash

# In termnial: 
# 1. Be in edaf55-camera-project folder
# 2. Type ./build_server.sh
# Will compile all C-files and create a runnable Server-file in bin_c

FILE_NAME="Server"
ABORT="Aborting..."

cd src_c/main
make -f Makefile
# {rm -rfv fake_server.dSYM} &>/dev/null

cd ../..

{mkdir bin_c} &>/dev/null
mv src_c/main/server bin_c/$FILE_NAME

printf "\n*****\nDo you want to transfer it to the camera? (y/n)\n"
read  -n 1 -p ">" answer
printf "\n"
if [ "$answer" = "y" ] || [ "$answer" = "Y" ]; then
    cd bin_c
    read  -n 1 -p "Camera #" nbr
    printf "\n"
    if [ $nbr -eq $nbr 2>/dev/null ] && [ $nbr -ne 0 ]; then
	scp $FILE_NAME rt@argus-$nbr.student.lth.se:~/
    else
	printf "%s\n" $ABORT 
    fi
else
    printf "%s\n" $ABORT
fi

echo "$file_name"
