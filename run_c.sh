#!/bin/bash

RUN_FILE="run_b5.sh"
mkdir bin_c &>/dev/null

cd bin_c
cat > $RUN_FILE << EOF
#!/bin/bash

./motion_server &
if [ -z "\$1" ]; then
    ./Server 6666 &
else
    ./Server \$1 &
fi
EOF
chmod 777 $RUN_FILE

SSH_FILE="ssh_to_camera.sh"

cat > $SSH_FILE << EOF
#!/usr/bin/expect -f

set nbr [lindex \$argv 0]
spawn ssh rt@argus-\$nbr
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
    puts "Timed out. Please try connecting to another camera."
    exit 1
  }
}
EOF
chmod 777 $SSH_FILE

cd ..

./build_server.sh

cd bin_c

wanted_to_scp=$(head -n 1 .output)

if [ "$wanted_to_scp" = "y" ]; then
    camera_nbr=$(sed -n '2p' < .output)
    printf "\n*****\nDo you want to ssh to camera $camera_nbr? (y/n)\n"
    read  -n 1 -p ">" answer
    printf "\n"
    if [ "$answer" = "y" ] || [ "$answer" = "Y" ]; then
        ./$SSH_FILE "$camera_nbr" 
    fi
fi
