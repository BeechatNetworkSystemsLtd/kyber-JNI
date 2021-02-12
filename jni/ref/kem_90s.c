#include <stddef.h>
#include <stdint.h>
#include "params.h"
#include "network_beechat_Kyber512_90s.h"
#include "indcpa.h"
#include "verify.h"
#include "symmetric.h"
#include "randombytes.h"

/*************************************************
* Name:        Java_network_beechat_Kyber512_190s_crypto_1kem_1keypair
*
* Description: Generates public and private key
*              for CCA-secure Kyber key encapsulation mechanism
*
* Arguments:   - uint8_t *pk: pointer to output public key
*                (an already allocated array of KYBER_PUBLICKEYBYTES bytes)
*              - uint8_t *sk: pointer to output private key
*                (an already allocated array of KYBER_SECRETKEYBYTES bytes)
*
* Returns 0 (success)
**************************************************/
JNIEXPORT jint JNICALL Java_network_beechat_Kyber512_190s_crypto_1kem_1keypair
    (JNIEnv *env, jobject thisObject, jcharArray pk, jcharArray sk)
{
  size_t i;
  UNUSED(env);
  UNUSED(thisObject);
  indcpa_keypair((uint8_t *)pk, (uint8_t *)sk);
  for(i=0;i<KYBER_INDCPA_PUBLICKEYBYTES;i++)
    ((uint8_t *)sk)[i+KYBER_INDCPA_SECRETKEYBYTES] = ((uint8_t *)pk)[i];
  hash_h(((uint8_t *)sk)+KYBER_SECRETKEYBYTES-2*KYBER_SYMBYTES, ((uint8_t *)pk), KYBER_PUBLICKEYBYTES);
  /* Value z for pseudo-random output on reject */
  randombytes(((uint8_t *)sk)+KYBER_SECRETKEYBYTES-KYBER_SYMBYTES, KYBER_SYMBYTES);
  return 0;
}

/*************************************************
* Name:        Java_network_beechat_Kyber512_190s_crypto_1kem_1enc
*
* Description: Generates cipher text and shared
*              secret for given public key
*
* Arguments:   - uint8_t *ct: pointer to output cipher text
*                (an already allocated array of KYBER_CIPHERTEXTBYTES bytes)
*              - uint8_t *ss: pointer to output shared secret
*                (an already allocated array of KYBER_SSBYTES bytes)
*              - const uint8_t *pk: pointer to input public key
*                (an already allocated array of KYBER_PUBLICKEYBYTES bytes)
*
* Returns 0 (success)
**************************************************/
JNIEXPORT jint JNICALL Java_network_beechat_Kyber512_190s_crypto_1kem_1enc
    (JNIEnv *env, jobject thisObject, jcharArray ct, jcharArray ss, jcharArray pk)
{
  uint8_t buf[2*KYBER_SYMBYTES];
  /* Will contain key, coins */
  uint8_t kr[2*KYBER_SYMBYTES];
  UNUSED(env);
  UNUSED(thisObject);

  randombytes(buf, KYBER_SYMBYTES);
  /* Don't release system RNG output */
  hash_h(buf, buf, KYBER_SYMBYTES);

  /* Multitarget countermeasure for coins + contributory KEM */
  hash_h(buf+KYBER_SYMBYTES, (uint8_t *)pk, KYBER_PUBLICKEYBYTES);
  hash_g(kr, buf, 2*KYBER_SYMBYTES);

  /* coins are in kr+KYBER_SYMBYTES */
  indcpa_enc((uint8_t *)ct, buf, (uint8_t *)pk, kr+KYBER_SYMBYTES);

  /* overwrite coins in kr with H(c) */
  hash_h(kr+KYBER_SYMBYTES, (uint8_t *)ct, KYBER_CIPHERTEXTBYTES);
  /* hash concatenation of pre-k and H(c) to k */
  kdf((uint8_t *)ss, kr, 2*KYBER_SYMBYTES);
  return 0;
}

/*************************************************
* Name:        Java_network_beechat_Kyber512_190s_crypto_1kem_1dec
*
* Description: Generates shared secret for given
*              cipher text and private key
*
* Arguments:   - uint8_t *ss: pointer to output shared secret
*                (an already allocated array of KYBER_SSBYTES bytes)
*              - const uint8_t *ct: pointer to input cipher text
*                (an already allocated array of KYBER_CIPHERTEXTBYTES bytes)
*              - const uint8_t *sk: pointer to input private key
*                (an already allocated array of KYBER_SECRETKEYBYTES bytes)
*
* Returns 0.
*
* On failure, ss will contain a pseudo-random value.
**************************************************/
JNIEXPORT jint JNICALL Java_network_beechat_Kyber512_190s_crypto_1kem_1dec
  (JNIEnv *env, jobject thisObject, jcharArray ss, jcharArray ct, jcharArray sk)
{
  size_t i;
  int fail;
  uint8_t buf[2*KYBER_SYMBYTES];
  /* Will contain key, coins */
  uint8_t kr[2*KYBER_SYMBYTES];
  uint8_t cmp[KYBER_CIPHERTEXTBYTES];
  const uint8_t *pk = ((uint8_t *)sk)+KYBER_INDCPA_SECRETKEYBYTES;
  UNUSED(env);
  UNUSED(thisObject);

  indcpa_dec(buf, (uint8_t *)ct, (uint8_t *)sk);

  /* Multitarget countermeasure for coins + contributory KEM */
  for(i=0;i<KYBER_SYMBYTES;i++)
    buf[KYBER_SYMBYTES+i] = ((uint8_t *)sk)[KYBER_SECRETKEYBYTES-2*KYBER_SYMBYTES+i];
  hash_g(kr, buf, 2*KYBER_SYMBYTES);

  /* coins are in kr+KYBER_SYMBYTES */
  indcpa_enc(cmp, buf, pk, kr+KYBER_SYMBYTES);

  fail = verify((uint8_t *)ct, cmp, KYBER_CIPHERTEXTBYTES);

  /* overwrite coins in kr with H(c) */
  hash_h(kr+KYBER_SYMBYTES, (uint8_t *)ct, KYBER_CIPHERTEXTBYTES);

  /* Overwrite pre-k with z on re-encryption failure */
  cmov(kr, ((uint8_t *)sk)+KYBER_SECRETKEYBYTES-KYBER_SYMBYTES, KYBER_SYMBYTES, fail);

  /* hash concatenation of pre-k and H(c) to k */
  kdf((uint8_t *)ss, kr, 2*KYBER_SYMBYTES);
  return 0;
}

