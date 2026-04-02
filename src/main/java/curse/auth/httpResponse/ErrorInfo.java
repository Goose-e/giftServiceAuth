package curse.auth.httpResponse;

import java.util.Collection;
import java.util.Iterator;

public class ErrorInfo {

    private final String error;
    private final String errorMsg;

    public ErrorInfo(String error, String errorMsg) {
        this.error = error;
        this.errorMsg = errorMsg;
    }

    public String getError() {
        return error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public static ErrorInfo create(String error, String errorMsg) {
        return new ErrorInfo(error, errorMsg);
    }

    @Deprecated
    public static final Collection<ErrorInfo> noErrors = new EmptyErrorCollection();

    public static final Collection<ErrorInfo> noCollectionsErrors = new EmptyErrorCollection();

    private static class EmptyErrorCollection implements Collection<ErrorInfo> {
        @Override
        public int size() { return 0; }
        @Override
        public boolean isEmpty() { return true; }
        @Override
        public boolean contains(Object o) { return false; }
        @Override
        public Iterator<ErrorInfo> iterator() { return java.util.Collections.emptyIterator(); }
        @Override
        public Object[] toArray() { return new Object[0]; }
        @Override
        public <T> T[] toArray(T[] a) { return a; }
        @Override
        public boolean add(ErrorInfo errorInfo) { return false; }
        @Override
        public boolean remove(Object o) { return false; }
        @Override
        public boolean containsAll(Collection<?> c) { return false; }
        @Override
        public boolean addAll(Collection<? extends ErrorInfo> c) { return false; }
        @Override
        public boolean removeAll(Collection<?> c) { return false; }
        @Override
        public boolean retainAll(Collection<?> c) { return false; }
        @Override
        public void clear() {}
    }
}
