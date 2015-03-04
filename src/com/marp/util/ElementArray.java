package com.marp.util;

public class ElementArray<E> {

	private Object[] array;

	
	/**
	 * Changes the size of the array
	 * @param newSize
	 */
	public void changeSize(int newSize, E def) {
		@SuppressWarnings("unchecked")
		E[] old = (E[]) array;
		array = new Object[newSize];
		if (old != null)
			for (int i = 0; i < old.length && i < array.length; i++)
				array[i] = old[i];
		for (int i = 0; i < newSize; i++) {
			if (array[i] == null)
				array[i] = def;
		}
	}

	/**
	 * Sets the maze at position index
	 * @param m
	 * @param index
	 */
	public void setElementAt(E m, int index) {
		array[index] = m;
	}
	
	/**
	 * Returns the maze at position index
	 * @param index
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public E getElementAt(int index) {
		return (E) array[index];
	}
	/**
	 * Returns the lenght of the array or -1 if it is not initialized
	 * @return
	 */
	public int getSize() {
		return array == null ? -1 : array.length;
	}


}
