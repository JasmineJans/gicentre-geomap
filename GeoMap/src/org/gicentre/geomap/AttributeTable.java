package org.gicentre.geomap;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import processing.core.PApplet;
import processing.core.PConstants;

//  ****************************************************************************************
/** Class for representing a table of attributes suitable for querying.
 *  @author Ben Fry (http://ben.fry.com/writing/map/Table.pde) with modifications by 
 *          Jo Wood and Iain Dillingham.
 *  @version 1.2, 29th October, 2013.
 */ 
//  ****************************************************************************************

/* This file is part of giCentre's geoMap library. geoMap is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * geoMap is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * source code (see COPYING.LESSER included with this source code). If not, see
 * http://www.gnu.org/licenses/.
 */
public class AttributeTable
{
	
	// TODO: When processing.data.Table is fully developed, use that instead and deprecate AttributeTable.
	// @deprecated Should use Processing 2.x built-in Table class instead.
	
	// --------------------------------- Object variables ---------------------------------

	private String[] header;		// Column headings.
	private String[][] data;		// All data stored as strings.
	private int rowCount;			// Number of rows in the table.
	private PApplet parent;			// Parent sketch used for reading and writing files.

	// ----------------------------------- Constructors -----------------------------------

	/** Creates a new empty 10 by 10 table.
	 *  @param parent Sketch controlling the location of files to load and save.
	 */
	public AttributeTable(PApplet parent) 
	{
		this(10,10,parent);
	}
	
	/** Creates a new empty table with the given dimensions
	 *  @param numRows Number of rows in table.
	 *  @param numCols Number of columns in table.
	 *  @param parent Sketch controlling the location of files to load and save.
	 */
	public AttributeTable(int numRows, int numCols, PApplet parent) 
	{
		this.parent = parent;
		data = new String[numRows][numCols];
		for (int row=0; row<data.length; row++)
		{
			for (int col=0; col<data[row].length; col++)
			{
				data[row][col] = new String();
			}
		}
		rowCount = numRows;
	}

	/** Creates a table from the given tab separated values file.
	 *  @param filename Name of file containing the TSVs.
	 */
	public AttributeTable(String filename, PApplet parent) 
	{
		this.parent = parent;
		String[] rows = parent.loadStrings(filename);

		if (rows == null)
		{
			System.err.println("Warning: "+filename+" not found. No data loaded into table.");
			data = new String[10][10];
			for (int row=0; row<data.length; row++)
			{
				for (int col=0; col<data[row].length; col++)
				{
					data[row][col] = new String();
				}
			}
			return;
		}

		data = new String[rows.length][];

		for (int i=0; i<rows.length; i++)
		{
			if (PApplet.trim(rows[i]).length() == 0)
			{
				continue;     // Skip empty rows
			}

			if (rows[i].trim().startsWith("#"))
			{
				if (header == null)
				{
					header = PApplet.split(rows[i].trim().substring(1), PConstants.TAB);
				}
				continue;    // Skip comment lines
			}

			// Split the row on the tabs
			String[] pieces = PApplet.split(rows[i].trim(), PConstants.TAB);

			// Copy to the table array
			data[rowCount] = pieces;
			rowCount++;
		}

		// Resize the 'data' array as necessary
		data = (String[][]) PApplet.subset(data, 0, rowCount);
	}

	// ------------------------------------- Methods -------------------------------------

	/** Sets the column headings for the table.
	 *  @param headings Text headings for each column in the table.
	 */
	public void setHeadings(String[] headings)
	{
		this.header = headings;
	}
	
	/** Reports the column headings for the table.
	 *  @return Text headings for each column in the table.
	 */
	public String[] getHeadings()
	{
		return header;
	}

	/** Reports the number of rows in the table.
	 *  @return Number of rows in the table.
	 */
	public int getRowCount() 
	{
		return rowCount;
	}

	/** Finds a row by its ID. The ID is always stored the first column.
	 *  @param id ID of the row to search for.
	 *  @return Row index corresponding to the first row containing the given ID or -1 if no ID is found.
	 */
	public int getRowIndex(String id)
	{
		for (int i=0; i<rowCount; i++)
		{
			if (data[i][0].equals(id)) 
			{
				return i;
			}
		}
		System.err.println("No row with ID '" +id+ "' was found");
		return -1;
	}
	
	/** Finds a row by its ID. The ID is always stored the first column.
	 *  @param id ID of the row to search for.
	 *  @return Row index corresponding to the first row containing the given ID or -1 if no ID is found.
	 */
	public int getRowIndex(int id)
	{
		return getRowIndex(Integer.toString(id));
	}

