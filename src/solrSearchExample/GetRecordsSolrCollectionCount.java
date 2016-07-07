package solrSearchExample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrRequest.METHOD;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

public final class GetRecordsSolrCollectionCount {

	private static GetRecordsSolrCollectionCount handler = null;

	GetRecordsSolrCollectionCount() {
		getInstance();
	}

	public static GetRecordsSolrCollectionCount getInstance() {
		synchronized (handler) {
			if (handler == null) {
				handler = new GetRecordsSolrCollectionCount();
			}
			return handler;
		}
	}

	public static Map<String, Long> getRecordsCountInCollection(
			final ArrayList<String> datesToBeReprocessed) {
		Map<String, Long> returnMap = new HashMap<String, Long>();
		long actualCount = getActualCount(datesToBeReprocessed);
		System.out.println(actualCount);

		returnMap.put("Count Before Delete", actualCount);

		return returnMap;
	}

	private static Long getActualCount(ArrayList<String> datesToBeReprocessed) {

		SolrQueryBuilder queryBuilder = new SolrQueryBuilder();
		SolrQuery query = queryBuilder.buildSolrQuery(datesToBeReprocessed);

		String zkHost = "ip1,ip2,ip3/solr";
		CloudSolrServer cloudServer = new CloudSolrServer(zkHost);
		cloudServer.setDefaultCollection("COLLECTION_NAME");
		cloudServer.setZkClientTimeout(250000);
		cloudServer.setZkConnectTimeout(250000);
		cloudServer.connect();

		QueryResponse response = null;
		try {
			// response = server.query(query, METHOD.POST);
			System.out.println("Query is" + query);
			response = cloudServer.query(query, METHOD.POST);
		} catch (SolrServerException e) {
			cloudServer.shutdown();
		}

		if (response != null) {
			SolrDocumentList list = response.getResults();

			for (SolrDocument doc : list) {
				for (String fieldName : doc.getFieldNames()) {
					System.out.println(fieldName);
				}
			}
			return new Long(response.getResults().getNumFound());
		} else {
			return (long) 1;
		}

	}
}