package ch.awae.utils.serial;

import java.io.ObjectStreamException;
import java.io.Serializable;

public interface SerializationProxy extends Serializable {

	Object readResolve() throws ObjectStreamException;

}
