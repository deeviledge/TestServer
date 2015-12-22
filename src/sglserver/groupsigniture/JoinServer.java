package sglserver.groupsigniture;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;

//import XML.editKeydataXml;

import sglserver.dynamicpeerinformation.DynamicPeerInformation;

/**
 * グループ署名アルゴリズムJOINのサーバ側を実行するクラス
 * @auhor masato
 * @version 1.0 
 * @作成日: 2008/5/12
 * @最終更新日:2008/11/17
 */
public class JoinServer {
	
	private Socket clientsoc;
	private ArrayList userlist;
	private String gname;
	private String gvalue;
	private String clientip;
	editEntityXml  ed = new editEntityXml ();
	String gnm1 = ed.getGNM1();
	String gnm2 = ed.getGNM2();
	static boolean k1 =true;		
	static boolean k2 =true;
	static boolean b =false;
	static boolean b2=true;
	static boolean bool =true;
	BigInteger GNM1 = new BigInteger(gnm1);
	BigInteger GNM2 = new BigInteger(gnm2);
	BigInteger TWO = new BigInteger("2");
	BigInteger p,q,l,d,alf,bet,ei,ei1,Ai,gcd,gcd2,lcm;
	private BigInteger n;
	private BigInteger a;
	private BigInteger a0;
	private BigInteger p1;
	private BigInteger q1;
	private BigInteger y;
	private BigInteger g;
	private BigInteger h;
	private BigInteger[][] cer ;
	
	/**
	 * コンストラクタ
	 * @param userlist	グループメンバリスト
	 * @param key		グループ公開鍵・秘密鍵
	 * @param gname 	グループ名
	 * @param gvalue	グループメンバ数
	 */
	public JoinServer (ArrayList userlist, BigInteger[][] key, String gname, String gvalue){
		this.userlist = userlist;
		this.gname = gname;
		this.gvalue = gvalue;
		n  = key[1][1];
		a  = key[1][2];
		a0 = key[1][3];
		y  = key[1][4];
		g  = key[1][5];
		h  = key[1][6];
		p1 = key[2][1];
		q1 = key[2][2];
		
	}
	
	/**
	 * アルゴリズムJOIN　
	 * @return　BigInteger[][] メンバー全員の会員証明書
	 * @throws UnknownHostException
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @author masato
	 */
	public BigInteger[][] Join() throws UnknownHostException, IOException, ClassNotFoundException{
		cer = new BigInteger[userlist.size()+1][3];
	
		for(int i=0; i<userlist.size(); i++){
			//グループメンバのIPを取得
			String username = (String) userlist.get(i);
			DynamicPeerInformation dpi = new DynamicPeerInformation();
			clientip = dpi.getIP(username);
			//System.out.println(dpi.getID(username));
			//System.out.println(clientip);
			//SGLクライアントに接続
			clientsoc = new Socket(clientip, 12346);
			// 入出力ストリームを取得する
			ObjectOutputStream oos = new ObjectOutputStream(clientsoc.getOutputStream());
			ObjectInputStream ois = new ObjectInputStream(clientsoc.getInputStream());			
			oos.writeObject("Join");
			oos.writeObject(gname);
			oos.writeObject(gvalue);
			oos.writeObject(userlist);
			//グループ公開鍵送信
			oos.writeObject(n);
			oos.writeObject(a);
			oos.writeObject(a0);
			oos.writeObject(y);
			oos.writeObject(g);
			oos.writeObject(h);
			//System.out.println("n="+n);
			//System.out.println("a="+a);
			//System.out.println("a0="+a0);
			//System.out.println("y="+y);
			//System.out.println("g="+g);
			//System.out.println("h="+h);
			while(bool){
				//c1を受信
				BigInteger c1 = (BigInteger)ois.readObject();
				//c1∈QR(n)をチェック
				String res = checkQR(c1,n);
				if(res.equals("OK")){
					oos.writeObject(res);
					//α_i,β_i∈]0,2^λ_2[
					alf = new BigInteger(16, new Random());
					bet = new BigInteger(16, new Random());
					//α_i,β_iを送信
					oos.writeObject(alf);
					oos.writeObject(bet);
					//c2を受信
					BigInteger c2 = (BigInteger)ois.readObject();
					//c2∈QR(n)をチェック
					String res2 = checkQR(c2,n);
					if(res2.equals("OK")){
						oos.writeObject(res2);
						p = p1.multiply(TWO).add(BigInteger.ONE);
						q = q1.multiply(TWO).add(BigInteger.ONE);
						l = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
						//eiを選択
						while(!b){
							ei1 = new BigInteger(1028, new Random());
							ei = GNM1.add(ei1);					 
							if(ei.compareTo(GNM1)==1){
								if(ei.compareTo(GNM2)==-1){
									if(ei.gcd(l).compareTo(BigInteger.ONE)==0){
										b = true;
									}
								}
							}
						}							
						b =false;		
						//Ai =(c_2a_0)^1/e_i(mod n);
						d = ei.modInverse(l);				
						Ai = c2.multiply(a0).mod(n).modPow(d,n);	
						oos.writeObject(ei);
						oos.writeObject(Ai);
						cer[i][1]= Ai;
						cer[i][2]= ei;
						//System.out.println("cer["+i+"][1]"+cer[i][1]);
						//System.out.println("cer["+i+"][2]"+cer[i][2]);
						bool = false;
					}else{
						oos.writeObject(res2);
						//System.out.println("失敗");
					} 
				}else{
					oos.writeObject(res);
					//System.out.println("失敗");
				}
			}
			bool = true;	
		}
		return cer;
		
	}
	
