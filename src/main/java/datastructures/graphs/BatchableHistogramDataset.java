package datastructures.graphs;

import datastructures.Batchable;
import org.jfree.data.statistics.HistogramDataset;

public class BatchableHistogramDataset extends HistogramDataset implements Batchable {

    private String batchKey;

    @Override
    public void setBatchKey(String key) {
        batchKey = key;
    }

    @Override
    public String getBatchKey() {
        return batchKey;
    }

    @Override
    public boolean isBatched() {
        return batchKey != null;
    }

    @Override
    public void removeFromBatch() {
        batchKey = null;
    }
}
