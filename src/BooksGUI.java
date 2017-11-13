import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.MenuBar;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.JTextComponent;

import org.xml.sax.helpers.XMLFilterImpl;

import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import java.util.AbstractSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.dom.NamedNodeMap;

import javax.swing.AbstractListModel;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import java.io.IOException;

public class BooksGUI extends JPanel implements ActionListener {
	Document document;
	BookSet bookSet;
	private static JFrame frame = new JFrame("Books GUI");
	public final static int PANEL_WIDTH = 600;
	public final static int PANEL_HEIGHT = 600;
	final JFileChooser fc = new JFileChooser();
	private JMenuBar menuBar = new JMenuBar();
	private JMenu fileMenu = new JMenu("File");
	private JMenuItem openMenu = new JMenuItem("Open");
	private JMenuItem saveMenu = new JMenuItem("Save");
	private JMenu editMenu = new JMenu("Edit");
	private JMenuItem addMenu = new JMenuItem("Add Book");
	private JMenuItem removeMenu = new JMenuItem("Remove Book");
	private JList<String> myJList;
	private JSplitPane splitPane;
	private JComponent component;
	private ListSelectionModel selectionModel;
	private JPanel rightPanel = new JPanel();
	private JPanel descriptionPanel = new JPanel();
	private JTextField descriptionField;
	private String description = "";
	public static String activeValue = "";
	private String actionBeingPerformed = "";

	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db;
	FileNameExtensionFilter xmlfilter = new FileNameExtensionFilter("xml files (*.xml)", "xml");

	public BooksGUI() {
		super();
		bookSet = new BookSet();
		descriptionPanel.setBackground(Color.RED);
		JPanel descriptionPanel = new JPanel(new BorderLayout());
		descriptionPanel.setBorder(new EmptyBorder(5, 0, 5, 0));

		JPanel innerDescriptionPanel = new JPanel(new BorderLayout());
		innerDescriptionPanel.add(new JLabel("Collection Description: 	"), BorderLayout.WEST);
		innerDescriptionPanel.add(descriptionField = new JTextField(), BorderLayout.CENTER);
		descriptionField.getDocument().addDocumentListener(new TextChangeListener(descriptionField));

		descriptionPanel.add(innerDescriptionPanel, BorderLayout.NORTH);
		frame.add(descriptionPanel, BorderLayout.NORTH);

//		rightPanel.setBackground(Color.BLUE);

		myJList = new JList<String>();
		myJList.addListSelectionListener(new NestedSelectionListener());

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

		splitPane.setDividerLocation(100 + splitPane.getInsets().left);
		splitPane.setLeftComponent(myJList);
		splitPane.setRightComponent(rightPanel);
		// splitPane.setOneTouchExpandable(true);

		// File Menu
		menuBar.add(fileMenu);
		openMenu.addActionListener(this);
		fileMenu.add(openMenu);
		saveMenu.addActionListener(this);
		fileMenu.add(saveMenu);
		// Edit Menu
		menuBar.add(editMenu);
		addMenu.addActionListener(this);
		editMenu.add(addMenu);
		removeMenu.addActionListener(this);
		editMenu.add(removeMenu);
		frame.setJMenuBar(menuBar);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object source = e.getSource();
		if (source == openMenu) {
			actionBeingPerformed = "Open";
			fc.setDialogTitle("Open XML file");
			fc.setFileFilter(xmlfilter);
			int returnVal = fc.showOpenDialog(BooksGUI.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();

				try {
					db = dbf.newDocumentBuilder();
					document = db.parse(file);
				} catch (ParserConfigurationException | SAXException | IOException exception) {

				}

				frame.setTitle("Books GUI - " + file.getName());
				bookSet = null;
				bookSet = new BookSet(document);
				myJList.setModel(bookSet.getISBNListModel());
				description = bookSet.getDescription();
				descriptionField.setText(description.trim());
				myJList.setSelectedIndex(0);
				myJList.setSelectedValue(myJList.getSelectedIndex(), false);
				component = bookSet.getBook((String) myJList.getSelectedValue()).preparePanel();
				splitPane.remove(2);
				splitPane.setRightComponent(component);
				splitPane.repaint();
				splitPane.revalidate();
			} else {
			}
		}
		if (source == saveMenu) {
			fc.setDialogTitle("Save XML file");
			fc.setFileFilter(xmlfilter);
			int returnVal = fc.showSaveDialog(BooksGUI.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				try {
					System.out.println(descriptionField.getText());
					BufferedWriter bw = new BufferedWriter(new FileWriter(file, false));
					bw.write("");
					bw.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>" + System.lineSeparator());
					bw.append("<!-- document of a list of computer books -->" + System.lineSeparator());
					bw.append("<books " + System.lineSeparator());
					bw.append("	xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" + System.lineSeparator());
					bw.append("	xsi:noNamespaceSchemaLocation=\"books.xsd\">" + System.lineSeparator());
					bw.append("	<description>" + System.lineSeparator());
					bw.append("		" + descriptionField.getText() + System.lineSeparator());
					bw.append("	</description>" + System.lineSeparator());
					for (int i = 0; i < bookSet.size(); i++) {
						String bookISBN = myJList.getModel().getElementAt(i);
						Book currentBook = bookSet.getBook(bookISBN);
						String isbn = currentBook.getISBN();
						String cover = "";
						String publisherName = "";
						String publisherWebsite = "";
						if (currentBook.hasPublisher()) {
							Publisher publisher = currentBook.getPublisher();
							publisherName = publisher.getName();
							publisherWebsite = publisher.getWebSite();
						}
						if (currentBook.getCover() == Book.Cover.HARD) {
							cover = "hard";
						} else {
							cover = "soft";
						}
						int edition = currentBook.getEdition();
						String title = currentBook.getTitle();
						Iterator<String> authorList = currentBook.getAuthors();
						bw.append("	<book isbn=\"" + isbn + "\" cover = \"" + cover + "\" edition=\"" + edition
								+ "\">" + System.lineSeparator());
						bw.append("		<title>" + title + "</title>" + System.lineSeparator());
						while (authorList.hasNext()) {
							bw.append("		<author>" + authorList.next() + "</author>" + System.lineSeparator());
						}
						bw.append("		<pub name=\"" + publisherName + "\" website=\"" + publisherWebsite + "\"/>" + System.lineSeparator());
						bw.append("	</book>" + System.lineSeparator());
					}
					bw.append("</books>");
					bw.close();
				} catch (IOException ex) {
				}

			} else {
			}
		}
		if (source == removeMenu) {
			actionBeingPerformed = "Remove";
			try {
				bookSet.remove(getActiveValue());
				myJList.setModel(bookSet.getISBNListModel());
				myJList.setSelectedIndex(0);
				myJList.setSelectedValue(myJList.getSelectedIndex(), false);
				component = bookSet.getBook((String) myJList.getSelectedValue()).preparePanel();
				splitPane.remove(2);
				splitPane.setRightComponent(component);
				splitPane.repaint();
				splitPane.revalidate();
			} catch (NullPointerException npe){
				JOptionPane.showMessageDialog(frame, "No books to remove!");
				splitPane.setRightComponent(rightPanel);
			}
		}
		if (source == addMenu) {
			actionBeingPerformed = "Add";
			String isbn = JOptionPane.showInputDialog("Please enter the book ISBN");
			JOptionPane.showMessageDialog(frame, "Creating book " + isbn);
			Book newBook = new Book(isbn);
			bookSet.add(newBook);
			myJList.setModel(bookSet.getISBNListModel());
			splitPane.remove(2);
			component = newBook.preparePanel();
			splitPane.setRightComponent(component);
			splitPane.repaint();
			splitPane.revalidate();
		}
	}