	/**
	 * QRをチェック
	 * @param n
	 * @return　a
	 * @author masato
	 */
	public String checkQR(BigInteger x,BigInteger n){
		String res = "NG";
		BigInteger gcd;	
		gcd = n.gcd(x);                    //gcd(n,a)
		if(gcd.equals(BigInteger.ONE)){
			//ヤコビ計算
			int pm = jacobi(x,n);
			if(pm==1){
				res = "OK";
			}else if(pm==-1||pm==0){
				res = "NG";
			}
			return res;
		}else{
			return res;
		}	
	}
	
	/**
	 * ヤコビ計算
	 * @param x
	 * @param n
	 * @return 0 or 1 or -1
	 * @author masato
	 */
	public int jacobi(BigInteger a,BigInteger n){
		BigInteger MONE = new BigInteger("-1");
		BigInteger ZERO = new BigInteger("0");
		BigInteger TWO = new BigInteger("2");
		BigInteger THREE = new BigInteger("3");
		BigInteger FOUR = new BigInteger("4");
		BigInteger EIGHT = new BigInteger("8");
		BigInteger swap;
		int pm= 1;
		boolean b1 = true ;
		try{
			//step1
			if(a.compareTo(ZERO)==-1){
				if(n.remainder(FOUR).intValue()==1){
					pm = pm*1;
				}else if(n.remainder(FOUR).intValue()==3){
					pm = pm*-1;
				}
				a = a.multiply(MONE); 
			}
			while(true){	
				//step2
				if(a.compareTo(ZERO)==0){
					pm = 0;
					return pm;
				}
				//step3
				while(b1){
					if(a.remainder(TWO).intValue()==0){
						if(n.remainder(EIGHT).intValue()==1||n.remainder(EIGHT).intValue()==7){
							pm = pm*1;
						}else if(n.remainder(EIGHT).intValue()==3||n.remainder(EIGHT).intValue()==5){
							pm = pm*-1;
						}
						a=a.divide(TWO);
					}else{
						b1 = false;
					}
				}			
				//step4
				b1 = true;
				if(a.compareTo(BigInteger.ONE)==0){
					return pm;
				}
				//step5
				if(a.compareTo(THREE)==1||a.compareTo(THREE)==0){
					if(a.remainder(FOUR).intValue()==3&&n.remainder(FOUR).intValue()==3){
						swap=a;
						a=n;
						n=swap;
						pm = pm*-1;
					}else{
						swap=a;
						a=n;
						n=swap;
					}
					a = a.remainder(n);
				}
			}	
		}catch(Exception e){		
		}
		return pm;
	}
}