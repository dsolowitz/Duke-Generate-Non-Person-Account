package edu.duke.oit.idms.test.idmws.client.result;

import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.SearchResult;

public class SearchResultBuilder {

    private Attributes attributes = new BasicAttributes();

    public SearchResult getSeachResult(String name){
        return new SearchResult(name,null,this.attributes);
    }


}
