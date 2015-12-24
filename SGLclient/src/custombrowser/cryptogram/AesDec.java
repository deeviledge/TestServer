/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package custombrowser.cryptogram;

import custombrowser.CustomBrowserController;
import custombrowser.CustomBrowserTabController;

public class AesDec
{
	Base64 base64;   //Base64クラスのオブジェクトを宣言
	public byte[] keys;     //読み込んだグループ鍵を保存する変数
	final static int NB = 4;   //256bit 固定として規格されている(データの長さ)
	final static int NBb = 16; 
	static byte w[];       
	static int nk;           //4,6,8(128,192,256 bit) 鍵の長さ
	static int nr;           //10,12,14 ラウンド数
	static byte key[];		 //読み込んだグループ鍵のコピーを保存する変数
	public static String input;     //入力された値を保存するための変数
	static byte[] cipher;	 //暗号文を保存すための変数
	public static byte[] plain;     //平文を保存するための変数
	int num;				 //文の長さを保存する変数
	byte[] base64_dec;       //英数字の暗号文をデコードした値を保存する変数

	public AesDec(CustomBrowserController dec){
		//System.out.println("Aes_decコンストラクタ");
	}

	public AesDec(CustomBrowserTabController customBrowserTabController) {
		// TODO 自動生成されたコンストラクター・スタブ
	}

	//mainメソッド
	public void Aes_dec()
	{

		nk = 8;               //鍵の長さ 4,6,8(128,192,256 bit)
		nr = nk + 6;          //ラウンド数 10,12,14
		nk = nk * 4;
		key = new byte[nk];   //keyの宣言

		System.arraycopy(keys,0,key,0,nk); //keyにkeysをコピー 
		KeyExpansion(key);    //暗号化するための鍵の準備

		int i,j;

		//取得した暗号文をbase64でデコードする
		base64_dec = Base64.decode(input);
		num = base64_dec.length;

		//平文の長さを16倍の数にするための作業
		if(base64_dec.length%16 == 0){
			num = base64_dec.length;  
		}else{
			num = ((base64_dec.length/16)+1)*16; 
		}

		cipher = new byte[num];

		//cipherにbase64_decをコピー
		for(i=0; i<base64_dec.length; i++){
			cipher[i] = base64_dec[i];
		}

		//パディングするための0を代入
		if(base64_dec.length%16 != 0){
			for(i=base64_dec.length+1;i<num;i++){
				cipher[i] = (byte)0x00;    //0を代入
			}
		}

		byte[][] input_ary = new byte[NBb][num/NBb];  //16バイトごとに暗号文を分割するときに使う配列
		byte[][] plain_ary = new byte[NBb][num/NBb];  //分割したデータをまとめるときに使う配列
		byte[] data_ary = new byte[16];  //16バイトごとに復号する文を代入する配列


		//input_aryに暗号文を代入
		for(i=0;i<num;i++){
			//input_aryに暗号文を16バイトごとに分割して代入
			input_ary[i%16][(int)Math.floor(i/16)] = cipher[i]; 
		}

		//cipherを復号化する
		for(i=0; i<num/16; i++){
			for(j=0; j<16; j++){
				data_ary[j] = input_ary[j][i];  //暗号文を16バイトずつ代入
			}
			invCipher(data_ary); //復号処理
			for(j=0; j<16; j++){
				plain_ary[j][i] = data_ary[j];  //16バイトごとに復号した文を2次元配列に保存
			}		
		}

		byte[] plain_pad = new byte[num]; //復号した文を一つにまとめるときに使用する配列

		//復号した文を一つにまとめる
		for(i=0;i<num;i++){
			plain_pad[i] = plain_ary[i%16][i/16]; 
		}

		//パディングの際に付けた'0'をとる
		int cnt = 0;
		while(plain_pad[cnt] != 0){
			cnt++;
		}

		plain = new byte[cnt];
		for(i=0; i<cnt; i++){
			plain[i] = plain_pad[i];
			//System.out.print(plain[i]+".");
		}

		//System.out.println();
		//System.out.println("----平文----");

		//平文を表示
		//System.out.println(new String(plain));

	}	

	/************************************************************************/
	/**   復号に必要な関数
	/************************************************************/

