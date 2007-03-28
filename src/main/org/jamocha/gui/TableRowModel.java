/*
 * Copyright 2007 Alexander Wilden
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://jamocha.sourceforge.net/
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */
package org.jamocha.gui;

/**
 * A Model for JTable that is supported by the {@link TableModelQuickSort}.
 * 
 * @author Alexander Wilden <october.rust@gmx.de>
 */
public interface TableRowModel {

	public Object getRowAt(int row);

	public void setRowAt(Object value, int row);

	public Object getValueAt(int row, int column);

	public void setValueAt(Object value, int row, int column);

	public Class getColumnClass(int column);

	public int getRowCount();

	public void fireTableDataChanged();

}
