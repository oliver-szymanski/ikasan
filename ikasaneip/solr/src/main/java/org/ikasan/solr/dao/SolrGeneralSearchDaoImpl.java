package org.ikasan.solr.dao;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.ikasan.solr.model.IkasanSolrDocument;
import org.ikasan.solr.model.IkasanSolrDocumentSearchResults;
import org.ikasan.spec.solr.SolrDaoBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Set;

/**
 * Created by Ikasan Development Team on 04/08/2017.
 */
public class SolrGeneralSearchDaoImpl extends SolrDaoBase implements SolrGeneralSearchDao<IkasanSolrDocumentSearchResults>
{
    /** Logger for this class */
    private static Logger logger = LoggerFactory.getLogger(SolrGeneralSearchDaoImpl.class);

    @Override
    public IkasanSolrDocumentSearchResults search(Set<String> moduleName, Set<String> flowNames, String searchString, long startTime, long endTime, int resultSize)
    {
        IkasanSolrDocumentSearchResults results = null;

        List<IkasanSolrDocument> beans = null;

        StringBuffer queryBuffer = new StringBuffer();

        queryBuffer.append(CREATED_DATE_TIME + COLON).append("[")
                .append(startTime).append(TO).append(endTime).append("]");


        logger.info("queryString: " + queryBuffer);

        SolrQuery query = new SolrQuery();
        query.setQuery(searchString);
        query.setStart(0);
        query.setRows(resultSize);
        query.setSort(CREATED_DATE_TIME, SolrQuery.ORDER.desc);
        query.set("defType", "dismax");
        query.setFilterQueries(queryBuffer.toString());

        String queryFilter = super.buildQuery(moduleName, flowNames, null, null, null, null, null, null);

        if(queryFilter != null && !queryFilter.isEmpty())
        {
            query.setFilterQueries(queryFilter);
        }

        try
        {
            logger.info("query: " + query);

            QueryResponse rsp = this.solrClient.query( query );

            beans = rsp.getBeans(IkasanSolrDocument.class);

            results = new IkasanSolrDocumentSearchResults(beans, rsp.getResults().getNumFound(), rsp.getQTime());
        }
        catch (Exception e)
        {
            throw new RuntimeException("Caught exception perform general ikasan search!", e);
        }

        return results;
    }
}