	//復号処理
	static int invCipher(byte[] data)
	{
		int i;

		AddRoundKey(data,(nr));

		for(i=nr-1;i>0;i--)
		{
			invShiftRows(data);
			invSubBytes(data);
			AddRoundKey(data,i);
			invMixColumns(data,i);
		}

		invShiftRows(data);
		invSubBytes(data);
		AddRoundKey(data,0);
		return(nr);
	}

	/************************************************************/

	//換字処理
	static void invSubBytes(byte[] data)
	{
		int i,j;
		for(i=0;i<NBb;i+=4)//�ｽ�ｽ�ｽ_�ｽI�ｽﾈ意厄ｿｽ�ｽ�ｽ�ｽ�ｽ�ｽd�ｽ�ｽ�ｽ[�ｽv�ｽﾉゑｿｽ�ｽﾄゑｿｽ�ｽ驍ｪ�ｽﾓ厄ｿｽ�ｽﾍ厄ｿｽ�ｽ�ｽ
		{
			for(j=0;j<4;j++)
			{
				data[i+j] = invSbox[(data[i+j] & 0xff)];
			}
		}
	}

	/************************************************************/

	//転置処理（横シフト）
	static void invShiftRows(byte[] data)
	{
		int i,j;
		byte[] cw = new byte[16];// byte[] cw = data;
		System.arraycopy(data,0,cw,0,NBb);

		for(i=0;i<NBb;i+=4*4)
		{
			for(j=1;j<4;j++)
			{
				cw[i+j+((j+0)&3)*4] = data[i+j+0*4];
				cw[i+j+((j+1)&3)*4] = data[i+j+1*4];
				cw[i+j+((j+2)&3)*4] = data[i+j+2*4];
				cw[i+j+((j+3)&3)*4] = data[i+j+3*4];
			}
		}
		System.arraycopy(cw,0,data,0,NBb);
	}

	/************************************************************/

	//乗算
	static int mul(int dt,int n)
	{
		int i,x=0;
		for(i=8;i>0;i>>=1){
			x <<= 1;
			if((x&0x100) != 0)
				x = (x ^ 0x1b) & 0xff;
			if(((n & i)) != 0)
				x ^= dt;
		}
		return(x);
	}

	/************************************************************/

	//転置処理（縦ミックス）
	static void invMixColumns(byte[] data,int n)
	{
		int i;
		byte[] x = new byte[16];

		for(i=0;i<NBb;i+=4)
		{
			x[i+0] =(byte)(mul(data[i+0],14) ^
					mul(data[i+1],11) ^
					mul(data[i+2],13) ^
					mul(data[i+3], 9));
			x[i+1] =(byte)(mul(data[i+1],14) ^
					mul(data[i+2],11) ^
					mul(data[i+3],13) ^
					mul(data[i+0], 9));
			x[i+2] =(byte)(mul(data[i+2],14) ^
					mul(data[i+3],11) ^
					mul(data[i+0],13) ^
					mul(data[i+1], 9));
			x[i+3] =(byte)(mul(data[i+3],14) ^
					mul(data[i+0],11) ^
					mul(data[i+1],13) ^
					mul(data[i+2], 9));
		} 	
		System.arraycopy(x,0,data,0,NBb);
	}

	/************************************************************/

	//排他的論理和
	static void Exor(byte[] data,int pos1,byte[] w,int pos2)
	{
		data[pos1+0] = (byte) (data[pos1+0] ^ w[pos2+0]);
		data[pos1+1] = (byte) (data[pos1+1] ^ w[pos2+1]);
		data[pos1+2] = (byte) (data[pos1+2] ^ w[pos2+2]);
		data[pos1+3] = (byte) (data[pos1+3] ^ w[pos2+3]);
		return;
	}

	/************************************************************/

	//拡張鍵の加算
	static void AddRoundKey(byte[] data,int n)
	{
		int i;
		for(i=0;i<NBb;i+=4)
		{
			Exor(data,i,w,i+NBb*n);// data[i] ^= w[i+NB*n];
		}
	}

	/************************************************************/

	//4バイト単位にSBoxを適用
	static byte[] SubWord(byte[] in)
	{
		in[0] = Sbox[(in[0] & 0xff)];
		in[1] = Sbox[(in[1] & 0xff)];
		in[2] = Sbox[(in[2] & 0xff)];
		in[3] = Sbox[(in[3] & 0xff)];
		return(in);
	}

	/************************************************************/

