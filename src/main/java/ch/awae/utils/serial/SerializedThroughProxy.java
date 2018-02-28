package ch.awae.utils.serial;

import java.io.ObjectStreamException;
import java.io.Serializable;

public interface SerializedThroughProxy extends Serializable {

	Object writeReplace() throws ObjectStreamException;
}
