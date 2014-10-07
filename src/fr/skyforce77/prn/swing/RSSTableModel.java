package fr.skyforce77.prn.swing;

import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.table.AbstractTableModel;

import fr.skyforce77.prn.save.DataBase;
import fr.skyforce77.prn.save.RSSEntry;

public class RSSTableModel extends AbstractTableModel {

	private static final long serialVersionUID = -282416444344794124L;

	private final String[] columns = {"Name","Url"};
	
	@SuppressWarnings("unchecked")
	public int getRowCount() {
        return ((CopyOnWriteArrayList<RSSEntry>)DataBase.getValue("feeds")).size();
    }
 
    public int getColumnCount() {
        return columns.length;
    }
 
    public String getColumnName(int columnIndex) {
        return columns[columnIndex];
    }

    @SuppressWarnings("unchecked")
	public Object getValueAt(int rowIndex, int columnIndex) {
    	CopyOnWriteArrayList<RSSEntry> entries = (CopyOnWriteArrayList<RSSEntry>)DataBase.getValue("feeds");
        switch(columnIndex){
            case 0:
                return entries.get(rowIndex).getName();
            case 1:
                return entries.get(rowIndex).getURL();
            default:
                return null;
        }
    }

}
