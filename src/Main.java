import java.math.BigInteger;
import java.util.Scanner;

public class Main
{
    public static void main(String[] args)
    {
//       Generate generate = new Generate();
//       generate.generationKeys();
//       generate.encryptionText();
//       generate.decryptionText();

        Test test = new Test();
        System.out.println(test.extendedEvklid(BigInteger.valueOf(180), BigInteger.valueOf(150))[2]);
    }
}
