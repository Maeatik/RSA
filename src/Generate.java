import java.io.File;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Generate
{
    final static BigInteger ZERO = new BigInteger("0");
    final static BigInteger ONE = new BigInteger("1");
    final static BigInteger TWO = new BigInteger("2");

    private static int lengthOfNumBit = 512;
    private static int lengthOfNumByte = 128;

    static File encryptedMessage = new File("encryptedMessage.txt");
    static File decryptedMessage = new File("decryptedMessage.txt");

    BigInteger q, p, n, phi, d = null;
    BigInteger e = ONE;
    int roundP, roundQ;
    Boolean isSimple;

    Random rnd = new Random();
    Test test = new Test();
    FileWork fileWork = new FileWork();

    Key key;

    public Generate()
    {
        this.key = generationKeys(512);
    }

    public Key generationKeys(int newLengthOfNumBit)
    {
        int tmp;
            if (newLengthOfNumBit < lengthOfNumBit)
            {
                 tmp = lengthOfNumBit / newLengthOfNumBit;
            }else{
                 tmp = newLengthOfNumBit/lengthOfNumBit;
            }
            lengthOfNumBit = newLengthOfNumBit;
            lengthOfNumByte = lengthOfNumByte/tmp;
            if ((lengthOfNumByte == 0 )||(lengthOfNumByte == 1))
                lengthOfNumByte = 2;
        do {
            p = new BigInteger(lengthOfNumBit, rnd);
            roundP = p.bitLength() - 1;
            isSimple = test.millerRabinTest(p, roundP);
        } while (!isSimple);

        do {
            q = new BigInteger(lengthOfNumBit, rnd);
            roundQ = q.bitLength() - 1;
            isSimple = test.millerRabinTest(q, roundQ);
        } while (!isSimple);

        n = p.multiply(q);
        //теорема Эйлера a^phi ≡ 1 (mod m)
        phi = p.subtract(ONE).multiply(q.subtract(ONE));

        do {
            if (phi.compareTo(TWO) > 0)
            {
                do {
                    e = e.add(ONE);
                } while (test.millerRabinTest(e, e.bitLength()-1));
            }
            else break;
        } while (!test.relativelyPrime(e, phi));//e и фи - относительно простые
        d = test.extendedEvklid(e, phi)[0];//вычисляется по расширенному алгоритму евклида
        if (d.compareTo(ZERO) < 0)
        {
            d = d.add(phi);
        }
        System.out.println("Ключи созданы");
        return new Key(e, p, q, n, d, phi);
    }

    public String encryptionText(String message, Key key)
    {
        e = key.E;
        n = key.N;

        BigInteger numMessage = test.stringToNumber(message);
        int byteLength = numMessage.toByteArray().length;

        if (byteLength < lengthOfNumByte)
        {
            BigInteger toPow = test.modulePow(numMessage, e, n);
            fileWork.writeToFile(encryptedMessage, String.valueOf(toPow));
            System.out.println("Результат в файле encryptedMessage.txt");
            return String.valueOf(toPow);
        }
        else
        {
            int block = 2;
            String[] messageArray;

            do {
                int blocks = message.length() / block;
                messageArray = message.split("(?<=\\G.{" + blocks + "})");
                block = block + 1;
                numMessage = test.stringToNumber(messageArray[0]);
            }while (!(numMessage.toByteArray().length < lengthOfNumByte));

            BigInteger[] toEncryptMessageArray = new BigInteger[messageArray.length];

            for (int i = 0; i < toEncryptMessageArray.length; i++)
                toEncryptMessageArray[i] = test.stringToNumber(messageArray[i]);

            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < toEncryptMessageArray.length; i++)
            stringBuilder.append(test.modulePow(toEncryptMessageArray[i], e, n)).append(" ");

            fileWork.writeToFile(encryptedMessage, stringBuilder.toString());
            System.out.println("Результат в файле encryptedMessage.txt");
            return stringBuilder.toString();
        }
    }

    public String decryptionText(String message, Key key)
    {
        BigInteger encMessage;
        d = key.D;
        p = key.P;
        q = key.Q;
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(message);

        StringBuilder stringBuilder = new StringBuilder();

        if (!matcher.find())
        {
            encMessage = new BigInteger(message);
            String str = test.KTO(encMessage, p, q, d);
            fileWork.writeToFile(decryptedMessage, str);

            return str;
        }
        else
        {
            String[] messageArray = message.split(" ");
            for(int i = 0; i < messageArray.length; i++)
            {
                encMessage = new BigInteger(messageArray[i].trim());
                String str = test.KTO(encMessage, p, q, d);
                stringBuilder.append(str);
            }
            System.out.println(stringBuilder.toString());
            fileWork.writeToFile(decryptedMessage, stringBuilder.toString());

            return  stringBuilder.toString();
        }
    }
}
