package com.github.jasminb.jsonapi;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.github.jasminb.jsonapi.annotations.Id;
import com.github.jasminb.jsonapi.annotations.Type;
//import org.json.JSONObject;
import org.junit.Test;
//import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class LinksTest {

    private final static String SERIALIZED_JSON = "{\n" +
            "\t\"data\": {\n" +
            "\t\t\"type\": \"dragon\",\n" +
            "\t\t\"id\": \"TROGDOR-ABC\",\n" +
            "\t\t\"links\": {\n" +
            "\t\t\t\"self\": \"http://www.homestarrunner.com/sbemail58.html\"\t\n" +
            "\t\t},\n" +
            "\t\t\"attributes\": {\n" +
            "\t\t\t\"otherInfo\": \"TROGDOR COMES IN THE NIGHT\"\n" +
            "\t\t}\n" +
            "\t}\n" +
            "}";


    @Test
    public void testSerializingWithLinks() throws Exception{
        ResourceConverter converter = new ResourceConverter(EntityWithLinks.class);
        byte[] bytes = converter.writeObject(new EntityWithLinks());
        final String serializedJson = new String(bytes);
        //JSONObject object = new JSONObject(serializedJson);
        //JSONAssert.assertEquals(SERIALIZED_JSON, object, false);

    }


    @Test
    public void testDeserializationWithLinks(){
        ResourceConverter converter = new ResourceConverter(EntityWithLinks.class);
        JSONAPIDocument<EntityWithLinks> entityWithLinksJSONAPIDocument = converter.readDocument(SERIALIZED_JSON.getBytes(), EntityWithLinks.class);
        EntityWithLinks entity = entityWithLinksJSONAPIDocument.get();
        assertEquals("http://www.homestarrunner.com/sbemail58.html", entity.links.getSelf().getHref());

    }

    @Type("dragon")
    public static class EntityWithLinks {

        @Id
        public String id = "TROGDOR-ABC";

        @com.github.jasminb.jsonapi.annotations.Links
        public com.github.jasminb.jsonapi.Links links;

        public String otherInfo = "TROGDOR COMES IN THE NIGHT";

        public String getOtherInfo() {
            return otherInfo;
        }

        public void setOtherInfo(String otherInfo) {
            this.otherInfo = otherInfo;
        }

        public EntityWithLinks() {
            Map<String, Link> linksMap = new HashMap<>();
            linksMap.put(JSONAPISpecConstants.SELF, new Link("http://www.homestarrunner.com/sbemail58.html"));
            links = new com.github.jasminb.jsonapi.Links(linksMap);
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    private static class MetaData{
        private String owner = "Strongbad";

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
            this.owner = owner;
        }

    }
}