package com.bala.learning.learning.luence.latest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.DoubleField;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.SortedDocValuesField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;

public class Indexer {

	private String formatFile = "/home/bala/lucene/formats/ads_format.csv";
	private String contentFile = "/home/bala/lucene/data/ads.csv";
	private String indexDir = "/home/bala/lucene/index/ads";
	private LuceneField[] luceneFields;
	private static ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();;

	public static void main(String args[]) throws IOException {
		if (args.length < 3) {
			System.out.println("usage : <format file> <content file> <index dir>");
			System.exit(0);
		}
		Indexer indexer = new Indexer(args[0], args[1], args[2]);
		Runnable runnable = new Runnable() {
			public void run() {
				// do your processing here
				try {
					indexer.createIndex();
					System.out.println("Finished refresh method");
				} catch (Throwable t) {
					System.out.println(t.getMessage());
				}

			}

		};
		scheduler.scheduleAtFixedRate(runnable, 40, 1, TimeUnit.SECONDS);
		indexer.createIndex();
	}

	public Indexer(String formatFile, String contentFile, String indexDir) {
		this.formatFile = formatFile;
		this.contentFile = contentFile;
		this.indexDir = indexDir;
	}

	public void createIndex() throws IOException {
		Directory dir = FSDirectory.open(Paths.get(indexDir));
		Analyzer analyzer = new WhitespaceAnalyzer();
		IndexWriterConfig iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(OpenMode.CREATE);
		readFormat(formatFile);
		IndexWriter writer = new IndexWriter(dir, iwc);
		createDocument(writer, contentFile);
		writer.commit();
		writer.close();
	}

	private void createDocument(IndexWriter writer, String contentFile) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(contentFile), StandardCharsets.UTF_8);
		for (String line : lines) {
			System.out.println("Indexing line : " + line);
			addDocument(line, writer);
		}
		System.out.println("Indexing done");
		System.out.println(writer.maxDoc());
	}

	private void addDocument(String line, IndexWriter writer) throws IOException {
		final String[] fields = StringUtils.splitPreserveAllTokens(line, ",");
		final Document doc = new Document();
		for (int i = 0; i < luceneFields.length; i++) {
			LuceneField field = luceneFields[i];
			if (field.type.equals("INT"))
				doc.add(new IntField(field.name, Integer.parseInt(fields[i]), field.store));
			else if (field.type.equals("DOUBLE"))
				doc.add(new DoubleField(field.name, Double.parseDouble(fields[i]),
						CustomFieldType.getDoubleFieldType()));
			else if (field.type.equals("STRING")) {
				doc.add(new StringField(field.name, fields[i], field.store));
				if (field.sortable)
					doc.add(new SortedDocValuesField(field.name, new BytesRef(fields[i])));
			} else if (field.type.equals("TEXT"))
				doc.add(new TextField(field.name, fields[i], field.store));
			else
				System.out.println("Unable to find the approperiate field");
			System.out.println(doc.toString());
		}
		writer.addDocument(doc);
	}

	private static class LuceneField {

		public final String name;
		public final String type;
		public final Field.Store store;
		public boolean sortable = false;

		public LuceneField(String line, String sep) {
			final String split[] = StringUtils.split(line, sep);
			name = split[0];
			type = split[1];
			if (split.length > 3)
				sortable = true;
			if (split[2].equals("YES")) {
				store = Field.Store.YES;
			} else {
				store = Field.Store.NO;
			}
		}

	}

	private final void readFormat(String formatFile) throws IOException {
		List<String> lines = Files.readAllLines(Paths.get(formatFile), StandardCharsets.UTF_8);
		createLucenField(lines);
		System.out.println(" Lucene Field length : " + luceneFields.length);

	}

	private void createLucenField(List<String> lines) {
		List<LuceneField> list = new LinkedList<LuceneField>();
		for (String line : lines)
			list.add(new LuceneField(line, ","));
		luceneFields = list.toArray(new LuceneField[] {});
	}
}
