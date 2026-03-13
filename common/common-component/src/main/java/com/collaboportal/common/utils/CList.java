package com.collaboportal.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;

/**
 * カスタムリストクラス
 * @param <T> リスト要素の型
 */
public class CList<T> implements List<T> {

	// 内部で使用するArrayList
	private ArrayList<T> list;

	/**
	 * コンストラクタ
	 */
	public CList() {
		list = new ArrayList<>();
    }

	/**
	 * 0件ハンドリングメソッド
	 * @return リスト
	 * @throws CommonException リストが空の場合にスローされる
	 */
	public List<T> handleNotFound() throws CommonException {
		if(list.isEmpty()) {
			throw new CommonException(InternalErrorCode.RECORD_NOT_FOUND_ERROR);
		}
		else {
			return list;
		}
	}

	@Override
    public  boolean isEmpty(){return list.isEmpty();}
    @Override
    public  boolean contains(Object o){return list.contains(o);}
    @Override
    public  Iterator<T> iterator(){return list.iterator();}
    @Override
    public  Object[] toArray(){return list.toArray();}
    @Override
    public  <T> T[] toArray(T[] a){return list.toArray(a);}
    @Override
    public  boolean add(T T){return list.add(T);}
    @Override
    public  boolean remove(Object o){return list.remove(o);}
    @Override
    public  boolean containsAll(Collection<?> c){return list.containsAll(c);}
    @Override
    public  boolean addAll(Collection<? extends T> c){return list.addAll(c);}
    @Override
    public  boolean addAll(int index, Collection<? extends T> c){return list.addAll(index,c);}
    @Override
    public  boolean removeAll(Collection<?> c){return list.removeAll(c);}
    @Override
    public  boolean retainAll(Collection<?> c){return list.retainAll(c);}
    @Override
    public  void clear(){list.clear();}
    @Override
    public  boolean equals(Object o){return list.equals(o);}
    @Override
    public  int hashCode(){return list.hashCode();}
    @Override
    public  T get(int index){return list.get(index);}
    @Override
    public  T set(int index, T element){return list.set(index, element);}
    @Override
    public  void add(int index, T element){list.add(index,element);}
    @Override
    public  T remove(int index){return list.remove(index);}
    @Override
    public  int indexOf(Object o){return list.indexOf(o);}
    @Override
    public  int lastIndexOf(Object o){return list.lastIndexOf(o);}
    @Override
    public  ListIterator<T> listIterator(){return list.listIterator();}
    @Override
    public  ListIterator<T> listIterator(int index){return list.listIterator(index);}
    @Override
    public  List<T> subList(int fromIndex, int toIndex){return list.subList(fromIndex,toIndex);}
	@Override
	public int size() {return list.size();}
  }