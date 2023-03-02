import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class PoWMinerSimulation {
    private static final int BLOCK_SIZE = 108 * 1024 * 1024; // 1 MB per block
    private static final int DIFFICULTY = 4; // number of leading zeros required for valid block

    private static final long TARGET_TIME = 600_000; // 10 minutes per block

    public static void main(String[] args) throws InterruptedException {

        Thread.sleep(10000);

        long start = System.currentTimeMillis();

        byte[] block = new byte[BLOCK_SIZE];
        Random random = new Random();
        random.nextBytes(block);

        byte[] nonce = new byte[8];
        BigInteger target = BigInteger.valueOf(2).pow(256 - DIFFICULTY);

        boolean blockFound = false;
        long hashCount = 0;
        while (!blockFound) {
            random.nextBytes(nonce);
            byte[] data = concatenate(block, nonce);
            byte[] hash = hash(data);
            hashCount++;

            BigInteger hashInt = new BigInteger(1, hash);
            if (hashInt.compareTo(target) <= 0) {
                System.out.println("Block found! Nonce: " + new BigInteger(1, nonce));
                blockFound = true;
            }
        }

        long end = System.currentTimeMillis();
        long time = end - start;
        double hashRate = (double) hashCount / (double) time * 1000;
        System.out.println("PoW mining time: " + time + "ms");
        System.out.println("Hash rate: " + hashRate + " hashes/s");
    }

    private static byte[] concatenate(byte[] a, byte[] b) {
        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;
    }

    private static byte[] hash(byte[] data) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        return digest.digest(data);
    }
}
