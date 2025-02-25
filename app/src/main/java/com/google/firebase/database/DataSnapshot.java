package com.google.firebase.database;

import com.google.firebase.database.core.Path;
import com.google.firebase.database.core.utilities.Validation;
import com.google.firebase.database.core.utilities.encoding.CustomClassMapper;
import com.google.firebase.database.snapshot.IndexedNode;
import com.google.firebase.database.snapshot.NamedNode;
import com.google.firebase.database.snapshot.Node;
import java.util.Iterator;

/* compiled from: com.google.firebase:firebase-database@@19.3.0 */
/* loaded from: classes.dex */
public class DataSnapshot {
    private final IndexedNode node;
    private final DatabaseReference query;

    /* JADX INFO: Access modifiers changed from: package-private */
    public DataSnapshot(DatabaseReference ref, IndexedNode node) {
        this.node = node;
        this.query = ref;
    }

    public DataSnapshot child(String path) {
        DatabaseReference childRef = this.query.child(path);
        Node childNode = this.node.getNode().getChild(new Path(path));
        return new DataSnapshot(childRef, IndexedNode.from(childNode));
    }

    public boolean hasChild(String path) {
        if (this.query.getParent() == null) {
            Validation.validateRootPathString(path);
        } else {
            Validation.validatePathString(path);
        }
        return !this.node.getNode().getChild(new Path(path)).isEmpty();
    }

    public boolean hasChildren() {
        return this.node.getNode().getChildCount() > 0;
    }

    public boolean exists() {
        return !this.node.getNode().isEmpty();
    }

    public Object getValue() {
        return this.node.getNode().getValue();
    }

    public Object getValue(boolean useExportFormat) {
        return this.node.getNode().getValue(useExportFormat);
    }

    public <T> T getValue(Class<T> valueType) {
        Object value = this.node.getNode().getValue();
        return (T) CustomClassMapper.convertToCustomClass(value, valueType);
    }

    public <T> T getValue(GenericTypeIndicator<T> t) {
        Object value = this.node.getNode().getValue();
        return (T) CustomClassMapper.convertToCustomClass(value, t);
    }

    public long getChildrenCount() {
        return this.node.getNode().getChildCount();
    }

    public DatabaseReference getRef() {
        return this.query;
    }

    public String getKey() {
        return this.query.getKey();
    }

    public Iterable<DataSnapshot> getChildren() {
        final Iterator<NamedNode> iter = this.node.iterator();
        return new Iterable<DataSnapshot>() { // from class: com.google.firebase.database.DataSnapshot.1
            @Override // java.lang.Iterable
            public Iterator<DataSnapshot> iterator() {
                return new Iterator<DataSnapshot>() { // from class: com.google.firebase.database.DataSnapshot.1.1
                    @Override // java.util.Iterator
                    public boolean hasNext() {
                        return iter.hasNext();
                    }

                    /* JADX WARN: Can't rename method to resolve collision */
                    @Override // java.util.Iterator
                    public DataSnapshot next() {
                        NamedNode namedNode = (NamedNode) iter.next();
                        return new DataSnapshot(DataSnapshot.this.query.child(namedNode.getName().asString()), IndexedNode.from(namedNode.getNode()));
                    }

                    @Override // java.util.Iterator
                    public void remove() {
                        throw new UnsupportedOperationException("remove called on immutable collection");
                    }
                };
            }
        };
    }

    public Object getPriority() {
        Object priority = this.node.getNode().getPriority().getValue();
        if (priority instanceof Long) {
            return Double.valueOf(((Long) priority).longValue());
        }
        return priority;
    }

    public String toString() {
        return "DataSnapshot { key = " + this.query.getKey() + ", value = " + this.node.getNode().getValue(true) + " }";
    }
}
