import java.math.BigInteger;
import java.util.ArrayList;


public class Test
{
    final static BigInteger ZERO = new BigInteger("0");
    final static BigInteger ONE = new BigInteger("1");
    final static BigInteger TWO = new BigInteger("2");
    final static BigInteger THREE = new BigInteger("3");
    static final BigInteger exp = new BigInteger(String.valueOf((int)Math.pow(2,16)));

    final static ArrayList<Integer> atkinSieveArray = atkinSieve();
    //Алгоритм нахождения всех простых чисел до заданного целого числа N
    static ArrayList<Integer> atkinSieve()
    {
        int lim = (int) Math.pow(2, 16);
        boolean isPrime[] = new boolean[lim + 1];
        int sqrtLim = (int) Math.sqrt(lim);

        for (int i = 0; i < lim; i++)
            isPrime[i] = false;

        for (int i = 1; i <= sqrtLim; i++)
        {
            for (int j = 1; j <= sqrtLim; j++) {
                //jSqr = jSqr + 2 * j - 1;
                int iSqr = i * i;
                int jSqr = j * j;
                int n = 4 * iSqr + jSqr;
                //уравнение первого случая -  4x² + y² = n
                if ((n <= lim) && ((n % 12 == 1) || (n % 12 == 5)))
                    isPrime[n] = !isPrime[n];
                //уравнение второго случая - 3x² + y² = n
                n = n - iSqr;
                if ((n <= lim) && (n % 12 == 7))
                    isPrime[n] = !isPrime[n];
                //уравнение третьего случая - 3x² − y² = n (при x > y)
                n = n - 2 * jSqr;
                if ((i > j) && (n <= lim) && (n % 12 == 11))
                    isPrime[n] = !isPrime[n];
            }
        }
        //убираем числа, кратные квадратам простых чисел в интервале [5, sqrtLim],
        //потому что в предыдущей проверке они не отсеялись
        for (int n = 5; n <= sqrtLim; n++)
        {
            if (isPrime[n])
            {
                int nSqr = n * n;
                for (int k = nSqr; k <= lim; k = k + nSqr)
                    isPrime[k] = false;
            }
        }
        isPrime[2] = true;
        isPrime[3] = true;
        isPrime[5] = true;
        ArrayList<Integer> list = new ArrayList<>();
        //добавим заведомо простые числа
        list.add(2);
        list.add(3);
        list.add(5);

        for(int i = 6; i <= lim; i++)
        {
            if (isPrime[i] && (i%3 != 0) && (i%5 != 0))
                list.add(i);
        }
        return list;
    }
    static BigInteger modulePow(BigInteger num, BigInteger power, BigInteger mod)
    {
        BigInteger res = ONE;
        if (power.compareTo(ZERO) > 0)
        {
            for (BigInteger i = num; power.compareTo(ZERO) > 0; power = power.shiftRight(1), i = i.multiply(i).mod(mod)) {
                if ((power.and(ONE)).compareTo(ZERO) != 0) {
                    res = (res.multiply(i).mod(mod));
                }
            }
        }
        if (power.compareTo(ZERO) < 0)
        {
            System.out.println("1 "+num);
            num = ONE.divide(num);
            System.out.println(""+ num);
            //power = power.abs();
            while (!power.equals(ZERO))
            {
                System.out.println(power.mod(TWO));
                if ((power.mod(TWO)).equals(ONE))
                    res = res.multiply(num).mod(mod);
                power = power.divide(TWO);
                num = num.multiply(num).mod(mod);
            }
        }
        return res;
    }
    static Boolean millerRabinTest(BigInteger num, int rounds)
    {
        if((TWO.compareTo(num) == 0) || (THREE.compareTo(num) == 0))
            return true;

        if ((TWO.compareTo(num) > 0) || (num.mod(TWO).compareTo(ZERO) == 0))
            return false;

        BigInteger testNum = num.subtract(ONE);

        int s = 0;

        while (testNum.mod(TWO).compareTo(ZERO) == 0)
        {
            testNum = testNum.divide(TWO);
            s = s + 1;
        }

        ArrayList<Integer> fromSieve = atkinSieveArray;

        for (int i = 0; i < rounds; i++)
        {
            int atkinNum = fromSieve.get(i);
            BigInteger modPowNum = modulePow(BigInteger.valueOf(atkinNum), testNum, num);

            if ((modPowNum.compareTo(ONE) == 0) || (modPowNum.compareTo(num.subtract(ONE)) == 0))
                continue;

            for (int r = 1; r < s; r++)
            {
                modPowNum = modulePow(modPowNum, TWO, num);
                // modPowNum = 1 - составное
                if (modPowNum.compareTo(ONE) == 0)
                    return false;
                // modPowNum = num - 1, то переходим к следующей итерации
                if (modPowNum.compareTo(num.subtract(ONE)) == 0)
                    break;
            }
            if (modPowNum.compareTo(num.subtract(ONE)) != 0)
                return false;
        }
        return true;
    }
    public static Boolean relativelyPrime(BigInteger a, BigInteger b)
    {
        return evklid(a, b).compareTo(ONE) == 0;
    }
    //обычный алгоритм евклида
    static BigInteger evklid(BigInteger a, BigInteger b)
    {
        while (b.compareTo(ZERO) != 0)
        {
            BigInteger modding = a.mod(b);
            a = b;
            b = modding;
        }
        return a;
    }
    //расширенный алгоритм евклида
    static BigInteger[] extendedEvklid(BigInteger a, BigInteger b)
    {
        BigInteger[] array = new BigInteger[3];
        if (a.compareTo(ZERO) == 0)
        {
            array[0] = ZERO;
            array[1] = ONE;
            array[2] = b;
            return array;
        }
        else if (b.compareTo(ZERO) == 0)
        {
            array[0] = ONE;
            array[1] = ZERO;
            array[2] = a;
            return array;
        }
        else
        {
            array = extendedEvklid(b.mod(a), a);
            BigInteger nil = array[0];
            array[0] = array[1].subtract((b.divide(a)).multiply(nil));
            array[1] = nil;
            return array;
        }
    }

    static BigInteger stringToNumber(String line){
        BigInteger res = BigInteger.ZERO;
        for(char c: line.toCharArray()){
            res = res.multiply(exp).add(BigInteger.valueOf(c));
        }
        return res;
    }


//    x ≡ r1 (mod m1),x ≡ r2 (mod m2), ..., x ≡ rn (mod mn)
    static String KTO(BigInteger number, BigInteger p, BigInteger q, BigInteger d)
    {
        BigInteger r1 = modulePow(number, d, p);
        BigInteger r2 = modulePow(number, d, q);
        BigInteger n1 = q; BigInteger n2 = p;
        BigInteger mod1 = extendedEvklid(n2, q)[1].mod(n2);
        BigInteger mod2 = extendedEvklid(n1, p)[1].mod(n1);
        BigInteger x = r1.multiply(n1).multiply(mod1).add(r2.multiply(n2).multiply(mod2));
        BigInteger m = x.mod(p.multiply(q));

        StringBuilder line = new StringBuilder();
        while (!m.equals(BigInteger.ZERO))
        {
            char c = (char) m.intValue();;
            line.append(c);
            m = m.divide(exp);
        }
        return line.reverse().toString();
    }
}