package eu.stratosphere.api.common.operators.util;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Interface for serializable iterator
 * @author qml_moon
 */
public interface SerializableIterator<E> extends Iterator<E>, Serializable {


		@Override
		public boolean hasNext();

		@Override
		public E next();

		@Override
		public void remove();
		
}