	//巡回置換
	static byte[] RotWord(byte[] in)
	{
		byte x = in[0];
		in[0] = in[1];
		in[1] = in[2];
		in[2] = in[3];
		in[3] = x;
		return(in);
	}

	/************************************************************/

	//排他的論理和
	static void Exor(byte[] w,int pos1,int pos2,byte[] temp)
	{
		w[pos1+0] = (byte) (w[pos2+0] ^ temp[0]);
		w[pos1+1] = (byte) (w[pos2+1] ^ temp[1]);
		w[pos1+2] = (byte) (w[pos2+2] ^ temp[2]);
		w[pos1+3] = (byte) (w[pos2+3] ^ temp[3]);
		return;
	}

	/************************************************************/

	//鍵の拡張
	static void KeyExpansion(byte[] key)
	{
		byte Rcon[]={0x01,0x02,0x04,0x08,0x10,0x20,0x40,(byte)0x80,0x1b,0x36};
		byte[] temp = new byte[4];
		int i;

		w = new byte[NBb*(nr+1)];

		for(i=0; i<nk; i++){
			w[i] = key[i];
		}
		for(i=nk;i<NBb*(nr+1);i+=4)
		{

			temp[0] = w[i-4];
			temp[1] = w[i-3];
			temp[2] = w[i-2];
			temp[3] = w[i-1];

			if((i%nk) == 0)
			{
				temp = SubWord(RotWord(temp));
				temp[0] ^= Rcon[(i/nk)-1];
			}
			else if(nk > 6*4 && (i%nk) == 4*4)
				temp = SubWord(temp);
			Exor(w,i,i-nk,temp);// w[i] = w[i-nk] ^ temp;
		}
	}

	/************************************************************/

