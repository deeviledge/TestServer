/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sglserver.groupinformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import sglserver.dynamicpeerinformation.DynamicPeerInformation;

/**
 *
 * @author okumura
 */
public class UserSorting {
     private int list_length;
    private ArrayList middle_list;
    
    private String id_num;
    private String key;
    private String value;
    private String[][] array={};
    DynamicPeerInformation dpi=new DynamicPeerInformation();
    Map<String,String> mapT=new TreeMap<String,String>();//自動的にソートしてくれる連想配列としてTreeMapを利用する

    public UserSorting(ArrayList<?> userlist){
        list_length=userlist.size();
        System.out.println("userlistは"+userlist);
        middle_list=userlist;
        array=new String[list_length][2];    
    }

    public void get_ID_num(){
        try{
            for(int j=0;j<list_length;j++){
                key=dpi.getValueOfID((String) middle_list.get(j));   //ID値をString型で取得
                value=(String) middle_list.get(j);                      //usernameをvalue値で取得
                //[[key1, value1],[key2, value2], [key3, value3], [key4, value4]]の
                //構造で二次元配列にkyeとvalueを格納
                array[j][0]=key;
                array[j][1]=value;
            }
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("◆◆◆DynamicPeerInformation.java内のget_value_of_IDメソッド処理中に例外が発生しました。◆◆◆");
            System.out.println("get_value_of_IDメソッド、または引数のkey、valueに問題がある可能性があります");
        }
            System.out.println("◇◇◇◇◇"+(Arrays.deepToString(array))+"◇◇◇◇◇"); 
    }
    
    public void sort(){
        
        try{
            TheComparator comparator = new TheComparator();
            comparator.setIndex( 0 );//1番目の要素(この場合はID)でソートするように設定
            System.out.println("ソート前");
            System.out.println(Arrays.deepToString(array));
            Arrays.sort( array, comparator );//TheComparator内のソートメソッド
            System.out.println("ソート後"); 
            System.out.println(Arrays.deepToString(array));
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("◆◆◆TheComparator.java内のgetIndexメソッド処理中に例外が発生しました。◆◆◆");
        }
        
    }
    
    public ArrayList<String> get_sorted(){
        try{            
            ArrayList<String> sorted_list = new ArrayList<String>();
            for(int j=0;j<list_length;j++){
                sorted_list.add(array[j][1]);
            }
            System.out.println("ソートリストの中身"+sorted_list);
            return sorted_list;
        }catch(Exception e){
            e.printStackTrace();
            System.out.println("◆◆◆get_user_ID.java内のget_sortedメソッド処理中に例外が発生しました。◆◆◆");
            System.out.println("get_sortedメソッドの呼出しに対してreturn nullを返します");
            return null;
        }
    }
    
}
