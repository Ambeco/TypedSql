package com.tbohne.sqlite.sample.datatypes;

import com.tbohne.sqlite.binders.SimpleColumnBinder;
import com.tbohne.sqlite.exceptions.ColumnInvalidException;

public class DaysSelectedBinder
		implements SimpleColumnBinder<DaysSelected, String>
{
	@Override
	public DaysSelected fromSql(String columnValue) {
		DaysSelected days = new DaysSelected();
		for (int i = 0; i < columnValue.length(); i++) {
			switch (columnValue.charAt(i)) {
				case 'U':
					days.selected[0] = true;
					break;
				case 'M':
					days.selected[1] = true;
					break;
				case 'T':
					days.selected[2] = true;
					break;
				case 'W':
					days.selected[3] = true;
					break;
				case 'R':
					days.selected[4] = true;
					break;
				case 'F':
					days.selected[5] = true;
					break;
				case 'S':
					days.selected[6] = true;
					break;
				default:
					throw new ColumnInvalidException("invalid DaysSelected value "
																					 + columnValue
																					 + ": "
																					 + columnValue.charAt(i)
																					 + " is not a valid day of the week");
			}
		}
		return days;
	}

	@Override
	public String toSql(DaysSelected javaValue) {
		StringBuilder sb = new StringBuilder();
		if (javaValue.selected[0]) {
			sb.append('U');
		}
		if (javaValue.selected[1]) {
			sb.append('M');
		}
		if (javaValue.selected[2]) {
			sb.append('T');
		}
		if (javaValue.selected[3]) {
			sb.append('W');
		}
		if (javaValue.selected[4]) {
			sb.append('R');
		}
		if (javaValue.selected[5]) {
			sb.append('F');
		}
		if (javaValue.selected[6]) {
			sb.append('S');
		}
		return sb.toString();
	}
}
