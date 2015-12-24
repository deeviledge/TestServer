package sglclient.groupsigniture;

import java.math.BigInteger;
import java.util.Random;
import sglclient.myinformation.MyInformation;

/**
 * SIGNを実行するクラス
 * @author masato
 * @version 1.2
 * @作成日: 2008/11/07
 * @最終更新日:2008/11/10
 */
public class Sign {
	
	int husg,hush,husy,husa0,husa,husd1,husd2,husd3,husd4,husT1,husT2,husT3,husm;
	static String ste;
	static String stlp;
	static String stk;
	static String strmd1;
	static String strmd2;
	static String stgnm1;
	static String stgnm2;
	static int ingnm1;
	static int inrmd1;
	static BigInteger n;
	static BigInteger a;
	static BigInteger a0;
	static BigInteger y;
	static BigInteger g;
	static BigInteger h;
	static BigInteger Ai;
	static BigInteger ei;
	static BigInteger xi;
	static BigInteger m;
	BigInteger w,T1,T2,T3,r1,r2,r3,r4,d1,d2,d3,d4,tes1,tes2,tes3,tes4,gcd,c,s1,s2,s3,s4 ;
	BigInteger TWO = new BigInteger("2");
	boolean bool = true;
	
	/**
	 * コンストラクタ
	 * @param gname
	 * @param message
	 * @throws Exception
	 */
	public Sign(String gname,String message) throws Exception{
		//messageを数値化
		m = new BigInteger("12345");
		
		//MyInformation.xmlからグループ公開鍵、会員証明書を取り出す
		MyInformation mi = new MyInformation();
		//editEntity.xmlからエンティティを取り出す
		editEntityXml eex = new editEntityXml();
		ste = eex.gete();
		stlp = eex.getlp();
		stk = eex.getk();
		strmd1 = eex.getrmd1();
		strmd2 = eex.getrmd2();
		stgnm1 = eex.getgnm1();
		stgnm2 = eex.getgnm2();
		ingnm1 = Integer.valueOf(stgnm1).intValue();
		inrmd1 = Integer.valueOf(strmd1).intValue();	
		n = new BigInteger(mi.getn(gname));
		a = new BigInteger(mi.geta(gname));
		a0 = new BigInteger(mi.geta0(gname));
		y = new BigInteger(mi.gety(gname));
		g = new BigInteger(mi.getg(gname));
		h = new BigInteger(mi.geth(gname));
		Ai = new BigInteger(mi.getAi(gname));
		ei = new BigInteger(mi.getei(gname));
		xi = new BigInteger(mi.getxi(gname));
		System.out.println("n="+n);
		System.out.println("a="+a);
		System.out.println("a0="+a0);
		System.out.println("y="+y);
		System.out.println("g="+g);
		System.out.println("h="+h);
		System.out.println("Ai="+Ai);
		System.out.println("ei="+ei);
		System.out.println("xi="+xi);
	}
	
