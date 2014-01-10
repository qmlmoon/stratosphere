package eu.stratosphere.api.common.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import eu.stratosphere.types.Record;
import eu.stratosphere.types.ValueUtil;

/*
 * input format for the non-file input
 */
public class CollectionInputFormat extends GenericInputFormat<Record> implements UnsplittableInput {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private transient boolean end;
	
	private List<Object> steam;
	
	protected int pos;
	
	private transient Object currObject;
	
	@Override
	public boolean reachedEnd() throws IOException {
		return this.end;
	}

	public boolean readObject() {
		if (pos < steam.size()) {
			currObject = steam.get(pos++);
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean readRecord(Record target, Object b) {
		target.clear();
		//check whether the record field is one-dimensional or multi-dimensional
		if (b.getClass().getName().equals("[Ljava.lang.Object;")) {
			for (Object s : (Object[])b){
				target.addField(ValueUtil.toStratosphere(s));
			}
		}
		else {
			target.setField(0, ValueUtil.toStratosphere(b));
		}
		return true;	
	}
	
	@Override
	public boolean nextRecord(Record record) throws IOException {
		if (readObject()) {
			return readRecord(record, this.currObject);
		} else {
			this.end = true;
			return false;
		}
	}
	
	public void setData(List<Object> data) {
		this.steam = new ArrayList<Object>(data);
		pos = 0;
	}

	
	
}
