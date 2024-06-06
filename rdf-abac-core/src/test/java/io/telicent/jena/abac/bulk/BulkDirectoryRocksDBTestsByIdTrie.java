package io.telicent.jena.abac.bulk;

import io.telicent.jena.abac.labels.Labels;
import io.telicent.jena.abac.labels.LabelsStore;
import io.telicent.jena.abac.labels.LabelsStoreRocksDB;
import io.telicent.jena.abac.labels.TrieNodeTable;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rocksdb.RocksDBException;

/**
 * Run {@link BulkDirectory} tests using the id-based RocksDB label store,
 * using a trie-based (test) node table for space efficiency,
 * using a setup extension which creates the right kind of store.
 */
@ExtendWith(RocksDBSetupExtension.class)
public class BulkDirectoryRocksDBTestsByIdTrie extends AbstractBulkDirectoryRocksDB {

    @Override
    LabelsStore createLabelsStore(LabelsStoreRocksDB.LabelMode labelMode) throws RocksDBException {
        return Labels.createLabelsStoreRocksDBById(dbDir, new TrieNodeTable(), labelMode);
    }
}

