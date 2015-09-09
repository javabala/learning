package com.bala.learning.learning.luence.latest;

import org.apache.lucene.document.FieldType;
import org.apache.lucene.index.DocValuesType;
import org.apache.lucene.index.IndexOptions;

public class CustomFieldType {

	public static FieldType getDoubleFieldType(){
		FieldType doubleFieldType = new FieldType();
		doubleFieldType.setTokenized(true);
		doubleFieldType.setOmitNorms(true);
		doubleFieldType.setIndexOptions(IndexOptions.DOCS);
		doubleFieldType
	        .setNumericType(FieldType.NumericType.DOUBLE);
		doubleFieldType.setStored(true);
		doubleFieldType.setDocValuesType(DocValuesType.NUMERIC);
		doubleFieldType.freeze();
		return doubleFieldType;
	}
}
