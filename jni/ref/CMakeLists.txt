set(KYBER_SRCS indcpa.c polyvec.c poly.c ntt.c cbd.c reduce.c verify.c randombytes.c)
set(KYBER_FIPS202_SRCS kem.c ${KYBER_SRCS} symmetric-shake.c)
set(KYBER_NINETIES_SRCS kem_90s.c ${KYBER_SRCS} symmetric-aes.c)
set(FIPS202_SRCS fips202.c)
set(AES256CTR_SRCS aes256ctr.c)
set(SHA2_SRCS sha256.c sha512.c)

find_package(Java)
find_package(Java COMPONENTS Development)

if(MSVC)
  add_compile_options(/nologo /O2 /W4 /wd4146 /wd4244)
  add_compile_options(/I${_JAVA_HOME}\include /I${_JAVA_HOME}\include\win32)
else()
  add_compile_options(-I${_JAVA_HOME}/include -I${_JAVA_HOME}/include/linux)
  add_compile_options(-Wall -fPIC -Wextra -Wpedantic -Werror)
  add_compile_options(-Wmissing-prototypes -Wredundant-decls -Wshadow -Wpointer-arith)
  add_compile_options(-O3 -fomit-frame-pointer)
endif()

add_library(fips202_ref_jni ${FIPS202_SRCS})
add_library(aes256ctr_ref_jni ${AES256CTR_SRCS})
add_library(sha2_ref_jni ${SHA2_SRCS})

# Kyber 512
add_library(kyber512_ref_jni SHARED ${KYBER_FIPS202_SRCS} ${FIPS202_SRCS})
add_library(kyber512_90s_ref_jni SHARED ${AES256CTR_SRCS} ${SHA2_SRCS} ${KYBER_NINETIES_SRCS} ${FIPS202_SRCS})
target_compile_definitions(kyber512_ref_jni PUBLIC KYBER_K=2)
target_compile_definitions(kyber512_90s_ref_jni PUBLIC KYBER_K=2 KYBER_90S)
target_link_libraries(kyber512_ref_jni INTERFACE fips202_ref_jni)
target_link_libraries(kyber512_90s_ref_jni INTERFACE aes256ctr_ref_jni sha2_ref)

add_custom_command(
  TARGET kyber512_90s_ref_jni POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E make_directory ${CMAKE_BINARY_DIR}/Release
)
add_custom_command(
  TARGET kyber512_90s_ref_jni POST_BUILD
  COMMAND ${CMAKE_COMMAND} -E make_directory ${CMAKE_BINARY_DIR}/Release/ref
)
if (MSVC)
  add_custom_command(
    TARGET kyber512_90s_ref_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy "kyber512_90s_ref_jni.dll" "${CMAKE_BINARY_DIR}/Release"
    COMMENT "Making Release: kyber512_90s_ref_jni.dll"
  )
else()
  add_custom_command(
    TARGET kyber512_90s_ref_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy "libkyber512_90s_ref_jni.so" "${CMAKE_BINARY_DIR}/Release"
    COMMENT "Making Release: kyber512_90s_ref_jni.so"
  )
endif()

if (MSVC)
  add_custom_command(
    TARGET kyber512_ref_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy "kyber512_ref_jni.dll" "${CMAKE_BINARY_DIR}/Release"
    COMMENT "Making Release: kyber512_ref_jni.dll"
  )
else()
  add_custom_command(
    TARGET kyber512_ref_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy "libkyber512_ref_jni.so" "${CMAKE_BINARY_DIR}/Release"
    COMMENT "Making Release: kyber512_ref_jni.so"
  )
endif()

if (MSVC)
  add_custom_command(
    TARGET kyber512_ref_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy_directory "${CMAKE_SOURCE_DIR}/jni/java/ref_win" "${CMAKE_BINARY_DIR}/Release/ref"
    COMMENT "Making Release: Java Classes"
  )
else()
  add_custom_command(
    TARGET kyber512_ref_jni POST_BUILD
    COMMAND ${CMAKE_COMMAND} -E copy_directory "${CMAKE_SOURCE_DIR}/jni/java/ref_lin" "${CMAKE_BINARY_DIR}/Release/ref"
    COMMENT "Making Release: Java Classes"
  )
endif()

