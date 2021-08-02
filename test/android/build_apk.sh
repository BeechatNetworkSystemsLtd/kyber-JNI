#!/bin/bash

HOME="../../toolchains"
SDK="${HOME}/android-sdk-linux"
BUILD_TOOLS="${SDK}/build-tools/25.0.0"
PLATFORM="${SDK}/platforms/android-21"
NDK="${HOME}/android-ndk-r13b"
ARM_TOOLCHAIN="${NDK}/toolchains/aarch64-linux-android-4.9/prebuilt/"
ARM_TOOLCHAIN+="linux-x86_64/bin/aarch64-linux-android-gcc"
REF_SRC="../../jni/ref"
#X86_TOOLCHAIN="${NDK}/toolchains/x86-4.9/prebuilt/"

rm -rf build

mkdir -p build/gen build/obj build/apk
mkdir -p build/apk/lib/arm64-v8a


"${ARM_TOOLCHAIN}" --sysroot="${NDK}/platforms/android-21/arch-arm64" \
    -march=armv8-a \
    -D KYBER_K=2 \
    -fPIC -shared -std=c99 -o build/apk/lib/arm64-v8a/libkyber512_ref_jni.so \
    ${REF_SRC}/kem.c \
    ${REF_SRC}/symmetric-shake.c \
    ${REF_SRC}/fips202.c \
    ${REF_SRC}/indcpa.c \
    ${REF_SRC}/polyvec.c \
    ${REF_SRC}/poly.c \
    ${REF_SRC}/ntt.c \
    ${REF_SRC}/cbd.c \
    ${REF_SRC}/reduce.c \
    ${REF_SRC}/verify.c \
    ${REF_SRC}/randombytes.c

"${ARM_TOOLCHAIN}" --sysroot="${NDK}/platforms/android-21/arch-arm64" \
    -march=armv8-a \
    -D KYBER_K=2 \
    -D KYBER_90S \
    -fPIC -shared -std=c99 -o build/apk/lib/arm64-v8a/libkyber512_90s_ref_jni.so \
    ${REF_SRC}/aes256ctr.c \
    ${REF_SRC}/sha256.c \
    ${REF_SRC}/sha512.c \
    ${REF_SRC}/kem_90s.c \
    ${REF_SRC}/symmetric-aes.c \
    ${REF_SRC}/fips202.c \
    ${REF_SRC}/indcpa.c \
    ${REF_SRC}/polyvec.c \
    ${REF_SRC}/poly.c \
    ${REF_SRC}/ntt.c \
    ${REF_SRC}/cbd.c \
    ${REF_SRC}/reduce.c \
    ${REF_SRC}/verify.c \
    ${REF_SRC}/randombytes.c


#"${X86_TOOLCHAIN}" --sysroot="${NDK}/platforms/android-16/arch-x86" \
#    -fPIC -shared -o build/apk/lib/x86/libhello.so jni/hello.c


"${BUILD_TOOLS}/aapt" package -f -m -J build/gen/ -S res \
    -M AndroidManifest.xml -I "${PLATFORM}/android.jar"

javac -source 1.7 -target 1.7 -bootclasspath "${JAVA_HOME}/jre/lib/rt.jar" \
    -classpath "${PLATFORM}/android.jar" -d build/obj \
    build/gen/net/test/R.java java/net/test/*.java

"${BUILD_TOOLS}/dx" --dex --output=build/apk/classes.dex build/obj/

"${BUILD_TOOLS}/aapt" package -f -M AndroidManifest.xml -S res/ \
    -I "${PLATFORM}/android.jar" \
    -F build/KyberTest.unsigned.apk build/apk/

"${BUILD_TOOLS}/zipalign" -f -p 4 \
    build/KyberTest.unsigned.apk build/KyberTest.aligned.apk


"${BUILD_TOOLS}/apksigner" sign --ks keystore.jks \
    --ks-key-alias androidkey --ks-pass pass:android \
    --key-pass pass:android --out build/KyberTest.apk \
    build/KyberTest.aligned.apk

