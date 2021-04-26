package network.beechat;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import es.vocali.util.AESCrypt;

public class Utility {



    public static void main(String[] args) throws IOException, GeneralSecurityException, InterruptedException {
        //Bob generates a random number and encrypts it with Alice's public key. Alice decrypts it with her private key.

        System.out.println("+---------------------------------------------+");
        System.out.println("|    kyber Post-Quantum Encryption utility    |");
        System.out.println("+---------------------------------------------+\n");

        System.out.println("Working directory: " + System.getProperty("user.dir"));
        //String property = System.getProperty("java.library.path");

        while (true) {
            System.out.println("\nEnter command: (type in 'help' to look up the available commands)");
            Scanner commandscanner = new Scanner(System.in);
            if (commandscanner.hasNext()) {
                String command = commandscanner.next();
                docommands(command);
                //commandscanner.close();
            } else {
                return;
            }
        }

        //;
    }

    private static void docommands(String command) throws GeneralSecurityException, IOException {
        Base58 b58 = new Base58();
        byte[] alice_skey = new byte[Kyber512.KYBER_SSBYTES];
        byte[] bob_skey = new byte[Kyber512.KYBER_SSBYTES];

        command = command.toLowerCase();
        String keyslocation = "";
        if (command.equals("createkeys")) {
            byte[] alice_pk = new byte[Kyber512.KYBER_PUBLICKEYBYTES];
            byte[] alice_sk = new byte[Kyber512.KYBER_SECRETKEYBYTES];

            System.out.println("\nEnter name of keys to be created: ");
            Scanner commandscanner = new Scanner(System.in);
            if (commandscanner.hasNext()) {
                keyslocation = commandscanner.next();}

            // Step 1:
            int rc = Kyber512.crypto_kem_keypair(alice_pk, alice_sk);
            String testOutput = new String(b58.encode(alice_sk));
            System.out.println("\nSecret key:\n" + testOutput);
            try (FileWriter writer = new FileWriter(keyslocation+".secret", false)) {
                writer.write(testOutput);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
            }
            testOutput = new String(b58.encode(alice_pk));
            System.out.println("Public key:\n" + testOutput);
            try (FileWriter writer = new FileWriter(keyslocation+".public", false)) {
                writer.write(testOutput);
                writer.flush();
                writer.close();
            } catch (IOException ex) {
            }

            //System.exit(rc);
            return;
        }
        else if (command.equals("encryptfile")) {
            byte[] ct = new byte[Kyber512.KYBER_CIPHERTEXTBYTES];
            String publickey = "";
            String filetoenc = "";
            String fileoutput = "";
            String zip = "";

            System.out.println("\nSpecify public key file name: ");
            Scanner commandscanner = new Scanner(System.in);
            if (commandscanner.hasNext()) {
                publickey = commandscanner.next();}
            System.out.println("\nSpecify file to encrypt: ");
            if (commandscanner.hasNext()) {
                filetoenc = commandscanner.next();}
            System.out.println("\nSpecify output name of encrypted file: ");
            if (commandscanner.hasNext()) {
                fileoutput = commandscanner.next();}
            System.out.println("\nWould you like to add the encrypted password and file in one Zip file? " +
                    "\n(type yes or no): ");
            if (commandscanner.hasNext()) {
                zip = commandscanner.next();}


            // Step 2: Load ciphertext key into ct, use alice.public to encapsulate, save key into bob_skey
            Scanner sc = new Scanner(new File(publickey));
            Kyber512.crypto_kem_enc(ct, bob_skey, b58.decode(sc.nextLine()));
            System.out.println("\nSKEY:" + b58.encode(bob_skey));
            //Encrypt file with skey
            //Create AESCrypt file
            AESCrypt encrypter = new AESCrypt(b58.encode(bob_skey));
            //Converting file to aes with shared sha3-256 secret
            encrypter.encrypt(2, filetoenc, fileoutput);

            //write ct (ciphertext) to file. This would be sent to Alice.
            try (FileOutputStream fos = new FileOutputStream("ct.key")) {
                fos.write(ct);
            }
            System.out.println(b58.encode(ct));

            System.out.println("\nFile successfully encrypted. ct.key & "+fileoutput+" created.");

            if (zip.equals("yes")){
                List<String> srcFiles = Arrays.asList("ct.key", fileoutput);
                FileOutputStream fos = new FileOutputStream("multiCompressed.zip");
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                for (String srcFile : srcFiles) {
                    File fileToZip = new File(srcFile);
                    FileInputStream fis = new FileInputStream(fileToZip);
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    fis.close();
                }
                zipOut.close();
                fos.close();
                System.out.println("\nFiles added to multiCompressed.zip.");
            }


            return;
        }
        else if (command.equals("decryptfile")) {
            byte[] ct_loaded;
            String filetodec = "";
            String privatekey = "";
            String fileoutput = "";

            Scanner commandscanner = new Scanner(System.in);

            System.out.println("\nSpecify your private key: ");
            if (commandscanner.hasNext()) {
                privatekey = commandscanner.next();}
            System.out.println("\nSpecify file to decrypt: ");
            if (commandscanner.hasNext()) {
                filetodec = commandscanner.next();}
            System.out.println("\nSpecify filename output: ");
            if (commandscanner.hasNext()) {
                fileoutput = commandscanner.next();}


            ct_loaded = Files.readAllBytes(Paths.get("ct.key"));
            System.out.println(b58.encode(ct_loaded));

            //Here is the issue:
            Scanner sc = new Scanner(new File(privatekey));

            // Step 3: Alice uses the loaded ciphertext and her secret key to decapsulate the password into alice_skey
            Kyber512.crypto_kem_dec(alice_skey, ct_loaded, b58.decode(sc.nextLine()));
            System.out.println("\nSKEY:" + b58.encode(alice_skey));

            //DECRYPT AESCrypt file
            AESCrypt decrypter = null;
            try {
                decrypter = new AESCrypt(b58.encode(alice_skey));
            } catch (GeneralSecurityException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            try {
                decrypter.decrypt(
                        filetodec,
                        fileoutput);
            } catch (IOException | GeneralSecurityException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return;
        }
        else if (command.equals("help")){
            System.out.println("Commands available:\n" +
                    "createkeys: creates .secret and .public keys. For example: createkeys followed by 'test' will " +
                    "output 'test.public' and 'test.secret'.\n" +
                    "encryptfile: encrypt a file with a public key. For example: encryptfile test.txt will output" +
                    " an encrypted file and the encrypted password (ct.key). Both must be sent to the recipient.\n" +
                    "decryptfile: decrypt a file with a secret key. For example, test.encrypted, ct.key and your " +
                    "private key are used to decrypt the file.");
        }
        else if (command.equals("exit")) {System.exit(0);}

    }
}