	/** Reports the id of the row at the given row index (first row is 0, second is 1 etc.).
	 *  @param rowIndex Row index to query.
	 *  @return First column value associated with the given row index.
	 */
	public String getRowName(int rowIndex)
	{
		return getStringAt(rowIndex, 0);
	}

	/** Reports the item at the given row and column location as a String.
	 *  @param rowIndex Row index of the table value to retrieve (first row is 0, second is 1 etc.).
	 *  @param columnIndex Column index of the table value to retrieve. (first column is 0, second is 1 etc.).
	 *  @return Table value at the given location reported as a string.
	 */
	public String getStringAt(int rowIndex, int columnIndex)
	{
		if ((rowIndex < 0) || (rowIndex >= data.length))
		{
			System.err.println("Unknown row index: "+rowIndex+" when querying table.");
			return "";
		}
		
		if ((columnIndex < 0) || (columnIndex >= data[rowIndex].length))
		{
			System.err.println("Unknown column index: "+columnIndex+" when querying row "+rowIndex+" in table.");
			return "";
		}
		return data[rowIndex][columnIndex];
	}
	
	/** Reports the ids of all items at the given column index that match the given text
	 *  @param attribute Text to search for.
	 *  @param columnIndex Column in table to search (first column is 0, second is 1 etc.).
	 *  @return List of ids corresponding to matched text. Will be an empty list if no matches found.
	 */
	public Set<Integer> match(String attribute, int columnIndex)
	{
		HashSet<Integer>matches = new HashSet<Integer>();
		for (int row=0; row<rowCount; row++)
		{
			if (attribute.equals(getStringAt(row,columnIndex)))
			{
				matches.add(new Integer(getStringAt(row, 0)));
			}
		}
		return matches;
	}
	
	/** Reports the item in the given column position with the given ID as a String.
	 *  @param id ID of the row to query.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc).
	 *  @return Table value at the given column reported as a string.
	 */
	public String getString(String id, int columnIndex)
	{
		return getStringAt(getRowIndex(id), columnIndex);
	}

	/** Reports the item in the row with the given ID at the given column location as a String.
	 *  @param id ID of the row to query.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc).
	 *  @return Table value at the given column reported as a string.
	 */
	public String getString(int id, int columnIndex)
	{
	    return getStringAt(getRowIndex(Integer.toString(id)), columnIndex);
	}

	/** Reports the item at the given row and column location as a whole number.
	 *  @param rowIndex Row of the table value to retrieve.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc).
	 *  @return Table value at the given row and column location reported as a whole number.
	 */
	public int getIntAt(int rowIndex, int columnIndex) 
	{
		return PApplet.parseInt(getStringAt(rowIndex, columnIndex));
	}

	/** Reports the item in the row with the given ID at the given column location as a whole number.
	 *  @param id ID of the row to query.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc).
	 *  @return Table value at the given row and column location reported as a whole number.
	 */
	public int getInt(String id, int columnIndex) 
	{
		return PApplet.parseInt(getString(id, columnIndex));
	}
	
	/** Reports the item in the row with the given ID at the given column location as a whole number.
	 *  @param id ID of the row to query.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc).
	 *  @return Table value at the given row and column location reported as a whole number.
	 */
	public int getInt(int id, int columnIndex)
	{
	    return PApplet.parseInt(getString(Integer.toString(id), columnIndex));
	}

	/** Reports the item at the given row and column location as a decimal number.
	 *  @param rowIndex Row of the table value to retrieve.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc).
	 *  @return Table value at the given row and column location reported as a decimal number.
	 */
	public float getFloatAt(int rowIndex, int columnIndex)
	{
		return PApplet.parseFloat(getStringAt(rowIndex, columnIndex));
	}

	/** Reports the item in the row with the given ID at the given column location as a decimal number.
	 *  @param id ID of the row to query.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc).
	 *  @return Table value at the given row and column location reported as a decimal number.
	 */
	public float getFloat(String id, int columnIndex)
	{
		return PApplet.parseFloat(getString(id, columnIndex));
	}
	
	/** Reports the item in the row with the given ID at the given column location as a decimal number.
	 *  @param id ID of the row to query.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc).
	 *  @return Table value at the given row and column location reported as a decimal number.
	 */
	public float getFloat(int id, int columnIndex)
	{
	    return PApplet.parseFloat(getString(Integer.toString(id), columnIndex));
	}
	
	// ------------------------------------ Mutators ------------------------------------
	
