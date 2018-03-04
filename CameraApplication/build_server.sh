#!/bin/bash

# In termnial: 
# 1. Be in edaf55-camera-project folder
# 2. Type ./build_server.sh
# Will compile all C-files and create a runnable Server-file in bin_c

FILE_NAME="Server"
ABORT="Aborting..."
OUTPUT_FILE=".output"

cd src_c/main
make -f Makefile

cd ../..

{mkdir bin_c} &>/dev/null
mv src_c/main/server bin_c/$FILE_NAME
mv src_c/main/motion_server bin_c/motion_server

rm -f bin_c/$OUTPUT_FILE &>/dev/null
touch bin_c/$OUTPUT_FILE

printf "\n*****\nDo you want to transfer it to the camera? (y/n)\n"
read  -n 1 -p ">" answer
printf "\n"
if [ "$answer" = "y" ] || [ "$answer" = "Y" ]; then
    cd bin_c
    read  -n 1 -p ">scp to Camera #" nbr
    printf "\n"
    if [ $nbr -eq $nbr 2>/dev/null ] && [ $nbr -ne 0 ]; then
        SCP_FILE="scp_to_camera.sh"
        cat > $SCP_FILE << EOF
#!/usr/bin/expect -f
set f1 [lindex \$argv 0]
set f2 [lindex \$argv 1]
set f3 [lindex \$argv 2]
set nbr [lindex \$argv 3]
spawn scp \$f1 \$f2 \$f3 rt@argus-\$nbr.student.lth.se:~/
expect {
  "(yes/no)?" {
    send "yes\r"
    exp_continue
  }
  "*?assword:" {
    send "sigge\r"
    interact
  }
  timeout {
    puts "Timed out after 10s. Please try sending files to other camera."
    exit 1
  }
}
EOF
        chmod 777 $SCP_FILE
        ./$SCP_FILE "$FILE_NAME" "motion_server" "run_b5.sh" "$nbr"
        echo 'y'$'\n'$nbr > $OUTPUT_FILE
    else
        echo 'n' > $OUTPUT_FILE
	printf "%s\n" $ABORT 
    fi
else
    echo 'n' > bin_c/$OUTPUT_FILE
    printf "%s\n" $ABORT
fi
