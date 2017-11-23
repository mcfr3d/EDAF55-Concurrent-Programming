#!/bin/bash

# In termnial: 
# 1. Be in edaf55-camera-project folder
# 2. Type ./build_server.sh
# Will compile all C-files and create a runnable Server-file in bin_c

cd src_c/main
make -f Makefile
# {rm -rfv fake_server.dSYM} &>/dev/null
cd ../..
mv src_c/main/simple_tcp_server Server
