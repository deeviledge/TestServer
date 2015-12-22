package sglserver.groupsigniture;

import java.math.BigInteger;
import sglserver.groupinformation.ReadGroupInfoXML;


/**
 * VERIFYを実行するクラス
 * @author masato
 * @version 1.1
 * @作成日: 2008/11/10
 * @最終更新日:2008/11/10
 */
public class Verify {

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
	static BigInteger m;
	BigInteger d1,d2,d3,d4,tes1,tes2,tes3,tes4,gcd;
	//BigInteger m = new BigInteger("123456789");
	BigInteger TWO = new BigInteger("2");
	static BigInteger c;
	static BigInteger s1;
	static BigInteger s2;
	static BigInteger s3;
	static BigInteger s4;
	static BigInteger T1;
	static BigInteger T2;
	static BigInteger T3;
	
	/**
	 * コンストラクタ
	 * @param gname　グループ名
	 * @param sign　署名
	 * @param message　メッセージ
	 */
	public Verify(String gname,BigInteger[] sign,String message){
		
		//messageを数値化
		m = new BigInteger("12345");
		
		//editEntity.xmlからエンティティを取り出す
		editEntityXml eex = new editEntityXml();
		
		//groupInformation.xmlからグループ鍵を取り出す
		ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
		
		ste = eex.gete();
		stlp = eex.getlp();
		stk = eex.getk();
		strmd1 = eex.getrmd1();
		strmd2 = eex.getrmd2();
		stgnm1 = eex.getgnm1();
		stgnm2 = eex.getgnm2();
		ingnm1 = Integer.valueOf(stgnm1).intValue();
		inrmd1 = Integer.valueOf(strmd1).intValue();	
		n = new BigInteger(rgi.getn());	
		a = new BigInteger(rgi.geta());
		a0 = new BigInteger(rgi.geta0());
		y = new BigInteger(rgi.gety());
		g = new BigInteger(rgi.getg());
		h = new BigInteger(rgi.geth());	
		System.out.println("n="+n);
		System.out.println("a="+a);
		System.out.println("a0="+a0);
		System.out.println("y="+y);
		System.out.println("g="+g);
		System.out.println("h="+h);
		//署名を取り出す
		c = sign[0];
		s1 = sign[1];
		s2 = sign[2];
		s3 = sign[3];
		s4 = sign[4];
		T1 = sign[5];
		T2 = sign[6];
		T3 = sign[7];	
	}
	
	/**
	 * アルゴリズムVERIFY　
	 * @return 検証結果
	 * @author masato
	 */
	public String resverify ()  {
		//d1'=a0^c・T1^{s1-c・2^γ１}/a^{s2-c・2^λ1}y^s3(mod n)
		//d2'=T2^{s1-c・2^γ1}/g^s3(mod n)
		//d3'=T2^c・g^s4(mod n)
		//d4'=T3^c・g^{s1-c・2^γ1}・h^s4(mod n)
		d1 = modDivide(a.modPow(s2.subtract(c.multiply(TWO.pow(inrmd1))),n).multiply(y.modPow(s3,n)).mod(n)
				,a0.modPow(c,n).multiply(T1.modPow(s1.subtract(c.multiply(TWO.pow(ingnm1))),n)).mod(n),n);
		d2 = modDivide(g.modPow(s3,n),T2.modPow(s1.subtract(c.multiply(TWO.pow(ingnm1))),n),n);
		d3 = T2.modPow(c,n).multiply(g.modPow(s4,n)).mod(n);
		d4 = T3.modPow(c,n).multiply(g.modPow(s1.subtract(c.multiply(TWO.pow(ingnm1))),n)).multiply(h.modPow(s4,n)).mod(n);	
		//c'=Hash(g||h||y||a||a0||T1||T2||T3||d1'||d2'||d3'||d4'||m)			
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
		String stc1 = stg.concat(sth).concat(sty).concat(sta).concat(sta0).concat(stT1).concat(stT2)
		.concat(stT3).concat(std1).concat(std2).concat(std3).concat(std4).concat(stm);
		BigInteger c1 = new BigInteger(stc1);
		int inc1 = Math.abs(c1.hashCode());
		BigInteger husc1 = new BigInteger(String.valueOf(inc1));
		//System.out.println("husc="+husc1);
		
		//c=?c'
		if(c.compareTo(husc1)==0){
			//System.out.println("first step ok");
			int s1L = (Integer.valueOf(stgnm2).intValue()+Integer.valueOf(stk).intValue())*Integer.valueOf(ste).intValue();
			int s2L = (Integer.valueOf(strmd2).intValue()+Integer.valueOf(stk).intValue())*Integer.valueOf(ste).intValue();
			int s3L = (Integer.valueOf(stgnm1).intValue()+Integer.valueOf(stlp).intValue()*2+Integer.valueOf(stk).intValue()+1)*Integer.valueOf(ste).intValue();
			int s4L	= (Integer.valueOf(stlp).intValue()*2+Integer.valueOf(stk).intValue())*Integer.valueOf(ste).intValue();
			if(s1L==s1.bitLength()&&s2L==s2.bitLength()&&s3L==s3.bitLength()&&s4L==s4.bitLength()){
				//System.out.println("second step ok");
				//System.out.println("verify");
				return "OK";
			}else{
				System.out.println("NG1");
				return "NG";
			}
		}else{
			System.out.println("NG2");
			return "NG";
		}	
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