	/** Sets the ID of the given row. This item will be stored in column 0 of the row.
	 *  @param rowIndex Row in which to set the name (0 is the first row, 1 the second etc.).
	 *  @param id New ID (column 0 value) to be associated with the given row.
	 */
	public void setRowName(int rowIndex, String id)
	{
		data[rowIndex][0] = id;
	}

	/** Sets the value at the given row and column location as the given String.
	 *  @param rowIndex Row of the table value to change (0 is the first row, 1 is the second etc.).
	 *  @param columnIndex Column of the table value to change (0 is the first column, 1 is the second etc.).
	 *  @param value New value to be associated with the given table cell.
	 */
	public void setStringAt(int rowIndex, int columnIndex, String value) 
	{
		data[rowIndex][columnIndex] = value;
	}

	/** Sets the value at the given row and column as the given String.
	 *  @param id ID of the row in which a value will be changed.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc.).
	 *  @param value New value to be associated with the given table cell.
	 */
	public void setString(int id, int columnIndex, String value)
	{
		data[getRowIndex(id)][columnIndex] = value;
	}
	
	/** Sets the value at the given row and column as the given String.
	 *  @param id ID of the row in which a value will be changed.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc.).
	 *  @param value New value to be associated with the given table cell.
	 */
	public void setString(String id, int columnIndex, String value)
	{
		data[getRowIndex(id)][columnIndex] = value;
	}
	
	/** Sets the value at the given row and column location as the given whole number.
	 *  @param rowIndex Row of the table value to change (0 is the first row, 1 is the second etc.).
	 *  @param columnIndex Column of the table value to change (0 is the first column, 1 is the second etc.).
	 *  @param value New value to be associated with the given table cell.
	 */
	public void setIntAt(int rowIndex, int columnIndex, int value)
	{
		data[rowIndex][columnIndex] = PApplet.str(value);
	}

	/** Sets the value at the given row and column as the given whole number.
	 *  @param id ID of the row in which a value will be changed.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc.).
	 *  @param value New value to be associated with the given table cell.
	 */
	public void setInt(String id, int columnIndex, int value) 
	{
		data[getRowIndex(id)][columnIndex] = PApplet.str(value);
	}
	
	/** Sets the value at the given row and column as the given whole number.
	 *  @param id ID of the row in which a value will be changed.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc.).
	 *  @param value New value to be associated with the given table cell.
	 */
	public void setInt(int id, int columnIndex, int value) 
	{
		data[getRowIndex(id)][columnIndex] = PApplet.str(value);
	}

	/** Sets the value at the given row and column location as the given decimal number.
	 *  @param rowIndex Row of the table value to change (0 is the first row, 1 is the second etc.).
	 *  @param columnIndex Column of the table value to change (0 is the first column, 1 is the second etc.).
	 *  @param value New value to be associated with the given table cell.
	 */
	public void setFloatAt(int rowIndex, int columnIndex, float value)
	{
		data[rowIndex][columnIndex] = PApplet.str(value);
	}

	/** Sets the value at the given row and column as the given decimal number.
	 *  @param id ID of the row in which a value will be changed.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc.).
	 *  @param value New value to be associated with the given table cell.
	 */
	public void setFloat(String id, int columnIndex, float value)
	{
		data[getRowIndex(id)][columnIndex] = PApplet.str(value);
	}
	
	/** Sets the value at the given row and column as the given decimal number.
	 *  @param id ID of the row in which a value will be changed.
	 *  @param columnIndex Column of the table value to retrieve (0 is the first column, 1 is the second etc.).
	 *  @param value New value to be associated with the given table cell.
	 */
	public void setFloat(int id, int columnIndex, float value)
	{
		data[getRowIndex(id)][columnIndex] = PApplet.str(value);
	}
	
	// ---------------------------- Formatting methods ----------------------------
	
	/** Calculates the maximum widths of the values in each column. A width is the number
	 *  of characters in a cell.
	 *  @return Maximum width of each of the columns in the table.
	 */
	public int[] calcMaxWidths()
	{
		// Find number of columns. Since the table could be ragged, need to check each row.
		int numCols = findNumCols();
		
		// Find the maximum number of characters in each column.
		int[] maxWidths = new int[numCols];
		if (header != null)
		{
			for (int col=0; col<header.length; col++)
			{
				maxWidths[col] = Math.max(maxWidths[col], header[col].length());
			}
		}
		for (int row=0; row<data.length; row++)
		{
			for (int col=0; col<data[row].length; col++)
			{
				maxWidths[col] = Math.max(maxWidths[col], data[row][col].length());
			}
		}
		
		return maxWidths;
	}
	
