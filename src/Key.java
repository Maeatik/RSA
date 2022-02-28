import java.math.BigInteger;

public class Key
{
    public final BigInteger E;
    public final BigInteger P;
    public final BigInteger Q;
    public final BigInteger N;
    public final BigInteger D;
    public final BigInteger PHI;


    public Key(BigInteger E, BigInteger P, BigInteger Q, BigInteger N, BigInteger D, BigInteger PHI) {
        this.P = P;
        this.Q = Q;
        this.N = N;
        this.D = D;
        this.PHI = PHI;
        this.E = E;
    }
}