	//変換テーブル（S-Box）
	static byte Sbox[]={
		(byte)0x63,(byte)0x7c,(byte)0x77,(byte)0x7b,(byte)0xf2,(byte)0x6b,(byte)0x6f,(byte)0xc5,
		(byte)0x30,(byte)0x01,(byte)0x67,(byte)0x2b,(byte)0xfe,(byte)0xd7,(byte)0xab,(byte)0x76,
		(byte)0xca,(byte)0x82,(byte)0xc9,(byte)0x7d,(byte)0xfa,(byte)0x59,(byte)0x47,(byte)0xf0,
		(byte)0xad,(byte)0xd4,(byte)0xa2,(byte)0xaf,(byte)0x9c,(byte)0xa4,(byte)0x72,(byte)0xc0,
		(byte)0xb7,(byte)0xfd,(byte)0x93,(byte)0x26,(byte)0x36,(byte)0x3f,(byte)0xf7,(byte)0xcc,
		(byte)0x34,(byte)0xa5,(byte)0xe5,(byte)0xf1,(byte)0x71,(byte)0xd8,(byte)0x31,(byte)0x15,
		(byte)0x04,(byte)0xc7,(byte)0x23,(byte)0xc3,(byte)0x18,(byte)0x96,(byte)0x05,(byte)0x9a,
		(byte)0x07,(byte)0x12,(byte)0x80,(byte)0xe2,(byte)0xeb,(byte)0x27,(byte)0xb2,(byte)0x75,
		(byte)0x09,(byte)0x83,(byte)0x2c,(byte)0x1a,(byte)0x1b,(byte)0x6e,(byte)0x5a,(byte)0xa0,
		(byte)0x52,(byte)0x3b,(byte)0xd6,(byte)0xb3,(byte)0x29,(byte)0xe3,(byte)0x2f,(byte)0x84,
		(byte)0x53,(byte)0xd1,(byte)0x00,(byte)0xed,(byte)0x20,(byte)0xfc,(byte)0xb1,(byte)0x5b,
		(byte)0x6a,(byte)0xcb,(byte)0xbe,(byte)0x39,(byte)0x4a,(byte)0x4c,(byte)0x58,(byte)0xcf,
		(byte)0xd0,(byte)0xef,(byte)0xaa,(byte)0xfb,(byte)0x43,(byte)0x4d,(byte)0x33,(byte)0x85,
		(byte)0x45,(byte)0xf9,(byte)0x02,(byte)0x7f,(byte)0x50,(byte)0x3c,(byte)0x9f,(byte)0xa8,
		(byte)0x51,(byte)0xa3,(byte)0x40,(byte)0x8f,(byte)0x92,(byte)0x9d,(byte)0x38,(byte)0xf5,
		(byte)0xbc,(byte)0xb6,(byte)0xda,(byte)0x21,(byte)0x10,(byte)0xff,(byte)0xf3,(byte)0xd2,
		(byte)0xcd,(byte)0x0c,(byte)0x13,(byte)0xec,(byte)0x5f,(byte)0x97,(byte)0x44,(byte)0x17,
		(byte)0xc4,(byte)0xa7,(byte)0x7e,(byte)0x3d,(byte)0x64,(byte)0x5d,(byte)0x19,(byte)0x73,
		(byte)0x60,(byte)0x81,(byte)0x4f,(byte)0xdc,(byte)0x22,(byte)0x2a,(byte)0x90,(byte)0x88,
		(byte)0x46,(byte)0xee,(byte)0xb8,(byte)0x14,(byte)0xde,(byte)0x5e,(byte)0x0b,(byte)0xdb,
		(byte)0xe0,(byte)0x32,(byte)0x3a,(byte)0x0a,(byte)0x49,(byte)0x06,(byte)0x24,(byte)0x5c,
		(byte)0xc2,(byte)0xd3,(byte)0xac,(byte)0x62,(byte)0x91,(byte)0x95,(byte)0xe4,(byte)0x79,
		(byte)0xe7,(byte)0xc8,(byte)0x37,(byte)0x6d,(byte)0x8d,(byte)0xd5,(byte)0x4e,(byte)0xa9,
		(byte)0x6c,(byte)0x56,(byte)0xf4,(byte)0xea,(byte)0x65,(byte)0x7a,(byte)0xae,(byte)0x08,
		(byte)0xba,(byte)0x78,(byte)0x25,(byte)0x2e,(byte)0x1c,(byte)0xa6,(byte)0xb4,(byte)0xc6,
		(byte)0xe8,(byte)0xdd,(byte)0x74,(byte)0x1f,(byte)0x4b,(byte)0xbd,(byte)0x8b,(byte)0x8a,
		(byte)0x70,(byte)0x3e,(byte)0xb5,(byte)0x66,(byte)0x48,(byte)0x03,(byte)0xf6,(byte)0x0e,
		(byte)0x61,(byte)0x35,(byte)0x57,(byte)0xb9,(byte)0x86,(byte)0xc1,(byte)0x1d,(byte)0x9e,
		(byte)0xe1,(byte)0xf8,(byte)0x98,(byte)0x11,(byte)0x69,(byte)0xd9,(byte)0x8e,(byte)0x94,
		(byte)0x9b,(byte)0x1e,(byte)0x87,(byte)0xe9,(byte)0xce,(byte)0x55,(byte)0x28,(byte)0xdf,
		(byte)0x8c,(byte)0xa1,(byte)0x89,(byte)0x0d,(byte)0xbf,(byte)0xe6,(byte)0x42,(byte)0x68,
		(byte)0x41,(byte)0x99,(byte)0x2d,(byte)0x0f,(byte)0xb0,(byte)0x54,(byte)0xbb,(byte)0x16
	};	

	/************************************************************/