	/**
	 * SIGNを実行する
	 * @return　署名
	 * @throws Exception
	 */
	public BigInteger[] sign() throws Exception {
		BigInteger[] sign = new BigInteger[8];

		//w∈{0,1}^2lp
		Random rnd =new Random();
		w = new BigInteger(Integer.valueOf(stlp).intValue()*2,rnd);
		
		//System.out.println("w="+w);
		//T1=Ai・y^w(mod n),T2=g^w(mod n),T3=g^ei	・h^w
		T1 = y.modPow(w,n).multiply(Ai).mod(n);
		T2 = g.modPow(w,n);
		T3 = g.modPow(ei,n).multiply(h.modPow(w,n)).mod(n);	
		//r1∈±{0,1}^{ε(γ2+k)},r2∈±{0,1}^{ε(λ2+k)},r3∈±{0,1}^{ε(γ1+2lp+k+1)},r4∈±{0,1}^{ε(2lp+k)}
		while(bool){
			r1 = new BigInteger(
					(Integer.valueOf(stgnm2).intValue()+Integer.valueOf(stk).intValue())*Integer.valueOf(ste).intValue(),rnd);
			int r1L = r1.bitLength();
			if(r1L==(Integer.valueOf(stgnm2).intValue()+Integer.valueOf(stk).intValue())*Integer.valueOf(ste).intValue()){
				bool=false;
			}
			
		}bool=true;
		while(bool){
			r2 = new BigInteger(
					(Integer.valueOf(strmd2).intValue()+Integer.valueOf(stk).intValue())*Integer.valueOf(ste).intValue(),rnd);
			int r2L = r2.bitLength();
			if(r2L==(Integer.valueOf(strmd2).intValue()+Integer.valueOf(stk).intValue())*Integer.valueOf(ste).intValue()){
				bool=false;
			}
		}bool=true;
		while(bool){
			r3 = new BigInteger(
					(Integer.valueOf(stgnm1).intValue()+Integer.valueOf(stlp).intValue()*2+Integer.valueOf(stk).intValue()+1)*Integer.valueOf(ste).intValue(),rnd);
			int r3L = r3.bitLength();
			if(r3L==(Integer.valueOf(stgnm1).intValue()+Integer.valueOf(stlp).intValue()*2+Integer.valueOf(stk).intValue()+1)*Integer.valueOf(ste).intValue()){
				bool=false;
			}
		}bool=true;
		
		while(bool){	
			r4 = new BigInteger(
					(Integer.valueOf(stlp).intValue()*2+Integer.valueOf(stk).intValue())*Integer.valueOf(ste).intValue(),rnd);
			int r4L = r4.bitLength();
			if(r4L==(Integer.valueOf(stlp).intValue()*2+Integer.valueOf(stk).intValue())*Integer.valueOf(ste).intValue()){
				bool=false;
			}
		}
		//System.out.println("r1="+r1);
		//System.out.println("r2="+r2);
		//System.out.println("r3="+r3);
		//System.out.println("r4="+r4);	
		//d1=t1^ri/(a^r2・y^r3)(mod n),d2=T2^r1/g^r3(mod n),d3=g^r4(mod n),d4=g^r1・h^r4(mod n)
		d1 = modDivide(a.modPow(r2,n).multiply(y.modPow(r3,n)).mod(n),T1.modPow(r1,n),n);
		d2 = g.modPow(w.multiply(r1).subtract(r3),n);
		d3 = g.modPow(r4,n);
		d4 = g.modPow(r1,n).multiply(h.modPow(r4,n)).mod(n);	
		//System.out.println("d1="+d1);
		//System.out.println("d2="+d2);
		//System.out.println("d3="+d3);
		//System.out.println("d4="+d4);	
		//c=Hash(g||h||y||a||a0||T1||T2||T3||d1||d2||d3||d4||m)		
		String stg = String.valueOf(Math.abs(g.hashCode()));
		String sth = String.valueOf(Math.abs(h.hashCode()));
		String sty = String.valueOf(Math.abs(y.hashCode()));
		String sta = String.valueOf(Math.abs(a.hashCode()));
		String sta0 = String.valueOf(Math.abs(a0.hashCode()));
		String stT1 = String.valueOf(Math.abs(T1.hashCode()));
		String stT2 = String.valueOf(Math.abs(T2.hashCode()));
		String stT3 = String.valueOf(Math.abs(T3.hashCode()));
		String std1 = String.valueOf(Math.abs(d1.hashCode()));
		String std2 = String.valueOf(Math.abs(d2.hashCode()));
		String std3 = String.valueOf(Math.abs(d3.hashCode()));
		String std4 = String.valueOf(Math.abs(d4.hashCode()));
		String stm = String.valueOf(Math.abs(m.hashCode()));
		String stc = stg.concat(sth).concat(sty).concat(sta).concat(sta0).concat(stT1).concat(stT2)
		.concat(stT3).concat(std1).concat(std2).concat(std3).concat(std4).concat(stm);
		BigInteger c = new BigInteger(stc);
		int inc = Math.abs(c.hashCode());
		BigInteger husc = new BigInteger(String.valueOf(inc));
		//System.out.println("husc="+husc);
		//s1=r1-c(ei-2^γ1),s2=r2-c(xi-2^λ１),s3=r3-ceiw,s4=r4-cw	
		s1 = r1.subtract(husc.multiply(ei.subtract(TWO.pow(ingnm1))));
		s2 = r2.subtract(husc.multiply(xi.subtract(TWO.pow(inrmd1))));
		s3 = r3.subtract(husc.multiply(ei.multiply(w)));
		s4 = r4.subtract(husc.multiply(w));
		//System.out.println("s1="+s1);
		//System.out.println("s2="+s2);
		//System.out.println("s3="+s3);
		//System.out.println("s4="+s4);
		//署名を渡す
		sign[0]=husc;
		sign[1]=s1;
		sign[2]=s2;
		sign[3]=s3;
		sign[4]=s4;
		sign[5]=T1;
		sign[6]=T2;
		sign[7]=T3;		
		System.out.println("署名完了********************");
		return sign;
	}
	
	/**
	 * 一次合同式の計算
	 * ax=b(mod n)
	 * @param a
	 * @param b
	 * @param n
	 * @return　x
	 * @author masato
	 */
	public  BigInteger modDivide(BigInteger a,BigInteger b, BigInteger n){
		BigInteger x = null,d,b1,x1;
		BigInteger[] ab = new BigInteger[2];

		//step.1 d = gcd(a,n)を計算
		d = a.gcd(n);
		//System.out.println("d="+d);
		//System.out.println("b="+b);
		//System.out.println("a ="+b.remainder(d));
		
		//step.2 b がd で割り切れない場合は、解は存在しない
		//       b がd で割り切れる場合は、b1=b/dとおく
		if(b.remainder(d).compareTo(BigInteger.ZERO)==0){
			b1 = b.divide(d);
			ab = Euclid(a,n);
			x1 = ab[0];
			x = x1.multiply(b1).mod(n);
			//System.out.println("b1="+b1);
			//System.out.println("x1="+x1);		
		}else{
			System.out.println("解なし");
		}
		return x;
	}	
	
	/**
	 * 拡張ユークリッドの互除法
	 * gcd(m, n) = c であるとき、am + bn = c である a と b が存在する
	 * @param m
	 * @param n
	 * @param c
	 * @return　a,b
	 * @author masato
	 */
	public static BigInteger[] Euclid(BigInteger m,BigInteger n){
		BigInteger q,r,t;
		BigInteger[] ab = new BigInteger[2];
		boolean bool = true;	
		//step.1 初期値
		BigInteger b = BigInteger.ONE;
		BigInteger a2 = b;
		BigInteger b2 = BigInteger.ZERO;
		BigInteger a = b2;	
		while(bool){		
			//step.2 　商と余りを計算
			q = m.divide(n);
			r = m.remainder(n);		
			//step.3　r=0であれば終了 a*m + b*n = c. 
			if(r.compareTo(BigInteger.ZERO)==0){
				bool=false;
				ab[0] = a;
				ab[1] = b;
				//System.out.println("a="+a);
				//System.out.println("b="+b);
			}			
			//step.4 m=n, n=r, t=a2, a2=a, a=t-q*a, t=b2, b2=b, b=t-q*b. 
			m = n;
			n = r;
			t = a2;
			a2 = a;
			a = t.subtract(q.multiply(a));
			t = b2;
			b2 = b;
			b = t.subtract(q.multiply(b));		
			//step.5 2 に戻る
		}
		return ab;
	}	
}
