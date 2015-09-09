package com.bala.learning.learning.luence.latest;

import java.io.IOException;
import java.nio.file.Paths;

import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.MMapDirectory;

public class IndexSearcher {

	private String[] fields;
	private String queryString = "carriers:UNITEDARABEMIRATES.ALL AND device_models:M.ALL.ALL";
	IndexReader reader;
	org.apache.lucene.search.IndexSearcher searcher;	
	QueryParser parser;

	public static void main(String args[]) throws ParseException, IOException {
		if(args.length < 1){
			System.out.println("Usage : <index dir>");
			System.exit(0);
		}
			
		IndexSearcher searcher = new IndexSearcher(args[0]);
		searcher.search();

	}

	public IndexSearcher(String index) throws IOException {
		this.fields = new String[] { "carriers","cost_metrics","ad_name" };
		reader = DirectoryReader.open(new MMapDirectory(Paths.get(index)));
		searcher = new org.apache.lucene.search.IndexSearcher(reader);
		parser = new MultiFieldQueryParser(fields, new WhitespaceAnalyzer());
	}

	public void search() throws ParseException, IOException {
		Query query1 = parser.parse(queryString);
		SortField[] sortFields = new SortField[2];
		sortFields[1] = new SortField("bid_range", SortField.Type.DOC, true);
		sortFields[0] = new SortField("cost_metrics", SortField.Type.STRING, true);
		Sort sort = new Sort(sortFields);
//		TermQuery tq = new TermQuery(new Term("ad_name","Test Ad"));
//		TermQuery tq1 = new TermQuery(new Term("cost_metrics","CPC"));
		BooleanQuery.Builder qBuilder = new BooleanQuery.Builder();
		qBuilder.add(query1, Occur.MUST);
//		qBuilder.add(tq, Occur.MUST);
//		qBuilder.add(tq1, Occur.MUST);
//		Query nq = NumericRangeQuery.newDoubleRange("bid_range", 0.10d, 0.15d, true, true);
//		qBuilder.add(nq, Occur.MUST);
		Query query = qBuilder.build();
		System.out.println("Searching for: " + query.toString());
		TopDocs totalRes = searcher.search(query, 5, sort);
		System.out.println("Total Res : "+totalRes.getMaxScore());
		ScoreDoc[] hits = totalRes.scoreDocs;
		System.out.println("search result length : "+hits.length);
		for(ScoreDoc hit : hits){
			Document doc = searcher.doc(hit.doc);
			System.out.println("ad_name : "+doc.get("ad_name")+"and ad_id : "+doc.get("ad_id") + " bid_range : "+doc.get("bid_range")+ " and cost metrics : "+doc.get("cost_metrics")+" and device models : "+doc.get("device_models"));
		}

	}

}