	//変換テーブル(逆S-BOX)
	static byte invSbox[]={
		(byte)0x52,(byte)0x09,(byte)0x6a,(byte)0xd5,(byte)0x30,(byte)0x36,(byte)0xa5,(byte)0x38,
		(byte)0xbf,(byte)0x40,(byte)0xa3,(byte)0x9e,(byte)0x81,(byte)0xf3,(byte)0xd7,(byte)0xfb,
		(byte)0x7c,(byte)0xe3,(byte)0x39,(byte)0x82,(byte)0x9b,(byte)0x2f,(byte)0xff,(byte)0x87,
		(byte)0x34,(byte)0x8e,(byte)0x43,(byte)0x44,(byte)0xc4,(byte)0xde,(byte)0xe9,(byte)0xcb,
		(byte)0x54,(byte)0x7b,(byte)0x94,(byte)0x32,(byte)0xa6,(byte)0xc2,(byte)0x23,(byte)0x3d,
		(byte)0xee,(byte)0x4c,(byte)0x95,(byte)0x0b,(byte)0x42,(byte)0xfa,(byte)0xc3,(byte)0x4e,
		(byte)0x08,(byte)0x2e,(byte)0xa1,(byte)0x66,(byte)0x28,(byte)0xd9,(byte)0x24,(byte)0xb2,
		(byte)0x76,(byte)0x5b,(byte)0xa2,(byte)0x49,(byte)0x6d,(byte)0x8b,(byte)0xd1,(byte)0x25,
		(byte)0x72,(byte)0xf8,(byte)0xf6,(byte)0x64,(byte)0x86,(byte)0x68,(byte)0x98,(byte)0x16,
		(byte)0xd4,(byte)0xa4,(byte)0x5c,(byte)0xcc,(byte)0x5d,(byte)0x65,(byte)0xb6,(byte)0x92,
		(byte)0x6c,(byte)0x70,(byte)0x48,(byte)0x50,(byte)0xfd,(byte)0xed,(byte)0xb9,(byte)0xda,
		(byte)0x5e,(byte)0x15,(byte)0x46,(byte)0x57,(byte)0xa7,(byte)0x8d,(byte)0x9d,(byte)0x84,
		(byte)0x90,(byte)0xd8,(byte)0xab,(byte)0x00,(byte)0x8c,(byte)0xbc,(byte)0xd3,(byte)0x0a,
		(byte)0xf7,(byte)0xe4,(byte)0x58,(byte)0x05,(byte)0xb8,(byte)0xb3,(byte)0x45,(byte)0x06,
		(byte)0xd0,(byte)0x2c,(byte)0x1e,(byte)0x8f,(byte)0xca,(byte)0x3f,(byte)0x0f,(byte)0x02,
		(byte)0xc1,(byte)0xaf,(byte)0xbd,(byte)0x03,(byte)0x01,(byte)0x13,(byte)0x8a,(byte)0x6b,
		(byte)0x3a,(byte)0x91,(byte)0x11,(byte)0x41,(byte)0x4f,(byte)0x67,(byte)0xdc,(byte)0xea,
		(byte)0x97,(byte)0xf2,(byte)0xcf,(byte)0xce,(byte)0xf0,(byte)0xb4,(byte)0xe6,(byte)0x73,
		(byte)0x96,(byte)0xac,(byte)0x74,(byte)0x22,(byte)0xe7,(byte)0xad,(byte)0x35,(byte)0x85,
		(byte)0xe2,(byte)0xf9,(byte)0x37,(byte)0xe8,(byte)0x1c,(byte)0x75,(byte)0xdf,(byte)0x6e,
		(byte)0x47,(byte)0xf1,(byte)0x1a,(byte)0x71,(byte)0x1d,(byte)0x29,(byte)0xc5,(byte)0x89,
		(byte)0x6f,(byte)0xb7,(byte)0x62,(byte)0x0e,(byte)0xaa,(byte)0x18,(byte)0xbe,(byte)0x1b,
		(byte)0xfc,(byte)0x56,(byte)0x3e,(byte)0x4b,(byte)0xc6,(byte)0xd2,(byte)0x79,(byte)0x20,
		(byte)0x9a,(byte)0xdb,(byte)0xc0,(byte)0xfe,(byte)0x78,(byte)0xcd,(byte)0x5a,(byte)0xf4,
		(byte)0x1f,(byte)0xdd,(byte)0xa8,(byte)0x33,(byte)0x88,(byte)0x07,(byte)0xc7,(byte)0x31,
		(byte)0xb1,(byte)0x12,(byte)0x10,(byte)0x59,(byte)0x27,(byte)0x80,(byte)0xec,(byte)0x5f,
		(byte)0x60,(byte)0x51,(byte)0x7f,(byte)0xa9,(byte)0x19,(byte)0xb5,(byte)0x4a,(byte)0x0d,
		(byte)0x2d,(byte)0xe5,(byte)0x7a,(byte)0x9f,(byte)0x93,(byte)0xc9,(byte)0x9c,(byte)0xef,
		(byte)0xa0,(byte)0xe0,(byte)0x3b,(byte)0x4d,(byte)0xae,(byte)0x2a,(byte)0xf5,(byte)0xb0,
		(byte)0xc8,(byte)0xeb,(byte)0xbb,(byte)0x3c,(byte)0x83,(byte)0x53,(byte)0x99,(byte)0x61,
		(byte)0x17,(byte)0x2b,(byte)0x04,(byte)0x7e,(byte)0xba,(byte)0x77,(byte)0xd6,(byte)0x26,
		(byte)0xe1,(byte)0x69,(byte)0x14,(byte)0x63,(byte)0x55,(byte)0x21,(byte)0x0c,(byte)0x7d
	};

}
