package sglclient.groupsigniture;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.util.Random;

//import sglserver.groupsigniture.editEntityXml;


/**
 * グループ署名アルゴリズムJOINのクライアント側を実行するクラス
 * @author masato
 * @作成日: 2008/10/15
 * @最終更新日:2008/10/15
 */
public class JoinClient {
	
	editEntityXml  ed = new editEntityXml ();
	BigInteger rnd,rnd2,xi,xii,n2,r,c1,c2,hh,gg,test1,test2 ;
	BigInteger n,a,a0,y,g,h; 
	BigInteger rmd1 = new BigInteger(ed.getrmd1());
	BigInteger rmd2 = new BigInteger(ed.getrmd2());
	BigInteger TWO = new BigInteger("2");
	boolean b=false,b2 = false;
	boolean bool = true;
	int x;
	private Socket soc;
	private ObjectOutputStream oos;
	private ObjectInputStream ois;
	
	/**
	 * コンストラクタ
	 * @param soc  ソケット
	 * @param ois  インプットストリーム
	 * @param oos　　アウトプットストリーム
	 */
	public JoinClient (Socket soc,ObjectInputStream ois,ObjectOutputStream oos){
		this.soc = soc;
		this.oos = oos;
		this.ois = ois;
	}
	
	/**
	 * JOINを実行しグループメンバの証であるグループ会員書を得る
	 * @return　グループ会員書
	 * @throws Exception
	 * @author masato
	 */
	public BigInteger[][] Join() throws Exception{
		BigInteger[][] cer =new BigInteger[3][7];
		//グループ公開鍵受信		
		n  = (BigInteger) ois.readObject();
		a  = (BigInteger) ois.readObject();
		a0 = (BigInteger) ois.readObject();
		y  = (BigInteger) ois.readObject();
		g  = (BigInteger) ois.readObject();
		h  = (BigInteger) ois.readObject();
		//System.out.println("n="+n);
		//System.out.println("a="+a);
		//System.out.println("a0="+a0);
		//System.out.println("y="+y);
		//System.out.println("g="+g);
		//System.out.println("h="+h);
		cer[1][1] = n;
		cer[1][2] = a;
		cer[1][3] = a0;
		cer[1][4]  = y;
		cer[1][5]  = g;
		cer[1][6]  = h;
		
		while(bool){
			//x_i∈]0,2^λ_2[
			xi = new BigInteger(16, new Random());
			//r∈]0,n^2[
			n2 = n.multiply(n);
			while(!b){
				rnd2 = new BigInteger(2048, new Random());
				if(n2.compareTo(rnd2)==1){
					b=true;
					r=rnd2;
				}
			}
			//c1=g^x_i・h^r(mod n)
			gg = g.modPow(xi,n);
			hh = h.modPow(r,n);
			c1 = gg.multiply(hh).mod(n);
			//c1をGMに送信
			oos.writeObject(c1);
			//結果を受信
			String req = (String)ois.readObject();
			if(req.equals("OK")){
				//α_i,β_iを受信
				BigInteger alf = (BigInteger)ois.readObject();
				BigInteger bet = (BigInteger)ois.readObject();
				//x_ii=2^λ_1+(α_ix_i+β_i(mod 2^λ_2))
				int r1 = rmd1.intValue();
				int r2 = rmd2.intValue();
				BigInteger x = alf.multiply(xi).add(bet).mod(TWO.pow(r2));
				xii = TWO.pow(r1).add(x);
				//c2=a^x_ii(mod n)
				c2 = a.modPow(xii,n);
				//c2を送信
				oos.writeObject(c2);
				//結果を受信
				String req2 = (String)ois.readObject();
				if(req2.equals("OK")){
					//ei,Aiを受信
					BigInteger ei = (BigInteger)ois.readObject();
					BigInteger Ai = (BigInteger)ois.readObject();
					//ei,Aiを検証
					test1 = Ai.modPow(ei,n);
					test2 = a.modPow(xii,n).multiply(a0).mod(n);
					//System.out.println("test1="+test1);
					//System.out.println("test2="+test2);
					cer[2][1] = Ai;
					cer[2][2] = ei;
					cer[2][3] = xii;
					//System.out.println("ei="+ei);
					//System.out.println("Ai="+Ai);
					//System.out.println("xii="+xii);
					bool = false;
				}else{
					//System.out.println("失敗");
				}
			}else{
				//System.out.println("失敗");
			}
		}
		return cer;
	}
}
