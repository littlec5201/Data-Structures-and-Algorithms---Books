
/******************************************************************************************
PROGRAM NAME:   ASSIGNMENT 3: BOOK SET PROGRAM
FILE:           BookSet.java
DESCRIPTION:    Holds a collection of books in a hash map with isbn codes as keys maintained
                in the order which books are added to the collection
*******************************************************************************************/
import javax.swing.ListModel;
import java.util.AbstractSet;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.ArrayList;
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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

public class BookSet extends AbstractSet {
	private LinkedHashMap<String, Book> booksMap;
	private ISBNListModel isbnListModel;
	private String description;
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	DocumentBuilder db;          
	Document document;
	private static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
	private static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";

	// default constructor that inititialises LinkedHashMap and call to super
	// class
	public BookSet() {
		super();
		this.booksMap = new LinkedHashMap<String, Book>();
		isbnListModel = (ISBNListModel)this.getISBNListModel();
	}

	// constructor that will accept a Collection of Books
	public BookSet(Collection<? extends Book> books) {
		super();
		this.booksMap = new LinkedHashMap<String, Book>();
		isbnListModel = (ISBNListModel)this.getISBNListModel();
		for (Book aBook : books) {
			this.add(aBook);
		}
		this.isbnListModel = null;
		this.description = null;
	}

	// constructor that will accept a Document that holds XML information
	public BookSet(Document document) {
		this.booksMap = new LinkedHashMap<String, Book>();
		this.isbnListModel = (ISBNListModel)this.getISBNListModel();
		try {
			db = dbf.newDocumentBuilder();
			this.document = document; 
			NodeList bookSetDescription = document.getElementsByTagName("description");
			description = bookSetDescription.item(0).getTextContent();
			
			NodeList bookNodeList = document.getElementsByTagName("book");
			this.description = "";
			for (int i = 0; i < bookNodeList.getLength(); i++) {
				Element book = (Element)bookNodeList.item(i);
				String isbn = book.getAttribute("isbn");
				
				NodeList titleList = book.getElementsByTagName("title");
				String title = titleList.item(0).getTextContent();
				
				Iterator<String> authors;
				NodeList authorList = book.getElementsByTagName("author");
				ArrayList<String> authorArrayList = new ArrayList<String>();
				for (int j = 0; j < authorList.getLength(); j++) {
					authorArrayList.add(authorList.item(j).getTextContent());
				}
				authors = authorArrayList.iterator();
				
				Publisher publisher;
				NodeList publisherList = book.getElementsByTagName("pub");
				Element publisherNode = (Element)publisherList.item(0);
				String publisherName = publisherNode.getAttribute("name");
				String publisherWebsite = publisherNode.getAttribute("website");
				publisher = new Publisher(publisherName, publisherWebsite);
				
				Book.Cover cover;
				if (book.getAttributes().getNamedItem("cover").getNodeValue() == "soft") {
					cover = Book.Cover.SOFT;
				} else {
					cover = Book.Cover.HARD;
				}
				
				int edition = Integer.parseInt(book.getAttribute("edition"));
//				int edition = Integer.parseInt(editionList.item(0).getTextContent());
//				System.out.println(isbn);
//				System.out.println(cover);
//				System.out.println(edition);
//				System.out.println(title);
//				System.out.println(authors);
//				System.out.println(publisher);
//				System.out.println();
				Book newBook = new Book(isbn, title, authors, publisher, cover, edition);
				this.add(newBook);
			}
		} catch (ParserConfigurationException e) {
			
		}
	}
	
