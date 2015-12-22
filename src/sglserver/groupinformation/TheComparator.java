/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sglserver.groupinformation;

import java.util.Comparator;


/**
 *
 * @author tatoo
 */
public class TheComparator implements Comparator {

    /** ソート対象のカラムの位置 */
    private int index = 0;

    /** ソートするためのカラム位置をセット */
    public void setIndex( int index ) {
        this.index = index;
    }

    public int compare( Object a, Object b ) {
        String[] strA = ( String[] ) a;
        String[] strB = ( String[] ) b;

        return ( strA[ index ].compareTo( strB[ index ] ) );
    }
}