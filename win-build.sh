#!/bin/bash
java -jar ./jar/packr-all-4.0.0.jar \
     --platform windows64 \
     --jdk ../zip/windows.zip \
     --useZgcIfSupportedOs \
     --executable SamuraiDash \
     --classpath ./lwjgl3/build/libs/SamuraiDash-1.0.0.jar \
     --mainclass com.tfg.samuraidash.lwjgl3.Lwjgl3Launcher \
     --vmargs Xmx3G XstartOnFirstThread \
     --resources assets/* \
     --output ./exe 