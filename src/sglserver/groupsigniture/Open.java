
package sglserver.groupsigniture;

import java.math.BigInteger;
import java.util.ArrayList;

import sglserver.groupinformation.ReadGroupInfoXML;

/**
 * グループ署名アルゴリズムOpenを実行するクラス
 * @author masato
 * @version 1.0
 * @作成日: 2008/11/13
 * @最終更新日:2008/11/13
 */
public class Open {
	
	static BigInteger n;
	static BigInteger c;
	static BigInteger s1;
	static BigInteger s2;
	static BigInteger s3;
	static BigInteger s4;
	static BigInteger T1;
	static BigInteger T2;
	static BigInteger T3;
	static BigInteger Ai2;
	static String gname;
	static BigInteger[] sign;
	static String message;
	
	/**
	 * コンストラクタ
	 * @param gname　グループ名
	 * @param sign	署名
	 * @param message　メッセージ
	 */
	public Open(String groupname,BigInteger[] sign1,String mess)  {	

		gname = groupname;
		sign = sign1;
		message = mess;
		
		//groupInformation.xmlからグループ鍵を取り出す
		ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
		//groupInformation.xmlからグループ鍵を取り出す
		n = new BigInteger(rgi.getn());
		
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
	 * アルゴリズムOPEN 
	 * @return　uanme 署名をしたユーザのユーザ名
	 * @author masato
	 */
	public String open() {
		boolean b = false;
		String res = null;
		String uname = null;
		String signer = null;
		ReadGroupInfoXML rgi = new ReadGroupInfoXML(gname);
		ArrayList member = rgi.getMemberName2(gname);
		int gvalue = member.size();
		System.out.println("グループメンバ数"+gvalue );
		//VERIFYを実行
		Verify ver = new Verify(gname,sign,message);
		res = ver.resverify();
		if(res.equals("OK")){
			for(int i=0;i<gvalue;i++){
				if(b==false){
					//グループメンバiのユーザ名取得
					uname = (String) member.get(i);
					//グループメンバiの証明書を取得
					BigInteger Ai = new BigInteger(rgi.getAi(gname,uname,0));
					BigInteger x = new BigInteger(rgi.getx());
					//Ai=T1/T2^x(mod n)
					Ai2 = modDivide(T2.modPow(x,n),T1,n);
					System.out.println("会員証明書のAi="+Ai);
					System.out.println("計算したAi="+Ai2);
					//比較
					if(Ai.equals(Ai2)){
						System.out.println("Aiの一致");
						System.out.println(uname+"の署名であることがわかりました");
						b = true;
						signer = uname;
					}
				}
			}
			if(b==false){
				System.out.println("署名したグループメンバが該当しません");
			}
		}else{	
			System.out.println(gname+"のグループメンバの署名ではありません");
		}
		return signer;
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
