package custombrowser.cryptogram;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
class Base64{

	//変換テーブル
	private static final String TABLE = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

  	public static String encode(String data){
  		try{
  			return encode(data.getBytes("UTF-8"));
  		}catch(Exception e){
  			return "";
  		}
  	}

  	//エンコード
  	public static String encode(byte data[]){
  		if(data.length == 0){ return ""; }

  		int i;
  		int index[] = new int[1 + data.length * 4 / 3];
  		int count = 0;
  		int limit = data.length - 3;
  		int mod;
  		int padding;
  		StringBuilder result = new StringBuilder("");

  		for(i = 0; i < limit; i+=3){
  			index[count++] = (data[i] & 0xfc) >>> 2;
			index[count++] = ((data[i] & 0x03) << 4) + ((data[i+1] & 0xf0) >>> 4);
			index[count++] = ((data[i+1] & 0x0f) << 2) + ((data[i+2] & 0xc0) >>> 6);
			index[count++] = ((data[i+2]) & 0x3f);
  		}

  		mod = data.length % 3;
  		if(mod == 0){
  			index[count++] = (data[i] & 0xfc) >>> 2;
  			index[count++] = ((data[i] & 0x03) << 4) + ((data[i+1] & 0xf0) >>> 4);
  			index[count++] = ((data[i+1] & 0x0f) << 2) + ((data[i+2] & 0xc0) >>> 6);
  			index[count++] = ((data[i+2]) & 0x3f);
  		}
  		else if(mod == 1){
  			index[count++] = (data[i] & 0xfc) >>> 2;
  			index[count++] = (data[i] & 0x03) << 4;
  		}
  		else if(mod == 2){
  			index[count++] = (data[i] & 0xfc) >>> 2;
  			index[count++] = ((data[i] & 0x03) << 4) + ((data[i+1] & 0xf0) >>> 4);
  			index[count++] = (data[i+1] & 0x0f) << 2;
  		}

  		for(i = 0 ; i < count; i++){
  			result.append(TABLE.charAt(index[i]));
  		}

  		padding = (4 - result.length() % 4) % 4;
  		for(i = 0 ; i < padding ; i++){
  			result.append("=");
  		}

  		return result.toString();
  	}

  	public static String decodeToString(String base64){
  		return new String(decode(base64));
  	}

  	//デコード
  	public static byte[] decode(String base64){
  		int i;
  		int length = base64.length();
  		int data[] = new int[length];
  		byte result[] = new byte[length * 3 / 4];
  		int mod;
  		int limit = length - 4;
  		int count = 0;

  		for(i = 0; i < length; i++){
  			int c = base64.charAt(i);
  			
  			if('A' <= c && c <= 'Z'){
  				data[i] = c - 'A';
  			}
  			else if('a' <= c && c <= 'z'){
  				data[i] = c - 'a' + 26;
  			}
  			else if('0' <= c && c <= '9'){
  				data[i] = c - '0' + 52;
  			}
  			else if(c == '+'){
  				data[i] = 62;
  			}
  			else if(c == '/'){
  				data[i] = 63;
  			}
  		}

  		for(i = 0; i < limit; i+=4){
  			result[count++] = (byte)(  ((data[i] & 0x03f) << 2)  + ((data[i+1] & 0x30) >>> 4)  );
  			result[count++] = (byte)(  ((data[i+1] & 0x0f) << 4) + ((data[i+2] & 0x3c) >>> 2)  );
  			result[count++] = (byte)(  ((data[i+2] & 0x03) << 6) +  (data[i+3] & 0x3f)         );
  		}

  		mod = length % 4;
  		if(mod == 0){
  			result[count++] = (byte)(  ((data[i] & 0x03f) << 2)  + ((data[i+1] & 0x30) >>> 4)  );
  			result[count++] = (byte)(  ((data[i+1] & 0x0f) << 4) + ((data[i+2] & 0x3c) >>> 2)  );
  			result[count++] = (byte)(  ((data[i+2] & 0x03) << 6) +  (data[i+3] & 0x3f)         );
  		}
  		else if(mod == 2){
  			result[count++] = (byte)(  ((data[i] & 0x03f) << 2)  + ((data[i+1] & 0x30) >>> 4)  );
  		}
  		else if(mod == 3){
  			result[count++] = (byte)(  ((data[i] & 0x03f) << 2)  + ((data[i+1] & 0x30) >>> 4)  );
  			result[count++] = (byte)(  ((data[i+1] & 0x0f) << 4) + ((data[i+2] & 0x3c) >>> 2)  );
  		}
  		
  		return result;
  	}
}
  
  