package ch.awae.utils.serial;

import java.io.ObjectStreamException;
import java.io.Serializable;

/**
 * Marks a class as being a serialization proxy.
 * 
 * A proxy is an instance that is serialized in place of an instance of another
 * class. Usually when deserializing a proxy object, it should be replaced by an
 * instance of the class that used this class as a proxy.
 * 
 * A class can also serve as a proxy for itself. This is especially useful when
 * during deserialization existing instances of the class should be used.
 * 
 * @see SerializedThroughProxy
 * 
 * @author Andreas Wälchli
 * @since awaeUtils 1.0.1
 */
public interface SerializationProxy extends Serializable {

    Object readResolve() throws ObjectStreamException;

}
