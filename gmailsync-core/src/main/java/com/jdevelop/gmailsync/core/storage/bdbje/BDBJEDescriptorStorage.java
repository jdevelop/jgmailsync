package com.jdevelop.gmailsync.core.storage.bdbje;

import java.util.Map;

import com.jdevelop.gmailsync.core.email.EmailDescriptor;
import com.jdevelop.gmailsync.core.storage.DescriptorStorageInterface;
import com.jdevelop.gmailsync.core.storage.exception.StorageException;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.collections.StoredMap;

public class BDBJEDescriptorStorage implements DescriptorStorageInterface {

    private final DatabaseInstance instance;

    private final Map<String, String> messagesMap;

    public BDBJEDescriptorStorage(DatabaseInstance instance) {
        this.instance = instance;
        TupleBinding<String> binding = new TupleBinding<String>() {

            @Override
            public String entryToObject(TupleInput input) {
                return input.readString();
            }

            @Override
            public void objectToEntry(String data, TupleOutput output) {
                output.writeString(data);
            }

        };
        messagesMap = new StoredMap<String, String>(instance
                .getMessagesDatabase(), binding, binding, true);
    }

    public void addDescriptor(EmailDescriptor descriptor)
            throws StorageException {
        messagesMap.put(descriptor.getMessageId(), "1");
    }

    public boolean checkIfDescriptorExists(EmailDescriptor descriptor)
            throws StorageException {
        return messagesMap.containsKey(descriptor.getMessageId());
    }

    @Override
    public void removeDescriptor(EmailDescriptor descriptor)
            throws StorageException {
        messagesMap.remove(descriptor.getMessageId());
    }

}
