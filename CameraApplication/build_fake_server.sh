#!/bin/bash

# In termnial: 
# 1. Be in edaf55-camera-project folder
# 2. Type ./build_server.sh
# Will compile all C-files and create a runnable Server-file in bin_c
# Then it runs the Server-file


cd src_c/main
make -f Makefile.fake
{rm -rfv fake_server.dSYM} &>/dev/null
cd ../..
mv src_c/main/fake_server bin_c/Fake_Server
./bin_c/Fake_Server