	public void makeDocument(String file) {
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(new File(file));
		} catch (ParserConfigurationException | SAXException | IOException e) {
		}
	}

	// this method adds a Book to the hashmap if is doesnt already contain
	// that isbn number and also adds the isbn value to the isbnListModel
	public boolean add(Book book) {
		if (!booksMap.containsKey(book.getISBN())) {
			isbnListModel.addISBN(book.getISBN());
			booksMap.put(book.getISBN(), book);
			System.out.println("Added " + book);
			return true;
		} else {
			System.out.println("Couldn't add " + book);
			return false;
		}
	}

	// this method removes a Book from the hashmap if it contains
	// that isbn number and also removes the isbn value to the isbnListModel
	public boolean remove(Object o) {
		if (booksMap.containsKey(o)) {
			booksMap.remove(o);
			isbnListModel.removeISBN((String)o);
			System.out.println("Removed " + o);
			return true;
		} else {
			System.out.println("Couldn't remove " + o);
			return false;
		}
	}

	// returns an iterator to iterate through the list
	public Iterator<Book> iterator() { // dont change
		return booksMap.values().iterator();
	}

	// returns the number of values in the hashmap
	public int size() {
		return booksMap.size();
	}

	// clears the hashmap
	public void clear() {
		booksMap.clear();
		booksMap = null;
	}

	// returns the book from hashmap without removing it with
	// the neccessary isbn number
	public Book getBook(String isbn) {
		return booksMap.get(isbn);
	}

	// returns a ListModel with isbn values
	public ListModel getISBNListModel() {
		if (isbnListModel == null) {
			Collection<String> coll = new ArrayList<String>();
			for (String key : booksMap.keySet()) {
				System.out.println(key);
				coll.add(key);
			}
			ListModel listModel = new ISBNListModel(coll);
			return listModel;
		} else {
			return isbnListModel;
		}
	}

	// returns description of XML file
	public String getDescription() {
		try {
			if (document == null) {
				db = dbf.newDocumentBuilder();
				document = db.parse(new File("ComputerBooks.xml"));
				NodeList nodeList = document.getElementsByTagName("description");
				String content = nodeList.item(0).getTextContent();
				return content;
			} else {
				NodeList nodeList = document.getElementsByTagName("description");
				String content = nodeList.item(0).getTextContent();
				return content;
			}
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			
		}
		return null;
	}

	// string representation of XML file
	public String toString() { // dont change
		Iterator<Book> iterator = iterator();
		String data = "";
		while (iterator.hasNext()) {
			Book book = iterator.next();
			data += book.toString() + "\n";
		}
		return data;
	}

	// This method returns all the child Nodes of the parentNode
	// that has a given NodeName (case insensitive)
	private Collection<Node> getAllChildNodes(Node parentNode, String name) { // dont
																				// change
		ArrayList<Node> nodeList = new ArrayList<Node>();
		NodeList childNodes = parentNode.getChildNodes();
		if (name != null) {
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				if (name.equalsIgnoreCase(childNode.getNodeName()))
					nodeList.add(childNode);
			}
		}
		return nodeList;
	}

	// This method returns the attribute of the parentNode as a String
	// that has a given NodeName (case sensitive!) or null if no such
	// attribute is found
	private String getAttributeString(Node parentNode, String name) { // dont															// change
		String attribute = null;
		if ((parentNode != null) && (name != null)) {
			NamedNodeMap attributeNodes = parentNode.getAttributes();
			if (attributeNodes != null) {
				Node attributeNode = attributeNodes.getNamedItem(name);
				if (attributeNode != null)
					attribute = attributeNode.getNodeValue();
			}
		}
		return attribute;
	}

	// This method returns the text content of a node which should
	// occur as Text child nodes of the node that have name "#text"
	// Note there should actually only be a single text node if DOM
	// tree has been normalized
	private String getTextContent(Node node) { // dont change
		String textContent = "";
		if (node != null) {
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				if (childNode instanceof Text)
					textContent += childNode.getNodeValue();
			}
		}
		return textContent;
	}

	// inner class that holds a ArrayList of strings that relate to
	// the isbn keys for the HashMap used for the Gui JList
	// DONT CHANGE THIS CLASS
	private class ISBNListModel extends AbstractListModel {
		private ArrayList<String> isbnList;

		// constructor initialises arrayList and sorts the collection out
		// in its natural order
		public ISBNListModel(Collection<String> isbnData) {
			super();
			isbnList = new ArrayList<String>();
			isbnList.addAll(isbnData);
			Collections.sort(isbnList);
		}

		// gets the element at the specified index
		public Object getElementAt(int index) {
			if (index < isbnList.size())
				return isbnList.get(index);
			else
				return null;
		}

		// gets the size of the arrayList
		public int getSize() {
			return isbnList.size();
		}

		// adds to the isbn to the arrayList and notifies
		// any listeners to it by fireIntervalAdded
		public boolean addISBN(String isbn) {
			int index = Collections.binarySearch(isbnList, isbn);
			index = (index * -1) - 1;
			isbnList.add(index, isbn);
			fireIntervalAdded(this, index, index);
			return true;
		}

		// removes the isbn from the arrayList and notifies
		// any listeners to it by fireIntervalRemoved
		public boolean removeISBN(String isbn) {
			int index = isbnList.indexOf(isbn);
			isbnList.remove(isbn);
			fireIntervalRemoved(this, index, index);
			return true;
		}
	}

	// main method to test the program
	public static void main(String args[]) {
		BookSet bs = new BookSet();
		String file = "ComputerBooks.xml";
		
		System.out.println("==============BOOK SET===============");
		Collection<String> authors = new ArrayList<String>();
		authors.add("Seth Hall");
		authors.add("Bob Chen");

		Book book1 = new Book("1234", "JAVA DEVELOPMENT", authors.iterator());
		Book book2 = new Book("1111", "Lunch Time", authors.iterator());
		Collection<Book> coll = new ArrayList<Book>();
		coll.add(book1);
		coll.add(book2);
		
		BookSet abc = new BookSet(coll);
		System.out.println(abc);
	}
}
