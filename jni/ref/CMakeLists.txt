set(KYBER_SRCS kex.c kem.c indcpa.c polyvec.c poly.c ntt.c cbd.c reduce.c verify.c)
set(KYBER_FIPS202_SRCS ${KYBER_SRCS} symmetric-shake.c)
set(KYBER_NINETIES_SRCS ${KYBER_SRCS} symmetric-aes.c)
set(FIPS202_SRCS fips202.c)
set(AES256CTR_SRCS aes256ctr.c)
set(SHA2_SRCS sha256.c sha512.c)

if(MSVC)
  add_compile_options(/nologo /O2 /W4 /wd4146 /wd4244)
else()
  add_compile_options(-Wall -Wextra -Wpedantic -Werror)
  add_compile_options(-Wmissing-prototypes -Wredundant-decls -Wshadow -Wpointer-arith)
  add_compile_options(-O3 -fomit-frame-pointer)
endif()

add_library(fips202_ref_jni ${FIPS202_SRCS})
add_library(aes256ctr_ref_jni ${AES256CTR_SRCS})
add_library(sha2_ref_jni ${SHA2_SRCS})

# Kyber 512
add_library(kyber512_ref_jni SHARED ${KYBER_FIPS202_SRCS})
add_library(kyber512_90s_ref_jni SHARED ${KYBER_NINETIES_SRCS})
target_compile_definitions(kyber512_ref_jni PUBLIC KYBER_K=2)
target_compile_definitions(kyber512_90s_ref_jni PUBLIC KYBER_K=2 KYBER_90S)
target_link_libraries(kyber512_ref_jni INTERFACE fips202_ref_jni)
target_link_libraries(kyber512_90s_ref_jni INTERFACE aes256ctr_ref_jni sha2_ref)