	// inner class that handles document change events on text components
	// such as JTextField and JTextArea
	private class TextChangeListener implements DocumentListener {
		private JTextComponent component;

		public TextChangeListener(JTextComponent component) {
			this.component = component;
		}

		public void changedUpdate(DocumentEvent e) {
			if (component == descriptionField)
				description = descriptionField.getText();
		}

		public void insertUpdate(DocumentEvent e) {
			changedUpdate(e);
		}

		public void removeUpdate(DocumentEvent e) {
			changedUpdate(e);
		}
	}

	public void setActiveValue(String activeValue) {
		this.activeValue = activeValue;
	}

	public String getActiveValue() {
		return this.activeValue;
	}

	private class NestedSelectionListener implements ListSelectionListener {
		public void valueChanged(ListSelectionEvent e) {
			setActiveValue((String) myJList.getSelectedValue());
			if (!e.getValueIsAdjusting()) {
				if (actionBeingPerformed != "Open" && actionBeingPerformed != "Save" && actionBeingPerformed != "Add" && actionBeingPerformed != "Remove") {
					component = bookSet.getBook((String) myJList.getSelectedValue()).preparePanel();
					splitPane.remove(2);
					splitPane.setRightComponent(component);
				}
				splitPane.repaint();
				splitPane.revalidate();
				actionBeingPerformed = null;
			}
		}
	}

	public static void main(String[] args) {
		BooksGUI gui = new BooksGUI();
		frame.getContentPane().add(gui);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(gui.splitPane, BorderLayout.CENTER);
		frame.pack();
		frame.setSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
		frame.setResizable(false);
		frame.setVisible(true);
		frame.repaint();
	}
}
