package com.tbohne.sqlite.sample.datatypes;

public class DaysSelected {
	public boolean[] selected;

	public DaysSelected() {
		selected = new boolean[7];
		for (int i = 0; i < 7; i++) {
			selected[i] = false;
		}
	}
}
