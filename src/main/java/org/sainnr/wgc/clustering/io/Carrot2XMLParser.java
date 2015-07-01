package org.sainnr.wgc.clustering.io;

import org.sainnr.wgc.clustering.data.SingleCluster;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class Carrot2XMLParser {

    public Set<SingleCluster> readFile(File file) throws FileNotFoundException,
            XMLStreamException {
        Set<SingleCluster> clusters = new HashSet<SingleCluster>();
        SingleCluster cluster = null;
//        String text = null;
        Set<Integer> docs = new HashSet<Integer>();
        int docId;

        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(file));
        while (reader.hasNext()) {
            int Event = reader.next();
            switch (Event) {
                case XMLStreamConstants.START_ELEMENT: {
                    if ("group".equals(reader.getLocalName())) {
                        cluster = new SingleCluster();
                        cluster.setId(Integer.parseInt(reader.getAttributeValue(0)));
                        cluster.setScore(Double.parseDouble(reader.getAttributeValue(2)));
                        docs = new HashSet<Integer>();
                    }
                    if ("document".equals(reader.getLocalName())) {
                        docId = Integer.parseInt(reader.getAttributeValue(0));
                        docs.add(docId);
                    }
                    break;
                }
                case XMLStreamConstants.CHARACTERS: {
//                    text = reader.getText().trim();
                    break;
                }
                case XMLStreamConstants.END_ELEMENT: {
                    if ("group".equals(reader.getLocalName())) {
                        cluster.setDocIds(docs);
                        clusters.add(cluster);
                    }
                    break;
                }
            }
        }
        return clusters;
    }
}
