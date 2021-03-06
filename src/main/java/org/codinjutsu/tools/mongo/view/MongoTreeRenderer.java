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

package org.codinjutsu.tools.mongo.view;

import org.apache.commons.lang.StringUtils;
import org.codinjutsu.tools.mongo.model.MongoCollection;
import org.codinjutsu.tools.mongo.model.MongoDatabase;
import org.codinjutsu.tools.mongo.model.MongoServer;
import org.codinjutsu.tools.mongo.utils.GuiUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

class MongoTreeRenderer extends DefaultTreeCellRenderer {

    private static final Icon MONGO_SERVER = GuiUtils.loadIcon("mongo_logo.png");
    private static final Icon MONGO_DATABASE = GuiUtils.loadIcon("database.png");
    private static final Icon MONGO_COLLECTION = GuiUtils.loadIcon("folder.png");

    @Override
    public Component getTreeCellRendererComponent(JTree mongoTree, Object value, boolean isSelected, boolean isExpanded, boolean isLeaf, int row, boolean focus) {

        DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;

        Object userObject = node.getUserObject();
        if (userObject instanceof MongoServer) {
            MongoServer mongoServer = (MongoServer) userObject;
            String host = String.format("%s/%s", mongoServer.getServerName(), mongoServer.getServerPort());
            String label = mongoServer.getLabel();
            super.getTreeCellRendererComponent(mongoTree, StringUtils.isBlank(label) ? host : label, isSelected, isExpanded, isLeaf, row, focus);
            setIcon(MONGO_SERVER);
            setToolTipText(host);
            return this;
        } else if (userObject instanceof MongoDatabase) {
            MongoDatabase mongoDatabase = (MongoDatabase) userObject;
            super.getTreeCellRendererComponent(mongoTree, mongoDatabase.getName(), isSelected, isExpanded, isLeaf, row, focus);
            setIcon(MONGO_DATABASE);
            return this;
        } else if (userObject instanceof MongoCollection) {
            MongoCollection mongoCollection = (MongoCollection) userObject;
            super.getTreeCellRendererComponent(mongoTree, mongoCollection.getName(), isSelected, isExpanded, isLeaf, row, focus);
            setIcon(MONGO_COLLECTION);
            return this;
        } else {
            return super.getTreeCellRendererComponent(mongoTree, value, isSelected, isExpanded, isLeaf, row, focus);
        }
    }
}
