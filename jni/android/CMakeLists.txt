set(KYBER_SRCS ../ref/indcpa.c ../ref/polyvec.c ../ref/poly.c ../ref/ntt.c ../ref/cbd.c ../ref/reduce.c ../ref/verify.c ../ref/randombytes.c)
set(KYBER_FIPS202_SRCS ../ref/kem.c ${KYBER_SRCS} ../ref/symmetric-shake.c)
set(KYBER_NINETIES_SRCS ../ref/kem_90s.c ${KYBER_SRCS} ../ref/symmetric-aes.c)
set(FIPS202_SRCS ../ref/fips202.c)
set(AES256CTR_SRCS ../ref/aes256ctr.c)
set(SHA2_SRCS ../ref/sha256.c ../ref/sha512.c)

set(CMAKE_C_COMPILER ../../../toolchains/android-ndk-r13b/toolchains/aarch64-linux-android-4.9/prebuilt/linux-x86_64/bin/aarch64-linux-android-gcc)
set(CMAKE_SYSROOT ../../../toolchains/android-ndk-r13b/platforms/android-21/arch-arm64)

if(MSYS OR MINGW)
else()
    add_compile_options(-fPIC -shared -std=c99 -o build/apk/lib/arm64-v8a/libkyber512_90s_ref_jni.so)
    add_compile_options(-D KYBER_K=2)
    add_compile_options(-march=armv8-a)
endif()

add_library(kyber512_android_jni SHARED ${KYBER_FIPS202_SRCS} ${FIPS202_SRCS})
add_library(kyber512_90s_android_jni SHARED ${AES256CTR_SRCS} ${SHA2_SRCS} ${KYBER_NINETIES_SRCS} ${FIPS202_SRCS})

add_custom_command(
  TARGET kyber512_90s_android_jni POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E make_directory ${CMAKE_BINARY_DIR}/Release/android
)

if (MSVC)
  add_custom_command(
    TARGET kyber512_android_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy "kyber512_android_jni.dll" "${CMAKE_BINARY_DIR}/Release/android"
    COMMENT "Making Release: kyber512_android_jni.dll"
  )
endif()
if (MSYS OR MINGW)
  add_custom_command(
    TARGET kyber512_android_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy "libkyber512_android_jni.dll" "${CMAKE_BINARY_DIR}/Release/android"
    COMMENT "Making Release: libkyber512_android_jni.dll"
  )
endif()
if(UNIX)
  add_custom_command(
    TARGET kyber512_android_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy "libkyber512_android_jni.so" "${CMAKE_BINARY_DIR}/Release/android"
    COMMENT "Making Release: libkyber512_android_jni.so"
  )
endif()


if (MSVC)
  add_custom_command(
    TARGET kyber512_android_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy "kyber512_90s_android_jni.dll" "${CMAKE_BINARY_DIR}/Release/android"
    COMMENT "Making Release: kyber512_90s_android_jni.dll"
  )
endif()
if (MSYS OR MINGW)
  add_custom_command(
    TARGET kyber512_android_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy "libkyber512_90s_android_jni.dll" "${CMAKE_BINARY_DIR}/Release/android"
    COMMENT "Making Release: libkyber512_90s_android_jni.dll"
  )
endif()
if(UNIX)
  add_custom_command(
    TARGET kyber512_android_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy "libkyber512_90s_android_jni.so" "${CMAKE_BINARY_DIR}/Release/android"
    COMMENT "Making Release: libkyber512_90s_90s_android_jni.so"
  )
endif()

