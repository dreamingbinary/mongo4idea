/*
 * Copyright (c) 2013 David Boissier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.codinjutsu.tools.mongo.logic;

import com.mongodb.*;
import com.mongodb.util.JSON;
import junit.framework.Assert;
import org.apache.commons.io.IOUtils;
import org.codinjutsu.tools.mongo.ServerConfiguration;
import org.codinjutsu.tools.mongo.model.MongoAggregateOperator;
import org.codinjutsu.tools.mongo.model.MongoCollection;
import org.codinjutsu.tools.mongo.model.MongoCollectionResult;
import org.codinjutsu.tools.mongo.model.MongoQueryOptions;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class MongoManagerTest {

    private MongoManager mongoManager;
    private ServerConfiguration serverConfiguration;


    @Test
    public void loadCollectionsWithEmptyFilter() throws Exception {
        MongoQueryOptions mongoQueryOptions = new MongoQueryOptions();
        mongoQueryOptions.setResultLimit(3);
        MongoCollectionResult mongoCollectionResult = mongoManager.loadCollectionValues(serverConfiguration, new MongoCollection("dummyCollection", "test"), mongoQueryOptions);
        Assert.assertNotNull(mongoCollectionResult);
        Assert.assertEquals(3, mongoCollectionResult.getMongoObjects().size());
    }

    @Test
    public void loadCollectionsWithMatchOperator() throws Exception {
        MongoQueryOptions mongoQueryOptions = new MongoQueryOptions();
        mongoQueryOptions.addQuery(MongoAggregateOperator.MATCH, "{ 'price': 15}");
        mongoQueryOptions.addQuery(MongoAggregateOperator.PROJECT, "{ 'label': 1, 'price': 1}");
        mongoQueryOptions.addQuery(MongoAggregateOperator.GROUP, "{ '_id': '$label', 'total': {'$sum': '$price'}}");
        MongoCollectionResult mongoCollectionResult = mongoManager.loadCollectionValues(serverConfiguration, new MongoCollection("dummyCollection", "test"), mongoQueryOptions);
        Assert.assertNotNull(mongoCollectionResult);

        List<DBObject> mongoObjects = mongoCollectionResult.getMongoObjects();

        Assert.assertEquals(2, mongoObjects.size());
        Assert.assertEquals("{ \"_id\" : \"tutu\" , \"total\" : 15}", mongoObjects.get(0).toString());
        Assert.assertEquals("{ \"_id\" : \"tata\" , \"total\" : 30}", mongoObjects.get(1).toString());
    }

    @Before
    public void setUp() throws Exception {
        Mongo mongo = new Mongo();
        DB db = mongo.getDB("test");

        DBCollection dummyCollection = db.getCollection("dummyCollection");
        clearCollection(dummyCollection);
        fillCollectionWithJsonData(dummyCollection, IOUtils.toString(getClass().getResourceAsStream("dummyCollection.json")));

        mongoManager = new MongoManager();
        serverConfiguration = new ServerConfiguration();
        serverConfiguration.setServerName("localhost");
        serverConfiguration.setServerPort(27017);
    }

    private static void fillCollectionWithJsonData(DBCollection collection, String jsonResource) throws IOException {
        Object jsonParsed = JSON.parse(jsonResource);
        if (jsonParsed instanceof BasicDBList) {
            BasicDBList jsonObject = (BasicDBList) jsonParsed;
            for (Object o : jsonObject) {
                DBObject dbObject = (DBObject) o;
                collection.save(dbObject);
            }
        } else {
            collection.save((DBObject) jsonParsed);
        }
    }

    private static void clearCollection(DBCollection collection) {
        DBCursor cursor = collection.find();
        while (cursor.hasNext()) {
            collection.remove(cursor.next());
        }
    }

}

