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
    (JNIEnv *env, jobject thisObject, jbyteArray pk, jbyteArray sk)
{
  size_t i;
  uint8_t _pk[KYBER_PUBLICKEYBYTES] = { 0 };
  uint8_t _sk[KYBER_SECRETKEYBYTES] = { 0 };
  jbyte _pkj[KYBER_PUBLICKEYBYTES] = { 0 };
  jbyte _skj[KYBER_SECRETKEYBYTES] = { 0 };

  UNUSED(thisObject);

  indcpa_keypair((uint8_t *)_pk, (uint8_t *)_sk);
  for(i=0;i<KYBER_INDCPA_PUBLICKEYBYTES;i++)
    ((uint8_t *)_sk)[i+KYBER_INDCPA_SECRETKEYBYTES] = ((uint8_t *)_pk)[i];
  hash_h(((uint8_t *)_sk)+KYBER_SECRETKEYBYTES-2*KYBER_SYMBYTES, ((uint8_t *)_pk), KYBER_PUBLICKEYBYTES);
  /* Value z for pseudo-random output on reject */
  randombytes(((uint8_t *)_sk)+KYBER_SECRETKEYBYTES-KYBER_SYMBYTES, KYBER_SYMBYTES);

  for (i = 0; i < KYBER_PUBLICKEYBYTES; i++)
  {
    _pkj[i] = _pk[i];
  }
  for (i = 0; i < KYBER_SECRETKEYBYTES; i++)
  {
    _skj[i] = _sk[i];
  }

  (*env)->SetByteArrayRegion(env, pk, 0, KYBER_PUBLICKEYBYTES, _pkj);
  (*env)->SetByteArrayRegion(env, sk, 0, KYBER_SECRETKEYBYTES, _skj);

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
    (JNIEnv *env, jobject thisObject, jbyteArray ct, jbyteArray ss, jbyteArray pk)
{
  uint8_t buf[2*KYBER_SYMBYTES];
  /* Will contain key, coins */
  uint8_t kr[2*KYBER_SYMBYTES];

  uint8_t _pk[KYBER_PUBLICKEYBYTES] = { 0 };
  jbyte _pkj[KYBER_PUBLICKEYBYTES] = { 0 };
  uint8_t _ct[KYBER_CIPHERTEXTBYTES] = { 0 };
  jbyte _ctj[KYBER_CIPHERTEXTBYTES] = { 0 };
  uint8_t _ss[KYBER_SSBYTES] = { 0 };
  jbyte _ssj[KYBER_SSBYTES] = { 0 };

  (*env)->GetByteArrayRegion(env, pk, 0, KYBER_PUBLICKEYBYTES, _pkj);
  (*env)->GetByteArrayRegion(env, ct, 0, KYBER_CIPHERTEXTBYTES, _ctj);
  (*env)->GetByteArrayRegion(env, ss, 0, KYBER_SSBYTES, _ssj);

  for (size_t ii = 0; ii < KYBER_PUBLICKEYBYTES; ii++) _pk[ii] = _pkj[ii];
  for (size_t ii = 0; ii < KYBER_CIPHERTEXTBYTES; ii++) _ct[ii] = _ctj[ii];
  for (size_t ii = 0; ii < KYBER_SSBYTES; ii++) _ss[ii] = _ssj[ii];

  UNUSED(thisObject);

  randombytes(buf, KYBER_SYMBYTES);
  /* Don't release system RNG output */
  hash_h(buf, buf, KYBER_SYMBYTES);

  /* Multitarget countermeasure for coins + contributory KEM */
  hash_h(buf+KYBER_SYMBYTES, _pk, KYBER_PUBLICKEYBYTES);
  hash_g(kr, buf, 2*KYBER_SYMBYTES);

  /* coins are in kr+KYBER_SYMBYTES */
  indcpa_enc(_ct, buf, _pk, kr+KYBER_SYMBYTES);

  /* overwrite coins in kr with H(c) */
  hash_h(kr+KYBER_SYMBYTES, _ct, KYBER_CIPHERTEXTBYTES);
  /* hash concatenation of pre-k and H(c) to k */
  kdf(_ss, kr, 2*KYBER_SYMBYTES);
  for (size_t ii = 0; ii < KYBER_PUBLICKEYBYTES; ii++) _pkj[ii] = _pk[ii];
  for (size_t ii = 0; ii < KYBER_CIPHERTEXTBYTES; ii++) _ctj[ii] = _ct[ii];
  for (size_t ii = 0; ii < KYBER_SSBYTES; ii++) _ssj[ii] = _ss[ii];
  (*env)->SetByteArrayRegion(env, pk, 0, KYBER_PUBLICKEYBYTES, _pkj);
  (*env)->SetByteArrayRegion(env, ct, 0, KYBER_CIPHERTEXTBYTES, _ctj);
  (*env)->SetByteArrayRegion(env, ss, 0, KYBER_SSBYTES, _ssj);
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
  (JNIEnv *env, jobject thisObject, jbyteArray ss, jbyteArray ct, jbyteArray sk)
{
  size_t i;
  int fail;
  uint8_t buf[2*KYBER_SYMBYTES];
  /* Will contain key, coins */
  uint8_t kr[2*KYBER_SYMBYTES];
  uint8_t cmp[KYBER_CIPHERTEXTBYTES];

  UNUSED(thisObject);

  uint8_t _sk[KYBER_SECRETKEYBYTES] = { 0 };
  jbyte _skj[KYBER_SECRETKEYBYTES] = { 0 };
  uint8_t _ct[KYBER_CIPHERTEXTBYTES] = { 0 };
  jbyte _ctj[KYBER_CIPHERTEXTBYTES] = { 0 };
  uint8_t _ss[KYBER_SSBYTES] = { 0 };
  jbyte _ssj[KYBER_SSBYTES] = { 0 };

  (*env)->GetByteArrayRegion(env, sk, 0, KYBER_SECRETKEYBYTES, _skj);
  (*env)->GetByteArrayRegion(env, ct, 0, KYBER_CIPHERTEXTBYTES, _ctj);
  (*env)->GetByteArrayRegion(env, ss, 0, KYBER_SSBYTES, _ssj);

  for (size_t ii = 0; ii < KYBER_SECRETKEYBYTES; ii++) _sk[ii] = _skj[ii];
  for (size_t ii = 0; ii < KYBER_CIPHERTEXTBYTES; ii++) _ct[ii] = _ctj[ii];
  for (size_t ii = 0; ii < KYBER_SSBYTES; ii++) _ss[ii] = _ssj[ii];

  const uint8_t *pk = (_sk)+KYBER_INDCPA_SECRETKEYBYTES;


  indcpa_dec(buf, _ct, _sk);

  /* Multitarget countermeasure for coins + contributory KEM */
  for(i=0;i<KYBER_SYMBYTES;i++)
    buf[KYBER_SYMBYTES+i] = (_sk)[KYBER_SECRETKEYBYTES-2*KYBER_SYMBYTES+i];
  hash_g(kr, buf, 2*KYBER_SYMBYTES);

  /* coins are in kr+KYBER_SYMBYTES */
  indcpa_enc(cmp, buf, pk, kr+KYBER_SYMBYTES);

  fail = verify(_ct, cmp, KYBER_CIPHERTEXTBYTES);

  /* overwrite coins in kr with H(c) */
  hash_h(kr+KYBER_SYMBYTES, _ct, KYBER_CIPHERTEXTBYTES);

  /* Overwrite pre-k with z on re-encryption failure */
  cmov(kr, (_sk)+KYBER_SECRETKEYBYTES-KYBER_SYMBYTES, KYBER_SYMBYTES, fail);

  /* hash concatenation of pre-k and H(c) to k */
  kdf(_ss, kr, 2*KYBER_SYMBYTES);
  for (size_t ii = 0; ii < KYBER_SECRETKEYBYTES; ii++) _skj[ii] = _sk[ii];
  for (size_t ii = 0; ii < KYBER_CIPHERTEXTBYTES; ii++) _ctj[ii] = _ct[ii];
  for (size_t ii = 0; ii < KYBER_SSBYTES; ii++) _ssj[ii] = _ss[ii];
  (*env)->SetByteArrayRegion(env, sk, 0, KYBER_SECRETKEYBYTES, _skj);
  (*env)->SetByteArrayRegion(env, ct, 0, KYBER_CIPHERTEXTBYTES, _ctj);
  (*env)->SetByteArrayRegion(env, ss, 0, KYBER_SSBYTES, _ssj);
  return 0;
}

