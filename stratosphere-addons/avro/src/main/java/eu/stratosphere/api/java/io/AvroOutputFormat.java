/***********************************************************************************************************************
 *
 * Copyright (C) 2010-2013 by the Stratosphere project (http://stratosphere.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/

package eu.stratosphere.api.java.io;


import eu.stratosphere.api.common.io.FileOutputFormat;
import eu.stratosphere.core.fs.Path;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import java.io.IOException;

public class AvroOutputFormat<E> extends FileOutputFormat<E> {

	private static final long serialVersionUID = 1L;

	private final Class<E> avroValueType;

	private transient DataFileWriter<E> dataFileWriter;


	public AvroOutputFormat(Path filePath, Class<E> type) {
		super(filePath);
		this.avroValueType = type;
	}

	public AvroOutputFormat(Class<E> type) {
		this.avroValueType = type;
	}

	@Override
	public void writeRecord(E record) throws IOException {
		dataFileWriter.append(record);
	}

	@Override
	public void open(int taskNumber, int numTasks) throws IOException {
		super.open(taskNumber, numTasks);
		DatumWriter<E> datumWriter;
		Schema schema = null;
		if (org.apache.avro.specific.SpecificRecordBase.class.isAssignableFrom(avroValueType)) {
			datumWriter = new SpecificDatumWriter<E>(avroValueType);
			try {
				schema = ((org.apache.avro.specific.SpecificRecordBase)avroValueType.newInstance()).getSchema();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		} else {
			datumWriter = new ReflectDatumWriter<E>(avroValueType);
			schema = ReflectData.get().getSchema(avroValueType);
		}
		dataFileWriter = new DataFileWriter<E>(datumWriter);
		dataFileWriter.create(schema, this.stream);
	}

	@Override
	public void close() throws IOException {
		dataFileWriter.flush();
		dataFileWriter.close();
		super.close();
	}
}
