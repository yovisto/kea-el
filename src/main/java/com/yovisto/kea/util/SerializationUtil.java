package com.yovisto.kea.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.log4j.Logger;

/**
 * -----------------------------------------------------------------------------
 * This program demonstrates how to write an application that saves the data
 * content of an arbitrary object by use of Java Object Serialization. In its
 * simplest form, object serialization is an automatic way to save and load the
 * state of an object. Basically, an object of any class that implements the
 * Serialization interface can be saved and restored from a stream. Special
 * stream subclasses, "ObjectInputStream" and "ObjectOutputStream", are used to
 * serialize primitive types and objects. Subclasses of Serializable classes are
 * also serializable. The default serialization mechanism saves the value of an
 * object's nonstatic and nontransient member variables.
 * 
 * One of the most important (and tricky) aspects about serialization is that
 * when an object is serialized, any object references it contains are also
 * serialized. Serialization can capture entire "graphs" of interconnected
 * objects and put them back together on the receiving end. The implication is
 * that any object we serialize must contain only references to other
 * Serializable objects. We can take control of marking nonserializable members
 * as transient or overriding the default serialization mechanisms. The
 * transient modifier can be applied to any instance variable to indicate that
 * its contents are not useful outside of the current context and should never
 * be saved.
 * 
 * In the following example, we create a Hashtable and write it to a disk file
 * called HTExample.ser. The Hashtable object is serializable because it
 * implements the Serializable interface.
 * 
 * The doLoad method, reads the Hashtable from the HTExample.ser file, using the
 * readObject() method of ObjectInputStream. The ObjectInputStream class is a
 * lot like DataInputStream, except that it includes the readObject() method.
 * The return type of readObject() is Object, so we will need to cast it to a
 * Hashtable.
 * 
 * @version 1.0
 * @author Jeffrey M. Hunter (jhunter@idevelopment.info)
 * @author http://www.idevelopment.info
 *         ------------------------------------------
 *         -----------------------------------
 */
public class SerializationUtil {

	/**
	 * Create a simple Hashtable and serialize it to a file called
	 * HTExample.ser.
	 * 
	 * @param serFileName
	 * @param object
	 */

	private static final Logger L = Logger.getLogger(SerializationUtil.class.getName());

	public static void doSave(Object object, String serFileName) {
		Hashtable<String, Object> h = new Hashtable<String, Object>();
		h.put("object", object);

		try {

			FileOutputStream fileOut = new FileOutputStream(serFileName);
			ObjectOutputStream out = new ObjectOutputStream(fileOut);
			out.writeObject(h);
			out.close();
			fileOut.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the contents of a previously serialized object from a file called
	 * HTExample.ser.
	 * 
	 * @param string
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Object doLoad(String serFileName) {

		Hashtable<String, Object> h = null;

		try {

			FileInputStream fileIn = new FileInputStream(serFileName);
			ObjectInputStream in = new ObjectInputStream(fileIn);
			h = (Hashtable) in.readObject();
			in.close();
			fileIn.close();

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (Enumeration e = h.keys(); e.hasMoreElements();) {
			Object obj = e.nextElement();
			return h.get(obj);
		}
		return null;
	}

	/**
	 * Sole entry point to the class and application.
	 * 
	 * @param args
	 *            Array of String arguments.
	 */
	public static void main(String[] args) {
		String t = "blah";

		doSave(t, "response.ser");
		String t2 = (String) doLoad("response.ser");
		L.info(t2);
	}

}
