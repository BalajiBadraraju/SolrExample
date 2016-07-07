package solrSearchExample;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.solr.client.solrj.SolrQuery;
import org.json.JSONException;

public class SolrQueryBuilder {

	String displayFields;
	boolean columnSortingApplied = false;
	public static final String ASTERISK = "*";
	public static final String EMPTY = "";
	public static final String ALL = "All";
	public static final String COLUMN1 = "COLUMN1";
	public static final String COLUMN2 = "COLUMN2";
	public static final String GROUP = "group";
	public static final String GROUP_FORMAT = "group.format";
	public static final String GROUP_FORMAT_TYPE = "simple";
	public static final String GROUP_COUNT = "group.ngroups";
	public static final String GROUP_FIELD = "group.field";
	private static SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss'Z'");

	// @Override
	public SolrQuery buildSolrQuery(ArrayList<String> datesToBeReprocessed) {

		SolrQuery solrQuery = new SolrQuery().setQuery("*");

		try {

			String dateRangeQuery = getBroadcastDateRange(datesToBeReprocessed);

			if (dateRangeQuery != null && !dateRangeQuery.equals("")) {
				solrQuery.addFilterQuery(dateRangeQuery);
			}
			
			solrQuery.setRows(0);

		} catch (Exception e) {
			System.out.println("Failed when creating query" + e.getMessage());
		}

		return solrQuery;
	}

	/**
	 * @param args
	 * @param logKey
	 * @return the date range filter query string
	 */
	private String getBroadcastDateRange(ArrayList<String> datesToBeReprocessed)
			throws JSONException {

		String dateRangeQuery = "";

		if (datesToBeReprocessed != null) {
			String startDateString;
			Calendar startDateCalendar = Calendar.getInstance();

			try {
				for (int i = 0; i < datesToBeReprocessed.size(); i++) {
					startDateString = datesToBeReprocessed.get(i);
					Date startDate = new SimpleDateFormat("MM/dd/yyyy")
							.parse(startDateString);
					startDateCalendar.setTime(startDate);
					if (dateRangeQuery != null && !dateRangeQuery.equals("")) {
						dateRangeQuery = dateRangeQuery + " OR DATE:\""+ dateFormat.format(startDateCalendar.getTime()) + "\"";
					} else {
						dateRangeQuery = "DATE:\""+ dateFormat.format(startDateCalendar.getTime()) + "\"";
					}
				}
			} catch (JSONException e1) {
				System.out.println("Error parsing query param" + e1);
			} catch (ParseException e) {
				System.out.println("Error parsing query param" + e);
			}
		}

		return dateRangeQuery;
	}

}