	/** Finds the number of columns in the table. Since the table may be ragged, this method will check each row
	 *  will return the maximum number of columns found in the entire table.
	 *  @return Maximum number of columns in the table.
	 */
	public int findNumCols()
	{
		int numCols = 0;
		
		if (header != null)
		{
			numCols = header.length;
		}
		
		for (int row=0; row<data.length; row++)
		{
			numCols = Math.max(numCols, data[row].length);
		}
		return numCols;
	}
	
	/** Writes this table in TSV format to standard output.
	 */
	public void write() 
	{
		write(new PrintWriter(new OutputStreamWriter(System.out)));
	}

	/** Writes this table as a TSV file with the given name.
	 *  @param fileName Name of file to contain the TSV output.
	 */
	public void write(String fileName) 
	{
		write(parent.createWriter(fileName));
	}
	
	/** Writes this table as a TSV file to the given writer.
	 *  @param writer Output writer in which to send table contents.
	 */
	public void write(PrintWriter writer) 
	{
		if (header != null)
		{
			for (int j=0; j<header.length; j++)
			{
				if (j != 0)
				{
					writer.print(PConstants.TAB);
				}
				if (header[j] != null)
				{
					if (j==0)
					{
						writer.print("#"+header[0]);
					}
					else
					{
						writer.print(header[j]);
					}
				}
			}
			writer.println();
		}


		for (int i=0; i<rowCount; i++)
		{
			for (int j=0; j<data[i].length; j++)
			{
				if (j != 0)
				{
					writer.print(PConstants.TAB);
				}
				if (data[i][j] != null)
				{
					writer.print(data[i][j]);
				}
			}
			writer.println();
		}
		writer.flush();
	}
	
	/** Writes this table as formatted text to standard output. This is designed for producing 'pretty'
	 *  text output so table can be examined more easily.
	 *  @param maxNumRows Maximum number of rows of the table to display.
	 */
	public void writeAsTable(int maxNumRows) 
	{
		writeAsTable(new PrintWriter(new OutputStreamWriter(System.out)),maxNumRows);
	}
	
	/** Writes this table as formatted text to the file with the given name. This is designed for producing 
	 *  'pretty' text output so table can be examined more easily.
	 *  @param fileName Name of file to contain the formatted output.
	 *  @param maxNumRows Maximum number of rows of the table to display.
	 */
	public void writeAsTable (String fileName, int maxNumRows) 
	{
		writeAsTable(parent.createWriter(fileName), maxNumRows);
	}
	
	/** Writes this table as formatted text to the given writer. This is designed for producing 'pretty'
	 *  text output so table can be examined more easily.
	 *  @param writer Output writer in which to send table contents.
	 *  @param maxNumRows Maximum number of rows of the table to display.
	 */
	public void writeAsTable(PrintWriter writer, int maxNumRows) 
	{
		int numRows = Math.min(maxNumRows, data.length);
		int[] maxWidths = calcMaxWidths();
		int numCols = maxWidths.length;

		int totalWidth = numCols+1;
		for (int col=0; col<maxWidths.length; col++)
		{
			totalWidth += maxWidths[col];
		}

		// Display formatted output.
		for (int i=0; i<totalWidth; i++)
		{
			writer.print("-");
		}
		writer.println();

		if (header != null)
		{
			writer.print("|");
			for (int col=0; col<numCols; col++)
			{
				int numSpaces = maxWidths[col];
				if (col<header.length) 
				{
					writer.print(header[col]);
					numSpaces -= header[col].length();
				}
				for (int space=0; space<numSpaces; space++)
				{
					writer.print(" ");
				}
				writer.print("|");
			}
			writer.println();

			for (int i=0; i<totalWidth; i++)
			{
				writer.print("-");
			}
			writer.println();
		}

		for (int row=0; row<numRows; row++)
		{
			writer.print("|");
			for (int col=0; col<numCols; col++)
			{
				int numSpaces = maxWidths[col];
				if (col<data[row].length)
				{
					writer.print(data[row][col]);
					numSpaces -= data[row][col].length();
				}
				for (int space=0; space<numSpaces; space++)
				{
					writer.print(" ");
				}
				writer.print("|");
			}
			writer.println();
		}

		if (numRows == data.length)
		{
			for (int i=0; i<totalWidth; i++)
			{
				writer.print("-");
			}
			writer.println();
		}
		writer.flush();
	}
}
