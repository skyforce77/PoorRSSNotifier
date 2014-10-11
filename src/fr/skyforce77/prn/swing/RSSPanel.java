package fr.skyforce77.prn.swing;

import it.sauronsoftware.feed4j.FeedParser;
import it.sauronsoftware.feed4j.bean.Feed;
import it.sauronsoftware.feed4j.bean.FeedItem;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import fr.skyforce77.prn.PRN;
import fr.skyforce77.prn.rss.FeedItemInfo;
import fr.skyforce77.prn.save.DataBase;
import fr.skyforce77.prn.save.RSSEntry;

public class RSSPanel extends JPanel{

	private static final long serialVersionUID = 3861075847304452437L;

	public JTable table;
	public RSSPanel instance;
	public RSSTableModel model;
	public GroupLayout layout;
	public JScrollPane tablepane;
	public JButton add;
	
	public RSSPanel() {
		instance = this;
		layout = new GroupLayout(this);
		setLayout(layout);
		
		model = new RSSTableModel();
		table = new JTable(model);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setResizingAllowed(false);
		table.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)) {
					if(table.getSelectionModel().getMaxSelectionIndex() == -1 && table.getSelectionModel().getMinSelectionIndex() == -1) {
						int rowNumber = table.rowAtPoint(e.getPoint());
						table.getSelectionModel().setSelectionInterval(rowNumber, rowNumber);
					}
					new RSSPopupMenu().show(table, e.getX(), e.getY());
				}
			}
		});
		tablepane = new JScrollPane(table);
		
		final JTextField url = new JTextField();
		url.setToolTipText("RSS url");
		
		add = new JButton("Add");
		add.addActionListener(new ActionListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!url.getText().equals("")) {
					try {
						RSSEntry entry = new RSSEntry(url.getText());
						URL url = new URL(entry.getURL());
						Feed feed = FeedParser.parse(url);
						FeedItem item = feed.getItem(0);
						DataBase.setValue(entry.getURL(), new Date().getTime());
						((CopyOnWriteArrayList<RSSEntry>)DataBase.getValue("feeds")).add(entry);
						DataBase.save();
						JOptionPane.showMessageDialog(instance, feed.getHeader().getTitle()+" added", "Add", JOptionPane.INFORMATION_MESSAGE);
						PRN.notify(new FeedItemInfo(feed, item));
					} catch(Exception e1) {
						JOptionPane.showMessageDialog(instance, "Une erreur est survenue.\nL'url semble incorrecte", "Erreur", JOptionPane.ERROR_MESSAGE);
						return;
					}
					url.setText("");
					model.fireTableDataChanged();
				}
			}
		});
		
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(tablepane)
				.addGroup(layout.createParallelGroup()
						.addComponent(url, 30, 30, 40)
						.addComponent(add, 30, 30, 40)));

		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(tablepane)
				.addGroup(layout.createSequentialGroup().addComponent(url).addComponent(add)));
	}
	
	public class RSSPopupMenu extends JPopupMenu {
		private static final long serialVersionUID = -341484848466376L;

		public RSSPopupMenu() {
			JMenuItem delete = new JMenuItem("Supprimer");
			delete.addActionListener(new ActionListener() {
				@SuppressWarnings("unchecked")
				@Override
				public void actionPerformed(ActionEvent arg0) {
					for(int s : table.getSelectedRows()) {
						((CopyOnWriteArrayList<RSSEntry>)DataBase.getValue("feeds")).remove(s);
						DataBase.save();
					}
					model.fireTableDataChanged();
				}
			});
			add(delete);
		}
	}
}
