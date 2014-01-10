package eu.stratosphere.api.common.operators;

import java.util.ArrayList;
import java.util.List;

import eu.stratosphere.api.common.io.CollectionInputFormat;
import eu.stratosphere.api.common.io.GenericInputFormat;

public class CollectionDataSource extends GenericDataSource<GenericInputFormat<?>> {

	private static String DEFAULT_NAME = "<Unnamed Collection Data Source>";
	
	protected final List<Object> data;

	// --------------------------------------------------------------------------------------------
	
	/**
	 * Creates a new instance for the given input using the given input format.
	 * 
	 * @param f The {@link CollectionInputFormat} implementation used to read the data.
	 * @param data The input list data. It should be a list of object.
	 * @param name The given name for the Pact, used in plans, logs and progress messages.
	 */
	public CollectionDataSource(CollectionInputFormat f, List<Object> data, String name) {
		super(f, name);
		this.data = data;
		f.setData(data);
	}	
	
	/**
	 * Creates a new instance for the given input using the given input format. The contract has the default name.
	 * The input types will be checked. If the input types don't agree, an exception will occur.
	 * 
	 * @param f The {@link CollectionInputFormat} implementation used to read the data.
	 * @param data The input list data. It should be a list of object.
	 */
	public CollectionDataSource(CollectionInputFormat f, Object[] data) {
		super(f, DEFAULT_NAME);
		this.data = new ArrayList<Object>();
		String type = null;
		List<String> typeList = new ArrayList<String>();
		for (Object o : data) {
			//check the input types for 1-dimension
			if (type != null && !type.equals(o.getClass().getName()))
				throw new RuntimeException("elements of input list should have the same type");
			else
				type = o.getClass().getName();
		
			//check the input types for 2-dimension
			if (typeList.size() == 0 && o.getClass().getName().equals("[Ljava.lang.Object;")) {
				for (Object s: (Object[])o) {
					typeList.add(s.getClass().getName());
				}
			}
			else if (o.getClass().getName().equals("[Ljava.lang.Object;")) {
				int index = 0;
				if (((Object[])o).length != typeList.size())
					throw new RuntimeException("elements of input list should have the same size");
				for (Object s:(Object[])o) 
					if (!s.getClass().getName().equals(typeList.get(index++)))
						throw new RuntimeException("elements of input list should have the same type");
			}
			
			this.data.add(o);
		}
		f.setData(this.data);
	}
	
	
	/**
	 * Creates a new instance for the given input using the given input format. The contract has the default name.
	 * The input types will be checked. If the input types don't agree, an exception will occur.
	 * 
	 * @param data The input list data. It should be a list of object.
	 */
	public CollectionDataSource(Object... args) {
		this(new CollectionInputFormat(), args);
	}
	
	public CollectionDataSource(Object[][] args) {
		this(new CollectionInputFormat(), args);
	}
	
//	public CollectionDataSource(List<Object> data) {
//		this(new CollectionInputFormat(), data);
//	}


	
	/**
	 * Returns the file path from which the input is read.
	 * 
	 * @return The path from which the input shall be read.
	 */
	public List<Object> getData() {
		return this.data;
	}
	
	// --------------------------------------------------------------------------------------------
	
	public String toString() {
		return this.data.toString();
	}
}
