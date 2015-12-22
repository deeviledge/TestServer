package sglserver.groupsigniture;

import java.math.BigInteger;
import java.util.Random;

/**
 * グループ署名アルゴリズムSetUpを実行するクラス
 * @auhor masato
 * @version 1.0 
 * @作成日: 2008/5/12
 * @最終更新日:2008/10/31
 */
public class SetUp {
	
	/**
	 * コンストラクタ
	 */
	public SetUp(){
		System.err.println("計算中・・・");
	}
	
	/**
	 * アルゴリズムSETUP　
	 * @return BigInteger[][] グループ公開鍵とグループ秘密鍵
	 * @author masato
	 */
	public BigInteger[][] Setup() {
		BigInteger p = null,q=null,p1=null,q1=null;
		BigInteger n,n1,a,a0,g,h,x=null,y,rnd;
		boolean b=false,b1=false,b2=false;
		BigInteger[][] key = new BigInteger[3][7];
		//Step.1p=2p'+1,q=2q'+1(p,qは素数)であるような素数p',q'を選択、n=pqを計算
		while(!b1){
			p1 = new BigInteger(512, 10, new Random());
			p = p1.add(p1).add(BigInteger.ONE);
			b1 = p.isProbablePrime(10);
		}
		while(!b2){
			q1 = new BigInteger(512, 10, new Random());
			q = q1.add(q1).add(BigInteger.ONE);
			b2 = q.isProbablePrime(10);		
		}
		if(p.equals(q)){
			System.err.println("p=q");
			System.exit(0);
		}
		//n=pq
		n = p.multiply(q);
		a  = chooseQR(n);        //a∈QR(n)
		a0 = chooseQR(n);       //a0∈QR(n)
		g  = chooseQR(n);       	//a0∈QR(n)
		h  = chooseQR(n);        //a0∈QR(n)
		//n1=p1q1
		n1 = p1.multiply(q1);
		//x∈Z_p1q1
		while(!b){
			rnd = new BigInteger(1024, new Random()); 
			if(n1.compareTo(rnd)==1){
				x = rnd;
				b = true;
			}else{
			}
		}
		//y=g^x(mod n)
		y = g.modPow(x,n);
		
		key[1][1]=n;
		key[1][2]=a;
		key[1][3]=a0;
		key[1][4]=y;
		key[1][5]=g;
		key[1][6]=h;
		key[2][1]=p1;
		key[2][2]=q1;
		key[2][3]=x;	
		return key;	
	}
	
	/**
	 * アルゴリズムSETUP　
	 * @return BigInteger[][] グループ公開鍵とグループ秘密鍵
	 * @author masato
	 * @throws Exception 
	 */
	public void Setup2() throws Exception {
		BigInteger p = null,q=null,p1=null,q1=null;
		BigInteger n,n1,a,a0,g,h,x=null,y,rnd;
		boolean b=false,b1=false,b2=false;
		BigInteger[][] key = new BigInteger[3][7];
		//Step.1p=2p'+1,q=2q'+1(p,qは素数)であるような素数p',q'を選択、n=pqを計算
		while(!b1){
			p1 = new BigInteger(512, 10, new Random());
			p = p1.add(p1).add(BigInteger.ONE);
			b1 = p.isProbablePrime(10);
		}
		while(!b2){
			q1 = new BigInteger(512, 10, new Random());
			q = q1.add(q1).add(BigInteger.ONE);
			b2 = q.isProbablePrime(10);		
		}
		if(p.equals(q)){
			System.err.println("p=q");
			System.exit(0);
		}
		//n=pq
		n = p.multiply(q);
		a  = chooseQR(n);        //a∈QR(n)
		a0 = chooseQR(n);       //a0∈QR(n)
		g  = chooseQR(n);       	//a0∈QR(n)
		h  = chooseQR(n);        //a0∈QR(n)
		//n1=p1q1
		n1 = p1.multiply(q1);
		//x∈Z_p1q1
		while(!b){
			rnd = new BigInteger(1024, new Random()); 
			if(n1.compareTo(rnd)==1){
				x = rnd;
				b = true;
			}else{
			}
		}
		//y=g^x(mod n)
		y = g.modPow(x,n);
		
		key[1][1]=n;
		key[1][2]=a;
		key[1][3]=a0;
		key[1][4]=y;
		key[1][5]=g;
		key[1][6]=h;
		key[2][1]=p1;
		key[2][2]=q1;
		key[2][3]=x;	
		editKeydataXml ek = new editKeydataXml();
		ek.pKey(key[1][1],key[1][2],key[1][3],key[1][4],key[1][5],key[1][6]);
		ek.sKey(key[2][1],key[2][2],key[2][3]);
		ek.saveFile();
		
	}
	
	/**
	 * QRを選択
	 * @param n
	 * @return　a
	 * @author masato
	 */
	public BigInteger chooseQR(BigInteger n){
		BigInteger a = null,rnd,gcd;
		boolean b=false;	
		while(!b){
			rnd = new BigInteger(1024, new Random()); 
			if(n.compareTo(rnd)==1){      //rnd∈Zn
				a = rnd.multiply(rnd).mod(n);      //a≡rnd^2(mod n)
				gcd = n.gcd(a);                    //gcd(n,a)
				if(gcd.equals(BigInteger.ONE)){
					b = true;
				}else{
					//System.out.println("tryagain");
				}
			}else{
				//System.out.println("tryagain");
			}
		}
		return a;
	}	
}
