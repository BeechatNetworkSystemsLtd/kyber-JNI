#ifndef KEM_H
#define KEM_H

#include <jni.h>
#include <stdint.h>
#include "params.h"

#define UNUSED(x) (void)(x)

/* Header for class network_beechat_Kyber512_90s */

#ifndef _Included_network_beechat_Kyber512_90s
#define _Included_network_beechat_Kyber512_90s
#ifdef __cplusplus
extern "C" {
#endif
/*
 * Class:     network_beechat_Kyber512_90s
 * Method:    crypto_kem_keypair
 * Signature: ([C[C)I
 */
JNIEXPORT jint JNICALL Java_network_beechat_Kyber512_190s_crypto_1kem_1keypair
  (JNIEnv *, jobject, jcharArray, jcharArray);

/*
 * Class:     network_beechat_Kyber512_90s
 * Method:    crypto_kem_enc
 * Signature: ([C[C[C)I
 */
JNIEXPORT jint JNICALL Java_network_beechat_Kyber512_190s_crypto_1kem_1enc
  (JNIEnv *, jobject, jcharArray, jcharArray, jcharArray);

/*
 * Class:     network_beechat_Kyber512_90s
 * Method:    crypto_kem_dec
 * Signature: ([C[C[C)I
 */
JNIEXPORT jint JNICALL Java_network_beechat_Kyber512_190s_crypto_1kem_1dec
  (JNIEnv *, jobject, jcharArray, jcharArray, jcharArray);

#ifdef __cplusplus
}
#endif
#endif
#endif

