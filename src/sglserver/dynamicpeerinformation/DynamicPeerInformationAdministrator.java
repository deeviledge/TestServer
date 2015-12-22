package sglserver.dynamicpeerinformation;

import java.net.Socket;

/**
 * ログインしているユーザーを管理するクラス
 * @author Hiroki
 * @version 1.1
 * @作成日: 2007/7/17
 * @最終更新日:2008/10/31
 */
public class DynamicPeerInformationAdministrator{
	private static DynamicPeerInformation dyinfo = new DynamicPeerInformation();
	
	/**
	 * ログインしたユーザーを追加
	 * @param soc ソケット
	 * @param id　ユーザID
	 * @param name　ユーザ名
	 * @param remoteip リモートIP
	 * @author hiroki
	 */
	public static void addEntry(Socket soc,String id, String name,String remoteip){
		String socIP = soc.getRemoteSocketAddress().toString();
		socIP = socIP.substring(0,socIP.indexOf(":"));
		dyinfo.appendUser(id,name,socIP.substring(1),remoteip);
	}

	/**
	 * ログオフしたユーザーを削除
	 * @param id　ユーザID
	 * @author hiroki
	 */
	public static void removeEntry(String id){
		dyinfo.removeUser(id);
	}
}
